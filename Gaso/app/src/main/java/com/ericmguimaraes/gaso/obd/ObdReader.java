package com.ericmguimaraes.gaso.obd;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.ObdLog;
import com.ericmguimaraes.gaso.model.ObdLogGroup;
import com.ericmguimaraes.gaso.persistence.ObdLogGroupDAO;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.AbsoluteLoadCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.AvailableCommandNames;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObdReader {
    private static final String TAG = ObdReader.class.getSimpleName();
    private final BluetoothSocket mBtSocket;
    private List<ObdCommand> mObdCommands = new ArrayList<>();
    private List<ObdCommand> mSetupCommands = new ArrayList<>();
    private boolean isRan = false;

    public ObdReader(BluetoothSocket btSocket) {
        mBtSocket = btSocket;

        // TODO: 19/03/17 stop running config commands after OK return
        mObdCommands.add(new EchoOffCommand());
        mObdCommands.add(new LineFeedOffCommand());
        mObdCommands.add(new TimeoutCommand(125));
        mObdCommands.add(new SelectProtocolCommand(ObdProtocols.AUTO));

        mObdCommands.add(new AmbientAirTemperatureCommand());
        mObdCommands.add(new SpeedCommand());
        mObdCommands.add(new RPMCommand());
        mObdCommands.add(new AbsoluteLoadCommand());
        mObdCommands.add(new ThrottlePositionCommand());
        mObdCommands.add(new AirFuelRatioCommand());
        mObdCommands.add(new AmbientAirTemperatureCommand());
    }

    public void setupObd() {
        Log.d(TAG, "Setting up OBD connection");
        try {
            for (ObdCommand command: mSetupCommands) {
                command.run(mBtSocket.getInputStream(), mBtSocket.getOutputStream());
            }
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    public List<ObdLog> readValues() {
        Log.d(TAG, "Reading values");

        List<ObdLog> obdValues = new ArrayList<>();
        for (ObdCommand command: mObdCommands) {
            try {
                command.run(mBtSocket.getInputStream(), mBtSocket.getOutputStream());
                ObdLog obdLog = getLog(command);
                obdValues.add(obdLog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        saveObdlog(obdValues);
        return obdValues;
    }

    private void saveObdlog(List<ObdLog> logs) {
        ObdLogGroup obdLogGroup = new ObdLogGroup();
        obdLogGroup.setLogs(logs);
        ObdLogGroupDAO dao = new ObdLogGroupDAO();
        dao.add(obdLogGroup);
    }

    private ObdLog getLog(ObdCommand command) {
        ObdLog obdLog = new ObdLog();
        final String cmdName = command.getName();
        final String cmdID = LookUpCommand(cmdName);

        Car c = SessionSingleton.getInstance().currentCar;
        if(c!=null)
            obdLog.setCarId(c.getid());
        try {
            obdLog.setPid(command.getCommandPID());
        } catch (IndexOutOfBoundsException e){
            obdLog.setPid(cmdID);
        }
        obdLog.setName(cmdID);
        obdLog.setParsed(true);
        obdLog.setData(command.getCalculatedResult());
        return obdLog;
    }

    public static String LookUpCommand(String txt) {
        for (AvailableCommandNames item : AvailableCommandNames.values()) {
            if (item.getValue().equals(txt)) return item.name();
        }
        return txt;
    }

    public void disconnect() {
        Log.d(TAG, "Disconnecting bluetooth");

        try {
            mBtSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
