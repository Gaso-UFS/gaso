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

package com.ericmguimaraes.gaso.config;

import com.ericmguimaraes.gaso.util.StringUtils;

/**
 * Created by ericm on 17-Aug-16.
 */
public class Constants {

    public static final String BLUETOOTH_LIST_KEY = "bluetooth_list_preference";
    public static final String UPLOAD_URL_KEY = "upload_url_preference";
    public static final String UPLOAD_DATA_KEY = "upload_data_preference";
    public static final String OBD_UPDATE_PERIOD_KEY = "obd_update_period_preference";
    public static final String VEHICLE_ID_KEY = "vehicle_id_preference";
    public static final String ENGINE_DISPLACEMENT_KEY = "engine_displacement_preference";
    public static final String VOLUMETRIC_EFFICIENCY_KEY = "volumetric_efficiency_preference";
    public static final String IMPERIAL_UNITS_KEY = "imperial_units_preference";
    public static final String COMMANDS_SCREEN_KEY = "obd_commands_screen";
    public static final String PROTOCOLS_LIST_KEY = "obd_protocols_preference";
    public static final String ENABLE_GPS_KEY = "enable_gps_preference";
    public static final String GPS_UPDATE_PERIOD_KEY = "gps_update_period_preference";
    public static final String GPS_DISTANCE_PERIOD_KEY = "gps_distance_period_preference";
    public static final String ENABLE_BT_KEY = "enable_bluetooth_preference";
    public static final String MAX_FUEL_ECON_KEY = "max_fuel_econ_preference";
    public static final String CONFIG_READER_KEY = "reader_config_preference";
    public static final String ENABLE_FULL_LOGGING_KEY = "enable_full_logging";
    public static final String DIRECTORY_FULL_LOGGING_KEY = "dirname_full_logging";
    public static final String DEV_EMAIL_KEY = "dev_email";


    /* PERMISSION REQUESTS */
    public static final int LOCATION_PERMISSION_REQUEST = 1;
    public static final int WRITE_ALL_SPENTS = 2;
    public static final int WRITE_MONTH_SPENT = 3;

    public static String genericError = "Desculpe, tivemos um problema.";

    public static final String PREFS_NAME = "config";
    public static final String USER_LOGGED_TAG = "user_logged";

    public static final String FIREBASE_USERS = "users";
    public static final String FIREBASE_CARS = "cars";
    public static final String FIREBASE_FAVORITE_CAR = "favorite_car";
    public static final String FIREBASE_EXPENSES = "expenses";
    public static final String FIREBASE_EXPENSES_MONTH = "monthYear";
}
