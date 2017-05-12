package com.ericmguimaraes.gaso.evaluation.evaluations;

import java.util.HashMap;

/**
 * Created by ericmguimaraes on 10/05/17.
 */

public class GeneralStationEvaluation extends Evaluation {

    private HashMap<Long, Double> ups;

    private HashMap<Long, Double> oks;

    private HashMap<Long, Double> downs;

    private double upTotal;

    private double okTotal;

    private double downTotal;

    public HashMap<Long, Double> getUps() {
        return ups;
    }

    public void setUps(HashMap<Long, Double> ups) {
        this.ups = ups;
    }

    public HashMap<Long, Double> getOks() {
        return oks;
    }

    public void setOks(HashMap<Long, Double> oks) {
        this.oks = oks;
    }

    public HashMap<Long, Double> getDowns() {
        return downs;
    }

    public void setDowns(HashMap<Long, Double> downs) {
        this.downs = downs;
    }

    public double getUpTotal() {
        return upTotal;
    }

    public void setUpTotal(double upTotal) {
        this.upTotal = upTotal;
    }

    public double getOkTotal() {
        return okTotal;
    }

    public void setOkTotal(double okTotal) {
        this.okTotal = okTotal;
    }

    public double getDownTotal() {
        return downTotal;
    }

    public void setDownTotal(double downTotal) {
        this.downTotal = downTotal;
    }
}
