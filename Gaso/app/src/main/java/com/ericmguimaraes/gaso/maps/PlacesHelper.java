package com.ericmguimaraes.gaso.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.ericmguimaraes.gaso.model.Station;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.Places;

import java.util.List;

/**
 * Created by ericm on 3/26/2016.
 */
public class PlacesHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Station lastStation;

    private GoogleApiClient googleApiClient;

    private Context context;

    private Activity activity;

    private float LIKELIHOOD_ACCEPTABLE = 85/100;

    public PlacesHelper(Activity activity) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }

    public void isAtGasStationAsync(final PlacesHelperInterface placesHelperInterface) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LocationHelper.LOCATION_PERMISSION_REQUEST);
            Log.e("getting location", "NO PERMISSION");
        }
        PendingResult<PlaceLikelihoodBuffer> places = Places.PlaceDetectionApi
                .getCurrentPlace(googleApiClient, null);
        places.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    if (placeLikelihood.getLikelihood()>LIKELIHOOD_ACCEPTABLE){
                        List<Integer> types = placeLikelihood.getPlace().getPlaceTypes();
                        for(int t:types){
                            if(t==Place.TYPE_GAS_STATION){
                                lastStation = new Station(placeLikelihood.getPlace());
                                placesHelperInterface.OnIsAtGasStationResult(lastStation);
                                break;
                            }
                        }
                    }
                }
                likelyPlaces.release();
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public interface PlacesHelperInterface{
        void OnIsAtGasStationResult(Station station);
    }

}
