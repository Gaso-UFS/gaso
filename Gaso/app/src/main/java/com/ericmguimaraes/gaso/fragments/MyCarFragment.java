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

package com.ericmguimaraes.gaso.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.activities.BluetoothConnectionActivity;
import com.ericmguimaraes.gaso.activities.CarListActivity;
import com.ericmguimaraes.gaso.activities.LoginActivity;
import com.ericmguimaraes.gaso.activities.PlainTextActivity;
import com.ericmguimaraes.gaso.bluetooth.BluetoothConnection;
import com.ericmguimaraes.gaso.bluetooth.BluetoothHelper;
import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.ObdLog;
import com.ericmguimaraes.gaso.model.ObdLogGroup;
import com.ericmguimaraes.gaso.services.LoggingService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCarFragment extends Fragment {

    private static final String OBD_FRAGMENT_TAG = "obd_fragment";
    private static final String TAG = "MY_CAR";
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

    Car car;

    @Bind(R.id.profile_image)
    CircleImageView profileImageView;

    ProfilePicLoaderTask profilePicLoaderTask;

    OnMyCarFragmentInteractionListener mListener;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            profilePicLoaderTask = new ProfilePicLoaderTask(user.getPhotoUrl());
            profilePicLoaderTask.execute();
        }

        obdConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SessionSingleton.getInstance().currentCar==null)
                    Snackbar.make(v,"Por favor, cadastre e/ou selecione um carro antes de conectar o seu OBD2",Snackbar.LENGTH_LONG).show();

                if(BluetoothHelper.getInstance().isBluetoothSupported()){
                    // old way
                    //Intent intent = new Intent(getContext(), BluetoothConnectionActivity.class);
                    //startActivity(intent);
                    mListener.onStartTripIsPressed();
                } else {
                    Toast.makeText(getContext(),"Não foi possivel conectar ao bluetooth e não há suporte a conexão wifi.",Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_my_car, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.car_list_menu_item:
                intent = new Intent(getActivity(), CarListActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_help:
                Intent intentHelp = new Intent(getActivity(), PlainTextActivity.class);
                intentHelp.putExtra(PlainTextActivity.EXTRA_TITLE, R.string.help_title);
                intentHelp.putExtra(PlainTextActivity.EXTRA_TEXT, R.string.help_text);
                startActivity(intentHelp);
                return true;
            case R.id.action_disclaimer:
                Intent intentDisclaimer = new Intent(getActivity(), PlainTextActivity.class);
                intentDisclaimer.putExtra(PlainTextActivity.EXTRA_TITLE, R.string.disclaimer_title);
                intentDisclaimer.putExtra(PlainTextActivity.EXTRA_TEXT, R.string.disclaimer_text);
                startActivity(intentDisclaimer);
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                forgetLoggedUser();
                intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void forgetLoggedUser() {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.USER_LOGGED_TAG, "");
        editor.apply();
    }

    private void updateCarAndUser() {
        SessionSingleton sessionSingleton = SessionSingleton.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        car = sessionSingleton.currentCar;
        if(car == null || user == null){
            nameText.setVisibility(View.GONE);
            modelText.setVisibility(View.GONE);
            profileImageView.setVisibility(View.GONE);
            noCarText.setVisibility(View.VISIBLE);
        } else {
            nameText.setVisibility(View.VISIBLE);
            modelText.setVisibility(View.VISIBLE);
            profileImageView.setVisibility(View.VISIBLE);
            noCarText.setVisibility(View.GONE);

            nameText.setText(user.getDisplayName());
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
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCarAndUser();
        if(getActivity()!=null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(LoggingService.SERVICE_BROADCAST_MESSAGE);
            filter.addAction(LoggingService.SERVICE_DATA_OBDGROUP);
            LocalBroadcastManager.getInstance(getContext())
                    .registerReceiver(mBroadcastReceiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    Handler handler;

    // TODO: 19/03/17 use it
    private void updateObdView(ObdLogGroup group) {
        ObdLogFragment obdFragment = (ObdLogFragment) getChildFragmentManager().findFragmentByTag(OBD_FRAGMENT_TAG);
        if (group != null && obdFragment != null && group.getLogs()!=null) {
            for (ObdLog obdLog : group.getLogs()) {
                if (obdLog != null) {
                    obdFragment.addOrUpdateJob(obdLog);
                }
            }
        }
    }

    private void showObdCard(){
        if(fabBluetooth!=null && isAdded()) {
            fabBluetooth.setVisibility(View.VISIBLE);
            obdDataCardView.setVisibility(View.VISIBLE);

            getChildFragmentManager().beginTransaction().replace(R.id.obd_content, ObdLogFragment.newInstance(), OBD_FRAGMENT_TAG).commit();

            fabBluetooth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyCarFragment.this.getActivity(), BluetoothConnectionActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void hideObdCard(){
        if(fabBluetooth!=null && isAdded()) {
            fabBluetooth.setVisibility(View.GONE);
            obdDataCardView.setVisibility(View.GONE);
            obdConnectButton.setVisibility(View.VISIBLE);
        }
    }

    public Bitmap loadImageFromWebOperations(Uri uri) {
        try {
            URL url = new URL(uri.toString());
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            Log.e("IMAGE_PROFILE",e.getMessage(),e);
            return null;
        }
    }

    private class ProfilePicLoaderTask extends AsyncTask<Void,Void,Void> {

        Bitmap bitmap;

        Uri uri;

        protected ProfilePicLoaderTask(Uri uri){
            this.uri =uri;
        }

        @Override
        protected Void doInBackground(Void... params) {
            bitmap = loadImageFromWebOperations(uri);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!isCancelled() && bitmap!=null && profileImageView!=null)
                profileImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(profilePicLoaderTask!=null)
            profilePicLoaderTask.cancel(true);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyCarFragmentInteractionListener) {
            mListener = (OnMyCarFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyCarFragmentInteractionListener");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMyCarFragmentInteractionListener {
        void onStartTripIsPressed();
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received broadcast from Service");

            if (intent.getAction().equals(LoggingService.SERVICE_BROADCAST_MESSAGE)) {
                switch (intent.getStringExtra(LoggingService.SERVICE_MESSAGE)) {
                    case LoggingService.SERVICE_BLUETOOTH_CONNECTING: {
                        Log.d(TAG, "Service connecting");
                        showMessage("Conectando...");
                        break;
                    }
                    case LoggingService.SERVICE_BLUETOOTH_CONNECTED: {
                        Log.d(TAG, "Service connected");

                        final String name = intent.getStringExtra(BluetoothConnection.BLUETOOTH_TARGET_DEVICE_NAME);
                        final String address = intent.getStringExtra(BluetoothConnection.BLUETOOTH_TARGET_DEVICE_ADDRESS);

                        showMessage(String.format(getString(R.string.bluetooth_connected_starting_logging),
                                                name, address));
                        showObdCard();

                        break;
                    }
                    case LoggingService.SERVICE_BLUETOOTH_ERROR: {
                        Log.d(TAG, "Service bluetooth error");

                        // TODO: Should open BluetoothActivity to possibly select new device
                        final String name = intent.getStringExtra(BluetoothConnection.BLUETOOTH_TARGET_DEVICE_NAME);
                        final String address = intent.getStringExtra(BluetoothConnection.BLUETOOTH_TARGET_DEVICE_ADDRESS);

                        showMessage(String.format(getString(R.string.bluetooth_connecting_errror),
                                                name, address));
                        break;
                    }
                    case LoggingService.SERVICE_NEW_DATA: {
                        ObdLogGroup group = (ObdLogGroup) intent.getSerializableExtra(LoggingService.SERVICE_DATA_OBDGROUP);
                        if(group!=null)
                            updateObdView(group);
                        break;
                    }
                    case LoggingService.SERVICE_BROKEN_PIPE: {
                        if (isAdded() && getActivity()!=null) {
                            showMessage("Tivemos algum problema, vamos encerrar a viagem.");
                            intent = new Intent(getActivity(), LoggingService.class);
                            intent.setAction(LoggingService.SERVICE_STOP_LOGGING);
                            getActivity().startService(intent);
                            hideObdCard();
                        }
                        break;
                    }
                }
            }
        }
    };

    private void showMessage(String message) {
        Snackbar.make(nameText,message,Snackbar.LENGTH_LONG).show();
    }

}