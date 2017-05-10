package com.ericmguimaraes.gaso.evaluation;

import android.os.AsyncTask;

import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.evaluation.evaluations.OBDConsumptionEvaluation;
import com.ericmguimaraes.gaso.evaluation.evaluators.FuelAmountEvaluator;
import com.ericmguimaraes.gaso.evaluation.evaluators.OBDConsumptionEvaluator;
import com.ericmguimaraes.gaso.model.FuzzyConsumption;
import com.ericmguimaraes.gaso.persistence.MilestoneDAO;
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

    public static void initEvaluation(Milestone milestone, OnEvaluationListener listener) {
        EvaluationHelper.milestone = milestone;
        EvaluationHelper.listener = listener;
        new EvaluationHelperAsyncTask().execute();
    }

    private static class EvaluationHelperAsyncTask extends AsyncTask<Void, Void , Void> {

        @Override
        protected Void doInBackground(Void... params) {
            UserDAO dao = new UserDAO();
            dao.findFuzzyConsumption(new FuzzyConsumption.FuzzyConsumptionListener() {
                @Override
                public void onConsumptionFound(FuzzyConsumption consumption) {
                    HashMap<String, Evaluation> evaluations = new HashMap<String, Evaluation>();

                    FuelAmountEvaluator fuelAmountEvaluator = new FuelAmountEvaluator(milestone);
                    Evaluation fuelAmountEvaluation = fuelAmountEvaluator.evaluate();
                    if(fuelAmountEvaluation!=null)
                        evaluations.put(fuelAmountEvaluation.getFeatureType().toString(), fuelAmountEvaluation);

                    OBDConsumptionEvaluator obdConsumptionEvaluator = new OBDConsumptionEvaluator(milestone, consumption);
                    OBDConsumptionEvaluation obdConsumptionEvaluation = (OBDConsumptionEvaluation) obdConsumptionEvaluator.evaluate();
                    if(obdConsumptionEvaluation!=null) {
                        evaluations.put(obdConsumptionEvaluation.getFeatureType().toString(), obdConsumptionEvaluation);
                        milestone.setConsumptionRateCar(obdConsumptionEvaluation.getConsumptionRateCar());
                        milestone.setConsumptionRateMilestone(obdConsumptionEvaluation.getConsumptionRateMilestone());
                    }

                    milestone.setEvaluations(evaluations);
                    MilestoneDAO dao = new MilestoneDAO();
                    dao.addOrUpdate(milestone);

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
