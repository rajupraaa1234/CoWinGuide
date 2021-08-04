package com.example.cowinguide.View.Login.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cowinguide.NetWork.NetworkHandler;
import com.example.cowinguide.R;
import com.example.cowinguide.Utility.Utility;
import com.example.cowinguide.View.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.cowinguide.Utility.Utility.isValidMobile;
import static com.example.cowinguide.Utility.Utility.showSnackBar;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    EditText username;
    EditText pass;
    EditText cpass;
    EditText email;
    EditText phone;
    TextView SignUp;
    ProgressBar progress;
    TextView login;
    FirebaseAuth auth;
    View mainView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_sign_up);
        init();
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    private void init(){
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        username = findViewById(R.id.username);
        pass = findViewById(R.id.etpassword);
        phone = findViewById(R.id.Etphone);
        cpass = findViewById(R.id.cpassword);
        SignUp = findViewById(R.id.signUpbtn);
        email = findViewById(R.id.Etemail);
        SignUp.setOnClickListener(this);
        progress = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        mainView = findViewById(R.id.mainRel);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login:
                GoTOLoginPage();
                break;
            case R.id.signUpbtn:
                ContinueClick();
                break;
        }
    }

    private void ContinueClick() {
        if(username.getText().toString().isEmpty()){
            showSnackBar(mainView,getString(R.string.please_enter_username));
            return;
        }else if(username.getText().toString().length()<3){
            showSnackBar(mainView,getString(R.string.nusername_length));
            return;
        }else if(phone.getText().toString().isEmpty()){
            showSnackBar(mainView,getString(R.string.please_enter_phone_numer));
            return;
        }else if(!isValidMobile(phone.getText().toString())){
            showSnackBar(mainView,getString(R.string.please_enter_valid_phone_number));
            return;
        }else if(email.getText().toString().isEmpty()){
            showSnackBar(mainView,getString(R.string.please_enter_email_id));
            return;
        }
        else if(!Utility.isvalidEmail(email.getText().toString())){
            showSnackBar(mainView,getString(R.string.please_enter_valid_email));
            return;
        }else if(pass.getText().toString().isEmpty()){
            showSnackBar(mainView,getString(R.string.please_enter_password));
            return;
        }else if(pass.getText().toString().length()<6){
            showSnackBar(mainView,getString(R.string.pass_length));
            return;
        }else if(cpass.getText().toString().isEmpty()){
            showSnackBar(mainView,getString(R.string.please_enter_c_pass));
            return;
        }else if(!pass.getText().toString().equals(cpass.getText().toString())){
            showSnackBar(mainView,getString(R.string.pass_mismatch));
            return;
        }else{
            if(NetworkHandler.isConnected(this)){
                setDisableScreen();
                progress.setVisibility(View.VISIBLE);
                goToSignUp();
            }else{
                showSnackBar(mainView,getString(R.string.internet_not_connected));
            }
        }
    }

    private void goToSignUp() {
         auth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(Task<AuthResult> task) {
                 if(task.isSuccessful()){
                     showSnackBar(mainView,getString(R.string.user_created));
                     progress.setVisibility(View.GONE);
                     setEnableScreen();
                     Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
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
                       showSnackBar(mainView,""+str);
                     progress.setVisibility(View.GONE);
                     setEnableScreen();
                 }
             }
         });
    }

    private void GoTOLoginPage(){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
    }

    private void setDisableScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void setEnableScreen(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}