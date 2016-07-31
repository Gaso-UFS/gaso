package com.ericmguimaraes.gaso.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.adapters.ViewPagerAdapter;
import com.ericmguimaraes.gaso.config.Config;
import com.ericmguimaraes.gaso.fragments.GasFragment;

import com.ericmguimaraes.gaso.fragments.MapGasoFragment;
import com.ericmguimaraes.gaso.fragments.MyCarFragment;
import com.ericmguimaraes.gaso.fragments.StationFragment;
import com.ericmguimaraes.gaso.lists.MonthlyExpensesFragment;
import com.ericmguimaraes.gaso.maps.LocationHelper;
import com.ericmguimaraes.gaso.obd.BluetoothHelper;
import com.ericmguimaraes.gaso.persistence.CarDAO;
import com.ericmguimaraes.gaso.persistence.UserDAO;
import com.ericmguimaraes.gaso.util.ConnectionDetector;
import com.ericmguimaraes.gaso.util.GPSTracker;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MyCarFragment.OnFragmentInteractionListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @BindString(R.string.gas)
    String gas;

    @BindString(R.string.spent)
    String spent;

    @BindString(R.string.my_car)
    String myCar;

    @Bind(R.id.gsp_out)
    RelativeLayout gpsRecyclerView;

    @Bind(R.id.net_out)
    RelativeLayout netRecyclerView;

    @Bind(R.id.blue_out)
    RelativeLayout blueRecyclerView;

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

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(1);

        init();

        servicesHandler = new Handler();

        statusChecker = new Runnable() {
            @Override
            public void run() {
                checkServicesStatus();
                servicesHandler.postDelayed(statusChecker, refreshTime);
            }
        };

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GasFragment(), gas);
        adapter.addFragment(new MyCarFragment(), myCar);
        adapter.addFragment(new MonthlyExpensesFragment(), spent);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    private void init(){
        UserDAO userDAO = new UserDAO(getApplicationContext());
        CarDAO carDAO = new CarDAO(getApplicationContext());
        if(Config.getInstance().currentUser==null)
            Config.getInstance().currentUser = userDAO.findFirst();
        if(Config.getInstance().currentCar==null)
            Config.getInstance().currentCar = carDAO.findFirst();
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO permission granted
                } else {
                    //TODO permission denied
                }
                return;
            }
        }
    }

    public void onBluetoothSelectionPressed() {

    }
}
