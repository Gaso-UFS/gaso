package com.ericmguimaraes.gaso.evaluation.evaluators;

import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.evaluation.FeatureType;
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.evaluation.evaluations.OBDConsumptionEvaluation;
import com.ericmguimaraes.gaso.model.FuzzyConsumption;

/**
 * Created by ericmguimaraes on 06/05/17.
 */

public class OBDConsumptionEvaluator extends Evaluator {

    private static final double ALLOWANCE = 0.5d;

    private FuzzyConsumption userConsumption;

    public OBDConsumptionEvaluator(Milestone milestone, FuzzyConsumption userConsumption) {
        super(milestone);
        this.userConsumption = userConsumption;
    }

    @Override
    public Evaluation evaluate() {
        OBDConsumptionEvaluation evaluation = new OBDConsumptionEvaluation();
        evaluation.setFeatureType(FeatureType.FUEL_CONSUMPTION_OBD_FUEL_LEVEL_AND_OBD_DISTANCE);
        double totalDistance = SessionSingleton.getInstance().currentCar.getTotalDistance();
        double totalPercentageFuelUsed = SessionSingleton.getInstance().currentCar.getTotalFuelPercentageUsed();
        if(totalPercentageFuelUsed==0 || totalDistance==0)
            return null;
        double milestoneDistance = milestone.getDistanceRolled();
        double milestoneFuelUsed = milestone.getCombustivePercentageConsumed();
        if(milestoneDistance==0 || milestoneFuelUsed==0)
            return null;
        double consumptionRateMilestone;
        double consumptionRateCar;
        if(milestone.isHasTankMax()){
            consumptionRateCar = totalDistance / ((totalPercentageFuelUsed * milestone.getTankMax())/100);
            consumptionRateMilestone = milestoneDistance / ((milestoneFuelUsed * milestone.getTankMax())/100);
        } else {
            consumptionRateCar = totalDistance / totalPercentageFuelUsed;
            consumptionRateMilestone = milestoneDistance / milestoneFuelUsed;
        }
        evaluation.setConsumptionRateCar(consumptionRateCar);
        evaluation.setConsumptionRateMilestone(consumptionRateMilestone);
        double diference = consumptionRateCar - consumptionRateMilestone;
        if(userConsumption==null || milestone.getFuzzyConsumption()==null || milestone.getFuzzyConsumption().isSimilar(userConsumption)) {
            if(diference>0 && diference>ALLOWANCE) {
                evaluation.setRate(-1);
                evaluation.setMessage("O consumo de combustível nesse percurso foi menor que a média geral de consumo do carro. Isso pode significar um problema no carro, variações na qualidade do combustível ou uma forma diferente de dirigir durante esse percurso.");
            } else if (diference<0 && Math.abs(diference)>ALLOWANCE) {
                evaluation.setRate(1);
                evaluation.setMessage("O consumo de combustível nesse percurso foi maior que a média geral de consumo do carro. Isso pode significar um problema no carro, variações na qualidade do combustível ou uma forma diferente de dirigir durante esse percurso.");
            } else {
                evaluation.setRate(0);
                evaluation.setMessage("O consumo de combustível nesse percurso foi similar que a média geral de consumo do carro.");
            }
        } else {
            evaluation.setRate(0);
            evaluation.setMessage("Não foi possível avaliar o consumo de combustível nesse percurso, pois o padrão de direção/consumo do motorista foi muito fora do esperado.");
        }
        return evaluation;
    }

}
