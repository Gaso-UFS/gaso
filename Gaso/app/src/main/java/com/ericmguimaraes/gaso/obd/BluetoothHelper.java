package com.ericmguimaraes.gaso.obd;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Set;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by ericm on 3/28/2016.
 */
public class BluetoothHelper {

    private static final int REQUEST_ENABLE_BT = 2;
    public static BluetoothHelper instance;

    BluetoothAdapter mBluetoothAdapter;

    boolean isBluetoothSupported = true;

    Activity activity;

    Set<BluetoothDevice> pairedDevices;

    Set<BluetoothDevice> devicesFound;

    private BluetoothHelper(Activity activity){
        this.activity=activity;
    }

    public BluetoothHelper getInstance(Activity activity){
        if(instance==null)
            instance = new BluetoothHelper(activity);
        return instance;
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
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(activity,enableBtIntent, REQUEST_ENABLE_BT,null);
        }
    }

    public boolean isBluetoothSupported() {
        return isBluetoothSupported;
    }

    private void getPairedDevices(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevices.add(device);
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devicesFound.add(device);
            }
        }
    };

    private void discoverDevices(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, filter);
    }



}
