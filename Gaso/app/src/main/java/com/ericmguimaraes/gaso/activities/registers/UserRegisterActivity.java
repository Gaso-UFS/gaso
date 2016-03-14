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
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.lists.UserListActivity;
import com.ericmguimaraes.gaso.model.User;
import com.ericmguimaraes.gaso.persistence.UserDAO;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class UserRegisterActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.input_name)
    TextInputEditText inputName;

    @Bind(R.id.input_email)
    TextInputEditText inputEmail;

    @Bind(R.id.btn_confirm)
    Button confirmBtn;

    @BindString(R.string.user_registered)
    String userRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
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
                if(inputName.getText().length()==0 || inputEmail.getText().length()==0){
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

        UserDAO userDAO = new UserDAO(getApplicationContext());
        User user = new User();
        user.setName(inputName.getText().toString());
        user.setEmail(inputEmail.getText().toString());
        userDAO.add(user);

        CharSequence text = userRegistered;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();

        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);

    }

}
