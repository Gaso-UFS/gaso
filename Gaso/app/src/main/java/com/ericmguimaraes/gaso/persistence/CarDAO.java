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

import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.model.Car;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

/**
 * Created by ericm on 2/27/2016.
 */
public class CarDAO {

    private DatabaseReference mDatabase;

    public CarDAO(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addOrUpdate(Car car){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            String key;
            if(car.getid()==null)
                key = mDatabase.child(Constants.FIREBASE_USERS).child(user.getUid()).child(Constants.FIREBASE_CARS).push().getKey();
            else
                key = car.getid();
            mDatabase.child(Constants.FIREBASE_USERS).child(user.getUid()).child(Constants.FIREBASE_CARS).child(key).setValue(true);
            car.setid(key);
            car.setCreationDate(new Date().getTime());
            mDatabase.child(Constants.FIREBASE_CARS).child(user.getUid()).child(key).setValue(car);
        }
    }

    public void remove(Car car){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_USERS).child(user.getUid()).child(Constants.FIREBASE_CARS).child(car.getid()).removeValue();
            mDatabase.child(Constants.FIREBASE_CARS).child(user.getUid()).child(car.getid()).removeValue();
        }
    }

    public void setFavoriteCar(Car car){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_USERS).child(user.getUid()).child(Constants.FIREBASE_FAVORITE_CAR).setValue(car.getid());
        }
    }

    public void loadFavoriteCar(final OneCarReceivedListener listener){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            DatabaseReference ref = mDatabase.child(Constants.FIREBASE_USERS).child(user.getUid()).child(Constants.FIREBASE_FAVORITE_CAR);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String carKey = dataSnapshot.getValue(String.class);
                    if(carKey!=null)
                        findCarByID(carKey,listener);
                    else
                        listener.onCarReceived(null);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        }
    }

    public void findCarByID(String carUid, final OneCarReceivedListener listener) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_CARS).child(user.getUid()).child(carUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listener.onCarReceived(dataSnapshot.getValue(Car.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        } else
            listener.onCancelled(null);

    }

    public interface OneCarReceivedListener {
        void onCarReceived(Car car);
        void onCancelled(DatabaseError databaseError);
    }

    public interface CarsListReceivedListener {
        void onCarReceived(List<Car> cars);
        void onCancelled(DatabaseError databaseError);
    }

}
