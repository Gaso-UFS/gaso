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
public class CarDAO {

    Context context;

    RealmConfiguration realmConfig;

    Realm realm;

    public CarDAO(Context context){
        this.context = context;
        realmConfig = new RealmConfiguration.Builder(context).build();
        realm = Realm.getInstance(realmConfig);
    }

    public void add(Car car){
        if(car.getId()==-1)
            car.setId((int) setUniqueId());
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(car);
        realm.commitTransaction();
    }

    public void remove(Car car){
        RealmQuery<Car> query = realm.where(Car.class);
        query.equals(car);
        RealmResults<Car> result = query.findAll();
        result.removeLast();
    }

    public List<Car> findAll(){
        RealmQuery<Car> query = realm.where(Car.class);
        RealmResults<Car> result = query.findAll();
        return new ArrayList<Car>(Arrays.<Car>asList((Car[]) result.toArray()));
    }

    public long setUniqueId() {
        Number num = realm.where(Car.class).max("id");
        if (num == null) return 1;
        else return ((long) num + 1);
    }
}
