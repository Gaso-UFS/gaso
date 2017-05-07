package com.ericmguimaraes.gaso.evaluation;

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
