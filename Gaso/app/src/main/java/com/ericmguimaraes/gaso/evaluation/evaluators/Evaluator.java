package com.ericmguimaraes.gaso.evaluation.evaluators;

import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;

/**
 * Created by ericm on 18-Oct-16.
 */

public abstract class Evaluator {

    protected Milestone milestone;

    protected Evaluator(Milestone milestone) {
        this.milestone = milestone;
    }

    public abstract Evaluation evaluate();

}
