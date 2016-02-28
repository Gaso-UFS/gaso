package com.ericmguimaraes.gaso;

import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.User;

/**
 * Created by ericm on 2/28/2016.
 */
public class Config {

    private static Config instance;

    public User currentUser;
    public Car currentCar;

    private Config(){
    }

    public static Config getInstance(){
        if(instance!=null)
            return instance;
        instance = new Config();
        return instance;
    }

}
