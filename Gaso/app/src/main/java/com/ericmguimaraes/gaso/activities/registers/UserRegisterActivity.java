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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.activities.MainActivity;
import com.ericmguimaraes.gaso.activities.UserListActivity;
import com.ericmguimaraes.gaso.config.SessionSingleton;
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

    @Bind(R.id.input_password)
    TextInputEditText inputPassword;

    @Bind(R.id.input_passwordConfirmation)
    TextInputEditText inputPasswordConfirmation;

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
                } else if ( !(inputPassword.getText().toString().isEmpty() && inputPasswordConfirmation.getText().toString().isEmpty())
                        && !inputPassword.getText().toString().equals(inputPasswordConfirmation.getText().toString())) {
                    Snackbar snackbar = Snackbar
                            .make(v, "Sua senha está diferente nos dois campos.", Snackbar.LENGTH_LONG);
                    inputPassword.setText("");
                    inputPasswordConfirmation.setText("");
                    snackbar.show();
                } else if(emailAlreadyRegistered()) {
                    Snackbar snackbar = Snackbar
                            .make(v, "Email já foi cadastrado anteriormente.", Snackbar.LENGTH_LONG);
                } else {
                    saveOnRealm();
                }
            }
        });
    }

    private boolean emailAlreadyRegistered() {
        UserDAO dao = new UserDAO(getApplicationContext());
        return dao.findbyEmail(inputEmail.getText().toString())!=null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSecurityWarningDialog();
    }

    private void showSecurityWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção!")
                .setMessage("Por enquanto esse método de login não é seguro, há a possibilidade da sua senha ser descoberta por " +
                        "pessoas com acesso ao seu aparelho. Por favor, prefira entrar com a conta do google que oferece total segurança ou " +
                        "não use senha.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void saveOnRealm() {

        UserDAO userDAO = new UserDAO(getApplicationContext());
        User user = new User();
        user.setName(inputName.getText().toString());
        user.setEmail(inputEmail.getText().toString());
        if(inputPassword.getText()!=null && !inputPassword.getText().toString().isEmpty())
            user.setPassword(inputPassword.getText().toString());

        userDAO.add(user);

        CharSequence text = userRegistered;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();

        SessionSingleton.getInstance().setCurrentUser(user);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

}
