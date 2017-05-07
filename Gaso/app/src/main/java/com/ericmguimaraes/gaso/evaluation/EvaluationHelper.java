package com.ericmguimaraes.gaso.evaluation;

import android.os.AsyncTask;

import com.ericmguimaraes.gaso.persistence.MilestoneDAO;

import java.util.ArrayList;
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
        // TODO: 03/05/17  initEvaluation

    }

    private static class EvaluationHelperAsyncTask extends AsyncTask<Void, Void , Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<Evaluation> evaluations = new ArrayList<>();

            FuelAmountEvaluator fuelAmountEvaluator = new FuelAmountEvaluator(milestone);
            Evaluation fuelAmountEvaluation = fuelAmountEvaluator.evaluate();
            evaluations.add(fuelAmountEvaluation);

            OBDConsumptionEvaluator obdConsumptionEvaluator = new OBDConsumptionEvaluator(milestone);
            Evaluation obdConsumptionEvaluation = obdConsumptionEvaluator.evaluate();
            evaluations.add(obdConsumptionEvaluation);

            milestone.setEvaluations(evaluations);
            MilestoneDAO dao = new MilestoneDAO();
            dao.addOrUpdate(milestone);

            listener.onDone();
            return null;
        }

    }

    public interface OnEvaluationListener {
        void onDone();
    }


}
