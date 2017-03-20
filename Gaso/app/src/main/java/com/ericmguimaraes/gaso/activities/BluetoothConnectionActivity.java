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
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.fragments.BluetoothFragment;
import com.ericmguimaraes.gaso.bluetooth.BluetoothConnectThread;
import com.ericmguimaraes.gaso.bluetooth.BluetoothHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BluetoothConnectionActivity extends AppCompatActivity implements BluetoothFragment.OnBluetoothDeviceListFragmentInteractionListener {

    public static final int REQUEST_ENABLE_BT = 0;
    public static final int REQUEST_CONNECT_DEVICE = 1;

    public static String BLUETOOTH_DEVICE_ADDRESS = "device_address";

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

        BluetoothHelper.getInstance().stopAdapterIfListening();

        Intent intent = new Intent();
        intent.putExtra(BLUETOOTH_DEVICE_ADDRESS, bluetoothDevice.getAddress());

        setResult(RESULT_OK, intent);
        finish();

        /* old way
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

        */
    }


}
