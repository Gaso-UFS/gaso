package com.ericmguimaraes.gaso.config;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.User;

/**
 * Created by ericm on 2/28/2016.
 */
public class Session {

    private static Session instance;

    public User currentUser;
    public Car currentCar;
    public BluetoothDevice device;
    public BluetoothSocket socket;
    public boolean isToStartAndBindService = false;

    private Session(){
    }

    public static Session getInstance(){
        if(instance!=null)
            return instance;
        instance = new Session();
        return instance;
    }

}
