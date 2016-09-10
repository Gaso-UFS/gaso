package com.ericmguimaraes.gaso.activities;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.fragments.BluetoothFragment;
import com.ericmguimaraes.gaso.bluetooth.BluetoothConnectThread;
import com.ericmguimaraes.gaso.bluetooth.BluetoothHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BluetoothConnectionActivity extends AppCompatActivity implements BluetoothFragment.OnBluetoothDeviceListFragmentInteractionListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.coordinator)
    CoordinatorLayout coordinator;

    Handler handler;

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

        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container, BluetoothFragment.newInstance()).commit();
    }

    @Override
    public void onBluetoothDeviceListFragmentInteraction(final BluetoothDevice bluetoothDevice) {
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
                    Snackbar snackbar = Snackbar
                            .make(coordinator, "Conectado com sucesso.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    SessionSingleton.getInstance().device = bluetoothDevice;
                    SessionSingleton.getInstance().socket = socket;
                    SessionSingleton.getInstance().isToStartAndBindService = true;

                    Intent intent = new Intent(BluetoothConnectionActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
        });

        thread.start();
    }


}
