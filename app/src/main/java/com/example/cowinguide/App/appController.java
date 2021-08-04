package com.example.cowinguide.App;

import android.app.Application;

import com.example.cowinguide.NetWork.NetworkHandler;
import com.example.cowinguide.Utility.Utility;


public class appController extends Application {
    private static appController instance;
    public static synchronized appController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        NetworkHandler.isConnected = Utility.getNetworkState(this);
    }
}
