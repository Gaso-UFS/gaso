package com.ericmguimaraes.gaso.obd;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by ericm on 3/31/2016.
 */
public class BluetoothConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private OnSocketConnectedListener listener;

    public BluetoothConnectThread(BluetoothDevice device, UUID MY_UUID, OnSocketConnectedListener listener) {
        BluetoothSocket tmp = null;
        mmDevice = device;
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;

        this.listener = listener;
    }

    public void run() {

        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
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
