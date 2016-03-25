package com.ericmguimaraes.gaso.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.maps.LocationHelper;
import com.ericmguimaraes.gaso.model.Location;
import com.ericmguimaraes.gaso.model.Station;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import butterknife.Bind;

public class MapGasoFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SupportMapFragment supportMapFragment;

    @Bind(R.id.map)
    MapView mapView;

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;

    ProgressBar progressBar;
    TextView emptyListTextView;

    Location location;

    LocationHelper locationHelper;

    private static int MAP_REFRESH_TIME = 500;

    private static final List<Station> stationList = new ArrayList<>();

    private Handler mapUpdateHandler;

    private WeakHashMap<Marker, String> markers;
    private boolean isNotificationPendent = false;
    private boolean isMapReady = false;

    public MapGasoFragment() {
        // Required empty public constructor
    }

    public static MapGasoFragment newInstance() {
        MapGasoFragment fragment = new MapGasoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        markers = new WeakHashMap<>();
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        FragmentManager fm = getChildFragmentManager();
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }
        locationHelper = LocationHelper.getINSTANCE(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();// needed to get the googleMap to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        emptyListTextView = (TextView) v.findViewById(R.id.emptyListText);

        mapUpdateHandler = new Handler();

        mapUpdater.run();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if(googleMap ==null)
            supportMapFragment.getMapAsync(this);
    }

             @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        isMapReady = true;
        isNotificationPendent = true;
        markers.clear();

        // latitude and longitude,
        double latitude = -10.918546;
        double longitude = -37.060854;

        location = locationHelper.getLastKnownLocation();
        if(location!=null) {
            latitude = location.getLat();
            longitude = location.getLng();
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        addMarkers();
        setUpMap();

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

    public synchronized void setStationList(List<Station> stationList) {
        synchronized (MapGasoFragment.stationList) {
            MapGasoFragment.stationList.clear();
            MapGasoFragment.stationList.addAll(stationList);
            isNotificationPendent = true;
        }
    }

    private void notifyDataChange() {
        if (isMapReady) {
            addMarkers();
            setUpMap();
            isNotificationPendent = false;
        } else
            isNotificationPendent = true;
    }

    private void addMarkers() {
        synchronized (MapGasoFragment.stationList) {
            if (isMapReady) {
                for (Station s : stationList) {
                    if(!markers.containsValue(s.getId())) {
                        Marker m = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(s.getLocation().getLat(), s.getLocation().getLng()))
                                .title(s.getName()));
                        markers.put(m, s.getId());
                    }
                }
            } else {
                isNotificationPendent = true;
            }
        }
    }

    private void setUpMap(){
        if(isMapReady) {
            try {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            } catch (Exception e){
                Log.e("Enable my location",e.getMessage(),e);
            }
        } else {
            isNotificationPendent = true;
        }
    }

    private Station findStationById(String id){
        for (Station s: stationList){
            if(s.getId().equals(id))
                return s;
        }
        return null;
    }

    Runnable mapUpdater = new Runnable() {
        @Override
        public void run() {
            if(isMapReady) {
                if (isNotificationPendent)
                    notifyDataChange();
                mapUpdateHandler.postDelayed(mapUpdater, MAP_REFRESH_TIME);
            }
        }
    };

}
