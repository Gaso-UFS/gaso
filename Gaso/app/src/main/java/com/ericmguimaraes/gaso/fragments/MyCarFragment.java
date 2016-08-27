package com.ericmguimaraes.gaso.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ericmguimaraes.gaso.activities.BluetoothConnectionActivity;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.SettingsActivity;
import com.ericmguimaraes.gaso.activities.CarListActivity;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.ObdLog;
import com.ericmguimaraes.gaso.model.User;
import com.ericmguimaraes.gaso.obd.BluetoothHelper;
import com.ericmguimaraes.gaso.services.ObdService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCarFragment extends Fragment implements ObdService.OnDataReceivedListener {

    @Bind(R.id.no_car_text)
    TextView noCarText;

    @Bind(R.id.name)
    TextView nameText;

    @Bind(R.id.model)
    TextView modelText;

    @Bind(R.id.obd_connect_button)
    Button obdConnectButton;

    @Bind(R.id.obd_data_card)
    CardView obdDataCardView;

    @Bind(R.id.fab_bluetooth)
    FloatingActionButton fabBluetooth;

    @Bind(R.id.obd_info_test)
    TextView obdInfoTest;

    User user;
    Car car;

    private ObdService mService;
    private boolean mBound;

    public MyCarFragment() {
        // Required empty public constructor
    }

    public static MyCarFragment newInstance() {
        MyCarFragment fragment = new MyCarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_car, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_my_car, menu);

        obdConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BluetoothHelper.getInstance().isBluetoothSupported()){
                    Intent intent = new Intent(getContext(), BluetoothConnectionActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),"Não foi possivel conectar ao bluetooth e não há suporte a conexão wifi.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_settings:
                intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.car_list_menu_item:
                intent = new Intent(getActivity(), CarListActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCarAndUser() {
        SessionSingleton sessionSingleton = SessionSingleton.getInstance();
        user = sessionSingleton.currentUser;
        car = sessionSingleton.currentCar;
        if(car == null){
            nameText.setVisibility(View.GONE);
            modelText.setVisibility(View.GONE);
            noCarText.setVisibility(View.VISIBLE);
        } else {
            nameText.setVisibility(View.VISIBLE);
            modelText.setVisibility(View.VISIBLE);
            noCarText.setVisibility(View.GONE);

            nameText.setText(user.getName());
            modelText.setText(car.getModel());
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCarAndUser();

        if(SessionSingleton.getInstance().isToStartAndBindService) {
            Intent intent = new Intent(getContext(), ObdService.class);
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            fabBluetooth.setVisibility(View.VISIBLE);
            obdDataCardView.setVisibility(View.VISIBLE);
            //TODO INICIAR EXIBICAO DOS DADOS DO OBD2
        }

    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            ObdService.ObdServiceBinder binder = (ObdService.ObdServiceBinder) service;
            mService = binder.getService();
            mService.setDevice(SessionSingleton.getInstance().device);
            mService.setSocket(SessionSingleton.getInstance().socket);
            mService.setContext(getContext());
            mService.addOnDataReceivedListener(MyCarFragment.this);
            mService.startReadingThread();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    Handler handler;

    @Override
    public void onDataReceived(final ObdLog obdLog) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateObdView(obdLog);
            }
        },50);

    }

    private void updateObdView(ObdLog obdLog) {
        if(obdLog!=null)
            obdInfoTest.setText(obdLog.getData());
    }

}
