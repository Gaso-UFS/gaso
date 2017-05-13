package com.ericmguimaraes.gaso.evaluation;

import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.evaluation.evaluations.GeneralStationEvaluation;
import com.ericmguimaraes.gaso.model.FuelSource;
import com.ericmguimaraes.gaso.persistence.StationEvaluationDAO;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ericmguimaraes on 10/05/17.
 */

public class StationEvaluationHelper {

    private Milestone milestone;

    public StationEvaluationHelper(Milestone milestone) {
        this.milestone = milestone;
    }

    public void evaluate() {
        if(milestone!=null && milestone.getFuelSources()!=null && milestone.getFuelSources().size()>0 && milestone.getEvaluations()!=null && milestone.getEvaluations().size()>0) {
            final List<FuelSource> fuelSourcesBefore = milestone.getFuelSources();
            float beforeFuelLevel = milestone.getInitialFuelLevel();
            List<Double> percentages = new ArrayList<>();
            // prepara as porcentagens de cada combustivel no milestone
            for(FuelSource f : fuelSourcesBefore) {
                percentages.add(f.getValue() / beforeFuelLevel);
            }
            final StationEvaluationDAO dao = new StationEvaluationDAO();
            for (int i=0; i<percentages.size(); i++) {
                FuelSource fs = fuelSourcesBefore.get(i);
                final Double percentage = percentages.get(i);
                if(!fs.isOutros()) {
                    Iterator<String> iterator = milestone.getEvaluations().keySet().iterator();
                    final int index = i;
                    while (iterator.hasNext()) {
                        final Evaluation eval = milestone.getEvaluations().get(iterator.next());
                        dao.findStationEvaluationById(fuelSourcesBefore.get(i).getStationId(), new StationEvaluationDAO.OneStationEvaluationReceivedListener() {
                            @Override
                            public void onStationEvaluationReceived(HashMap<String, GeneralStationEvaluation> evaluations) {
                                if(evaluations==null)
                                    evaluations = new HashMap<String, GeneralStationEvaluation>();
                                GeneralStationEvaluation ge = new GeneralStationEvaluation();
                                ge.setFeatureType(eval.getFeatureType());
                                if(eval.getRate()>0) {
                                    ge.setUpTotal(percentage+ge.getUpTotal());
                                    if(ge.getUps()==null)
                                        ge.setUps(new HashMap<String, Double>());
                                    ge.getUps().put(String.valueOf(new Date().getTime()), percentage);
                                } if (eval.getRate()<0) {
                                    ge.setDownTotal(percentage+ge.getDownTotal());
                                    if(ge.getDowns()==null)
                                        ge.setDowns(new HashMap<String, Double>());
                                    ge.getDowns().put(String.valueOf(new Date().getTime()), percentage);
                                } else {
                                    ge.setOkTotal(percentage+ge.getOkTotal());
                                    if(ge.getOks()==null)
                                        ge.setOks(new HashMap<String, Double>());
                                    ge.getOks().put(String.valueOf(new Date().getTime()), percentage);
                                }
                                evaluations.put(eval.getFeatureType(), ge);
                                dao.addOrUpdate(fuelSourcesBefore.get(index).getStationId(), evaluations);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //ignore
                            }
                        });
                    }
                }
            }

        }
    }

}
