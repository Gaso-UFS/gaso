package com.ericmguimaraes.gaso.evaluation;

import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.evaluation.evaluations.GeneralStationEvaluation;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.model.FuelSource;
import com.ericmguimaraes.gaso.model.Station;
import com.ericmguimaraes.gaso.persistence.StationDAO;
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
            List<FuelSource> fuelSourcesBefore = milestone.getFuelSources();
            float beforeFuelLevel = milestone.getInitialFuelLevel();
            List<Double> percentages = new ArrayList<>();
            // prepara as porcentagens de cada combustivel no milestone
            for(FuelSource f : fuelSourcesBefore) {
                percentages.add(f.getValue() / beforeFuelLevel);
            }
            final StationDAO dao = new StationDAO();
            for (int i=0; i<percentages.size(); i++) {
                FuelSource fs = fuelSourcesBefore.get(i);
                final Double percentage = percentages.get(i);
                if(!fs.isOutros()) {
                    Iterator<String> iterator = milestone.getEvaluations().keySet().iterator();
                    while (iterator.hasNext()) {
                        final Evaluation eval = milestone.getEvaluations().get(iterator.next());
                        dao.findStationById(fuelSourcesBefore.get(i).getStationId(), new StationDAO.OneStationReceivedListener() {
                            @Override
                            public void onStationReceived(Station station) {
                                if(station!=null) {
                                    if(station.getGeneralEvaluations()==null)
                                        station.setGeneralEvaluations(new HashMap<String, GeneralStationEvaluation>());
                                    if(!station.getGeneralEvaluations().containsKey(eval.getFeatureType())){
                                        GeneralStationEvaluation ge = new GeneralStationEvaluation();
                                        ge.setFeatureType(eval.getFeatureType());
                                        if(eval.getRate()>0) {
                                            ge.setUpTotal(percentage+ge.getUpTotal());
                                            if(ge.getUps()==null)
                                                ge.setUps(new HashMap<Long, Double>());
                                            ge.getUps().put(new Date().getTime(), percentage);
                                        } if (eval.getRate()<0) {
                                            ge.setDownTotal(percentage+ge.getDownTotal());
                                            if(ge.getDowns()==null)
                                                ge.setDowns(new HashMap<Long, Double>());
                                            ge.getDowns().put(new Date().getTime(), percentage);
                                        } else {
                                            ge.setOkTotal(percentage+ge.getOkTotal());
                                            if(ge.getOks()==null)
                                                ge.setOks(new HashMap<Long, Double>());
                                            ge.getOks().put(new Date().getTime(), percentage);
                                        }
                                        station.getGeneralEvaluations().put(eval.getFeatureType(), ge);
                                    }
                                    dao.addOrUpdate(station);
                                }
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
