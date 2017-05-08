package com.ericmguimaraes.gaso.evaluation.evaluations;

import com.google.firebase.database.Exclude;

/**
 * Created by ericmguimaraes on 07/05/17.
 */
public class OBDConsumptionEvaluation extends Evaluation {

    @Exclude
    private double consumptionRateCar;

    @Exclude
    private double consumptionRateMilestone;

    public double getConsumptionRateCar() {
        return consumptionRateCar;
    }

    public void setConsumptionRateCar(double consumptionRateCar) {
        this.consumptionRateCar = consumptionRateCar;
    }

    public double getConsumptionRateMilestone() {
        return consumptionRateMilestone;
    }

    public void setConsumptionRateMilestone(double consumptionRateMilestone) {
        this.consumptionRateMilestone = consumptionRateMilestone;
    }
}
