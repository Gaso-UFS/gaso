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
        realmConfig = new RealmConfiguration.Builder(context)
                .deleteRealmIfMigrationNeeded().build();
    }

    public void add(Car car){
        realm = Realm.getInstance(realmConfig);
        if(car.getId()==-1)
            car.setId((int) setUniqueId());
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(car);
        realm.commitTransaction();
    }

    public void remove(Car car){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Car> query = realm.where(Car.class);
        query.equals(car);
        RealmResults<Car> result = query.findAll();
        realm.beginTransaction();
        if(!result.isEmpty())
            result.deleteLastFromRealm();
        realm.commitTransaction();
    }

    public List<Car> findAll(){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Car> query = realm.where(Car.class);
        RealmResults<Car> result = query.findAll();
        List<Car> list = new ArrayList<>();
        for(Car c: result){
            list.add(createNewCar(c));
        }
        return list;
    }

    public List<Car> findAllbyUser(User user){
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Car> query = realm.where(Car.class).equalTo("user.id",user.getId());
        RealmResults<Car> result = query.findAll();
        List<Car> list = new ArrayList<>();
        for(Car c: result){
            list.add(createNewCar(c));
        }
        return list;
    }

    public long setUniqueId() {
        realm = Realm.getInstance(realmConfig);
        Number num = realm.where(Car.class).max("id");
        if (num == null) return 1;
        else return ((long) num + 1);
    }

    private Car createNewCar(Car oldCar){
        Car newCar = new Car();
        newCar.setModel(oldCar.getModel());
        newCar.setDescription(oldCar.getDescription());
        return newCar;
    }

    public Car findFirst(User user) {
        realm = Realm.getInstance(realmConfig);
        return realm.where(Car.class).equalTo("user.id",user.getId()).findFirst();
    }

    public long count(){
        realm = Realm.getInstance(realmConfig);
        realm = Realm.getInstance(realmConfig);
        RealmQuery<Car> query = realm.where(Car.class);
        return query.count();
    }
}
