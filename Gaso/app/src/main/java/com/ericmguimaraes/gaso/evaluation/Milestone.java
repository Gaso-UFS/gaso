package com.ericmguimaraes.gaso.evaluation;

/**
 * Created by ericm on 18-Oct-16.
 */

public class Milestone {

    private String uid;
    private long creationDate;
    private float combustiveConsumed;
    private float distanceRolled;

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
}
