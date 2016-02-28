package com.ericmguimaraes.gaso.persistence;

import android.content.Context;

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
        realm = Realm.getInstance(realmConfig);
    }

    public void add(User user){
        if(user.getId()==-1)
            user.setId((int) setUniqueId());
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
    }

    public void remove(User user){
        RealmQuery<User> query = realm.where(User.class);
        query.equals(user);
        RealmResults<User> result = query.findAll();
        result.removeLast();
    }

    public List<User> findAll(){
        RealmQuery<User> query = realm.where(User.class);
        RealmResults<User> result = query.findAll();
        return new ArrayList<User>(Arrays.<User>asList((User[]) result.toArray()));
    }

    public long setUniqueId() {
        Number num = realm.where(User.class).max("id");
        if (num == null) return 1;
        else return ((long) num + 1);
    }

}
