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

package com.ericmguimaraes.gaso.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by ericm on 3/28/2016.
 */
public class BluetoothHelper {

    private static final int REQUEST_ENABLE_BT = 2;
    private static BluetoothHelper instance;

    BluetoothAdapter mBluetoothAdapter;

    boolean isBluetoothSupported = true;

    Activity activity;

    OnNewDeviceFoundListener listener;

    private HashMap<String,BluetoothDevice> devicesFound;

    private BluetoothHelper(){
        devicesFound = new HashMap<>();
    }

    public static BluetoothHelper getInstance(){
        if(instance==null)
            instance = new BluetoothHelper();
        return instance;
    }

    public void initBluetoothHelper(Activity activity){
        if(this.activity==null) {
            this.activity = activity;
            setUpBluetooth();
        }
    }

    private void setUpBluetooth(){
        getBluetoothAdapter();
        if(!isBluetoothSupported)
            return;
        enableBluetooth();
    }

    private void getBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            isBluetoothSupported = false;
        }
    }

    private void enableBluetooth() {
        if(activity==null)
            throw new RuntimeException("Call initBluetoothHelper before using this method.");
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(activity,enableBtIntent, REQUEST_ENABLE_BT,null);
        }
    }

    public boolean isBluetoothSupported() {
        return isBluetoothSupported;
    }

    public List<BluetoothDevice> getPairedDevices(){
        List<BluetoothDevice> list = new ArrayList<>();
        list.addAll(mBluetoothAdapter.getBondedDevices());
        for(BluetoothDevice device:list)
            devicesFound.put(device.getAddress(),device);
        return list;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devicesFound.put(device.getAddress(),device);
                listener.onNewDeviceFoundListener(device);
            }
        }
    };

    public void discoverDevices(OnNewDeviceFoundListener listener){
        if(activity==null)
            throw new RuntimeException("Call initBluetoothHelper before using this method.");

        this.listener = listener;

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, filter);
    }

    public void unRegisteReceiver(){
        if(activity!=null)
            activity.unregisterReceiver(mReceiver);
    }

    public HashMap<String, BluetoothDevice> getDevicesFound() {
        return devicesFound;
    }

    public interface OnNewDeviceFoundListener {
        void onNewDeviceFoundListener(BluetoothDevice bluetoothDevice);
    }

}
