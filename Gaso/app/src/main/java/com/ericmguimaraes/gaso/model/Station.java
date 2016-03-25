package com.ericmguimaraes.gaso.model;

import com.google.android.gms.location.places.Place;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ericm on 2/28/2016.
 */
public class Station extends RealmObject {

    public Station(){
    }

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
            money_rate = place.getPriceLevel();
        } else {
            throw new IllegalArgumentException("Place is not a gas station.");
        }
    }

    @PrimaryKey
    private String id;

    private String name;

    private String address;

    private String phoneNumber;

    private Location location;

    private float generalRate;

    private float combustiveRate;

    private float money_rate;

    private RealmList<Combustive> combustives;

    private String reference;

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

    public RealmList<Combustive> getCombustives() {
        return combustives;
    }

    public void setCombustives(RealmList<Combustive> combustives) {
        this.combustives = combustives;
    }

    public float getMoney_rate() {
        return money_rate;
    }

    public void setMoney_rate(float money_rate) {
        this.money_rate = money_rate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
