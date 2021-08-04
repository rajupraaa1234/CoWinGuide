package com.example.cowinguide.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.cowinguide.R;
import com.example.cowinguide.View.Login.SignUp.SignUpActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

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

}
