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
