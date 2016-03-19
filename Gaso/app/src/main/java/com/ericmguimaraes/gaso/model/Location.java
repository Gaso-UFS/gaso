package com.ericmguimaraes.gaso.model;

import io.realm.RealmObject;

/**
 * Created by ericm on 3/19/2016.
 */
public class Location extends RealmObject {

    private double lng;

    private double lat;

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
}
