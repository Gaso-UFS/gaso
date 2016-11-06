package com.ericmguimaraes.gaso.evaluation;


import com.ericmguimaraes.gaso.model.Car;

/**
 * Created by ericm on 18-Oct-16.
 */

public abstract class Feature<T> {

    String name;

    T value;

    long timestamp;

    Car car;

}
