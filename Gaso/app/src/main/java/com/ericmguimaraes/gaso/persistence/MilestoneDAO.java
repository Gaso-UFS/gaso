/*
 *     Gaso
 *
 *     Copyright (C) 2016  Eric Guimarães
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ericmguimaraes.gaso.persistence;

import android.support.annotation.Nullable;

import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.model.FuzzyConsumption;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ericm on 2/27/2016.
 */
public class MilestoneDAO {

    private DatabaseReference mDatabase;

    public MilestoneDAO(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addOrUpdate(Milestone milestone){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            String key;
            if(milestone.getUid()==null || milestone.getUid().isEmpty()) {
                key = mDatabase.child(Constants.FIREBASE_MILESTONES).child(user.getUid()).child(Constants.FIREBASE_CARS).child(SessionSingleton.getInstance().currentCar.getid()).push().getKey();
                milestone.setUid(key);
            }
            mDatabase.child(Constants.FIREBASE_MILESTONES).child(user.getUid()).child(Constants.FIREBASE_CARS).child(SessionSingleton.getInstance().currentCar.getid()).child(milestone.getUid()).setValue(milestone);
        }
    }

    public void remove(Milestone milestone){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_MILESTONES).child(user.getUid()).child(Constants.FIREBASE_CARS).child(SessionSingleton.getInstance().currentCar.getid()).child(milestone.getUid()).removeValue();
        }
    }

    public void findMilestoneByID(String milestoneUid, final OneMilestoneReceivedListener listener) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_MILESTONES).child(user.getUid()).child(Constants.FIREBASE_CARS).child(SessionSingleton.getInstance().currentCar.getid()).child(milestoneUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listener.onMilestoneReceived(dataSnapshot.getValue(Milestone.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        }
    }

    public void findLastMilestone(final OneMilestoneReceivedListener listener){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_MILESTONES).child(user.getUid()).child(Constants.FIREBASE_CARS).child(SessionSingleton.getInstance().currentCar.getid()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Milestone m = dataSnapshot.getValue(Milestone.class);
                    listener.onMilestoneReceived(m);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        }
    }

    public Milestone createNewMilestone(final float amountOBDRefil, final float fuelLevel, @Nullable final Expense expense) {
        final Milestone milestone = new Milestone();
        milestone.setCreationDate(new Date().getTime());
        milestone.setCombustiveConsumed(0);
        milestone.setFuzzyConsumption(new FuzzyConsumption());
        milestone.setDistanceRolled(0);
        milestone.setInitialFuelLevel(fuelLevel);
        milestone.setCar(SessionSingleton.getInstance().currentCar);
        milestone.setExpense(expense);
        findLastMilestone(new OneMilestoneReceivedListener() {
            @Override
            public void onMilestoneReceived(@Nullable Milestone lastmilestone) {
                milestone.calculateFuelSource(amountOBDRefil, fuelLevel, lastmilestone, expense);
                addOrUpdate(milestone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: 07/05/17 ignore?
            }
        });
        return milestone;
    }

    public void findAll(final MilestonesListReceivedListener listener){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_MILESTONES).child(user.getUid()).child(Constants.FIREBASE_CARS).child(SessionSingleton.getInstance().currentCar.getid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Milestone> list = new ArrayList<Milestone>();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        list.add((Milestone) postSnapshot.getValue(Milestone.class));
                    }
                    listener.onMilestonesReceived(list);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        }
    }

    public interface OneMilestoneReceivedListener {
        void onMilestoneReceived(@Nullable Milestone milestone);
        void onCancelled(DatabaseError databaseError);
    }

    public interface MilestonesListReceivedListener {
        void onMilestonesReceived(List<Milestone> milestones);
        void onCancelled(DatabaseError databaseError);
    }

}
