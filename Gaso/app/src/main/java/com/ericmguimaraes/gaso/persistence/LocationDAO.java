package com.ericmguimaraes.gaso.persistence;

import android.content.Context;

import com.ericmguimaraes.gaso.model.Location;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ericm on 2/27/2016.
 */
public class LocationDAO {
    //TODO

    Context context;

    RealmConfiguration realmConfig;

    Realm realm;

    public LocationDAO(Context context){
        this.context = context;
        realmConfig = new RealmConfiguration.Builder(context).build();
    }

    public void add(Location location){
        realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(location);
        realm.commitTransaction();
    }

    public void remove(Location location){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Location> query = realm.where(Location.class);
        query.equals(location);
        RealmResults<Location> result = query.findAll();
        realm.beginTransaction();
        if(!result.isEmpty())
            result.removeLast();
        realm.commitTransaction();
    }

    public List<Location> findAll(){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Location> query = realm.where(Location.class);
        RealmResults<Location> result = query.findAll();
        List<Location> list = new ArrayList<>();
        for(Location c: result){
            list.add(createNewLocation(c));
        }
        return list;
    }

    public long setUniqueId() {
        realm = Realm.getInstance(realmConfig);
        Number num = realm.where(Location.class).max("id");
        if (num == null) return 1;
        else return ((long) num + 1);
    }

    private Location createNewLocation(Location oldLocation){
        Location newLocation = new Location();
        return newLocation;
    }

    public Location findFirst() {
        realm = Realm.getInstance(realmConfig);
        return realm.where(Location.class).findFirst();
    }

    public long count(){
        realm = Realm.getInstance(realmConfig);
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Location> query = realm.where(Location.class);
        return query.count();
    }
}
