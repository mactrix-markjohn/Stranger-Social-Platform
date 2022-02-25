package com.mactrixapp.www.stranger;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.FindStrangerAdapter;
import com.mactrixapp.www.stranger.Adapters.InterestSearchAdapter;
import com.mactrixapp.www.stranger.AsyncTaskPack.AsyncClass;
import com.mactrixapp.www.stranger.Model.IsListContain;
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentInterestSearch extends Fragment {


    Context context;
    private EditText interestsearchentry;
    private RelativeLayout interestsearchbutton;
    private RelativeLayout interestemptylayout;
    private RelativeLayout interestsearchresult;
    private TextView interestsearchcount;
    private ListView interestsearchlist;
    private IsListContain contain;
    private DatabaseReference userReference;
    private FirebaseUser currentuser;
    private ArrayList<User> strangerslist;
    private double latitude;
    private double longitude;
    private RelativeLayout progresslay;
    private LocationRequest locationRequest;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_interest_search,container,false);
        interestsearchentry =(EditText) view.findViewById(R.id.interestsearchentry);
        interestsearchbutton = (RelativeLayout)view.findViewById(R.id.interestsearchbotton);
        interestemptylayout = (RelativeLayout)view.findViewById(R.id.interestemptylayout);
        interestsearchresult = (RelativeLayout)view.findViewById(R.id.interstresultlayout);
        interestsearchcount = (TextView)view.findViewById(R.id.interstresultcount);
        interestsearchlist = (ListView)view.findViewById(R.id.interestsearchlist);
        progresslay = (RelativeLayout)view.findViewById(R.id.progresslay);








        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // initialize the Firebase and other important instances
        contain = new IsListContain();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user));


        //userlocation(); // user location
      // new Thread(this::userlocation).start();

       new AsyncClass(this::userlocation).execute();


        interestsearchbutton.setOnClickListener((isbv) ->{

            // Interest search button click is implemented here
            strangerslist = new ArrayList<>();
            progresslay.setVisibility(View.VISIBLE);
            wildsearch();


        });

    }

    public void wildsearch(){

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get all the User on the Platform
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);

                    if(user != null){

                        String info = user.getInterest() != null ? user.getInterest().toLowerCase() : user.getInterest();
                        String entry = !interestsearchentry.getText().toString().isEmpty() ? interestsearchentry.getText().toString().toLowerCase(): interestsearchentry.getText().toString();

                        if (info != null && info.contains(entry)) {

                            // TODO: In this Space I will get the current User's location and check if the stranger location
                            // TODO: is within 3km radius around the user


                                float[] result = new float[2];
                                Location.distanceBetween(latitude, longitude, user.getLatitude(), user.getLongitude(), result);

                                if (result[0] <= 3000) {

                                    if (!contain.isUserContain(strangerslist,user)) {
                                        strangerslist.add(user);

                                    }

                                }

                        }


                    }



                }

                InterestSearchAdapter interestSearchAdapter = new InterestSearchAdapter(context,strangerslist);
                interestsearchlist.setAdapter(interestSearchAdapter);
                interestsearchcount.setText(String.valueOf(strangerslist.size()));
                if(interestsearchlist.getCount() != 0){

                    int c = interestsearchlist.getCount();
                    progresslay.setVisibility(View.GONE);
                    interestemptylayout.setVisibility(View.GONE);
                    interestsearchresult.setVisibility(View.VISIBLE);

                    try {
                        Snackbar.make(progresslay,"We found "+c+" Strangers",Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (Exception e) {
                        String a = "";
                    }


                }else{
                    progresslay.setVisibility(View.GONE);
                    interestemptylayout.setVisibility(View.VISIBLE);
                    interestsearchresult.setVisibility(View.GONE);

                    try {
                        Snackbar.make(progresslay,"Sorry, No Stranger was found, try again",Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (Exception e) {
                        String a = "";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void userlocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // if the Permission is not granted

            return;
        }

        LocationRequest locationRequestb = new LocationRequest();
        locationRequestb.setInterval(10000);
        locationRequestb.setFastestInterval(5000);
        locationRequestb.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationRequest locationRequestHigh = new LocationRequest();
        locationRequestHigh.setInterval(10000);
        locationRequestHigh.setFastestInterval(5000);
        locationRequestHigh.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequestb)
                .addLocationRequest(locationRequestHigh);
        builder.setNeedBle(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());
        result.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                LocationSettingsStates states = locationSettingsResponse.getLocationSettingsStates();
                if(states.isGpsUsable()){

                    locationRequest = new LocationRequest();
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(5000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                }else{

                    locationRequest = new LocationRequest();
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(5000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }

                addLocationservice();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                locationRequest = new LocationRequest();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                addLocationservice();
            }
        });








    }

    public void addLocationservice(){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    // Here we collect the current user location every 10 seconds
                    Location location = locationResult.getLastLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();


                }
            }, Looper.myLooper());
        }
    }
}
