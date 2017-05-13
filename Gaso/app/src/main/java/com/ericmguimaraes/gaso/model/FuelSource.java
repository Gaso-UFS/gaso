package com.ericmguimaraes.gaso.model;

/**
 * Created by ericmguimaraes on 03/05/17.
 */

public class FuelSource {

    String stationId;

    String stationName;

    double value;

    public FuelSource() {
    }

    public FuelSource(double value) {
        this.value = value;
        stationId = "";
        stationName = "Outros";
    }

    public FuelSource(String stationId, String stationName, double value) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.value = value;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public boolean isOutros(){
        return stationId!=null && stationId.isEmpty();
    }
}
