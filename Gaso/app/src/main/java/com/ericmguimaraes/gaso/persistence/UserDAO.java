package com.ericmguimaraes.gaso.persistence;

import android.content.Context;

import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ericm on 2/27/2016.
 */
public class UserDAO {

    Context context;

    RealmConfiguration realmConfig;

    Realm realm;

    public UserDAO(Context context){
        this.context = context;
        realmConfig = new RealmConfiguration.Builder(context).build();
    }

    public void add(User user){
        realm = Realm.getInstance(realmConfig);
        if(user.getId()==-1)
            user.setId((int) setUniqueId());
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
    }

    public void remove(User user){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<User> query = realm.where(User.class);
        query.equals(user);
        RealmResults<User> result = query.findAll();
        realm.beginTransaction();
        if(!result.isEmpty())
            result.deleteLastFromRealm();
        realm.commitTransaction();
    }

    public List<User> findAll(){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<User> query = realm.where(User.class);
        RealmResults<User> result = query.findAll();
        List<User> list = new ArrayList<>();
        for(User u: result){
            list.add(createNewUser(u));
        }
        return list;
    }

    public User findFirst(){
        realm = Realm.getInstance(realmConfig);
        return realm.where(User.class).findFirst();
    }

    public long setUniqueId() {
        realm = Realm.getInstance(realmConfig);
        Number num = realm.where(User.class).max("id");
        if (num == null) return 1;
        else return ((long) num + 1);
    }

    private User createNewUser(User oldUser){
        User newUser = new User();
        newUser.setName(oldUser.getName());
        newUser.setEmail(oldUser.getEmail());
        return newUser;
    }

    public long count(){
        realm = Realm.getInstance(realmConfig);
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Car> query = realm.where(Car.class);
        return query.count();
    }

}
