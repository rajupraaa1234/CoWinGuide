package com.example.cowinguide.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.cowinguide.R;
import com.example.cowinguide.View.Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.cowinguide.Utility.Utility.showSnackBar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    Button logout;
    RelativeLayout homeRel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_home);
        init();
    }

    private void init(){
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);
        homeRel = findViewById(R.id.homeRel);
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logout:{
                logoutUser();
                break;
            }
        }
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent =new Intent(this, LoginActivity.class);
        showSnackBar(homeRel,getString(R.string.logout_successfully));
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}