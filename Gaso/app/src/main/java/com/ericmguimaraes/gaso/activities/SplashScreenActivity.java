package com.ericmguimaraes.gaso.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.model.User;
import com.ericmguimaraes.gaso.persistence.UserDAO;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUser();
    }

    private void checkUser() {
        User u = getUserLogged();
        if(u==null){
            goToLogin(null);
        } else {
            SessionSingleton.getInstance().currentUser = u;
            goToMain();
        }
    }

    private User getUserLogged(){
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        String email = settings.getString(Constants.USER_LOGGED_TAG,"");
        UserDAO dao = new UserDAO(getApplicationContext());
        User u = dao.findbyEmail(email);
        u = copyUser(u);
        return u;
    }

    private User copyUser(@Nullable User user) {
        if(user==null)
            return null;
        User userCopy = new User();
        userCopy.setPassword(user.getPassword());
        userCopy.setEmail(user.getEmail());
        userCopy.setName(user.getName());
        userCopy.setId(user.getId());
        return userCopy;
    }

    private void goToMain() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }

        },300);
    }

    private void goToLogin(final String msg) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView logo = (ImageView) findViewById(R.id.gaso_icon);
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                if(msg!=null)
                    intent.putExtra("message",msg);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashScreenActivity.this, logo, "gaso_icon");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(intent, options.toBundle());
                }else {
                    startActivity(intent);
                }
            }
        },300);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else
                    finish();
            }
        },1000);
    }
}
