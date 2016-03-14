package com.ericmguimaraes.gaso.activities.registers;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.Config;
import com.ericmguimaraes.gaso.model.Spent;
import com.ericmguimaraes.gaso.persistence.SpentDAO;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SpentRegisterActivity extends AppCompatActivity {

        @Bind(R.id.toolbar)
        Toolbar toolbar;

        @Bind(R.id.input_type)
        TextInputEditText inputType;

        @Bind(R.id.input_total)
        TextInputEditText inputTotal;

        @Bind(R.id.input_amount)
        TextInputEditText inputAmount;

        @Bind(R.id.input_station)
        TextInputEditText inputStation;

        @Bind(R.id.btn_confirm)
        Button confirmBtn;

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
                    if(inputType.getText().length()==0 || inputTotal.getText().length()==0 || inputAmount.getText().length()==0 || inputStation.getText().length()==0){
                        Log.d("Field Required", "");
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

            SpentDAO dao = new SpentDAO(getApplicationContext());
            Spent s = new Spent();
            s.setUser(Config.getInstance().currentUser);
            s.setCar(Config.getInstance().currentCar);
            s.setDate(new Date());
            s.setType(Integer.parseInt(inputType.getText().toString()));
            s.setTotal(Double.parseDouble(inputTotal.getText().toString()));
            s.setStation(null); //TODO
            s.setAmount(Double.parseDouble(inputAmount.getText().toString()));
            dao.add(s);

            CharSequence text = "Gasto adicionado com sucesso.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();

            onBackPressed();

        }

}
