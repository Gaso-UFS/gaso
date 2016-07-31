package com.ericmguimaraes.gaso.services;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ericmguimaraes.gaso.obd.BluetoothConnectThread;

/**
 * Created by ericm on 30-Jul-16.
 */
public class ObdService extends Service {

    private static final int RETRY_LIMIT = 5;
    private BluetoothSocket socket;

    private BluetoothDevice device;
    private int retryCount = 0;

    public ObdService(BluetoothDevice device, BluetoothSocket socket) {
        this.device = device;
        this.socket = socket;
    }

    public ObdService(BluetoothDevice device) {
        this.device = device;
        createSocket();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createSocket(){
        BluetoothConnectThread thread = new BluetoothConnectThread(device, null, new BluetoothConnectThread.OnSocketConnectedListener() {
            @Override
            public void onSocketConnected(BluetoothSocket socket) {
                if(socket==null) {
                    retry();
                } else {
                    ObdService.this.socket = socket;
                    getInfo();
                }
            }
        });
        thread.start();
    }

    private void getInfo() {
        if(socket==null || !socket.isConnected())
            retry();

    }

    private void retry() {
        if(retryCount>RETRY_LIMIT)
            stopSelf();
        createSocket();
    }
}
