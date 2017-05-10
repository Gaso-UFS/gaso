package com.ericmguimaraes.gaso.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrianodias on 4/29/17.
 */

public class FuzzyConsumption implements Serializable {

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

    public int getVerylow() {
        return verylow;
    }

    public int getLow() {
        return low;
    }

    public int getAverage() {
        return average;
    }

    public int getHigh() {
        return high;
    }

    public int getVeryhigh() {
        return veryhigh;
    }

    public void incrementComsuption(String str) {
        if (str.equals("muito_baixo")) {
            incrementVeryLow();
        } else if (str.equals("baixo")) {
            incrementLow();
        } else if (str.equals("medio")) {
            incrementAverage();
        } else if (str.equals("alto")) {
            incrementHigh();
        } else if (str.equals("muito_alto")) {
            incrementVeryHigh();
        }
    }

    private List<Integer> getValuesOfComsumptions() {
        List<Integer> c = new ArrayList<>();
        c.add(verylow);
        c.add(low);
        c.add(average);
        c.add(high);
        c.add(veryhigh);
        return c;
    }

    private List<String> getNamesOfComsumptions() {
        List<String> c = new ArrayList<>();
        c.add("muito_baixo");
        c.add("baixo");
        c.add("medio");
        c.add("alto");
        c.add("muito_alto");
        return c;
    }

    public String mostFrequentConsumptionName() {
        int max = 0;
        int indexOfMax = 0;
        List<Integer> comsumptions = getValuesOfComsumptions();
        for (Integer comsumption : comsumptions) {
            if (comsumption > max) {
                max = comsumption;
                indexOfMax = comsumptions.indexOf(comsumption);
            }
        }
        return getNamesOfComsumptions().get(indexOfMax);
    }

    public int getTotal() {
        int total = verylow+low+average+high+veryhigh;
        return total;
    }

    public float getPercentage(int value){
        return ((int) ((float) value/getTotal()*100));
    }

}
