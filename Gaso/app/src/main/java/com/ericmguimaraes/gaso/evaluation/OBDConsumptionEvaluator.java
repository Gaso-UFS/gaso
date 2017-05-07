package com.ericmguimaraes.gaso.evaluation;

import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.persistence.CarDAO;
import com.ericmguimaraes.gaso.persistence.MilestoneDAO;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseError;

import java.util.List;

/**
 * Created by ericmguimaraes on 06/05/17.
 */

public class OBDConsumptionEvaluator extends Evaluator {

    @Override
    public Evaluation evaluate() {
        MilestoneDAO dao = new MilestoneDAO();
        dao.findLastMilestone(new MilestoneDAO.OneMilestoneReceivedListener() {
            @Override
            public void onMilestoneReceived(final Milestone milestone) {
                if(milestone!=null) {
                    CarDAO dao = new CarDAO();
                    dao.findCarByID(milestone.getCarUid(), new CarDAO.OneCarReceivedListener() {
                        @Override
                        public void onCarReceived(Car car) {
                            milestone.setCar(car);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // TODO: 06/05/17 ignore?
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: 06/05/17 ignore?
            }
        });
        return null;
    }

}
