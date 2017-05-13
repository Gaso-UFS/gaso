package com.ericmguimaraes.gaso.services;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.ObdLog;
import com.ericmguimaraes.gaso.model.ObdLogGroup;
import com.ericmguimaraes.gaso.obd.ObdReader;
import com.ericmguimaraes.gaso.persistence.CarDAO;
import com.ericmguimaraes.gaso.persistence.MilestoneDAO;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"MissingPermission"})
public class LoggingThread implements Runnable,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener {
    private static final String TAG = LoggingThread.class.getSimpleName();
    private static final float DISTANCE_THRESHOLD = 10;

    private static final int UPDATE_TIME = 2 * 1000;

    private final WeakReference<LoggingService> mLoggingServiceReference;

    private final ObdReader mObdReader;

    private boolean mShouldBeLogging = true;

    private Location mLastLocation;
    private Location mSegmentBeginning;

    private float[] mAcc;
    private int count = 0;
    private boolean refilBroadcastSent = false;

    public LoggingThread(LoggingService service) {
        this(service, null);
    }

    public LoggingThread(final LoggingService service, BluetoothSocket btSocket) {
        mLoggingServiceReference = new WeakReference<LoggingService>(service);
        mObdReader = new ObdReader(btSocket);

        SensorManager sensorManager = (SensorManager) service.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void run() {

        while (mShouldBeLogging) {
            try {
                Thread.sleep(UPDATE_TIME);

                final long starTime = System.currentTimeMillis();

                List<ObdLog> obdValues = null;
                try {
                    obdValues = mObdReader.readValues();
                } catch (ObdReader.BrokenPipeException e) {
                    sendBrokenPipeBroadcast();
                }

                final long deltaTime = System.currentTimeMillis() - starTime;
                Log.d(TAG, "Comm delay -> " + deltaTime);

                if(obdValues==null)
                    obdValues = new ArrayList<>();

                for(ObdLog o : obdValues)
                    Log.d("OBD_VALUES",o.getData());

                if(obdValues.size()>1)
                    sendInformationBroadcast(obdValues);

                if(mObdReader.isHasGotDistanceFuel()) {
                    calculateConsumption();
                    sendUnsupportedAnalysisBroadcas(true);
                } else
                    sendUnsupportedAnalysisBroadcas(false);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error", e);
                e.printStackTrace();
            }
        }

        mObdReader.disconnect();

        Log.d(TAG, "Finished logging");
    }

    private void calculateConsumption() {
        if(!SessionSingleton.getInstance().isConsumptionCalculated) {
            SessionSingleton.getInstance().isConsumptionCalculated = true;
            final String distanceStr = mObdReader.getDistanceobdLog().getData();
            float currentFuelLevel = Float.parseFloat(mObdReader.getFuelLevelLog().getData());
            final float lastFuelLevel = SessionSingleton.getInstance().currentCar.getLastFuelPercentageLevel();
            final float fuelDiference = lastFuelLevel - currentFuelLevel;
            saveDistanceAndFuelConsumedOnCar(distanceStr, fuelDiference);
            final MilestoneDAO dao = new MilestoneDAO();
            dao.findLastMilestone(new MilestoneDAO.OneMilestoneReceivedListener() {
                @Override
                public void onMilestoneReceived(Milestone milestone) {
                    if (fuelDiference >= 0) {
                        if (milestone == null)
                            return;
                        milestone.setDistanceRolled(milestone.getDistanceRolled() + Float.parseFloat(distanceStr));
                        milestone.setCombustivePercentageConsumed(milestone.getCombustivePercentageConsumed() + fuelDiference);
                        dao.addOrUpdate(milestone);
                    } else {
                        if (!refilBroadcastSent)
                            sendBroadcastRefilled(fuelDiference * -1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (count == 0)
                        calculateConsumption();
                    count++;
                }
            });
        }
    }

    private void saveDistanceAndFuelConsumedOnCar(String distanceStr, float fuelDiference) {
        final CarDAO dao = new CarDAO();
        float dist = Float.parseFloat(distanceStr);
        Car car = SessionSingleton.getInstance().currentCar;
        if(fuelDiference>0)
            car.setTotalFuelPercentageUsed(car.getTotalFuelPercentageUsed()+fuelDiference);
        if(car.getLastDistanceRead()<dist) {
            car.setTotalDistance(car.getTotalDistance()+dist);
        } else {
            car.setTotalDistance(car.getTotalDistance()+dist-car.getLastDistanceRead());
        }
        car.setLastDistanceRead(dist);
        dao.addOrUpdate(SessionSingleton.getInstance().currentCar);
    }

    private void sendBroadcastRefilled(float diference) {
        refilBroadcastSent = true;
        LoggingService service = mLoggingServiceReference.get();

        if (service == null) return;

        Intent intent = new Intent(LoggingService.SERVICE_BROADCAST_MESSAGE);
        intent.putExtra(LoggingService.SERVICE_MESSAGE, LoggingService.SERVICE_COMBUSTIVE_REFIL);
        intent.putExtra(LoggingService.SERVICE_REFIL_DIFERENCE, diference);

        service.mBroadcastManager.sendBroadcast(intent);
    }

    private void sendBrokenPipeBroadcast() {
        LoggingService service = mLoggingServiceReference.get();

        if (service == null) return;

        Intent intent = new Intent(LoggingService.SERVICE_BROADCAST_MESSAGE);
        intent.putExtra(LoggingService.SERVICE_MESSAGE, LoggingService.SERVICE_BROKEN_PIPE);

        service.mBroadcastManager.sendBroadcast(intent);
    }

    private void sendUnsupportedAnalysisBroadcas(boolean supported) {
        LoggingService service = mLoggingServiceReference.get();
        if (service == null) return;
        Intent intent = new Intent(LoggingService.SERVICE_BROADCAST_MESSAGE);
        if(!supported)
            intent.putExtra(LoggingService.SERVICE_MESSAGE, LoggingService.SERVICE_UNSUPPORTED_ANALYSIS);
        else
            intent.putExtra(LoggingService.SERVICE_MESSAGE, LoggingService.SERVICE_SUPPORTED_ANALYSIS);
        service.mBroadcastManager.sendBroadcast(intent);
    }

    private void sendInformationBroadcast(List<ObdLog> obdValues) {
        ObdLogGroup obdLogGroup = new ObdLogGroup();
        obdLogGroup.setLogs(obdValues);

        if(mLastLocation!=null){
            obdLogGroup.setLatitude(mLastLocation.getLatitude());
            obdLogGroup.setLongitude(mLastLocation.getLongitude());
        }

        LoggingService service = mLoggingServiceReference.get();

        if (service == null) return;

        if(mLastLocation!=null)
            Log.d("LOCATION", mLastLocation.getLatitude()+":"+mLastLocation.getLongitude()+"");
        else
            Log.d("LOCATION", "mLastLocation==null");

        Intent intent = new Intent(LoggingService.SERVICE_BROADCAST_MESSAGE);
        intent.putExtra(LoggingService.SERVICE_MESSAGE, LoggingService.SERVICE_NEW_DATA);
        intent.putExtra(LoggingService.SERVICE_LOCATION_LATLNG, mLastLocation);
        intent.putExtra(LoggingService.SERVICE_ACCELEROMETER, mAcc);
        intent.putExtra(LoggingService.SERVICE_DATA_OBDGROUP, obdLogGroup);

        service.mBroadcastManager.sendBroadcast(intent);
    }

    protected synchronized void stopLogging() {
        Log.d(TAG, "stopLogging");

        mShouldBeLogging = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");

        LoggingService service = mLoggingServiceReference.get();

        if (service == null) return;

        LocationServices.FusedLocationApi.requestLocationUpdates(service.mGoogleApiClient, service.mLocationRequest, this);

        Location location = LocationServices.FusedLocationApi.getLastLocation(service.mGoogleApiClient);

        if (location != null) {
            synchronized (this) {
                mLastLocation = location;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");

        mLastLocation = location;

        if (mSegmentBeginning == null) {
            mSegmentBeginning = mLastLocation;
        } else if (mSegmentBeginning.distanceTo(mLastLocation) >= DISTANCE_THRESHOLD) {
            // TODO: Trigger save data
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");

        LoggingService service = mLoggingServiceReference.get();

        if (service == null) return;

        Intent intent = new Intent(LoggingService.SERVICE_BROADCAST_MESSAGE);
        intent.putExtra(LoggingService.SERVICE_MESSAGE, LoggingService.SERVICE_LOCATION_ERROR);
        service.mBroadcastManager.sendBroadcast(intent);

        intent = new Intent(service, LoggingService.class);
        intent.setAction(LoggingService.SERVICE_STOP_LOGGING);
        service.startService(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAcc = event.values;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}