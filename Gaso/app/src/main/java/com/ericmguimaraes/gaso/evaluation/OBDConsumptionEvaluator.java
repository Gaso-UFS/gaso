package com.ericmguimaraes.gaso.evaluation;

/**
 * Created by ericmguimaraes on 06/05/17.
 */

public class OBDConsumptionEvaluator extends Evaluator {

    public OBDConsumptionEvaluator(Milestone milestone) {
        super(milestone);
    }

    @Override
    public Evaluation evaluate() {
        Evaluation evaluation = new Evaluation();
        evaluation.setFeatureType(FeatureType.FUEL_CONSUMPTION_OBD_FUEL_LEVEL_AND_OBD_DISTANCE);
                
        return null;
    }

}
