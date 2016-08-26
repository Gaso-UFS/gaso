package com.ericmguimaraes.gaso.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.activities.MainActivity;
import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.model.ObdLog;
import com.ericmguimaraes.gaso.obd.BluetoothConnectThread;
import com.github.pires.obd.commands.control.IgnitionMonitorCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.control.PendingTroubleCodesCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.AbsoluteLoadCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.fuel.FuelTrimCommand;
import com.github.pires.obd.commands.fuel.WidebandAirFuelRatioCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.FuelPressureCommand;
import com.github.pires.obd.commands.pressure.FuelRailPressureCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.AvailableCommandNames;
import com.github.pires.obd.exceptions.UnsupportedCommandException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = ObdService.class.getName();
    protected Context ctx;
    protected boolean isRunning = false;
    protected Long queueCounter = 0L;
    protected BlockingQueue<ObdCommandJob> jobsQueue = new LinkedBlockingQueue<>();

    SharedPreferences prefs;
    protected NotificationManager notificationManager;

    private BluetoothDevice dev = null;
    private BluetoothSocket sock = null;

    // Run the executeQueue in a different thread to lighten the UI thread
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                executeQueue();
            } catch (InterruptedException e) {
                t.interrupt();
            }
        }
    });
    private boolean isServiceBound = true;


    @Override
    public void onCreate() {
        super.onCreate();
        if(listeners==null)
            listeners = new ArrayList<>();
        Log.d(TAG, "Creating service..");
        t.start();
        Log.d(TAG, "Service created.");
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
        super.onDestroy();
        Log.d(TAG, "Destroying service...");
        notificationManager.cancel(NOTIFICATION_ID);
        t.interrupt();
        Log.d(TAG, "Service destroyed.");
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
            ObdLog data = getObdData();
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

    public interface OnDataReceivedListener {
        void onDataReceived(ObdLog obdLog);
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

    private void saveObdData(ObdLog data) {
        //TODO salvar os dados no realm
    }

    private ObdLog getObdData() {
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

    /**
     * This method will add a job to the queue while setting its ID to the
     * internal queue counter.
     *
     * @param job the job to queue.
     */
    public void queueJob(ObdCommandJob job) {
        // This is a good place to enforce the imperial units option
        job.getCommand().useImperialUnits(prefs.getBoolean(Constants.IMPERIAL_UNITS_KEY, false));

        queueCounter++;
        Log.d(TAG, "Adding job[" + queueCounter + "] to queue..");

        job.setId(queueCounter);
        try {
            jobsQueue.put(job);
            Log.d(TAG, "Job queued successfully.");
        } catch (InterruptedException e) {
            job.setState(ObdCommandJob.ObdCommandJobState.QUEUE_ERROR);
            Log.e(TAG, "Failed to queue job.");
        }
    }

    /**
     * Runs the queue until the service is stopped
     */
    protected void executeQueue() throws InterruptedException {
        Log.d(TAG, "Executing queue..");
        while (!Thread.currentThread().isInterrupted()) {
            ObdCommandJob job = null;
            try {
                job = jobsQueue.take();

                // log job
                Log.d(TAG, "Taking job[" + job.getId() + "] from queue..");

                if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NEW)) {
                    Log.d(TAG, "Job state is NEW. Run it..");
                    job.setState(ObdCommandJob.ObdCommandJobState.RUNNING);
                    if (sock.isConnected()) {
                        job.getCommand().run(sock.getInputStream(), sock.getOutputStream());
                    } else {
                        job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                        Log.e(TAG, "Can't run command on a closed socket.");
                    }
                } else
                    // log not new job
                    Log.e(TAG,
                            "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
            } catch (InterruptedException i) {
                Thread.currentThread().interrupt();
            } catch (UnsupportedCommandException u) {
                if (job != null) {
                    job.setState(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED);
                }
                Log.d(TAG, "Command not supported. -> " + u.getMessage());
            } catch (IOException io) {
                if (job != null) {
                    if(io.getMessage().contains("Broken pipe"))
                        job.setState(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE);
                    else
                        job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                Log.e(TAG, "IO error. -> " + io.getMessage());
            } catch (Exception e) {
                if (job != null) {
                    job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                Log.e(TAG, "Failed to run command. -> " + e.getMessage());
            }

            if (job != null) {
                final ObdCommandJob job2 = job;
                stateUpdate(job2);
            }

            if(queueEmpty())
                resetQueue();
        }

    }

    private void resetQueue(){
        if(jobsQueue==null)
            jobsQueue = new LinkedBlockingQueue<>();
        jobsQueue.clear();
        jobsQueue.add(new ObdCommandJob(new IgnitionMonitorCommand()));
        jobsQueue.add(new ObdCommandJob(new ModuleVoltageCommand()));
        jobsQueue.add(new ObdCommandJob(new PendingTroubleCodesCommand()));
        jobsQueue.add(new ObdCommandJob(new VinCommand()));
        jobsQueue.add(new ObdCommandJob(new AbsoluteLoadCommand()));
        jobsQueue.add(new ObdCommandJob(new LoadCommand()));
        jobsQueue.add(new ObdCommandJob(new MassAirFlowCommand()));
        jobsQueue.add(new ObdCommandJob(new OilTempCommand()));
        jobsQueue.add(new ObdCommandJob(new RPMCommand()));
        jobsQueue.add(new ObdCommandJob(new RuntimeCommand()));
        jobsQueue.add(new ObdCommandJob(new ThrottlePositionCommand()));
        jobsQueue.add(new ObdCommandJob(new AirFuelRatioCommand()));
        jobsQueue.add(new ObdCommandJob(new ConsumptionRateCommand()));
        jobsQueue.add(new ObdCommandJob(new FindFuelTypeCommand()));
        jobsQueue.add(new ObdCommandJob(new FuelLevelCommand()));
        jobsQueue.add(new ObdCommandJob(new FuelTrimCommand()));
        jobsQueue.add(new ObdCommandJob(new WidebandAirFuelRatioCommand()));
        jobsQueue.add(new ObdCommandJob(new BarometricPressureCommand()));
        jobsQueue.add(new ObdCommandJob(new FuelPressureCommand()));
        jobsQueue.add(new ObdCommandJob(new FuelRailPressureCommand()));
        jobsQueue.add(new ObdCommandJob(new IntakeManifoldPressureCommand()));
        jobsQueue.add(new ObdCommandJob(new AirIntakeTemperatureCommand()));
        jobsQueue.add(new ObdCommandJob(new AmbientAirTemperatureCommand()));
        jobsQueue.add(new ObdCommandJob(new EngineCoolantTemperatureCommand()));
    }

    /**
     * Stop OBD connection and queue processing.
     */
    public void stopService() {
        Log.d(TAG, "Stopping service..");

        notificationManager.cancel(NOTIFICATION_ID);
        jobsQueue.clear();
        isRunning = false;

        if (sock != null)
            // close socket
            try {
                sock.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

        // kill service
        stopSelf();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean queueEmpty() {
        return jobsQueue.isEmpty();
    }

    protected void showNotification(String contentTitle, String contentText, int icon, boolean ongoing, boolean notify, boolean vibrate) {
        final PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx, MainActivity.class), 0);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx);
        notificationBuilder.setContentTitle(contentTitle)
                .setContentText(contentText).setSmallIcon(icon)
                .setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis());
        // can cancel?
        if (ongoing) {
            notificationBuilder.setOngoing(true);
        } else {
            notificationBuilder.setAutoCancel(true);
        }
        if (vibrate) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        if (notify) {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.getNotification());
        }
    }

    public void setContext(Context c) {
        ctx = c;
    }

    public void stateUpdate(final ObdCommandJob job) {
        final String cmdName = job.getCommand().getName();
        String cmdResult = "";
        final String cmdID = LookUpCommand(cmdName);

        ObdLog obdLog = new ObdLog();
        obdLog.setPid(job.getCommand().getCommandPID());
        obdLog.setParsed(true);

        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
            cmdResult = job.getCommand().getResult();
            obdLog.setStatus(job.getState().toString());
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
            if (isServiceBound)
                stopLiveData();
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
            cmdResult = "Não suportado";
        } else {
            cmdResult = job.getCommand().getFormattedResult();
        }

        obdLog.setData(cmdResult);

        for(OnDataReceivedListener l:listeners){
            l.onDataReceived(obdLog);
        }
    }

    private void stopLiveData() {
        //TODO
        this.stopSelf();
    }

    public static String LookUpCommand(String txt) {
        for (AvailableCommandNames item : AvailableCommandNames.values()) {
            if (item.getValue().equals(txt)) return item.name();
        }
        return txt;
    }


}
