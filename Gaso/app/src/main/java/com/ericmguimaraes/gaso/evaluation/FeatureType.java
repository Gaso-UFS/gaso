package com.ericmguimaraes.gaso.evaluation;

/**
 * Created by ericmguimaraes on 06/05/17.
 */

public enum FeatureType {

    FUEL_CONSUMPTION_OBD_FUEL_LEVEL_AND_OBD_DISTANCE("FUEL_CONSUMPTION_OBD_FUEL_LEVEL_AND_OBD_DISTANCE"), OBD_FUEL_AMOUNT("OBD_FUEL_AMOUNT");

    public String name;

    FeatureType(String name) {
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

}
