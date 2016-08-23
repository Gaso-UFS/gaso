package com.ericmguimaraes.gaso.model;

import io.realm.RealmObject;

/**
 * Created by ericm on 22-Aug-16.
 */
public class ObdLog extends RealmObject {

    private long id;

    private String pid;

    private String mode;

    private String status;

    private String presence;

    private long carId;

    private String data;

    private Boolean parsed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPresence() {
        return presence;
    }

    public void setPresence(String presence) {
        this.presence = presence;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Boolean getParsed() {
        return parsed;
    }

    public void setParsed(Boolean parsed) {
        this.parsed = parsed;
    }
}
