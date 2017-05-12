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

package com.ericmguimaraes.gaso.model;

import com.ericmguimaraes.gaso.evaluation.evaluations.GeneralStationEvaluation;
import com.google.android.gms.location.places.Place;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ericm on 2/28/2016.
 */
public class Station {

    private String id;

    private String name;

    private String address;

    private String phoneNumber;

    private Location location;

    private float generalRate;

    private float combustiveRate;

    private float moneyRate;

    private List<Combustive> combustives;

    private String reference;

    private HashMap<String, GeneralStationEvaluation> generalEvaluations;

    public Station() {}

    public Station(Place place){
        List<Integer> types = place.getPlaceTypes();
        boolean isGasStation = false;
        for (Integer i: types){
            if(i==Place.TYPE_GAS_STATION){
                isGasStation = true;
                break;
            }
        }
        if(isGasStation){
            id = place.getId();
            name = place.getName().toString();
            address = place.getAddress().toString();
            phoneNumber = place.getPhoneNumber().toString();
            Location l = new Location(place.getLatLng().latitude,place.getLatLng().longitude);
            location = l;
            generalRate = place.getRating();
            moneyRate = place.getPriceLevel();
        } else {
            throw new IllegalArgumentException("Place is not a gas station.");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getGeneralRate() {
        return generalRate;
    }

    public void setGeneralRate(float generalRate) {
        this.generalRate = generalRate;
    }

    public float getCombustiveRate() {
        return combustiveRate;
    }

    public void setCombustiveRate(float combustiveRate) {
        this.combustiveRate = combustiveRate;
    }

    public float getMoneyRate() {
        return moneyRate;
    }

    public void setMoneyRate(float moneyRate) {
        this.moneyRate = moneyRate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<Combustive> getCombustives() {
        return combustives;
    }

    public void setCombustives(List<Combustive> combustives) {
        this.combustives = combustives;
    }

    public HashMap<String, GeneralStationEvaluation> getGeneralEvaluations() {
        return generalEvaluations;
    }

    public void setGeneralEvaluations(HashMap<String, GeneralStationEvaluation> generalEvaluations) {
        this.generalEvaluations = generalEvaluations;
    }
}
