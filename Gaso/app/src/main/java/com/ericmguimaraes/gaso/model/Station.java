package com.ericmguimaraes.gaso.model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ericm on 2/28/2016.
 */
public class Station extends RealmObject {

    @PrimaryKey
    private long id;

    private String name;

    private String address;

    private String phone_number;

    private double lat;

    private double lng;

    private float general_rate;

    private float combustive_rate;

    private float money_rate;

    private RealmList<Combustive> combustives;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public float getGeneral_rate() {
        return general_rate;
    }

    public void setGeneral_rate(float general_rate) {
        this.general_rate = general_rate;
    }

    public float getCombustive_rate() {
        return combustive_rate;
    }

    public void setCombustive_rate(float combustive_rate) {
        this.combustive_rate = combustive_rate;
    }

    public RealmList<Combustive> getCombustives() {
        return combustives;
    }

    public void setCombustives(RealmList<Combustive> combustives) {
        this.combustives = combustives;
    }

    public float getMoney_rate() {
        return money_rate;
    }

    public void setMoney_rate(float money_rate) {
        this.money_rate = money_rate;
    }
}
