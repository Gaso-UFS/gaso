package com.ericmguimaraes.gaso;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import com.ericmguimaraes.gaso.activities.MainActivity;
import com.ericmguimaraes.gaso.maps.GooglePlaces;
import com.ericmguimaraes.gaso.maps.PlacesHelper;
import com.ericmguimaraes.gaso.model.Location;
import com.ericmguimaraes.gaso.model.Station;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ActivityInstrumentationTestCase2 {

    public ApplicationTest(Class activityClass) {
        super(activityClass);
    }

    public ApplicationTest() {
        super(MainActivity.class);
    }

    public void testCaseGooglePlaces(){
        GooglePlaces googlePlaces = new GooglePlaces();
        List<Station> result = null;
        try {
            double lat = -10.9556067;
            double lgn = -37.0559822;
            Location l = new Location();
            l.setLat(lat);
            l.setLng(lgn);
            result = googlePlaces.getStationsList(l,null);
            if(googlePlaces.getParser().hasNextToken()){
                Thread.sleep(4000);
                result.addAll(googlePlaces.getStationsList(l, googlePlaces.getParser().getNextPageToken()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(result != null && !result.isEmpty() && result.size() > 20);
    }

    public void testCaseCurrentLocation(){
        PlacesHelper placesHelper = new PlacesHelper(getActivity());
        placesHelper.isAtGasStationAsync(new PlacesHelper.PlacesHelperInterface() {
            @Override
            public void OnIsAtGasStationResult(Station station) {

            }
        });
        assertTrue(true);
    }

}