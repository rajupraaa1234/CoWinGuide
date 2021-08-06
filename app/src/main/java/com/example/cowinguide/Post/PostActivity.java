package com.example.cowinguide.Post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cowinguide.Adapter.CallLogPojo;
import com.example.cowinguide.Home.HomeActivity;
import com.example.cowinguide.NetWork.NetworkHandler;
import com.example.cowinguide.R;
import com.example.cowinguide.Utility.AppConstant;
import com.example.cowinguide.Utility.GPSTracker;
import com.example.cowinguide.Utility.SessionManager.Session.Sessionmanager;
import com.example.cowinguide.Utility.Utility;
import com.example.cowinguide.View.Login.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.cowinguide.Utility.Utility.showSnackBar;

public class PostActivity extends AppCompatActivity {

    EditText fname;
    EditText sname;
    EditText txtLocation;
    Spinner service_type;
    TextView callType;
    TextView date;
    TextView phone;
    Button currentLocationBtn;
    Button continue_button;
    ProgressBar progress;
    LinearLayout postLin;
    ImageView backArrow;
    String ServiceType = "";
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    MarkerOptions mymarkerOptions;
    Double myLat = 0.0;
    Double myLong = 0.0;
    private String MycityName="";
    private boolean isTypedAddress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        init();
        setUpSpinner();
        isTypedAddress = false;
        getIntentData();
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> adp3 = ArrayAdapter.createFromResource(this,
                R.array.ServicesName, android.R.layout.simple_list_item_1);

        adp3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        service_type.setAdapter(adp3);
        service_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                if(position!=0){
                    ServiceType = service_type.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void init(){
        fname = findViewById(R.id.first_name_et);
        sname = findViewById(R.id.last_name_et);
        txtLocation = findViewById(R.id.location);
        service_type = findViewById(R.id.service_type);
        callType = findViewById(R.id.Call_type);
        date = findViewById(R.id.Call_date);
        phone = findViewById(R.id.post_phone);
        continue_button = findViewById(R.id.continue_button);
        currentLocationBtn = findViewById(R.id.getLocation);
        progress = findViewById(R.id.progress);
        postLin = findViewById(R.id.postLin);
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkHandler.isConnected()){
                    LatLng latLng = Utility.getLocationFromAddress(PostActivity.this,txtLocation.getText().toString().trim());

                    if(latLng==null){
                        showSnackBar(postLin,getString(R.string.address_not_valid));
                    }else{
                        myLat = latLng.latitude;
                        myLong = latLng.longitude;
                        if(myLat!=null && myLong!=null){
                            UploadDataOnDB();
                        }else{
                            showSnackBar(postLin,getString(R.string.your_current_location));
                        }
                    }
                }else{
                    showSnackBar(postLin,getString(R.string.internet_problem));
                }
            }
        });
        currentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkHandler.isConnected()){
                    getCurrentLocationUsingGPS();
                }else{
                    showSnackBar(postLin,getString(R.string.internet_problem));
                }
            }
        });
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myfragment);
        client = LocationServices.getFusedLocationProviderClient(this);
    }

    private void UploadDataOnDB() {
        String first_name = fname.getText().toString().trim();
        String last_name = sname.getText().toString().trim();
        String Location = txtLocation.getText().toString().trim();
        String CallType = callType.getText().toString().trim();
        String eDate = date.getText().toString().trim();
        String ePhone = phone.getText().toString().trim();

        if(first_name.isEmpty() || last_name.isEmpty() || Location.isEmpty() || ServiceType.isEmpty() || CallType.isEmpty() || eDate.isEmpty() || ePhone.isEmpty()){
            if(first_name.isEmpty()){
                showSnackBar(postLin,getString(R.string.please_enter_first_name));
            }else if(first_name.length()<3){
                showSnackBar(postLin,getString(R.string.first_name_length));
            }else if(last_name.isEmpty()){
                showSnackBar(postLin,getString(R.string.please_enter_last_name));
            }else if(last_name.length()<3){
                showSnackBar(postLin,getString(R.string.last_name_length));
            }else if(ServiceType.isEmpty()){
                showSnackBar(postLin,getString(R.string.services_type));
            }
        }else{
            hitFireBaseDataBase();
        }
    }


    private void getIntentData() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null) {
            CallLogPojo data = (CallLogPojo) bundle.getSerializable(AppConstant.DATA);
            String name = Sessionmanager.get().getSessionName();
            if (name != null && !name.isEmpty()) {
                String fullName[] = name.split(" ");
                if (fullName.length >= 1) {
                    fname.setText(fullName[0]);
                }
                if (fullName.length >= 2) {
                    sname.setText(fullName[1]);
                }
            }
            callType.setText("" + data.getType());
            date.setText("" + data.getDate());
            phone.setText(data.getNumber());
        }
    }

    private void UpdateUi() {
        isTypedAddress = true;
        progress.setVisibility(View.VISIBLE);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        txtLocation.setText(MycityName);
        txtLocation.setFocusable(false);
        txtLocation.setFocusableInTouchMode(false);
        txtLocation.setClickable(false);
        progress.setVisibility(View.GONE);
    }

    private void getMyPlace(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);
        MycityName = cityName;
        Log.i("MyCityName"," " +cityName + " state : " + stateName);
    }

    private void hitFireBaseDataBase(){
        progress.setVisibility(View.VISIBLE);
        String first_name = fname.getText().toString().trim();
        String last_name = sname.getText().toString().trim();
        String Location = txtLocation.getText().toString().trim();
        String CallType = callType.getText().toString().trim();
        String eDate = date.getText().toString().trim();
        String ePhone = phone.getText().toString().trim();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("name",first_name+last_name);
        map.put("number",ePhone);
        map.put("Calltype",CallType);
        map.put("location",Location);
        map.put("date",eDate);
        map.put("ServiceType",ServiceType);
        map.put("Latitue",String.valueOf(myLat));
        map.put("Longitute",String.valueOf(myLong));

        firebaseFirestore.collection(AppConstant.Collections)
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                         progress.setVisibility(View.GONE);
                        showSnackBar(postLin,getString(R.string.post_successfully));
                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        progress.setVisibility(View.GONE);
                        showSnackBar(postLin,getString(R.string.poat_faild));
                    }
                });
    }


    public void getCurrentLocationUsingGPS() {
        if (checkPermissionLocation()) {
            Location locationAndCity = getLocationAndCity(this, this);
            if (locationAndCity != null) {
                myLat = locationAndCity.getLatitude();
                myLong = locationAndCity.getLongitude();
                getMyPlace(myLat, myLong);
                UpdateUi();
            }else{
                showSnackBar(postLin,getString(R.string.location_failed));
            }
        } else {
            reqForPermissionLocation();
        }
    }

    private boolean checkPermissionLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            return ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void reqForPermissionLocation() {
        requestPermissions(new String[]{Manifest.permission
                .ACCESS_FINE_LOCATION}, 44);
    }

    public Location getLocationAndCity(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            GPSTracker gpsTracker = new GPSTracker(activity);
            Location location = new Location("");
            location.setLatitude(gpsTracker.getLatitude());
            location.setLongitude(gpsTracker.getLongitude());
            Log.i("MyGPSTracker " , " " + gpsTracker.getLatitude() + " " + gpsTracker.getLongitude());
            return location;
        }
        return null;
    }
}