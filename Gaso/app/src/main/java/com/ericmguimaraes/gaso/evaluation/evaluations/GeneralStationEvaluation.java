package com.ericmguimaraes.gaso.evaluation.evaluations;

import java.util.HashMap;

/**
 * Created by ericmguimaraes on 10/05/17.
 */

public class GeneralStationEvaluation extends Evaluation {

    private HashMap<String, Double> ups;

    private HashMap<String, Double> oks;

    private HashMap<String, Double> downs;

    private double upTotal;

    private double okTotal;

    private double downTotal;

    public HashMap<String, Double> getUps() {
        return ups;
    }

    public void setUps(HashMap<String, Double> ups) {
        this.ups = ups;
    }

    public HashMap<String, Double> getOks() {
        return oks;
    }

    public void setOks(HashMap<String, Double> oks) {
        this.oks = oks;
    }

    public HashMap<String, Double> getDowns() {
        return downs;
    }

    public void setDowns(HashMap<String, Double> downs) {
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
