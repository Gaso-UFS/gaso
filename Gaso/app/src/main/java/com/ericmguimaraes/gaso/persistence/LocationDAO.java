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

    Context context;

    RealmConfiguration realmConfig;

    Realm realm;

    public LocationDAO(Context context){
        this.context = context;
        realmConfig = new RealmConfiguration.Builder(context).deleteRealmIfMigrationNeeded().build();
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
            result.deleteLastFromRealm();
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

    private long setUniqueId() {
        realm = Realm.getInstance(realmConfig);
        Number num = realm.where(Location.class).max("id");
        if (num == null) return 1;
        else return ((long) num + 1);
    }

    private Location createNewLocation(Location oldLocation){
        Location newLocation = new Location();
        newLocation.setLng(oldLocation.getLng());
        newLocation.setLat(oldLocation.getLat());
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
