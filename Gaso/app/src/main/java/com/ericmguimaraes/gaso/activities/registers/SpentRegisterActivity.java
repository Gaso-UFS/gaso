package com.ericmguimaraes.gaso.activities.registers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.Config;
import com.ericmguimaraes.gaso.model.Spent;
import com.ericmguimaraes.gaso.model.Station;
import com.ericmguimaraes.gaso.persistence.SpentDAO;
import com.ericmguimaraes.gaso.util.DatePickerFragment;
import com.ericmguimaraes.gaso.util.TimePickerFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SpentRegisterActivity extends AppCompatActivity implements DatePickerFragment.DatePickerInterface, TimePickerFragment.TimePickerInterface, AdapterView.OnItemSelectedListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.typeSpinner)
    Spinner typeSpinner;

    @Bind(R.id.input_total)
    TextInputEditText inputTotal;

    @Bind(R.id.input_amount)
    TextInputEditText inputAmount;

    @Bind(R.id.input_station)
    TextInputEditText inputStation;

    @Bind(R.id.input_date)
    TextInputEditText inputDate;

    @Bind(R.id.input_hour)
    TextInputEditText inputHour;

    @Bind(R.id.btn_confirm)
    Button confirmBtn;

    int PLACE_PICKER_REQUEST = 1;
    PlacePicker.IntentBuilder builder;

    Station stationSelected;
    Calendar calendarSelected;
    int typeSelected = -1;

    SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yy");
    SimpleDateFormat houtFormat = new SimpleDateFormat("HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spent_register);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeSelected==-1 || inputTotal.getText().length() == 0 || inputAmount.getText().length() == 0 || inputStation.getText().length() == 0 || inputDate.getText().length() == 0 || inputHour.getText().length() == 0) {
                    Log.d("Field Required", "");
                    Snackbar snackbar = Snackbar
                            .make(v, "Complete os campos obrigatorios.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    saveOnRealm();
                }
            }
        });

        builder = new PlacePicker.IntentBuilder();

        inputStation.setOnClickListener(inputStationOnClickListener);
        inputDate.setOnClickListener(inputDateOnClickListener);
        inputHour.setOnClickListener(inputHourOnClickListener);

        inputStation.setOnFocusChangeListener(inputStationOnFocusChangeListener);
        inputDate.setOnFocusChangeListener(inputDateOnFocusChangeListener);
        inputHour.setOnFocusChangeListener(inputHourOnFocusChangeListener);

        Calendar nowCalendar = Calendar.getInstance();
        inputDate.setText(dayFormat.format(nowCalendar.getTime()));
        inputHour.setText(houtFormat.format(nowCalendar.getTime()));

        setSpinner();

    }

    public void setSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.combustives_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(this);
    }

    View.OnClickListener inputStationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callPlacePicker();
        }
    };

    View.OnClickListener inputDateOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callDatePicker();
        }
    };

    View.OnClickListener inputHourOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callHourPicker();
        }
    };

    View.OnFocusChangeListener inputStationOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus)
                callPlacePicker();
        }
    };

    View.OnFocusChangeListener inputDateOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus)
                callDatePicker();
        }
    };

    View.OnFocusChangeListener inputHourOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus)
                callHourPicker();
        }
    };

    private void callHourPicker() {
        DialogFragment newFragment = new TimePickerFragment(this);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void callDatePicker() {
        DialogFragment newFragment = new DatePickerFragment(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void callPlacePicker(){
        try {
            startActivityForResult(builder.build(SpentRegisterActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e("PLACE PICKER", e.getMessage(), e);
            Toast t = Toast.makeText(getApplicationContext(),"Ops, tivemos um problema.", Toast.LENGTH_LONG);
            t.show();
        }
    }

    private void saveOnRealm() {

        SpentDAO dao = new SpentDAO(getApplicationContext());
        Spent s = new Spent();
        s.setUser(Config.getInstance().currentUser);
        s.setCar(Config.getInstance().currentCar);
        s.setDate(calendarSelected==null?new Date():calendarSelected.getTime());
        s.setType(typeSelected);
        s.setTotal(Double.parseDouble(inputTotal.getText().toString()));
        s.setStation(stationSelected);
        s.setAmount(Double.parseDouble(inputAmount.getText().toString()));
        dao.add(s);

        CharSequence text = "Gasto adicionado com sucesso.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();

        onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                try
                {
                    stationSelected = new Station(place);
                    inputStation.setText(place.getName());
                }
                catch (IllegalArgumentException e)
                {
                    String toastMsg = String.format("Ops, parece que %s não é um posto. Tente novamente.", place.getName());
                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(calendarSelected==null)
            calendarSelected = Calendar.getInstance();
        calendarSelected.set(Calendar.YEAR,year);
        calendarSelected.set(Calendar.MONTH,month);
        calendarSelected.set(Calendar.DAY_OF_MONTH,day);
        inputDate.setText(dayFormat.format(calendarSelected.getTime()));
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(calendarSelected==null)
            calendarSelected = Calendar.getInstance();
        calendarSelected.set(Calendar.HOUR,hourOfDay);
        calendarSelected.set(Calendar.MINUTE,minute);
        inputHour.setText(houtFormat.format(calendarSelected.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        typeSelected = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        typeSelected = -1;
    }
}
