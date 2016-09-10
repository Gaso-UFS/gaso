package com.ericmguimaraes.gaso.persistence;

import android.content.Context;
import android.util.Log;

import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.model.Spent;
import com.ericmguimaraes.gaso.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        realmConfig = new RealmConfiguration.Builder(context).deleteRealmIfMigrationNeeded().build();
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

    public List<Spent> findAll(User user){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Spent> query = realm.where(Spent.class).equalTo("user.id",user.getId());
        RealmResults<Spent> result = query.findAll();
        List<Spent> list = new ArrayList<>();
        for(Spent c: result){
            list.add(createNewSpent(c));
        }
        return list;
    }

    public List<Spent> findByMonthAndYear(Date date,User user){
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(date);
        firstDay.set(Calendar.DAY_OF_MONTH,firstDay.getActualMinimum(Calendar.DAY_OF_MONTH));
        firstDay.set(Calendar.HOUR,0);
        firstDay.set(Calendar.MINUTE,0);

        Calendar lastDay = Calendar.getInstance();
        lastDay.setTime(date);
        lastDay.set(Calendar.DAY_OF_MONTH,lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        lastDay.set(Calendar.HOUR,23);
        lastDay.set(Calendar.MINUTE,59);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

        Log.d("DATE_SPENT",format.format(firstDay.getTime())+" : "+format.format(lastDay.getTime()));

        realm = Realm.getInstance(realmConfig);
        RealmQuery<Spent> query = realm.
                where(Spent.class)
                .equalTo("user.email", SessionSingleton.getInstance().currentUser.getEmail())
                .between("date",firstDay.getTime(),lastDay.getTime())
                .equalTo("user.id",user.getId());
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
