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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericm on 22-Aug-16.
 */
public class ObdLogGroup implements Serializable {

    private String uid;

    private List<ObdLog> logs;

    private long timestamp;
    private double latitude;
    private double longitude;

    public List<ObdLog> getLogs() {
        return logs;
    }

    public void setLogs(List<ObdLog> logs) {
        this.logs = logs;
    }

    public void addLog(ObdLog log){
        if(logs==null)
            logs = new ArrayList<>();
        logs.add(log);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
