package com.ericmguimaraes.gaso.evaluation.evaluators;

import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.evaluation.FeatureType;
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.evaluation.evaluations.OBDConsumptionEvaluation;

/**
 * Created by ericmguimaraes on 06/05/17.
 */

public class OBDConsumptionEvaluator extends Evaluator {

    private static final double ALLOWANCE = 0.2d;

    public OBDConsumptionEvaluator(Milestone milestone) {
        super(milestone);
    }

    @Override
    public Evaluation evaluate() {
        OBDConsumptionEvaluation evaluation = new OBDConsumptionEvaluation();
        evaluation.setFeatureType(FeatureType.FUEL_CONSUMPTION_OBD_FUEL_LEVEL_AND_OBD_DISTANCE);
        double totalDistance = SessionSingleton.getInstance().currentCar.getTotalDistance();
        double totalFuelUsed = SessionSingleton.getInstance().currentCar.getTotalFuelUsed();
        if(totalFuelUsed==0 || totalDistance==0)
            return null;
        double consumptionRateCar = totalDistance/totalFuelUsed;
        double milestoneDistance = milestone.getDistanceRolled();
        double milestoneFuelUsed = milestone.getCombustiveConsumed();
        if(milestoneDistance==0 || milestoneFuelUsed==0)
            return null;
        double consumptionRateMilestone = milestoneDistance/milestoneFuelUsed;
        evaluation.setConsumptionRateCar(consumptionRateCar);
        evaluation.setConsumptionRateMilestone(consumptionRateMilestone);
        double diference = consumptionRateCar - consumptionRateMilestone;
        if(diference>0 && diference>ALLOWANCE) {
            evaluation.setRate(1);
            evaluation.setMessage("O consumo de combustível nesse percurso foi menor que a média geral de consumo do carro. Isso pode significar um problema no carro, variações na qualidade do combustível ou uma forma diferente de dirigir durante esse percurso.");
        } if (diference<0 && Math.abs(diference)>ALLOWANCE) {
            evaluation.setRate(-1);
            evaluation.setMessage("O consumo de combustível nesse percurso foi maior que a média geral de consumo do carro. Isso pode significar um problema no carro, variações na qualidade do combustível ou uma forma diferente de dirigir durante esse percurso.");
        } else {
            evaluation.setRate(0);
            evaluation.setMessage("O consumo de combustível nesse percurso foi similar que a média geral de consumo do carro.");
        }
        return evaluation;
    }

}
