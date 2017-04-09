package com.ericmguimaraes.gaso.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.activities.MainActivity;
import com.ericmguimaraes.gaso.bluetooth.BluetoothConnection;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;

public class LoggingService extends Service {
    private static final String TAG = LoggingService.class.getSimpleName();

    private static boolean RUNNING = false;

    public static final String SERVICE_START =
            "io.github.malvadeza.floatingcar.logging_service.service_start";
    public static final String SERVICE_START_LOGGING =
            "io.github.malvadeza.floatingcar.logging_service.service_start_logging";
    public static final String SERVICE_STOP_LOGGING =
            "io.github.malvadeza.floatingcar.logging_service.service_stop_logging";
    public static final String SERVICE_BROADCAST_MESSAGE =
            "io.github.malvadeza.floatingcar.logging_service.broadcast_message";
    public static final String SERVICE_STARTED =
            "io.github.malvadeza.floatingcar.logging_service.service_started";
    public static final String SERVICE_NEW_DATA =
            "io.github.malvadeza.floatingcar.logging_service.location_changed";
    public static final String SERVICE_NEW_TRIP_DATA =
            "io.github.malvadeza.floatingcar.logging_service.new_trip";
    public static final String SERVICE_LOCATION_LATLNG =
            "io.github.malvadeza.floatingcar.logging_service.location_latlng";
    public static final String SERVICE_ACCELEROMETER =
            "io.github.malvadeza.floatingcar.logging_service.accelerometer";
    public static final String SERVICE_DATA_OBDGROUP =
            "io.github.malvadeza.floatingcar.logging_service.data_obdgroup";
    public static final String SERVICE_LOCATION_ERROR =
            "io.github.malvadeza.floatingcar.logging_service.location_latlng";
    public static final String SERVICE_BLUETOOTH_CONNECTING =
            "io.github.malvadeza.floatingcar.logging_service.service_bluetooth_connecting";
    public static final String SERVICE_BLUETOOTH_CONNECTED =
            "io.github.malvadeza.floatingcar.logging_service.service_bluetooth_connected";
    public static final String SERVICE_BLUETOOTH_ERROR =
            "io.github.malvadeza.floatingcar.logging_service.service_bluetooth_error";
    public static final String SERVICE_MESSAGE =
            "io.github.malvadeza.floatingcar.logging_service.message";

    private final IBinder mBinder = new ObdServiceBinder();

    protected LocalBroadcastManager mBroadcastManager;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;

    private BluetoothConnection mBtConnection;

    private LoggingThread mLoggingThread;

    private BluetoothHandler mBtHandler;
    private BluetoothAdapter mBtAdapter;

    public LoggingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        mBroadcastManager = LocalBroadcastManager.getInstance(this);

        Log.d("SERVICE","entrou");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null) {
                mBtAdapter = bluetoothManager.getAdapter();
            }
        } else {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "onStartCommand");

        if (intent.getAction().equals(SERVICE_START)) {
            Log.d(TAG, "Starting connection to device");
            mBtHandler = new BluetoothHandler(this);

            BluetoothDevice btDevice = intent.getParcelableExtra("bluetoothDevice");

            mBtConnection = new BluetoothConnection(mBtHandler, mBtAdapter);
            mBtConnection.connect(btDevice);
        } else if (intent.getAction().equals(SERVICE_START_LOGGING)) {
            Log.d(TAG, "Start logging data");

            mLoggingThread = new LoggingThread(this, mBtConnection.getSocket());
            mBtHandler = null;

            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(mLoggingThread)
                        .addOnConnectionFailedListener(mLoggingThread)
                        .addApi(LocationServices.API)
                        .build();
            }

            if (mLocationRequest == null) {
                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(3 * 1000)
                        .setFastestInterval(1 * 1000);
            }

            mGoogleApiClient.connect();

            Intent stopIntent = new Intent(this, LoggingService.class);
            stopIntent.setAction(SERVICE_STOP_LOGGING);

            PendingIntent pStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // TODO: 19/03/17 dispach details on main
            Intent startDetailsIntent = new Intent(this, MainActivity.class);
            startDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pStartDetailsIntent = PendingIntent.getActivity(this, 1, startDetailsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.notification_content_title))
                    .setTicker("Ticker text")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setContentIntent(pStartDetailsIntent)
                    .addAction(android.R.drawable.ic_dialog_alert, "Stop Logging", pStopIntent)
                    .build();

            startForeground(10, notification);

            new Thread(mLoggingThread).start();
        } else if (intent.getAction().equals(SERVICE_STOP_LOGGING)) {
            stopForeground(true);
            mLoggingThread.stopLogging();
            stopSelf();
        }

        RUNNING = true;

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        RUNNING = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static synchronized boolean isRunning() {
        return RUNNING;
    }

    public static class BluetoothHandler extends Handler {
        private final WeakReference<LoggingService> loggingServiceReference;

        private BluetoothHandler(LoggingService service) {
            loggingServiceReference = new WeakReference<LoggingService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            LoggingService service = loggingServiceReference.get();

            if (service == null) return;

            switch (msg.what) {
                case BluetoothConnection.BLUETOOTH_CONNECTING_DEVICE: {
                    String name = msg.getData().getString(BluetoothConnection.BLUETOOTH_TARGET_DEVICE_NAME);
                    String address = msg.getData().getString(BluetoothConnection.BLUETOOTH_TARGET_DEVICE_ADDRESS);

                    Intent intent = new Intent(LoggingService.SERVICE_BROADCAST_MESSAGE);
                    intent.putExtra(LoggingService.SERVICE_MESSAGE, LoggingService.SERVICE_BLUETOOTH_CONNECTING);

                    service.mBroadcastManager.sendBroadcast(intent);

                    break;
                }
                case BluetoothConnection.BLUETOOTH_CONNECTED_DEVICE: {
                    String name = msg.getData().getString(BluetoothConnection.BLUETOOTH_TARGET_DEVICE_NAME);
                    String address = msg.getData().getString(BluetoothConnection.BLUETOOTH_TARGET_DEVICE_ADDRESS);

                    Intent intent = new Intent(LoggingService.SERVICE_BROADCAST_MESSAGE);
                    intent.putExtra(LoggingService.SERVICE_MESSAGE, LoggingService.SERVICE_BLUETOOTH_CONNECTED);
                    intent.putExtra(BluetoothConnection.BLUETOOTH_TARGET_DEVICE_NAME, name);
                    intent.putExtra(BluetoothConnection.BLUETOOTH_TARGET_DEVICE_ADDRESS, address);

                    service.mBroadcastManager.sendBroadcast(intent);

                    intent = new Intent(service, LoggingService.class);
                    intent.setAction(LoggingService.SERVICE_START_LOGGING);

                    service.startService(intent);

                    break;
                }
                case BluetoothConnection.BLUETOOTH_STATE_CHANGE: {
                    break;
                }
                case BluetoothConnection.BLUETOOTH_CONNECTION_ERROR: {
                    LoggingService.RUNNING = false;
                    Intent intent = new Intent(LoggingService.SERVICE_BROADCAST_MESSAGE);
                    intent.putExtra(LoggingService.SERVICE_MESSAGE, LoggingService.SERVICE_BLUETOOTH_ERROR);

                    service.mBroadcastManager.sendBroadcast(intent);
                    break;
                }
                default: {
                    Log.e(TAG, "msg.what -> " + msg.what);
                    throw new IllegalArgumentException("Should never be reached");
                }
            }

        }
    }

    public class ObdServiceBinder extends Binder {
        public LoggingService getService() {
            return LoggingService.this;
        }
    }

}
