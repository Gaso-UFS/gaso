package com.ericmguimaraes.gaso.evaluation;

/**
 * Created by ericm on 18-Oct-16.
 */

public class Evaluation {

    private String uid;

    private double rate;

    private String message;

    private FeatureType featureType;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }
}