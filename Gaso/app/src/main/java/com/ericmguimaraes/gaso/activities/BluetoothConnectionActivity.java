package com.ericmguimaraes.gaso.activities;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.fragments.BluetoothFragment;
import com.ericmguimaraes.gaso.fragments.dummy.DummyContent;
import com.ericmguimaraes.gaso.obd.BluetoothConnectThread;
import com.ericmguimaraes.gaso.obd.BluetoothHelper;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BluetoothConnectionActivity extends AppCompatActivity implements BluetoothFragment.OnBluetoothDeviceListFragmentInteractionListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.coordinator)
    CoordinatorLayout coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        BluetoothHelper.getInstance().initBluetoothHelper(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container, BluetoothFragment.newInstance()).commit();
    }

    @Override
    public void onBluetoothDeviceListFragmentInteraction(BluetoothDevice bluetoothDevice) {
        Snackbar snackbar = Snackbar
                .make(coordinator, "Conectando a "+bluetoothDevice.getName(), Snackbar.LENGTH_LONG);
        snackbar.show();

        BluetoothConnectThread thread = new BluetoothConnectThread(bluetoothDevice, null, new BluetoothConnectThread.OnSocketConnectedListener() {
            @Override
            public void onSocketConnected(BluetoothSocket socket) {
                if(socket==null) {
                    Snackbar snackbar = Snackbar
                            .make(coordinator, "Tivemos um problema ao conectar ao dispositivo, por favor tente novamente.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {

                    //TODO Start service and come back to main screen

                    try {
                        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                        new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
                        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());

                    } catch (Exception e) {
                        // handle errors
                    }
                }
            }
        });

        thread.start();
    }
}
