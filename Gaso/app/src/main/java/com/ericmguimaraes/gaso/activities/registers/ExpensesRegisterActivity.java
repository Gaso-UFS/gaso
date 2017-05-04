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

package com.ericmguimaraes.gaso.activities.registers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.model.Station;
import com.ericmguimaraes.gaso.persistence.CarDAO;
import com.ericmguimaraes.gaso.persistence.ExpensesDAO;
import com.ericmguimaraes.gaso.persistence.MilestoneDAO;
import com.ericmguimaraes.gaso.util.DatePickerFragment;
import com.ericmguimaraes.gaso.evaluation.EvaluationHelper;
import com.ericmguimaraes.gaso.util.GsonManager;
import com.ericmguimaraes.gaso.util.Mask;
import com.ericmguimaraes.gaso.util.MaskEditTextChangedListener;
import com.ericmguimaraes.gaso.util.TimePickerFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExpensesRegisterActivity extends AppCompatActivity implements DatePickerFragment.DatePickerInterface, TimePickerFragment.TimePickerInterface, AdapterView.OnItemSelectedListener {

    public static final String REFIL_EXTRA = "refil";
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

    @Bind(R.id.amount_mine_layout)
    TextInputLayout amountMineLayout;

    @Bind(R.id.diffCheckbox)
    CheckBox diffCheckbox;

    @Bind(R.id.input_amount_mine)
    TextInputEditText inputAmountMine;

    int PLACE_PICKER_REQUEST = 1;
    PlacePicker.IntentBuilder builder;

    Station stationSelected;
    Calendar calendarSelected;
    int typeSelected = -1;

    SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yy");
    SimpleDateFormat houtFormat = new SimpleDateFormat("HH:mm");
    private float lastAmount = -1;
    private boolean obdRefil = false;


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
                    // TODO: 02/05/17 criar dialogo para confirmar cadastro com dados nulos
                    Log.d("Field Required", "");
                    Snackbar snackbar = Snackbar
                            .make(v, "Complete os campos obrigatorios.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    saveOnDatabase();
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

        MaskEditTextChangedListener maskAmount = new MaskEditTextChangedListener("##.##L", inputAmount);
        inputAmount.addTextChangedListener(maskAmount);

        MaskEditTextChangedListener maskDate = new MaskEditTextChangedListener("##/##/##", inputDate);
        inputDate.addTextChangedListener(maskDate);

        MaskEditTextChangedListener maskHour = new MaskEditTextChangedListener("##:##", inputHour);
        inputHour.addTextChangedListener(maskHour);

        Intent intent = getIntent();
        if(intent!=null){
            if(intent.hasExtra("station_gson")){
                stationSelected = GsonManager.getGsonInstance().fromJson(intent.getStringExtra("station_gson"), Station.class);
                if(stationSelected!=null) {
                    inputStation.setText(stationSelected.getName());
                }
            }
        }

        inputTotal.addTextChangedListener(Mask.moneyMask(inputTotal));

        obdRefil = getIntent()!=null && getIntent().hasExtra(ExpensesRegisterActivity.REFIL_EXTRA);

        if(obdRefil) {
            inputAmount.setText(Float.toString(getIntent().getExtras().getFloat(REFIL_EXTRA)) + "L");
            lastAmount = getIntent().getExtras().getFloat(REFIL_EXTRA);
            diffCheckbox.setVisibility(View.VISIBLE);
            amountMineLayout.setVisibility(View.GONE);
            diffCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        amountMineLayout.setVisibility(View.VISIBLE);
                    else
                        amountMineLayout.setVisibility(View.GONE);
                }
            });
        } else {
            diffCheckbox.setVisibility(View.GONE);
            amountMineLayout.setVisibility(View.GONE);
        }
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
            hideSoftKeyboard(v);
        }
    };

    View.OnClickListener inputDateOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callDatePicker();
            hideSoftKeyboard(v);
        }
    };

    View.OnClickListener inputHourOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callHourPicker();
            hideSoftKeyboard(v);
        }
    };

    View.OnFocusChangeListener inputStationOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                callPlacePicker();
                hideSoftKeyboard(v);
            }
        }
    };

    View.OnFocusChangeListener inputDateOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                callDatePicker();
                hideSoftKeyboard(v);
            }
        }
    };

    View.OnFocusChangeListener inputHourOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus) {
                callHourPicker();
                hideSoftKeyboard(v);
            }
        }
    };

    private void hideSoftKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

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
            startActivityForResult(builder.build(ExpensesRegisterActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e("PLACE PICKER", e.getMessage(), e);
            Toast t = Toast.makeText(getApplicationContext(),"Ops, tivemos um problema.", Toast.LENGTH_LONG);
            t.show();
        }
    }

    private void saveOnDatabase() {
        if(obdRefil) {
            saveAmountOnCar();
            MilestoneDAO dao = new MilestoneDAO();
            //// TODO: 03/05/17 enviar quantidade total de combustivel e valor extra do posto atual
            dao.createNewMilestone();
            EvaluationHelper.initEvaluation();
        }
        saveExpense();
        sucessEventUI();
    }

    private void sucessEventUI() {
        CharSequence text = "Gasto adicionado com sucesso.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
        onBackPressed();
    }

    private void saveExpense() {
        ExpensesDAO dao = new ExpensesDAO();
        Expense e = new Expense();
        e.setDate(calendarSelected==null?new Date().getTime():calendarSelected.getTime().getTime());
        e.setType(typeSelected);
        String parsableDouble = inputTotal.getText().toString().replace("R$","").replace(",","").replace("$","");
        e.setTotal(Double.parseDouble(parsableDouble));
        e.setStationUid(stationSelected.getId());
        e.setAmount(Double.parseDouble(inputAmount.getText().toString().replace("L","")));
        e.setCarUid(SessionSingleton.getInstance().currentCar.getid());
        e.setStationName(stationSelected.getName());
        e.setStation(stationSelected);
        e.setCar(SessionSingleton.getInstance().currentCar);
        e.setAmountMine(lastAmount);
        dao.add(e);
    }

    private void saveAmountOnCar() {
        final CarDAO dao = new CarDAO();
        dao.findCarByID(SessionSingleton.getInstance().getCurrentUser(getApplicationContext()).getUid(), new CarDAO.OneCarReceivedListener() {
            @Override
            public void onCarReceived(Car car) {
                car.setLastFuelLevel(lastAmount);
                dao.addOrUpdate(car);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ignore error
            }
        });
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
        calendarSelected.set(Calendar.HOUR_OF_DAY,hourOfDay);
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
