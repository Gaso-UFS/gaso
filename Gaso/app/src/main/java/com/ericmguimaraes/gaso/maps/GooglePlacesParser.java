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

package com.ericmguimaraes.gaso.maps;

import android.util.Log;

import com.ericmguimaraes.gaso.model.Location;
import com.ericmguimaraes.gaso.model.Station;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericm on 3/19/2016.
 */
public class GooglePlacesParser {

    private String nextPageToken;

    /*
    * Transforma o resultado da chamada de lista de posto
    * em objeto
    *
     */
    public List<Station> listFromJson(String json){
        List<Station> stations = new ArrayList<>();
        if(json!=null && !json.isEmpty())
            try{
                JSONObject jsonObject = new JSONObject(json);
                if(jsonObject.has("next_page_token"))
                    nextPageToken = jsonObject.getString("next_page_token");
                else
                    nextPageToken = null;
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Station station = new Station();
                    station.setId(jsonArray.getJSONObject(i).getString("place_id"));
                    station.setName(jsonArray.getJSONObject(i).getString("name"));
                    station.setAddress(jsonArray.getJSONObject(i).getString("vicinity"));
                    station.setReference(jsonArray.getJSONObject(i).getString("reference"));
                    Location location = new Location();
                    location.setLat(jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                    location.setLng(jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                    station.setLocation(location);
                    stations.add(station);
                }
            } catch (Exception e){
                Log.e("Parsing json",e.getMessage(),e);
                return null;
            }
        return stations;
    }

    /*
    *   Esse metodo recebe o json da chamada de detalhes de um lugar
    *   e adiciona a station passada
    *
     */
    public Station stationFromJson(Station station, String json){
        if(station!=null && json!=null && !json.isEmpty())
            try {
                JSONObject jsonObject = new JSONObject(json);
                station.setAddress(jsonObject.getString("formatted_address"));
                station.setPhoneNumber(jsonObject.getString("formatted_phone_number"));
            } catch (Exception e){
                Log.e("Parsing json",e.getMessage(),e);
                return null;
            }
        return station;
    }

    public boolean hasNextToken(){
        return nextPageToken!=null && !nextPageToken.isEmpty();
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
