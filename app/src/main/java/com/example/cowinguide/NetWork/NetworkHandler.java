package com.example.cowinguide.NetWork;

import android.content.Context;
import android.view.View;

import com.example.cowinguide.Utility.Utility;


public class NetworkHandler {

    public static boolean isConnected;

    public static boolean isConnected() {
        return isConnected;
    }


    public static boolean isConnected(Context context) {
        if (!isConnected) {
            NetworkHandler.isConnected =Utility.getNetworkState(context);
        }
        return isConnected;
    }

    public static boolean isConnected(View anyView) {
        if (!isConnected) {
            if (anyView != null) {
               // DialogUtil.showNoNetworkSnackBar(anyView);
                NetworkHandler.isConnected = Utility.getNetworkState(anyView.getContext());
            }
        }
        return isConnected;
    }

    public static boolean isConnected(View anyView, View.OnClickListener retryListener) {
      //  if (!isConnected) DialogUtil.showNoNetworkSnackBar(anyView);
        return isConnected;
    }


}
