package com.example.cowinguide.Home.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cowinguide.Adapter.ConsumerApdater;
import com.example.cowinguide.Adapter.CustomerServicePojo;
import com.example.cowinguide.NetWork.NetworkHandler;
import com.example.cowinguide.R;
import com.example.cowinguide.Utility.AppConstant;
import com.example.cowinguide.Utility.GPSTracker;
import com.example.cowinguide.Utility.Utility;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.cowinguide.Utility.Utility.showSnackBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsumerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsumerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConsumerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsumerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsumerFragment newInstance(String param1, String param2) {
        ConsumerFragment fragment = new ConsumerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView recyclerView;
    String TAG = "ConsumerFragment";
    ProgressBar progressBar;
    ArrayList<CustomerServicePojo> arr = new ArrayList<>();
    ConsumerApdater adapter;
    FrameLayout Cframlayout;
    TextView nodata;
    EditText searchBox;
    ImageView crossImg;
    Double myLat=0.0;
    Double myLong=0.0;
    ImageView CLocation;
    MarkerOptions mymarkerOptions;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    Boolean isTypedLocation = false;
    String CityName= "";
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(isTypedLocation==false) {
                if (searchBox.getText().toString().isEmpty()) {
                    crossImg.setImageResource(R.drawable.ic_cross_fill);
                    getDataFromFireBase();
                } else{
                    crossImg.setImageResource(R.drawable.ic_baseline_done_24);
                }
            }else{
                crossImg.setImageResource(R.drawable.ic_cross_fill);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_consumer, container, false);
        supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.Cmyfragment);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.CProgress);
        nodata = view.findViewById(R.id.cNoData);
        Cframlayout = view.findViewById(R.id.Cframlayout);

        searchBox = view.findViewById(R.id.name_search_et);
        crossImg = view.findViewById(R.id.cross_icon);
        CLocation = view.findViewById(R.id.current_add);
        searchBox.addTextChangedListener(textWatcher);
        setAdapter();
        if(NetworkHandler.isConnected()){
            getDataFromFireBase();
        }else{
            showSnackBar(Cframlayout,getString(R.string.internet_problem));
        }

        crossImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("OnCrossClicked : ","Clicked");
                if(isTypedLocation == false && !searchBox.getText().toString().isEmpty()) {
                      String add = searchBox.getText().toString();
                    Log.i("OnCrossClicked : ","IfClicked");
                      LatLng latLng = Utility.getLocationFromAddress(requireActivity(),add);
                      if(latLng!=null) {
                          myLat = latLng.longitude;
                          myLong = latLng.longitude;
                          foundLatLong();
                      }else{
                          showSnackBar(Cframlayout,getString(R.string.address_not_valid));
                      }
                }else{
                    if(!searchBox.getText().toString().isEmpty()) {
                        Log.i("OnCrossClicked : ", "ElseClicked");
                        searchBox.setText("");
                        isTypedLocation = false;
                        searchBox.setFocusable(true);
                        searchBox.setFocusableInTouchMode(true);
                        searchBox.setClickable(true);
                        getDataFromFireBase();
                    }
                }

            }
        });

        CLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("OnCrossClicked : ","GoogleIfClicked");
                if(NetworkHandler.isConnected()){
                    Log.i("OnCrossClicked : ","GoogleIfClicked");
                    GoogleMapCall();
                }else{
                    Log.i("OnCrossClicked : ","GoogleElseClicked");
                    showSnackBar(Cframlayout,getString(R.string.internet_problem));
                }
            }
        });
    }

    private void GoogleMapCall() {
        getCurrentLocationUsingGPS();
    }

    private void UpdateUi() {
        isTypedLocation = true;
        progressBar.setVisibility(View.VISIBLE);
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
        searchBox.setText(CityName);
        searchBox.setFocusable(false);
        searchBox.setFocusableInTouchMode(false);
        searchBox.setClickable(false);
        progressBar.setVisibility(View.GONE);
    }

    public void setAdapter(){
        adapter = new ConsumerApdater(requireActivity(),arr);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }
    private void getDataFromFireBase(){
        progressBar.setVisibility(View.VISIBLE);
        int size = arr.size();
        arr.clear();
        adapter.notifyItemRangeRemoved(0,size);
        //arr = new ArrayList<CustomerServicePojo>();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(AppConstant.Collections).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            progressBar.setVisibility(View.GONE);
                            nodata.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            return;
                        } else {
                            nodata.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            List<CustomerServicePojo> types = documentSnapshots.toObjects(CustomerServicePojo.class);
                            Log.i("ListSize"," " +types.size());
                            arr.clear();
                            arr.addAll(types);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    }})
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            progressBar.setVisibility(View.GONE);
                            showSnackBar(Cframlayout,getString(R.string.error));
                        }
                    });

    }



    private void getMyPlace(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);
        CityName = cityName;
        Log.i("CMyCityName"," " +cityName + " state : " + stateName);
    }


    private void foundLatLong(){
        progressBar.setVisibility(View.VISIBLE);
        if(arr!=null || arr.size()>0){
            ArrayList<CustomerServicePojo> serachArr = new ArrayList<CustomerServicePojo>();
            for(int i=0;i< arr.size();i++){
                String lat1 = arr.get(i).getLatitue();
                String long1 = arr.get(i).getLongitute();
                double dist = Utility.getDistance(Double.parseDouble(lat1),Double.parseDouble(long1),myLat,myLong);
                Log.i("MyDistance"," " + dist);
                if(dist<=10000){
                    serachArr.add(arr.get(i));
                }
            }
            if(serachArr.size()==0){
                nodata.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else {
                arr = serachArr;
                adapter = new ConsumerApdater(requireActivity(), serachArr);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Log.i("MyLocation", " " + myLat + " " + myLong);
                nodata.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public void getCurrentLocationUsingGPS() {
        if (checkPermissionLocation()) {
            Location locationAndCity = getLocationAndCity(requireActivity(), requireActivity());
            if (locationAndCity != null) {
                myLat = locationAndCity.getLatitude();
                myLong = locationAndCity.getLongitude();
                getMyPlace(myLat, myLong);
                UpdateUi();
                foundLatLong();
            }else{
                showSnackBar(Cframlayout,getString(R.string.location_failed));
            }
        } else {
            reqForPermissionLocation();
        }
    }

    private boolean checkPermissionLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
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