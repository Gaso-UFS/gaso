package com.ericmguimaraes.gaso.fragments;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.SettingsActivity;
import com.ericmguimaraes.gaso.maps.GooglePlaces;
import com.ericmguimaraes.gaso.maps.LocationHelper;
import com.ericmguimaraes.gaso.model.Location;
import com.ericmguimaraes.gaso.model.Station;

import java.util.ArrayList;
import java.util.List;

public class GasFragment extends Fragment {

    Menu menu;
    boolean isMapAttached = true;

    LocationHelper locationHelper;

    GooglePlaces googlePlaces;

    Location location;

    private List<Station> stationsList = null;

    private boolean isSearching = true;

    private Handler handler;

    MapGasoFragment mapGasoFragment;

    StationFragment stationFragment;

    public GasFragment() {
    }

    public static GasFragment newInstance() {
        GasFragment fragment = new GasFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        locationHelper = LocationHelper.getINSTANCE(getContext());
        googlePlaces = new GooglePlaces();

        location = locationHelper.getLastKnownLocation();

        if(location!=null)
            new StationSearch().execute();
        else
            isSearching = false;

        handler = new Handler();
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
        mapGasoFragment = MapGasoFragment.newInstance(stationsList);
        FragmentTransaction ft;
        ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.content,mapGasoFragment);
        ft.commit();
        isMapAttached = true;
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
                ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.content,mapGasoFragment);
                ft.commit();
                menu.findItem(R.id.map_menu_item).setVisible(false);
                menu.findItem(R.id.stations_list_menu_item).setVisible(true);
                isMapAttached = true;
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
            startHandler();
            return null;
        }

        private void startHandler() {
            handler.postDelayed(nextPage, 4000);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isSearching = false;
        }

    }

    public boolean isSearching() {
        return isSearching;
    }

    Runnable nextPage = new Runnable() {
        @Override
        public void run() {
            if(location!=null && googlePlaces.getParser().hasNextToken()) {
                new NextSearchPage().execute(location.getLat(), location.getLng());
                handler.postDelayed(nextPage, 4000);
            } else {
                updateData();
            }
        }
    };

    private void updateData() {
        stationFragment.setStationList(stationsList);
        mapGasoFragment.setStationList(stationsList);
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
            return null;
        }
    }
}
