package com.ericmguimaraes.gaso.evaluation;

import android.os.AsyncTask;

import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.evaluation.evaluations.GeneralStationEvaluation;
import com.ericmguimaraes.gaso.evaluation.evaluations.OBDConsumptionEvaluation;
import com.ericmguimaraes.gaso.evaluation.evaluators.FuelAmountEvaluator;
import com.ericmguimaraes.gaso.evaluation.evaluators.OBDConsumptionEvaluator;
import com.ericmguimaraes.gaso.model.FuzzyConsumption;
import com.ericmguimaraes.gaso.persistence.MilestoneDAO;
import com.ericmguimaraes.gaso.persistence.StationDAO;
import com.ericmguimaraes.gaso.persistence.UserDAO;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ericmguimaraes on 03/05/17.
 */

public final class EvaluationHelper {

    private static Milestone milestone;

    private static OnEvaluationListener listener;

    private static EvaluationHelperAsyncTask task;

    private static boolean isToEvaluateAgain = false;

    private static Milestone backupMilestone;

    public static void initEvaluation(Milestone milestone, OnEvaluationListener listener) {
        if(task==null) {
            EvaluationHelper.milestone = milestone;
            EvaluationHelper.listener = listener;
            task = new EvaluationHelperAsyncTask();
            task.execute();
        } else {
            backupMilestone = milestone;
            isToEvaluateAgain = true;
        }
    }

    private static class EvaluationHelperAsyncTask extends AsyncTask<Void, Void , Void> {

        @Override
        protected Void doInBackground(Void... params) {
            UserDAO dao = new UserDAO();
            dao.findFuzzyConsumption(new FuzzyConsumption.FuzzyConsumptionListener() {
                @Override
                public void onConsumptionFound(FuzzyConsumption consumption) {
                    HashMap<String, Evaluation> evaluations = milestone.getEvaluations();
                    if(evaluations==null)
                        evaluations = new HashMap<String, Evaluation>();

                    milestone.setCar(SessionSingleton.getInstance().currentCar);
                    milestone.setTankMax(SessionSingleton.getInstance().currentCar.getTankMaxLevel());

                    FuelAmountEvaluator fuelAmountEvaluator = new FuelAmountEvaluator(milestone);
                    Evaluation fuelAmountEvaluation = fuelAmountEvaluator.evaluate();
                    if(fuelAmountEvaluation!=null)
                        evaluations.put(fuelAmountEvaluation.getFeatureType(), fuelAmountEvaluation);

                    OBDConsumptionEvaluator obdConsumptionEvaluator = new OBDConsumptionEvaluator(milestone, consumption);
                    OBDConsumptionEvaluation obdConsumptionEvaluation = (OBDConsumptionEvaluation) obdConsumptionEvaluator.evaluate();
                    if(obdConsumptionEvaluation!=null) {
                        evaluations.put(obdConsumptionEvaluation.getFeatureType(), obdConsumptionEvaluation);
                        milestone.setConsumptionRateCar(obdConsumptionEvaluation.getConsumptionRateCar());
                        milestone.setConsumptionRateMilestone(obdConsumptionEvaluation.getConsumptionRateMilestone());
                    }

                    milestone.setEvaluations(evaluations);

                    StationEvaluationHelper stationEvaluationHelper = new StationEvaluationHelper(milestone);
                    stationEvaluationHelper.evaluate();

                    MilestoneDAO dao = new MilestoneDAO();
                    dao.addOrUpdate(milestone);

                    task = null;
                    if(isToEvaluateAgain) {
                        milestone = backupMilestone;
                        task = new EvaluationHelperAsyncTask();
                        task.execute();
                    }

                    if(listener!=null)
                        listener.onDone();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //ignore
                }
            });
            return null;
        }

    }

    public interface OnEvaluationListener {
        void onDone();
    }


}
