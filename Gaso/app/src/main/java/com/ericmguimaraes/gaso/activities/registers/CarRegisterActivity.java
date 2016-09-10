package com.ericmguimaraes.gaso.activities.registers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.activities.CarListActivity;
import com.ericmguimaraes.gaso.activities.MainActivity;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.persistence.CarDAO;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class CarRegisterActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.input_car)
    TextInputEditText inputCar;

    @Bind(R.id.input_car_description)
    TextInputEditText inputCarDescrition;

    @Bind(R.id.btn_confirm)
    Button confirmBtn;

    @BindString(R.string.car_registered)
    String carRegistered;

    @Bind(R.id.welcome_car)
    TextView welcomeCarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_register);
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
                if(inputCar.getText().length()==0){
                    Log.d("Field Required", "");
                    Snackbar snackbar = Snackbar
                            .make(v, "Complete os campos obrigatorios.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    saveOnRealm();
                }
            }
        });

        if(getIntent().getExtras()==null || !getIntent().getExtras().getBoolean("first_access",false))
            welcomeCarText.setVisibility(View.GONE);
    }

    private void saveOnRealm() {

        CarDAO carDAO = new CarDAO(getApplicationContext());
        Car car = new Car();
        car.setDescription(inputCarDescrition.getText().toString());
        car.setModel(inputCar.getText().toString());
        car.setUser(SessionSingleton.getInstance().getCurrentUser(getApplicationContext()));
        carDAO.add(car);

        CharSequence text = carRegistered;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();

        SessionSingleton.getInstance().currentCar = car;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

}
