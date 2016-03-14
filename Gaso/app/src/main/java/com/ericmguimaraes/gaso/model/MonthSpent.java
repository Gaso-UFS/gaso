package com.ericmguimaraes.gaso.model;

import java.util.Date;

/**
 * Created by ericm on 3/12/2016.
 */
public class MonthSpent {

    private Date month;
    private double value;

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
