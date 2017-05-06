package com.ericmguimaraes.gaso.evaluation;

import com.ericmguimaraes.gaso.model.Consumption;
import com.ericmguimaraes.gaso.model.FuelSource;

import java.util.List;

/**
 * Created by ericm on 18-Oct-16.
 */

public class Milestone {

    private String uid;
    private long creationDate;
    private float combustiveConsumed;
    private float distanceRolled;
    private Consumption consumption;
    List<FuelSource> fuelSources;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public float getCombustiveConsumed() {
        return combustiveConsumed;
    }

    public void setCombustiveConsumed(float combustiveConsumed) {
        this.combustiveConsumed = combustiveConsumed;
    }

    public float getDistanceRolled() {
        return distanceRolled;
    }

    public void setDistanceRolled(float distanceRolled) {
        this.distanceRolled = distanceRolled;
    }

    public  void setConsumption(Consumption consumption) {
        this.consumption = consumption;
    }

    public Consumption getConsumption() {
        return consumption;
    }

    public List<FuelSource> getFuelSources() {
        return fuelSources;
    }

    public void setFuelSources(List<FuelSource> fuelSources) {
        this.fuelSources = fuelSources;
    }
}
