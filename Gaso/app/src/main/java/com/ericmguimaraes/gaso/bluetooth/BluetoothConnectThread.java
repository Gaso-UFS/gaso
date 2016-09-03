package com.ericmguimaraes.gaso.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by ericm on 3/31/2016.
 */
public class BluetoothConnectThread extends Thread {
    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private OnSocketConnectedListener listener;
    private UUID MY_UUID;

    public BluetoothConnectThread(BluetoothDevice device, @Nullable UUID MY_UUID, OnSocketConnectedListener listener) {
        if(MY_UUID==null)
            this.MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        mmDevice = device;
        this.listener = listener;
    }

    public void run() {
        BluetoothSocket tmp = null;
        try {
            tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e("BLUE_CONN",e.getMessage(),e);
            ParcelUuid[] uuid = mmDevice.getUuids();
            if(uuid==null || uuid.length<1)
                listener.onSocketConnected(null);
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(uuid[0].getUuid());
            } catch (IOException e1) {
                Log.e("BLUE_CONN",e.getMessage(),e1);
                listener.onSocketConnected(null);
            }
        }
        mmSocket = tmp;
        try {
            mmSocket.connect();
        } catch (IOException e) {
            Log.e("BLUE_CONN",e.getMessage(),e);
            try {
                mmSocket.close();
            } catch (IOException e1) {
                Log.e("BLUE_CONN",e1.getMessage(),e1);
            }
            listener.onSocketConnected(null);
            return;
        }

        listener.onSocketConnected(mmSocket);
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

    public interface OnSocketConnectedListener {
        void onSocketConnected(BluetoothSocket socket);
    }
}
