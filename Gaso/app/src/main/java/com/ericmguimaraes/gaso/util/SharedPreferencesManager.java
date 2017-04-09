package com.ericmguimaraes.gaso.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ericmguimaraes.gaso.R;

/**
 * Created by eric on 01/09/16.
 */
public class SharedPreferencesManager {

    private static SharedPreferencesManager instance;
    private static final String PREF_NAME = "pref_name";

    private static final String bluetooth_device_key = "bluetooth_device_key";

    private static final String show_disclaimer_key = "show_disclaimer_key";

    private static SharedPreferences settings;

    private static SharedPreferences.Editor editor;

    private SharedPreferencesManager(Context context){
        if(context!=null) {
            settings = context.getSharedPreferences(PREF_NAME, 0);
            editor = settings.edit();
        }
    }

    public static SharedPreferencesManager getInstance(Context context){
        if(instance==null || editor==null)
            instance = new SharedPreferencesManager(context);
        return instance;
    }

    public void saveDeviceKey(String key) {
        editor.putString(bluetooth_device_key,key);
        editor.apply();
    }

    public String getDeviceKey() {
        return settings.getString(bluetooth_device_key,"");
    }

    public void setDisclamerShowed() {
        editor.putBoolean(show_disclaimer_key,true);
        editor.apply();
    }

    public Boolean disclamerHasBeenShowed() {
        return settings.getBoolean(show_disclaimer_key,false);
    }
}
