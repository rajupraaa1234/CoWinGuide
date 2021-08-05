package com.example.cowinguide.View.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cowinguide.Home.HomeActivity;
import com.example.cowinguide.NetWork.NetworkHandler;
import com.example.cowinguide.R;
import com.example.cowinguide.Utility.SessionManager.Session.Sessionmanager;
import com.example.cowinguide.Utility.Utility;
import com.example.cowinguide.View.Login.SignUp.SignUpActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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
    Button mgoogle;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private String TAG = "MYLoginActivity";
    private int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hideStatusBar();
        setContentView(R.layout.activity_login);
        init();
        CreateRequest();
    }

    private void CreateRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String name = user.getDisplayName();
                            Sessionmanager.get().SetSocialLogin(true);
                            Sessionmanager.get().setSessionName(name);
                            Log.i("MySocialLogin"," "  + name);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                          //  updateUI(user);
                        } else {
                            String str = task.getException().getLocalizedMessage();
                            showSnackBar(LoginRelMain,""+str);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // updateUI(null);
                        }
                    }
                });
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
        mgoogle = findViewById(R.id.googleBtn);
        mgoogle.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
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
            case R.id.googleBtn:
                signIn();
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
            if(NetworkHandler.isConnected(this)){
                progressBar.setVisibility(View.VISIBLE);
                setDisableScreen();
                GoToLoginFromFireBase(email,pass);
            }else{
                showSnackBar(LoginRelMain,getString(R.string.internet_not_connected));
            }

        }
    }

    private void GoToLoginFromFireBase(String email, String pass) {
          firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(Task<AuthResult> task) {
                  if(task.isSuccessful()){
                      //Log.i("MYMAnualLogin",""+ mAuth.getCurrentUser().getDisplayName());
                     // Sessionmanager.get().setSecondName(""+mAuth.getCurrentUser().getDisplayName());
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