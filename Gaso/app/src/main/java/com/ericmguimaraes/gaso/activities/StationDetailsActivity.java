/*
 *     Gaso
 *
 *     Copyright (C) 2016  Eric Guimarães
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ericmguimaraes.gaso.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.evaluation.FeatureType;
import com.ericmguimaraes.gaso.evaluation.evaluations.GeneralStationEvaluation;
import com.ericmguimaraes.gaso.maps.PlacesHelper;
import com.ericmguimaraes.gaso.model.Station;
import com.ericmguimaraes.gaso.persistence.StationDAO;
import com.ericmguimaraes.gaso.persistence.StationEvaluationDAO;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.ericmguimaraes.gaso.evaluation.FeatureType.FUEL_CONSUMPTION_OBD_FUEL_LEVEL_AND_OBD_DISTANCE;

public class StationDetailsActivity extends AppCompatActivity {

    @Bind(R.id.stationNameText)
    TextView stationNameText;

    @Bind(R.id.addressText)
    TextView addressText;

    @Bind(R.id.phoneText)
    TextView phoneText;

    @Bind(R.id.quality_rating)
    RatingBar qualityRating;

    @Bind(R.id.money_rating)
    RatingBar moneyRating;

    @Bind(R.id.general_rating)
    RatingBar generalRating;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.content)
    RelativeLayout contentRelativeLayout;

    @Bind(R.id.obdFuelAmount)
    LinearLayout obdFuelAmountLayout;

    @Bind(R.id.baixaFuelAmount)
    TextView baixaFuelAmount;

    @Bind(R.id.igualFuelAmount)
    TextView igualFuelAmount;

    @Bind(R.id.altaFuelAmount)
    TextView altaFuelAmount;

    @Bind(R.id.obdFuelDistance)
    LinearLayout obdFuelDistanceLayout;

    @Bind(R.id.baixaFuelDistance)
    TextView baixaFuelDistance;

    @Bind(R.id.igualFuelDistance)
    TextView igualFuelDistance;

    @Bind(R.id.altaFuelDistance)
    TextView altaFuelDistance;

    String stationId;

    Station station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        contentRelativeLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        if(!intent.hasExtra("station_id"))
            throw new IllegalArgumentException("extra station_id is missing");
        stationId = intent.getStringExtra("station_id");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStationFromGoogle();
    }

    private void getStationFromGoogle() {
        final StationDAO dao = new StationDAO();
        PlacesHelper placesHelper = new PlacesHelper(this);
        placesHelper.findStationByID(stationId, new PlacesHelper.StationFoundListener() {
            @Override
            public void OnFindStationResult(final Station station) {
                if(station==null){
                    dao.findStationById(stationId, new StationDAO.OneStationReceivedListener() {
                        @Override
                        public void onStationReceived(Station gasStation) {
                            if (gasStation != null) {
                                StationDetailsActivity.this.station = gasStation;
                                fillFields();
                            } else {
                                showAlertNoDetails();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            showAlertNoDetails();
                        }
                    });
                } else {
                    dao.addOrUpdate(station, new StationDAO.OneStationReceivedListener() {
                        @Override
                        public void onStationReceived(Station gasStation) {
                            StationDetailsActivity.this.station = gasStation;
                            fillFields();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            StationDetailsActivity.this.station = station;
                            fillFields();
                            Toast toast = Toast.makeText(getApplicationContext(),"ops, tivemos um problema.",Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                }
            }
        });
    }

    private void showAlertNoDetails() {
        Toast toast = Toast.makeText(getApplicationContext(),"ops, tivemos um problema.",Toast.LENGTH_SHORT);
        toast.show();
        onBackPressed();
    }

    private void fillFields() {
        stationNameText.setText(station.getName());
        addressText.setText(station.getAddress());
        phoneText.setText(station.getPhoneNumber());
        qualityRating.setRating(station.getCombustiveRate());
        moneyRating.setRating(station.getMoneyRate());
        generalRating.setRating(station.getGeneralRate());

        contentRelativeLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        fillAvaliations();
    }

    private void fillAvaliations() {
        StationEvaluationDAO dao = new StationEvaluationDAO();
        dao.findStationEvaluationById(stationId, new StationEvaluationDAO.OneStationEvaluationReceivedListener() {
                @Override
                public void onStationEvaluationReceived(HashMap<String, GeneralStationEvaluation> evaluations) {
                    if (evaluations != null) {

                        HashMap<String, GeneralStationEvaluation> generalEvaluations = evaluations;
                        for (String key : generalEvaluations.keySet()) {
                            if (key.equals(FUEL_CONSUMPTION_OBD_FUEL_LEVEL_AND_OBD_DISTANCE)) {
                                obdFuelDistanceLayout.setVisibility(View.VISIBLE);
                                baixaFuelDistance.setText(String.format("%.2f", generalEvaluations.get(key).getDownTotal()));
                                igualFuelDistance.setText(String.format("%.2f", generalEvaluations.get(key).getOkTotal()));
                                altaFuelDistance.setText(String.format("%.2f", generalEvaluations.get(key).getUpTotal()));
                            }
                            if (key.equals(FeatureType.OBD_FUEL_AMOUNT)) {
                                obdFuelAmountLayout.setVisibility(View.VISIBLE);
                                baixaFuelAmount.setText(String.format("%.2f", generalEvaluations.get(key).getDownTotal()));
                                igualFuelAmount.setText(String.format("%.2f", generalEvaluations.get(key).getOkTotal()));
                                altaFuelAmount.setText(String.format("%.2f", generalEvaluations.get(key).getUpTotal()));
                            }
                        }

                        station.setGeneralEvaluations(evaluations);
                        StationDAO stationDAO = new StationDAO();
                        stationDAO.addOrUpdate(station);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //ignore
                }
            }
        );


    }

}
