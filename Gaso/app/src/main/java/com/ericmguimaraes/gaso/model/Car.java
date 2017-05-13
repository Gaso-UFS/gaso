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

/**
 * Created by ericm on 2/27/2016.
 */
public class Car {

    private String uid;
    private String model;
    private String description;
    private long creationDate;
    private float lastFuelPercentageLevel;
    private float totalDistance;
    private float lastDistanceRead;
    private double totalFuelPercentageUsed;
    private double tankMaxLevel;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getid() {
        return uid;
    }

    public void setid(String id) {
        this.uid = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public float getLastFuelPercentageLevel() {
        return lastFuelPercentageLevel;
    }

    public void setLastFuelPercentageLevel(float lastFuelPercentageLevel) {
        this.lastFuelPercentageLevel = lastFuelPercentageLevel;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }

    public float getLastDistanceRead() {
        return lastDistanceRead;
    }

    public void setLastDistanceRead(float lastDistanceRead) {
        this.lastDistanceRead = lastDistanceRead;
    }

    public double getTotalFuelPercentageUsed() {
        return totalFuelPercentageUsed;
    }

    public void setTotalFuelPercentageUsed(double totalFuelPercentageUsed) {
        this.totalFuelPercentageUsed = totalFuelPercentageUsed;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getTankMaxLevel() {
        return tankMaxLevel;
    }

    public void setTankMaxLevel(double tankMaxLevel) {
        this.tankMaxLevel = tankMaxLevel;
    }

    public boolean isHasTankMaxLevel(){
        return tankMaxLevel>0;
    }

}
