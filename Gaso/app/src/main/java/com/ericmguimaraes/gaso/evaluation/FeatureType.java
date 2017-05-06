package com.ericmguimaraes.gaso.evaluation;

/**
 * Created by ericmguimaraes on 06/05/17.
 */

enum FeatureType {

    FUEL_CONSUMPTION_OBD_FUEL_LEVEL_AND_OBD_DISTANCE(0), OBD_FUEL_LEVEL_AND_PAID_FUEL_LEVEL(1);

    public int id;

    FeatureType(int id) {
        this.id = id;
    }

}
