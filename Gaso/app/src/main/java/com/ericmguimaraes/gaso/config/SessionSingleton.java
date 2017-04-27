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

package com.ericmguimaraes.gaso.config;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.ericmguimaraes.gaso.model.Car;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by ericm on 2/28/2016.
 */
public class SessionSingleton {

    private static SessionSingleton instance;
    
    public Car currentCar;

    private SessionSingleton(){
    }

    public static SessionSingleton getInstance(){
        if(instance!=null)
            return instance;
        instance = new SessionSingleton();
        return instance;
    }


    public FirebaseUser getCurrentUser(Context context) {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
