package com.ericmguimaraes.gaso;

import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.model.FuelSource;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MilestoneUnitTest {

    @Test
    public void testCalculateFuel() throws Exception {
        Milestone m = new Milestone();
        m.setInitialFuelLevel(75);
        Milestone mbefore = new Milestone();
        List<FuelSource> before = new ArrayList<>();
        before.add(new FuelSource(50d));
        before.add(new FuelSource("1","name1",50d));
        mbefore.setFuelSources(before);
        mbefore.setInitialFuelLevel(100);
        Expense e = new Expense();
        e.setStationUid("2");
        e.setStationName("name2");
        m.setExpense(e);
        m.calculateFuelSource(25, mbefore);
        assertTrue( m.getFuelSources().size()>0 && m.getFuelSources().get(0).isOutros() && m.getFuelSources().get(0).getValue()==25);
        assertTrue(m.getFuelSources().get(1).getValue()==25 && m.getFuelSources().get(1).getStationId().equals("1"));
        assertTrue(m.getFuelSources().get(2).getValue()==25 && m.getFuelSources().get(2).getStationId().equals("2"));
    }

}