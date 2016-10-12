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

import com.ericmguimaraes.gaso.model.Combustive;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ericm on 2/27/2016.
 */
public class CombustiveDAO {

    //TODO

    Context context;

    RealmConfiguration realmConfig;

    Realm realm;

    public CombustiveDAO(Context context){
        this.context = context;
        realmConfig = new RealmConfiguration.Builder(context).deleteRealmIfMigrationNeeded().build();
    }

    public void add(Combustive combustive){
        realm = Realm.getInstance(realmConfig);
        if(combustive.getId()==-1)
            combustive.setId((int) setUniqueId());
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(combustive);
        realm.commitTransaction();
    }

    public void remove(Combustive combustive){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Combustive> query = realm.where(Combustive.class);
        query.equals(combustive);
        RealmResults<Combustive> result = query.findAll();
        realm.beginTransaction();
        if(!result.isEmpty())
            result.deleteLastFromRealm();
        realm.commitTransaction();
    }

    public List<Combustive> findAll(){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Combustive> query = realm.where(Combustive.class);
        RealmResults<Combustive> result = query.findAll();
        List<Combustive> list = new ArrayList<>();
        for(Combustive c: result){
            list.add(createNewCombustive(c));
        }
        return list;
    }

    public long setUniqueId() {
        realm = Realm.getInstance(realmConfig);
        Number num = realm.where(Combustive.class).max("id");
        if (num == null) return 1;
        else return ((long) num + 1);
    }

    private Combustive createNewCombustive(Combustive oldCombustive){
        Combustive newCombustive = new Combustive();
        return newCombustive;
    }

    public Combustive findFirst() {
        realm = Realm.getInstance(realmConfig);
        return realm.where(Combustive.class).findFirst();
    }

    public long count(){
        realm = Realm.getInstance(realmConfig);
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Combustive> query = realm.where(Combustive.class);
        return query.count();
    }
}
