package com.ericmguimaraes.gaso.persistence;

import android.content.Context;

import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.Station;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ericm on 2/27/2016.
 */
public class StationDAO {

    Context context;

    RealmConfiguration realmConfig;

    Realm realm;

    public StationDAO(Context context){
        this.context = context;
        realmConfig = new RealmConfiguration.Builder(context).build();
    }

    public Station addOrMerge(Station station){
        realm = Realm.getInstance(realmConfig);
        Station fromDatabase = findById(station.getId());
        Station stationToSave;
        if(fromDatabase!=null)
            stationToSave = merge(station,fromDatabase);
        else
            stationToSave = station;
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(stationToSave);
        realm.commitTransaction();
        return stationToSave;
    }

    private Station merge(Station stationFromMemory, Station stationFromDatabase) {
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
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Station> query = realm.where(Station.class);
        query.equals(station);
        RealmResults<Station> result = query.findAll();
        realm.beginTransaction();
        if(!result.isEmpty())
            result.deleteLastFromRealm();
        realm.commitTransaction();
    }

    public List<Station> findAll(){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Station> query = realm.where(Station.class);
        RealmResults<Station> result = query.findAll();
        List<Station> list = new ArrayList<>();
        for(Station u: result){
            list.add(createNewStation(u));
        }
        return list;
    }

    public Station findFirst(){
        realm = Realm.getInstance(realmConfig);
        return realm.where(Station.class).findFirst();
    }

    public Station findById(String id){
        realm = Realm.getInstance(realmConfig);
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Station> query = realm.where(Station.class);
        query.equalTo("id",id);
        Station result = query.findFirst();
        return result;
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

    public long count(){
        realm = Realm.getInstance(realmConfig);
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Car> query = realm.where(Car.class);
        return query.count();
    }

}
