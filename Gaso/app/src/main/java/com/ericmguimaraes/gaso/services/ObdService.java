package com.ericmguimaraes.gaso.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.model.ObdData;
import com.ericmguimaraes.gaso.obd.BluetoothConnectThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericm on 30-Jul-16.
 */
public class ObdService extends Service {

    private static final int RETRY_LIMIT = 10;
    private static final int ONGOING_NOTIFICATION_ID = 999;
    private BluetoothSocket socket;

    private BluetoothDevice device;
    private int retryCount = 0;

    private List<OnDataReceivedListener> listeners;

    private final IBinder mBinder = new ObdServiceBinder();

    ReadObdThread readObdThread;

    @Override
    public void onCreate() {
        super.onCreate();
        if(listeners==null)
            listeners = new ArrayList<>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        retryCount=0;
        return mBinder;
    }

    public class ObdServiceBinder extends Binder {
        public ObdService getService() {
            return ObdService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        retryCount=0;
        if(device!=null)
            startReadingThread();
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    public void startReadingThread() {
        if(readObdThread==null)
            readObdThread = new ReadObdThread();
        if(readObdThread.isInterrupted() || !readObdThread.isAlive())
            readObdThread.start();
    }

    private class ReadObdThread extends Thread {
        @Override
        public void run() {
            try {
                while(!isInterrupted()){
                    if(device==null) {
                        retryCount++;
                        if(retryCount-1>RETRY_LIMIT){
                            stopSelf();
                            break;
                        } else {
                            wait(1000);
                            continue;
                        }
                    }
                    startReading();
                }
            } catch (InterruptedException e) {
                Log.d("ERROR_WAITING",e.getMessage(),e);
            }
        }
    }

    private void closeSocket() {
        if(socket!=null)
            try {
                socket.close();
            } catch (IOException e) {
                Log.e("CLOSE_SOCKET_SERVICE",e.getMessage(),e);
            }
    }

    private void createSocket(){
        closeSocket();
        BluetoothConnectThread thread = new BluetoothConnectThread(device, null, new BluetoothConnectThread.OnSocketConnectedListener() {
            @Override
            public void onSocketConnected(BluetoothSocket socket) {
                if(socket==null) {
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        Log.d("ERROR_WAITING",e.getMessage(),e);
                    }
                    retry();
                } else {
                    ObdService.this.socket = socket;
                    startReading();
                }
            }
        });
        thread.start();
    }

    private void startReading() {
        if(socket==null || !socket.isConnected()){
            retry();
            return;
        }
        startForegroundService();
        while(socket.isConnected()) {
            ObdData data = getObdData();
            saveObdData(data);
            if(listeners!=null)
                for (OnDataReceivedListener listener : listeners)
                    listener.onDataReceived(data);
        }
        retry();
    }

    private boolean retry() {
        if(retryCount>RETRY_LIMIT) {
            stopSelf();
            return false;
        }
        createSocket();
        retryCount++;
        return true;
    }

    interface OnDataReceivedListener {
        void onDataReceived(ObdData obdData);
    }

    public void addOnDataReceivedListener(OnDataReceivedListener listener) {
        if(listeners==null)
            listeners = new ArrayList<>();
        listeners.add(listener);
    }

    public void removeOnDataReceivedListener(OnDataReceivedListener listener) {
        listeners.remove(listener);
    }

    public void clearOnDataReceivedListeners() {
        listeners.clear();
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    private void saveObdData(ObdData data) {
        //TODO salvar os dados no realm
    }

    private ObdData getObdData() {
        //TODO iniciar a leitura dos dados com o biblioteca
        return null;
    }

    private void startForegroundService(){
        //TODO criar notificacao melhor
        Notification notification = new Notification(R.drawable.ic_globe_notification, "Gaso - OBD2 Conectado!", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, ObdService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        startForeground(ONGOING_NOTIFICATION_ID, notification);

        /*
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_globe_notification)
                        .setContentTitle("Gaso")
                        .setContentText("OBD2 Conectado!")
                        .setOngoing(true);

        Intent resultIntent = new Intent(this, ObdService.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ObdService.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(ONGOING_NOTIFICATION_ID, mBuilder.build());
         */
    }

}
