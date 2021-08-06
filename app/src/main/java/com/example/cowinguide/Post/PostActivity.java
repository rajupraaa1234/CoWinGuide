package com.example.cowinguide.Post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cowinguide.Adapter.CallLogPojo;
import com.example.cowinguide.Home.HomeActivity;
import com.example.cowinguide.NetWork.NetworkHandler;
import com.example.cowinguide.R;
import com.example.cowinguide.Utility.AppConstant;
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
    EditText service_type;
    TextView callType;
    TextView date;
    TextView phone;
    Button currentLocationBtn;
    Button continue_button;
    ProgressBar progress;
    LinearLayout postLin;
    ImageView backArrow;

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
        isTypedAddress = false;
        getIntentData();
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
                    DemandPerMissionForMap();
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
        String ServiceType = service_type.getText().toString().trim();
        String CallType = callType.getText().toString().trim();
        String eDate = date.getText().toString().trim();
        String ePhone = phone.getText().toString().trim();

        if(first_name.isEmpty() || last_name.isEmpty() || Location.isEmpty() || ServiceType.isEmpty() || CallType.isEmpty() || eDate.isEmpty() || ePhone.isEmpty()){
            showSnackBar(postLin,getString(R.string.please_enter_all_details));
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

    private void DemandPerMissionForMap() {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getMyLocation();
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location){
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        //for find direction and cordinate w.r.t earth
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            myLat = location.getLatitude();
                            myLong = location.getLongitude();
                            getMyPlace(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here...");
                            UpdateUi();
                            mymarkerOptions = markerOptions;
                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {

                                }
                            });
                        }
                    }
                });
            }

        });
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
        txtLocation.setEnabled(false);
        txtLocation.setFocusable(false);
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
        String ServiceType = service_type.getText().toString().trim();
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
}