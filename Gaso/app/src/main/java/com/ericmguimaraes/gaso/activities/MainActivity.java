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

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.activities.registers.CarRegisterActivity;
import com.ericmguimaraes.gaso.adapters.ViewPagerAdapter;
import com.ericmguimaraes.gaso.bluetooth.BluetoothHelper;
import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.fragments.GasFragment;
import com.ericmguimaraes.gaso.fragments.MonthlyExpensesFragment;
import com.ericmguimaraes.gaso.fragments.MyCarFragment;
import com.ericmguimaraes.gaso.fragments.ObdLogFragment;
import com.ericmguimaraes.gaso.maps.LocationHelper;
import com.ericmguimaraes.gaso.model.CombustiveType;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.model.ObdLog;
import com.ericmguimaraes.gaso.persistence.ExpensesDAO;
import com.ericmguimaraes.gaso.services.LoggingService;
import com.ericmguimaraes.gaso.util.CSVHelper;
import com.ericmguimaraes.gaso.util.ConnectionDetector;
import com.ericmguimaraes.gaso.util.GPSTracker;
import com.ericmguimaraes.gaso.util.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ObdLogFragment.OnObdLogListFragmentInteractionListener, MyCarFragment.OnMyCarFragmentInteractionListener {

    private static final String TAG = "MAIN_ACT";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @BindString(R.string.gas)
    String gas;

    @BindString(R.string.expense)
    String expense;

    @BindString(R.string.my_car)
    String myCar;

    @Bind(R.id.gsp_out)
    RelativeLayout gpsRecyclerView;

    @Bind(R.id.net_out)
    RelativeLayout netRecyclerView;

    @Bind(R.id.analysis_out)
    RelativeLayout analysisRecyclerView;

    @Bind(R.id.blue_out)
    RelativeLayout blueRecyclerView;

    private BluetoothAdapter mBtAdapter;

    private final int refreshTime = 10000;

    private Handler servicesHandler;

    private Runnable statusChecker;

    private ConnectionDetector connectionDetector;

    private GPSTracker gpsTracker;

    private boolean isGpsAlertShown = false;

    private boolean isConnected = false;

    private boolean isGpsConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        init();

        servicesHandler = new Handler();

        statusChecker = new Runnable() {
            @Override
            public void run() {
                checkServicesStatus();
                servicesHandler.postDelayed(statusChecker, refreshTime);
            }
        };

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
    public void onStartTripIsPressed(){
        if (mBtAdapter == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_not_found_error), Toast.LENGTH_LONG).show();
                }
            });

            return;
        }

        if (requestLocationPermissions()) {
            return;
        }

        if (!LoggingService.isRunning()) {
            if (!mBtAdapter.isEnabled()) {
                Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(btIntent, BluetoothConnectionActivity.REQUEST_ENABLE_BT);
            } else {
                String bluetoothDeviceAddress = SharedPreferencesManager.getInstance(getApplicationContext()).getDeviceKey();

                if (bluetoothDeviceAddress.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, BluetoothConnectionActivity.class);
                    startActivityForResult(intent, BluetoothConnectionActivity.REQUEST_CONNECT_DEVICE);
                } else {
                    Log.d(TAG, "Start tracking trip");

                    startBluetoothService(bluetoothDeviceAddress);
                }
            }
        }
    }


    private boolean requestLocationPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
                && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            }, Constants.PERMISSION_REQUEST_LOCATION);

            return true;
        }

        return false;
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(GasFragment.newInstance(), gas);
        adapter.addFragment(MyCarFragment.newInstance(), myCar);
        adapter.addFragment(MonthlyExpensesFragment.newInstance(), expense);
        viewPager.setAdapter(adapter);
    }

    private void init(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)
            goToLogin();

        if(SessionSingleton.getInstance().currentCar==null)
            firstAccess();
        else
            initUI();
    }

    private void initUI() {
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(1);
    }

    private void goToLogin() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private void firstAccess() {
        Intent intent = new Intent(this, CarRegisterActivity.class);
        intent.putExtra("first_access",true);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRepeatingTask();
        BluetoothHelper.getInstance().initBluetoothHelper(this);
        if(!BluetoothHelper.getInstance().isBluetoothSupported()){
            blueRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void showGpsLayout(){
        gpsRecyclerView.setVisibility(View.VISIBLE);
    }

    public void hideGpsLayout(){
        gpsRecyclerView.setVisibility(View.GONE);
    }

    public void showNetLayout(){
        netRecyclerView.setVisibility(View.VISIBLE);
    }

    public void hideNetLayout(){
        netRecyclerView.setVisibility(View.GONE);
    }

    public void showAnalysisLayout(){
        analysisRecyclerView.setVisibility(View.VISIBLE);
    }

    public void hideAnalysisLayout(){
        analysisRecyclerView.setVisibility(View.GONE);
    }

    public void hideAnalysistLayout(){
        analysisRecyclerView.setVisibility(View.GONE);
    }

    public boolean checkServicesStatus(){
        boolean status = true;
        connectionDetector = new ConnectionDetector(this);
        gpsTracker = new GPSTracker(this);
        if(!connectionDetector.isConnectingToInternet()) {
            showNetLayout();
            status = false;
            isConnected = false;
        } else {
            hideNetLayout();
            isConnected = true;
        }
        if(!gpsTracker.canGetLocation()){
            status = false;
            if(!isGpsAlertShown){
                gpsTracker.showSettingsAlert();
                isGpsAlertShown = true;
            }
            showGpsLayout();
            isGpsConnected = false;
        } else {
            hideGpsLayout();
            isGpsConnected = true;
        }
        return status;
    }

    void startRepeatingTask() {
        statusChecker.run();
    }

    void stopRepeatingTask() {
        servicesHandler.removeCallbacks(statusChecker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isGpsConnected() {
        return isGpsConnected;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LocationHelper.LOCATION_PERMISSION_REQUEST: {
                if (grantResults .length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO permission granted
                } else {
                    //TODO permission denied
                }
                LocationHelper.isLocationPermissionAsked = true;
                return;
            }
            case Constants.WRITE_ALL_SPENTS: {
                if (grantResults .length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
                    createCSVFile();
                } else {
                    Snackbar.make(viewPager,"Não é possivel exportar os dados sem permissão.",Snackbar.LENGTH_LONG).show();
                }
                LocationHelper.isLocationPermissionAsked = true;
            }
        }
    }

    @Override
    public void onObdLogListFragmentInteraction(ObdLog log) {
        //TODO do something with log
    }

    private void startBluetoothService(String bluetoothDeviceAddress) {
        BluetoothDevice bluetoothDevice = mBtAdapter.getRemoteDevice(bluetoothDeviceAddress);
        Intent intent = new Intent(MainActivity.this, LoggingService.class);
        intent.setAction(LoggingService.SERVICE_START);
        intent.putExtra("bluetoothDevice", bluetoothDevice);

        startService(intent);
    }

    public void createCSVFile() {
        final List<String[]> data = new ArrayList<>();
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        ExpensesDAO dao = new ExpensesDAO();
        dao.findAll(new ExpensesDAO.OnExpensesReceivedListener() {
            @Override
            public void OnExpensesReceived(List<Expense> expenses) {
                for(Expense s: expenses)
                    data.add(new String[]{
                            s.getUid(),
                            s.getCar().getModel(),
                            CombustiveType.fromInteger(s.getType()).toString(),
                            s.getStationName(),
                            format.format(s.getDate()),
                            Double.toString(s.getAmount())+"L",
                            Double.toString(s.getTotal())});

                String msg = "";

                String fileName = "gaso_gastos.csv";

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.WRITE_ALL_SPENTS);
                    Log.e("writing expenses", "NO PERMISSION");
                    return;
                }
                try {
                    CSVHelper.createCSV(fileName, data);
                    msg = "Arquivo "+fileName+" foi salvo com sucesso.";
                } catch (IOException e) {
                    Log.e("SAVING_FILE",e.getMessage(),e);
                    msg = "Desculpe, tivemos um problema exportando o arquivo.";
                }
                Snackbar.make(viewPager,msg,Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(viewPager,Constants.genericError,Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case BluetoothConnectionActivity.REQUEST_CONNECT_DEVICE: {
                if(resultCode==RESULT_OK) {
                    String address = data.getStringExtra(BluetoothConnectionActivity.BLUETOOTH_DEVICE_ADDRESS);

                    Log.d(TAG, "Device address -> " + address);

                    startBluetoothService(address);

                }
                break;
            }
            case BluetoothConnectionActivity.REQUEST_ENABLE_BT: {
                Log.d(TAG, "Bluetooth enabled");

                Toast.makeText(this, R.string.bluetooth_enabled_message, Toast.LENGTH_SHORT).show();

                break;
            }
        }
    }

}
