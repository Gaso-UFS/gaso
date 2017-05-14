package com.ericmguimaraes.gaso;

import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.evaluation.evaluators.OBDConsumptionEvaluator;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.FuzzyConsumption;
import com.ericmguimaraes.gaso.services.LoggingService;
import com.ericmguimaraes.gaso.services.LoggingThread;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class LoggingThreadUnitTest {
    @Test
    public void testDistanceCalculation() throws Exception {
        LoggingThread loggingThread = new LoggingThread(null, null);
        Car c = new Car();

        //firstRead
        double distance = loggingThread.calculateOverDistance(c, 100);
        assertTrue(distance==0);
        assertTrue(c.getLastDistanceRead()==100);
        assertTrue(c.getTotalDistance()==0);

        //secondRead no move
        distance = loggingThread.calculateOverDistance(c, 100);
        assertTrue(distance==0);
        assertTrue(c.getLastDistanceRead()==100);
        assertTrue(c.getTotalDistance()==0);

        //move 10km
        distance = loggingThread.calculateOverDistance(c, 110);
        assertTrue(distance==10);
        assertTrue(c.getLastDistanceRead()==110);
        assertTrue(c.getTotalDistance()==10);

        //move 10km
        distance = loggingThread.calculateOverDistance(c, 120);
        assertTrue(distance==10);
        assertTrue(c.getLastDistanceRead()==120);
        assertTrue(c.getTotalDistance()==20);

        //reset codes and move 20km
        distance = loggingThread.calculateOverDistance(c, 20);
        assertTrue(distance==20);
        assertTrue(c.getLastDistanceRead()==20);
        assertTrue(c.getTotalDistance()==40);

        //move 10km
        distance = loggingThread.calculateOverDistance(c, 30);
        assertTrue(distance==10);
        assertTrue(c.getLastDistanceRead()==30);
        assertTrue(c.getTotalDistance()==50);

        //move 1km
        distance = loggingThread.calculateOverDistance(c, 31);
        assertTrue(distance==1);
        assertTrue(c.getLastDistanceRead()==31);
        assertTrue(c.getTotalDistance()==51);

        //move 0km
        distance = loggingThread.calculateOverDistance(c, 31);
        assertTrue(distance==0);
        assertTrue(c.getLastDistanceRead()==31);
        assertTrue(c.getTotalDistance()==51);

        //move 10km
        distance = loggingThread.calculateOverDistance(c, 41);
        assertTrue(distance==10);
        assertTrue(c.getLastDistanceRead()==41);
        assertTrue(c.getTotalDistance()==61);

    }


    @Test
    public void testFuelCalculation() throws Exception {
        LoggingThread loggingThread = new LoggingThread(null, null);
        Car c = new Car();

        //firstRead
        float fuel = loggingThread.calculateOverFuel(c, 100);
        assertTrue(fuel==0);
        assertTrue(c.getLastFuelPercentageLevel()==100);
        assertTrue(c.getTotalFuelPercentageUsed()==0);

        //secondRead
        fuel = loggingThread.calculateOverFuel(c, 100);
        assertTrue(fuel==0);
        assertTrue(c.getLastFuelPercentageLevel()==100);
        assertTrue(c.getTotalFuelPercentageUsed()==0);

        //spent 10
        fuel = loggingThread.calculateOverFuel(c, 90);
        assertTrue(fuel==10);
        assertTrue(c.getLastFuelPercentageLevel()==90);
        assertTrue(c.getTotalFuelPercentageUsed()==10);

        //spent 10
        fuel = loggingThread.calculateOverFuel(c, 80);
        assertTrue(fuel==10);
        assertTrue(c.getLastFuelPercentageLevel()==80);
        assertTrue(c.getTotalFuelPercentageUsed()==20);

        //refil 20
        fuel = loggingThread.calculateOverFuel(c, 100);
        assertTrue(fuel==0);
        assertTrue(c.getLastFuelPercentageLevel()==100);
        assertTrue(c.getTotalFuelPercentageUsed()==20);

        //spent 20
        fuel = loggingThread.calculateOverFuel(c, 80);
        assertTrue(fuel==20);
        assertTrue(c.getLastFuelPercentageLevel()==80);
        assertTrue(c.getTotalFuelPercentageUsed()==40);

        //spent 0
        fuel = loggingThread.calculateOverFuel(c, 80);
        assertTrue(fuel==0);
        assertTrue(c.getLastFuelPercentageLevel()==80);
        assertTrue(c.getTotalFuelPercentageUsed()==40);

        //spent 1
        fuel = loggingThread.calculateOverFuel(c, 79);
        assertTrue(fuel==1);
        assertTrue(c.getLastFuelPercentageLevel()==79);
        assertTrue(c.getTotalFuelPercentageUsed()==41);

    }
}