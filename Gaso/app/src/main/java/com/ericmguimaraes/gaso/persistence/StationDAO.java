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
import com.ericmguimaraes.gaso.model.Station;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericm on 2/27/2016.
 */
public class StationDAO {

    private DatabaseReference mDatabase;

    public StationDAO(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addOrUpdate(final Station station){
        mDatabase.child(Constants.FIREBASE_STATIONS).child(station.getId()).setValue(station);
    }

    public void addOrUpdate(final Station station, final OneStationReceivedListener listener){
        if (listener != null) {
            mDatabase.child(Constants.FIREBASE_STATIONS).child(station.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Station gasStation = dataSnapshot.getValue() != null ? merge(station, dataSnapshot.getValue(Station.class)) : station;
                    mDatabase.child(Constants.FIREBASE_STATIONS).child(gasStation.getId()).setValue(gasStation);
                    listener.onStationReceived(gasStation);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        } else {
            mDatabase.child(Constants.FIREBASE_STATIONS).child(station.getId()).setValue(station);
        }
    }

    private Station merge(Station stationFromMemory, Station stationFromDatabase) {
        if (stationFromMemory == null)
            return  stationFromMemory;
        Station returnStation = new Station();
        returnStation.setId(stationFromMemory.getId());
        returnStation.setName(stationFromMemory.getName());
        returnStation.setAddress(stationFromMemory.getAddress());
        returnStation.setPhoneNumber(stationFromMemory.getPhoneNumber());
        returnStation.setReference(stationFromMemory.getReference());
        returnStation.setLocation(stationFromMemory.getLocation());
        returnStation.setCombustiveRate(stationFromMemory.getCombustiveRate());
        returnStation.setCombustives(stationFromMemory.getCombustives());
        returnStation.setGeneralRate((stationFromMemory.getGeneralRate()+stationFromDatabase.getGeneralRate())/2);
        returnStation.setMoneyRate(stationFromMemory.getMoneyRate());
        return returnStation;
    }

    public void remove(Station station){
        if(station.getId() !=null) {
            mDatabase.child(Constants.FIREBASE_STATIONS).child(station.getId()).removeValue();
        }
    }
    public void removeAll() {
        mDatabase.child(Constants.FIREBASE_STATIONS).removeValue();
    }

    public void findStationById(String id, final OneStationReceivedListener listener) {
        mDatabase.child(Constants.FIREBASE_STATIONS).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onStationReceived(dataSnapshot.getValue(Station.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onCancelled(databaseError);
            }
        });
    }

    public void findAll(final StationsListReceivedListener listener){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_STATIONS).orderByChild("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Station> list = new ArrayList<Station>();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        list.add((Station) postSnapshot.getValue(Station.class));
                    }
                    listener.onStationsReceived(list);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        }
    }

    public interface OneStationReceivedListener {
        void onStationReceived(Station station);
        void onCancelled(DatabaseError databaseError);
    }

    public interface StationsListReceivedListener {
        void onStationsReceived(List<Station> stations);
        void onCancelled(DatabaseError databaseError);
    }
    private Station createNewStation(Station oldStation){
        Station newStation = new Station();
        newStation.setId(oldStation.getId());
        newStation.setName(oldStation.getName());
        newStation.setAddress(oldStation.getAddress());
        newStation.setPhoneNumber(oldStation.getPhoneNumber());
        newStation.setReference(oldStation.getReference());
        newStation.setLocation(oldStation.getLocation());
        newStation.setCombustiveRate(oldStation.getCombustiveRate());
        newStation.setCombustives(oldStation.getCombustives());
        newStation.setGeneralRate(oldStation.getGeneralRate());
        newStation.setMoneyRate(oldStation.getMoneyRate());
        return newStation;
    }


}
