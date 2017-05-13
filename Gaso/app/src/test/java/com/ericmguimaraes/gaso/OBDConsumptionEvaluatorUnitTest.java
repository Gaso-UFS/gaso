package com.ericmguimaraes.gaso;

import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.evaluation.evaluators.FuelAmountEvaluator;
import com.ericmguimaraes.gaso.evaluation.evaluators.OBDConsumptionEvaluator;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.model.FuzzyConsumption;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class OBDConsumptionEvaluatorUnitTest {
    @Test
    public void testEvaluation() throws Exception {
        Milestone m = new Milestone();
        Car c = new Car();
        c.setTankMaxLevel(100);
        m.setTankMax(100);
        c.setTotalDistance(1000);
        c.setTotalFuelPercentageUsed(100);
        SessionSingleton.getInstance().currentCar = c;
        m.setDistanceRolled(1000);
        m.setCombustivePercentageConsumed(100);
        Evaluation e = new OBDConsumptionEvaluator(m, null).evaluate();
        assertTrue(e.getRate()==0);

        m.setDistanceRolled(1100);
        m.setCombustivePercentageConsumed(100);
        e = new OBDConsumptionEvaluator(m, null).evaluate();
        assertTrue(e.getRate()>0);

        m.setDistanceRolled(900);
        m.setCombustivePercentageConsumed(100);
        e = new OBDConsumptionEvaluator(m, null).evaluate();
        assertTrue(e.getRate()<0);

        m.setDistanceRolled(990);
        m.setCombustivePercentageConsumed(100);
        e = new OBDConsumptionEvaluator(m, null).evaluate();
        assertTrue(e.getRate()==0);

        FuzzyConsumption consumptionAverage = new FuzzyConsumption();
        consumptionAverage.setAverage(20);
        consumptionAverage.setHigh(20);
        consumptionAverage.setLow(20);
        consumptionAverage.setVeryhigh(20);
        consumptionAverage.setVerylow(20);

        m.setDistanceRolled(900);
        m.setCombustivePercentageConsumed(100);
        m.setFuzzyConsumption(consumptionAverage);
        e = new OBDConsumptionEvaluator(m, consumptionAverage).evaluate();
        assertTrue(e.getRate()<0);

        FuzzyConsumption consumptionHigh = new FuzzyConsumption();
        consumptionHigh.setAverage(20);
        consumptionHigh.setHigh(60);
        consumptionHigh.setLow(20);
        consumptionHigh.setVeryhigh(0);
        consumptionHigh.setVerylow(0);

        m.setDistanceRolled(900);
        m.setCombustivePercentageConsumed(100);
        m.setFuzzyConsumption(consumptionAverage);
        e = new OBDConsumptionEvaluator(m, consumptionHigh).evaluate();
        assertTrue(e.getRate()==0);

    }
}