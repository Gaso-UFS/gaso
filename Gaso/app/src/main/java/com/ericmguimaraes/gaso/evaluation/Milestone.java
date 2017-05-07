package com.ericmguimaraes.gaso.evaluation;

import android.support.annotation.Nullable;

import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.model.FuzzyConsumption;
import com.ericmguimaraes.gaso.model.FuelSource;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericm on 18-Oct-16.
 */

public class Milestone {

    private String uid;
    private long creationDate;
    private float combustiveConsumed;
    private float distanceRolled;
    private FuzzyConsumption fuzzyConsumption;
    private List<FuelSource> fuelSources;
    private float initialFuelLevel;
    private List<Evaluation> evaluations;
    private String expenseUid;

    @Exclude
    private Car car;

    @Exclude
    private Expense expense;

    private String carUid;
    private double consumptionRateCar;
    private double consumptionRateMilestone;

    public Milestone() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public float getCombustiveConsumed() {
        return combustiveConsumed;
    }

    public void setCombustiveConsumed(float combustiveConsumed) {
        this.combustiveConsumed = combustiveConsumed;
    }

    public float getDistanceRolled() {
        return distanceRolled;
    }

    public void setDistanceRolled(float distanceRolled) {
        this.distanceRolled = distanceRolled;
    }

    public  void setFuzzyConsumption(FuzzyConsumption fuzzyConsumption) {
        this.fuzzyConsumption = fuzzyConsumption;
    }

    public FuzzyConsumption getFuzzyConsumption() {
        return fuzzyConsumption;
    }

    public List<FuelSource> getFuelSources() {
        return fuelSources;
    }

    public void setFuelSources(List<FuelSource> fuelSources) {
        this.fuelSources = fuelSources;
    }

    public void setInitialFuelLevel(float initialFuelLevel) {
        this.initialFuelLevel = initialFuelLevel;
    }

    public float getInitialFuelLevel() {
        return initialFuelLevel;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
        this.carUid = car.getid();
    }

    public String getCarUid() {
        return carUid;
    }

    public void setCarUid(String carUid) {
        this.carUid = carUid;
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public String getExpenseUid() {
        return expenseUid;
    }

    public void setExpenseUid(String expenseUid) {
        this.expenseUid = expenseUid;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
        this.expenseUid = expense.getUid();
    }

    public void calculateFuelSource(float amountOBDRefil, @Nullable Milestone before) {
        List<FuelSource> fuelSources = new ArrayList<>();
        if(before==null || before.getFuelSources()==null) {
            if(expense==null)
                fuelSources.add(new FuelSource("","Outros",initialFuelLevel));
            else {
                fuelSources.add(new FuelSource("","Outros",initialFuelLevel-expense.getAmountOBDRefil()));
                fuelSources.add(new FuelSource(expense.getStation().getId(),expense.getStationName(), expense.getAmountOBDRefil()));
            }
        } else {
            List<FuelSource> fuelSourcesBefore = before.getFuelSources();
            float beforeFuelLevel = before.getInitialFuelLevel();
            List<Double> percentages = new ArrayList<>();
            // prepara as porcentagens de cada combustivel no milestone anterior
            for(FuelSource f : fuelSourcesBefore) {
                percentages.add(f.getValue() / beforeFuelLevel);
            }
            // adiciona os novos valores de combustiveis baseado na procentagem e quantidade de restante
            for(int i = 0; i<percentages.size(); i++) {
                FuelSource f = fuelSourcesBefore.get(i);
                fuelSources.add(new FuelSource(f.getStationId(), f.getStationName(), percentages.get(i) * (initialFuelLevel-amountOBDRefil)));
            }
            //adiciona o refil
            if (expense==null) {
                boolean found = false;
                for(FuelSource f : fuelSources) {
                    if (f.getStationId().equals("")) {
                        f.setValue(f.getValue() + amountOBDRefil);
                        found = true;
                    }
                }
                if(!found)
                    fuelSources.add(new FuelSource("","Outros",amountOBDRefil));
            } else {
                fuelSources.add(new FuelSource(expense.getStation().getId(),expense.getStationName(), expense.getAmountOBDRefil()));
            }
        }
    }

    public void setConsumptionRateCar(double consumptionRateCar) {
        this.consumptionRateCar = consumptionRateCar;
    }

    public void setConsumptionRateMilestone(double consumptionRateMilestone) {
        this.consumptionRateMilestone = consumptionRateMilestone;
    }

    public double getConsumptionRateCar() {
        return consumptionRateCar;
    }

    public double getConsumptionRateMilestone() {
        return consumptionRateMilestone;
    }
}
