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

import android.content.Context;
import android.util.Log;

import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.model.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ericm on 2/28/2016.
 */
public class ExpensesDAO {

    private DatabaseReference mDatabase;

    public ExpensesDAO(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void add(Expense expense){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            String key = mDatabase.child(Constants.FIREBASE_EXPENSES).child(user.getUid()).push().getKey();
            expense.setUid(key);
            if(expense.getCar()!=null)
                expense.setCarUid(expense.getCar().getid());
            expense.setCar(null);
            if(expense.getStation()!=null)
                expense.setStationName(expense.getStationName());
            expense.setStation(null);
            mDatabase.child(Constants.FIREBASE_EXPENSES).child(user.getUid()).child(key).setValue(expense);
            if(expense.getDate()!=0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(expense.getDate());
                String monthYear = calendar.get(Calendar.MONTH)+":"+calendar.get(Calendar.YEAR);
                mDatabase.child(Constants.FIREBASE_EXPENSES_MONTH).child(user.getUid()).child(monthYear).child(key).setValue(true);
            }
        }
    }

    public void remove(Expense expense){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_EXPENSES).child(user.getUid()).child(expense.getUid()).removeValue();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(expense.getDate());
            String monthYear = cal.get(Calendar.MONTH)+":"+cal.get(Calendar.YEAR);
            mDatabase.child(Constants.FIREBASE_EXPENSES_MONTH).child(user.getUid()).child(monthYear).child(expense.getUid()).removeValue();
        }
    }

    public void findAll(final OnExpensesReceivedListener listener){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_EXPENSES).child(user.getUid()).orderByChild("date").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Expense> list = new ArrayList<Expense>();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        list.add((Expense) postSnapshot.getValue(Expense.class));
                    }
                    listener.OnExpensesReceived(list);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        }
    }

    public void findByMonthAndYear(Date date, final OnExpensesReceivedListener listener){
        final List<Expense> list = new ArrayList<>();
        list.clear();
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        String monthYear = day.get(Calendar.MONTH)+":"+day.get(Calendar.YEAR);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_EXPENSES_MONTH).child(user.getUid()).child(monthYear).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final HashMap<String,String> uids = (HashMap<String, String>) dataSnapshot.getValue();
                    if(uids!=null) {
                        Iterator<String> i = uids.keySet().iterator();
                        final int[] counter = {0};
                        while (i.hasNext()) {
                            final boolean[] hasError = {false};
                            findByUid(i.next(), new OnOneExpensesReceivedListener() {
                                @Override
                                public void OnExpenseReceived(final Expense expense) {
                                    CarDAO dao = new CarDAO();
                                    dao.findCarByID(expense.getCarUid(), new CarDAO.OneCarReceivedListener() {
                                        @Override
                                        public void onCarReceived(Car car) {
                                            counter[0]++;
                                            list.add(expense);
                                            if (counter[0] == uids.size())
                                                listener.OnExpensesReceived(list);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            listener.onCancelled(databaseError);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    listener.onCancelled(databaseError);
                                    hasError[0] = true;
                                }
                            });
                            if (hasError[0])
                                break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        }
    }

    private void findByUid(String uid, final OnOneExpensesReceivedListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_EXPENSES).child(user.getUid()).child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Expense e = dataSnapshot.getValue(Expense.class);
                    CarDAO dao = new CarDAO();
                    if(e!=null)
                        dao.findCarByID(e.getCarUid(), new CarDAO.OneCarReceivedListener() {
                            @Override
                            public void onCarReceived(Car car) {
                                e.setCar(car);
                                listener.OnExpenseReceived(e);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                listener.onCancelled(databaseError);
                            }
                        });
                    else
                        listener.onCancelled(null);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        }
    }

    public interface OnOneExpensesReceivedListener {
        void OnExpenseReceived(Expense expense);
        void onCancelled(DatabaseError databaseError);
    }

    public interface OnExpensesReceivedListener {
        void OnExpensesReceived(List<Expense> expenses);
        void onCancelled(DatabaseError databaseError);
    }

}
