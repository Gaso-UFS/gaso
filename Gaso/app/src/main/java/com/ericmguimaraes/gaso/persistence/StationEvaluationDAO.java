package com.ericmguimaraes.gaso.persistence;

import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.evaluation.evaluations.GeneralStationEvaluation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by adrianodias on 5/13/17.
 */

public class StationEvaluationDAO {


    private DatabaseReference mDatabase;

    public StationEvaluationDAO(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addOrUpdate(String stationId, HashMap<String, GeneralStationEvaluation> evaluation) {
        mDatabase.child(Constants.FIREBASE_STATIONS_EVALUATIONS).child(stationId).setValue(evaluation);
    }

    public void findStationEvaluationById(String stationId, final OneStationEvaluationReceivedListener listener) {
        mDatabase.child(Constants.FIREBASE_STATIONS_EVALUATIONS).child(stationId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String,GeneralStationEvaluation>> indicator = new GenericTypeIndicator<HashMap<String, GeneralStationEvaluation>>() {};
                HashMap<String,GeneralStationEvaluation> evaluation = dataSnapshot.getValue(indicator);
                listener.onStationEvaluationReceived(evaluation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onCancelled(databaseError);
            }
        });
    }

    public interface OneStationEvaluationReceivedListener {
        void onStationEvaluationReceived(HashMap<String, GeneralStationEvaluation> evaluation);
        void onCancelled(DatabaseError databaseError);
    }
}
