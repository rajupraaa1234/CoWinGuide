package com.example.cowinguide.Home.Fragment;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cowinguide.Adapter.ConsumerApdater;
import com.example.cowinguide.Adapter.CustomerServicePojo;
import com.example.cowinguide.NetWork.NetworkHandler;
import com.example.cowinguide.R;
import com.example.cowinguide.Utility.AppConstant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
        return inflater.inflate(R.layout.fragment_consumer, container, false);
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
        if(NetworkHandler.isConnected()){
            getDataFromFireBase();
        }else{
            showSnackBar(Cframlayout,getString(R.string.internet_problem));
        }

    }


    private void getDataFromFireBase(){
        progressBar.setVisibility(View.VISIBLE);
        arr = new ArrayList<CustomerServicePojo>();
        adapter = new ConsumerApdater(requireActivity(),arr);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
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

    private double getDistance(Double lat1,Double lang1,Double lat2,Double long2){
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