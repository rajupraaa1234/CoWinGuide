package com.example.cowinguide.Utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.cowinguide.R;
import com.example.cowinguide.View.Login.SignUp.SignUpActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class Utility {

    public static boolean getNetworkState(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }



    public static  void showSnackBar(View anyView, String msg) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            final Snackbar snackBar = Snackbar.make(anyView, msg, Snackbar.LENGTH_LONG);
            snackBar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
            snackBar.setTextColor(anyView.getContext().getColor(R.color.white));
            snackBar.setBackgroundTint(anyView.getContext().getColor(R.color.black));
            snackBar.show();
        }else{
            Snackbar.make(anyView,msg, Snackbar.LENGTH_LONG).show();
        }
    }

    public static boolean isValidMobile(String phone) {
            return phone.length() > 6 && phone.length() <= 13;
    }

    public static boolean isvalidEmail(String email){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static LatLng getLocationFromAddress(Context context,String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.size()==0) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    public static double getDistance(Double lat1, Double lang1, Double lat2, Double long2){
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lang1);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(long2);

        double distance=startPoint.distanceTo(endPoint);
        return distance;
    }

}
