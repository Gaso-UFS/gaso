package com.ericmguimaraes.gaso.model;

import io.realm.RealmObject;

/**
 * Created by ericm on 3/15/2016.
 */
public class Combustive extends RealmObject {

    private int type;

    private float value;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}