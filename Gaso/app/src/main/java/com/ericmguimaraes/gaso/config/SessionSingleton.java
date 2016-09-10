package com.ericmguimaraes.gaso.config;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.User;
import com.ericmguimaraes.gaso.persistence.UserDAO;

/**
 * Created by ericm on 2/28/2016.
 */
public class SessionSingleton {

    private static SessionSingleton instance;

    private User currentUser;
    public Car currentCar;
    public BluetoothDevice device;
    public BluetoothSocket socket;
    public boolean isToStartAndBindService = false;

    private SessionSingleton(){
    }

    public static SessionSingleton getInstance(){
        if(instance!=null)
            return instance;
        instance = new SessionSingleton();
        return instance;
    }

    public User getCurrentUser(Context context) {
        if(currentUser==null)
            return getUserLogged(context);
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private User getUserLogged(Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String email = settings.getString(Constants.USER_LOGGED_TAG,"");
        UserDAO dao = new UserDAO(context);
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

}
