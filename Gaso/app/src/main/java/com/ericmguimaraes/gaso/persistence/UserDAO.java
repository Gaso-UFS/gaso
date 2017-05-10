package com.ericmguimaraes.gaso.persistence;

import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.model.FuzzyConsumption;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by ericmguimaraes on 10/05/17.
 */

public class UserDAO {

    private DatabaseReference mDatabase;

    public UserDAO(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void findFuzzyConsumption(final FuzzyConsumption.FuzzyConsumptionListener listener) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            DatabaseReference ref = mDatabase.child(Constants.FIREBASE_USERS).child(user.getUid()).child(Constants.FIREBASE_FUZZYCONSUMPTION);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FuzzyConsumption consumption = dataSnapshot.getValue(FuzzyConsumption.class);
                    if(consumption!=null)
                        listener.onConsumptionFound(consumption);
                    else
                        listener.onConsumptionFound(null);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onCancelled(databaseError);
                }
            });
        }
    }

    public void addFuzzyConsumption(FuzzyConsumption consumption) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mDatabase.child(Constants.FIREBASE_USERS).child(user.getUid()).child(Constants.FIREBASE_FUZZYCONSUMPTION).setValue(consumption);
        }
    }
}
