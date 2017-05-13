package com.ericmguimaraes.gaso;

import com.ericmguimaraes.gaso.evaluation.Milestone;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MilestoneUnitTest {
    @Test
    public void testCalculateFuel() throws Exception {
        Milestone m = new Milestone();
        m.calculateFuelSource(100, null);
        assertTrue(m.getFuelSources().size()>0&&m.getFuelSources().get(0).isOutros()&&m.getFuelSources().get(0).getValue()==100);
    }
}