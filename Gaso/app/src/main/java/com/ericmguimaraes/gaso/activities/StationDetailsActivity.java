package com.ericmguimaraes.gaso.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.maps.PlacesHelper;
import com.ericmguimaraes.gaso.model.Station;
import com.ericmguimaraes.gaso.persistence.StationDAO;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        PlacesHelper placesHelper = new PlacesHelper(this);
        placesHelper.findStationByID(stationId, new PlacesHelper.StationFoundListener() {
            @Override
            public void OnFindStationResult(Station station) {
                if(station==null){
                    Toast toast = Toast.makeText(getApplicationContext(),"ops, tivemos um problema.",Toast.LENGTH_SHORT);
                    toast.show();
                    onBackPressed();
                } else {
                    StationDAO dao = new StationDAO(getApplicationContext());
                    StationDetailsActivity.this.station = dao.addOrMerge(station);
                    fillFields();
                }
            }
        });
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
    }

}