package com.ericmguimaraes.gaso.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.activities.registers.SpentRegisterActivity;
import com.ericmguimaraes.gaso.config.SettingsActivity;
import com.ericmguimaraes.gaso.maps.GooglePlaces;
import com.ericmguimaraes.gaso.maps.LocationHelper;
import com.ericmguimaraes.gaso.maps.PlacesHelper;
import com.ericmguimaraes.gaso.model.Location;
import com.ericmguimaraes.gaso.model.Station;

import java.util.ArrayList;
import java.util.List;

public class GasFragment extends Fragment {

    private static int LOCATION_REFRESH_TIME = 2*60*1000; //millis
    private static int STATIONS_REFRESH_DISTANCE = 1000; //m
    private int REQUEST_PLACE_REFRESH_DISTANCE = 500;

    Menu menu;

    boolean isMapAttached = false;

    LocationHelper locationHelper;

    PlacesHelper placesHelper;

    GooglePlaces googlePlaces;

    Location location;

    private List<Station> stationsList = null;

    private boolean isSearching = true;

    private Handler nextPageHandler;

    private Handler locationHandler;

    MapGasoFragment mapGasoFragment;

    StationFragment stationFragment;

    Location lastLocation;

    public GasFragment() {
    }

    public static GasFragment newInstance() {
        GasFragment fragment = new GasFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        locationHelper = LocationHelper.getINSTANCE(getActivity());
        googlePlaces = new GooglePlaces();

        placesHelper = new PlacesHelper(getActivity());

        nextPageHandler = new Handler();
        locationHandler = new Handler();

        locationChecker.run();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gas, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stationFragment = StationFragment.newInstance(stationsList);
        mapGasoFragment = MapGasoFragment.newInstance();
        FragmentTransaction ft;
        ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.content, stationFragment);
        ft.commit();
        isMapAttached=false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_gas, menu);
        this.menu = menu;
        if(isMapAttached){
            menu.findItem(R.id.map_menu_item).setVisible(false);
            menu.findItem(R.id.stations_list_menu_item).setVisible(true);
        } else {
            menu.findItem(R.id.map_menu_item).setVisible(true);
            menu.findItem(R.id.stations_list_menu_item).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        FragmentTransaction ft;
        switch (id) {
            case R.id.action_settings:
                intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.map_menu_item:
                if(!isSearching && locationHelper!=null && locationHelper.isConnected()) {
                    ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.content, mapGasoFragment);
                    ft.commit();
                    menu.findItem(R.id.map_menu_item).setVisible(false);
                    menu.findItem(R.id.stations_list_menu_item).setVisible(true);
                    isMapAttached = true;
                }
                return true;
            case R.id.stations_list_menu_item:
                isMapAttached = false;
                ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.content, stationFragment);
                ft.commit();
                menu.findItem(R.id.map_menu_item).setVisible(true);
                menu.findItem(R.id.stations_list_menu_item).setVisible(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isSearching() {
        return isSearching;
    }

    Runnable locationChecker = new Runnable() {
        @Override
        public void run() {
            location = locationHelper.getLastKnownLocation();
            if(location!=null) {
                double distance = -1;
                boolean firstTime = lastLocation==null;
                if(!firstTime)
                    distance = LocationHelper.distance(location,lastLocation);
                if(firstTime || distance>STATIONS_REFRESH_DISTANCE){
                    lastLocation=location;
                    StationSearch task = new StationSearch();
                    task.execute(location.getLat(), location.getLng());
                }
                if(firstTime || distance>REQUEST_PLACE_REFRESH_DISTANCE){
                    placesHelper.isAtGasStationAsync(new PlacesHelper.CurrentPlaceListener() {
                        @Override
                        public void OnIsAtGasStationResult(Station station) {
                            showSpentRequestDialog(station);
                        }
                    });
                }
            }
            locationHandler.postDelayed(locationChecker, LOCATION_REFRESH_TIME);
        }
    };

    private void showSpentRequestDialog(final Station station) {
        new AlertDialog.Builder(getContext())
                .setTitle("Ei")
                .setMessage("Você está num posto? Deseja cadastrar um gasto?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), SpentRegisterActivity.class);
                        intent.putExtra("station_id",station.getId());
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }


    private void updateData() {
        stationFragment.setStationList(stationsList);
        mapGasoFragment.setStationList(stationsList);
    }

    public class StationSearch extends AsyncTask<Double,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSearching = true;
            stationsList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Double... params) {
            double lat = params[0];
            double lgn = params[1];
            Location l = new Location();
            l.setLat(lat);
            l.setLng(lgn);
            stationsList.addAll(googlePlaces.getStationsList(l, null));
            nextPageHandler.postDelayed(new NextPageGetter(l), 4000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateData();
            isSearching = false;
        }

    }

    public class NextSearchPage extends AsyncTask<Double,Void,Void> {

        @Override
        protected Void doInBackground(Double... params) {
            double lat = params[0];
            double lgn = params[1];
            Location l = new Location();
            l.setLat(lat);
            l.setLng(lgn);
            stationsList.addAll(googlePlaces.getStationsList(l, googlePlaces.getParser().getNextPageToken()));
            updateData();
            return null;
        }
    }

    private class NextPageGetter implements Runnable {

        Location location;

        public NextPageGetter(Location location){
            this.location = location;
        }

        @Override
        public void run() {
            if(location!=null && googlePlaces.getParser().hasNextToken()) {
                new NextSearchPage().execute(location.getLat(), location.getLng());
                nextPageHandler.postDelayed(this, 4000);
            } else if(!googlePlaces.getParser().hasNextToken()){
                //isSearching = false;
            }
        }
    }
}
