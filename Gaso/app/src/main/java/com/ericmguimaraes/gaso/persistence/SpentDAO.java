package com.ericmguimaraes.gaso.persistence;

import android.content.Context;

import com.ericmguimaraes.gaso.model.Spent;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ericm on 2/28/2016.
 */
public class SpentDAO {

    Context context;

    RealmConfiguration realmConfig;

    Realm realm;

    public SpentDAO(Context context){
        this.context = context;
        realmConfig = new RealmConfiguration.Builder(context).build();
    }

    public void add(Spent spent){
        realm = Realm.getInstance(realmConfig);
        if(spent.getId()==-1)
            spent.setId((int) setUniqueId());
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(spent);
        realm.commitTransaction();
    }

    public void remove(Spent spent){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Spent> query = realm.where(Spent.class);
        query.equals(spent);
        RealmResults<Spent> result = query.findAll();
        realm.beginTransaction();
        if(!result.isEmpty())
            result.deleteLastFromRealm();
        realm.commitTransaction();
    }

    public List<Spent> findAll(){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Spent> query = realm.where(Spent.class);
        RealmResults<Spent> result = query.findAll();
        List<Spent> list = new ArrayList<>();
        for(Spent c: result){
            list.add(createNewSpent(c));
        }
        return list;
    }

    public List<Spent> findByMonth(int month){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Spent> query = realm.where(Spent.class).equalTo("month",month);
        RealmResults<Spent> result = query.findAll();
        List<Spent> list = new ArrayList<>();
        for(Spent c: result){
            list.add(createNewSpent(c));
        }
        return list;
    }

    public long setUniqueId() {
        realm = Realm.getInstance(realmConfig);
        Number num = realm.where(Spent.class).max("id");
        if (num == null) return 1;
        else return ((long) num + 1);
    }

    private Spent createNewSpent(Spent oldSpent){
        Spent newSpent = new Spent();
        newSpent.setAmount(oldSpent.getAmount());
        newSpent.setCar(oldSpent.getCar());
        newSpent.setDate(oldSpent.getDate());
        newSpent.setStation(oldSpent.getStation());
        newSpent.setTotal(oldSpent.getTotal());
        newSpent.setType(oldSpent.getType());
        newSpent.setUser(oldSpent.getUser());
        return newSpent;
    }

    public Spent findFirst() {
        realm = Realm.getInstance(realmConfig);
        return realm.where(Spent.class).findFirst();
    }
}
