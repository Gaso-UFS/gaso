package com.ericmguimaraes.gaso;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.User;
import com.ericmguimaraes.gaso.persistence.CarDAO;
import com.ericmguimaraes.gaso.persistence.UserDAO;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.input_name)
    TextInputEditText inputName;

    @Bind(R.id.input_email)
    TextInputEditText inputEmail;

    @Bind(R.id.input_car)
    TextInputEditText inputCar;

    @Bind(R.id.input_car_description)
    TextInputEditText inputCarDescrition;

    @Bind(R.id.btn_confirm)
    Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
                if(inputCar.getText().length()==0 || inputName.getText().length()==0 || inputEmail.getText().length()==0){
                    Log.d("Field Required","");
                    Snackbar snackbar = Snackbar
                            .make(v, "Complete os campos obrigatorios.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    saveOnRealm();
                }
            }
        });
    }

    private void saveOnRealm() {
        UserDAO userDAO = new UserDAO(getApplicationContext());
        User user = new User();
        user.setName(inputName.getText().toString());
        user.setEmail(inputEmail.getText().toString());
        userDAO.add(user);

        CarDAO carDAO = new CarDAO(getApplicationContext());
        Car car = new Car();
        car.setDescription(inputCarDescrition.getText().toString());
        car.setModel(inputCar.getText().toString());
        carDAO.add(car);

        //TODO launch main acticity

    }


}
