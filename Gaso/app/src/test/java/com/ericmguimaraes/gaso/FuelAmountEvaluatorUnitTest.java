package com.ericmguimaraes.gaso;

import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.evaluation.evaluators.FuelAmountEvaluator;
import com.ericmguimaraes.gaso.model.Expense;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class FuelAmountEvaluatorUnitTest {
    @Test
    public void testEvaluation() throws Exception {
        Milestone m = new Milestone();
        m.setTankMax(100);
        Expense ex = new Expense();
        ex.setAmount(100);
        ex.setAmountPercentageOBDRefil(98);
        m.setExpense(ex);
        Evaluation e = new FuelAmountEvaluator(m).evaluate();
        assertTrue(e.getRate()<0);
        m = new Milestone();
        m.setTankMax(100);
        ex = new Expense();
        ex.setAmount(90);
        ex.setAmountPercentageOBDRefil(92);
        m.setExpense(ex);
        e = new FuelAmountEvaluator(m).evaluate();
        assertTrue(e.getRate()>0);
        m = new Milestone();
        m.setTankMax(100);
        ex = new Expense();
        ex.setAmount(100);
        ex.setAmountPercentageOBDRefil(99.1d);
        m.setExpense(ex);
        e = new FuelAmountEvaluator(m).evaluate();
        assertTrue(e.getRate()==0);
    }
}