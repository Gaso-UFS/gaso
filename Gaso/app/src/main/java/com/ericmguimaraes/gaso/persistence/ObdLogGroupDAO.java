package com.ericmguimaraes.gaso.persistence;

import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.model.ObdLogGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ericm on 17-Oct-16.
 */

public class ObdLogGroupDAO {

    private DatabaseReference mDatabase;

    public ObdLogGroupDAO(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void add(ObdLogGroup group){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            if(SessionSingleton.getInstance().currentCar!=null) {
                String key = mDatabase.child(Constants.FIREBASE_OBD_LOG).child(user.getUid()).child(SessionSingleton.getInstance().currentCar.getid()).push().getKey();
                group.setUid(key);
                group.setTimestamp(new Date().getTime());
                mDatabase.child(Constants.FIREBASE_OBD_LOG).child(user.getUid()).child(SessionSingleton.getInstance().currentCar.getid()).child(key).setValue(group);
            } else {
                String key = mDatabase.child(Constants.FIREBASE_UNTRACEABLE_OBD_LOG).push().getKey();
                group.setUid(key);
                group.setTimestamp(new Date().getTime());
                mDatabase.child(Constants.FIREBASE_UNTRACEABLE_OBD_LOG).child(key).setValue(group);
            }
        }
    }

}
