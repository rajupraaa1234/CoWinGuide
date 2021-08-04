package com.example.cowinguide.View.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cowinguide.Home.HomeActivity;
import com.example.cowinguide.R;
import com.example.cowinguide.Utility.Utility;
import com.example.cowinguide.View.Login.SignUp.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import static com.example.cowinguide.Utility.Utility.isValidMobile;
import static com.example.cowinguide.Utility.Utility.showSnackBar;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    TextView create;
    EditText Lemail;
    EditText Lpass;
    TextView loginBtn;
    RelativeLayout LoginRelMain;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_login);
        init();
    }


    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void init(){
        create = findViewById(R.id.create);
        create.setOnClickListener(this);
        Lemail = findViewById(R.id.emailId);
        Lpass = findViewById(R.id.LoginPass);
        LoginRelMain = findViewById(R.id.LoginRelMain);
        loginBtn = findViewById(R.id.getstarted);
        progressBar = findViewById(R.id.progress);
        firebaseAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.create:
                GoOnSignUp();
                break;
            case R.id.getstarted:
                GotoLogin();
                break;
        }
    }

    private void GotoLogin() {
        String email = Lemail.getText().toString();
        String pass = Lpass.getText().toString();

        if(email.isEmpty()){
            showSnackBar(LoginRelMain,getString(R.string.please_enter_email_id));
            return;
        }
        else if(!Utility.isvalidEmail(email)) {
            showSnackBar(LoginRelMain, getString(R.string.please_enter_valid_email));
            return;
        }else if(pass.isEmpty()){
            showSnackBar(LoginRelMain,getString(R.string.please_enter_password));
            return;
        }else{
            progressBar.setVisibility(View.VISIBLE);
            setDisableScreen();
            GoToLoginFromFireBase(email,pass);
        }
    }

    private void GoToLoginFromFireBase(String email, String pass) {
          firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(Task<AuthResult> task) {
                  if(task.isSuccessful()){
                      showSnackBar(LoginRelMain,getString(R.string.login_success));
                      progressBar.setVisibility(View.GONE);
                      Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
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
                  }else{
                      String str = task.getException().getLocalizedMessage();
                      showSnackBar(LoginRelMain,""+str);
                      progressBar.setVisibility(View.GONE);
                      setEnableScreen();
                  }
              }
          });
    }

    private void setDisableScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void setEnableScreen(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void GoOnSignUp() {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}