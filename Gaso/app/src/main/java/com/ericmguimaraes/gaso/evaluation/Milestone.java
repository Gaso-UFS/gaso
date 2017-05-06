package com.ericmguimaraes.gaso.evaluation;

import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.FuzzyConsumption;
import com.ericmguimaraes.gaso.model.FuelSource;
import com.google.firebase.database.Exclude;

import java.util.List;

/**
 * Created by ericm on 18-Oct-16.
 */

public class Milestone {

    private String uid;
    private long creationDate;
    private float combustiveConsumed;
    private float distanceRolled;
    private FuzzyConsumption fuzzyConsumption;
    private List<FuelSource> fuelSources;
    private float initialFuelLevel;
    private List<Evaluation> evaluations;

    @Exclude
    private Car car;

    private String carUid;

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

    public  void setFuzzyConsumption(FuzzyConsumption fuzzyConsumption) {
        this.fuzzyConsumption = fuzzyConsumption;
    }

    public FuzzyConsumption getFuzzyConsumption() {
        return fuzzyConsumption;
    }

    public List<FuelSource> getFuelSources() {
        return fuelSources;
    }

    public void setFuelSources(List<FuelSource> fuelSources) {
        this.fuelSources = fuelSources;
    }

    public void setInitialFuelLevel(float initialFuelLevel) {
        this.initialFuelLevel = initialFuelLevel;
    }

    public float getInitialFuelLevel() {
        return initialFuelLevel;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getCarUid() {
        return carUid;
    }

    public void setCarUid(String carUid) {
        this.carUid = carUid;
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }
}
