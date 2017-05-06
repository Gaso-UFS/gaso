package com.ericmguimaraes.gaso.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrianodias on 4/29/17.
 */

public class Consumption implements Serializable {

    private int verylow = 0;
    private int low = 0;
    private int average = 0;
    private int high = 0;
    private int veryhigh = 0;

    public void incrementVeryLow() {
        verylow++;
    }
    public void incrementLow() {
        low++;
    }
    public void incrementAverage() {
        average++;
    }
    public void incrementHigh() {
        high++;
    }
    public void incrementVeryHigh() {
        veryhigh++;
    }

    public void incrementComsuption(String str) {
        switch (str) {
            case "muito_baixo":
                incrementVeryLow();
            case "baixo":
                incrementLow();
            case "medio":
                incrementAverage();
            case "alto":
                incrementHigh();
            case "muito_alto":
                incrementVeryHigh();
            default: break;
        }
    }

    public int getComsumptionValue(String consumption) {
        switch (consumption) {
            case "muito_baixo":
                return verylow;
            case "baixo":
                return low;
            case "medio":
                return average;
            case "alto":
                return high;
            case "muito_alto":
                return veryhigh;
            default:
                return 0;
        }
    }

    public List<Integer> getValuesOfComsumptions() {
        List<Integer> c = new ArrayList<>();
        c.add(verylow);
        c.add(low);
        c.add(average);
        c.add(high);
        c.add(veryhigh);
        return c;
    }

    public String[] getNamesOfComsumptions() {
        return new String[] {"muito_baixo", "baixo", "medio", "alto", "muito_alto"};
    }

    public String mostFrequentConsumptionName() {
        int max = 0;
        int indexOfMax = 0;
        List<Integer> comsumptions = getValuesOfComsumptions();
        for(int i = 0; i < comsumptions.size(); i++) {
            if(comsumptions.get(i) > max) {
                max = comsumptions.get(i);
                indexOfMax = i;
            }
        }
        return getNamesOfComsumptions()[indexOfMax];
    }

}
