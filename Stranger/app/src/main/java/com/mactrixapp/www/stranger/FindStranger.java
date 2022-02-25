package com.mactrixapp.www.stranger;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.maps.model.LatLng;
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
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class FindStranger extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Switch nameswitch;
    private EditText nameentry;
    private Switch professionswitch;
    private EditText professionentry;
    private Switch instituteswitch;
    private EditText instituteentry;
    private Switch interestswitch;
    private TextView interesteentry;
    private Switch workstateswitch;
    private TextView workstateentry;
    private Switch genderswitch;
    private TextView genderentry;
    private Switch locationswitch;
    private EditText locationentry;
    private BottomSheetBehavior<View> sheetBehavior;
    private SharedPref searchtypePref;
    private String name;
    private String username;
    private String profession;
    private String institute;
    private String interest;
    private String workstate;
    private String gender;
    private String location;
    private ArrayList<String> stringkey;
    private HashMap<String, TextView> entryMap;
    private ArrayList<User> strangersresult;
    private DatabaseReference userReference;
    private FirebaseUser currentUser;
    private String lat;
    private String lng;
    private ListView findlist;
    private RelativeLayout progresslay;
    private double latitude;
    private double longitude;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_region);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userlocation();

        // initialize Sharedpref and other Collection API and also Firebase Database and FirebaseUser
        searchtypePref = new SharedPref(this, getString(R.string.searchtype));
        stringkey = new ArrayList<>();
        entryMap = new HashMap<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user));

        // initialize the views
        progresslay = (RelativeLayout) findViewById(R.id.progresslay);
        nameswitch = (Switch) findViewById(R.id.nameswitch);
        nameentry = (EditText) findViewById(R.id.nameentry);
        name = "name";
        username = "username";
        entryMap.put(name, nameentry);

        professionswitch = (Switch) findViewById(R.id.professioswitch);
        professionentry = (EditText) findViewById(R.id.professionentry);
        profession = "profession";
        entryMap.put(profession, professionentry);


        instituteswitch = (Switch) findViewById(R.id.instituteswitch);
        instituteentry = (EditText) findViewById(R.id.instituteentry);
        institute = "institute";
        entryMap.put(institute, instituteentry);

        interestswitch = (Switch) findViewById(R.id.interestswitch);
        interesteentry = (EditText) findViewById(R.id.interestentry);
        interest = "interest";
        entryMap.put(interest, interesteentry);

        workstateswitch = (Switch) findViewById(R.id.workstateswitch);
        workstateentry = (TextView) findViewById(R.id.workstateentry);
        workstate = "workstate";
        entryMap.put(workstate, workstateentry);

        genderswitch = (Switch) findViewById(R.id.genderswitch);
        genderentry = (TextView) findViewById(R.id.genderentry);
        gender = "gender";
        entryMap.put(gender, genderentry);

        locationswitch = (Switch) findViewById(R.id.locationswitch);
        locationentry = (EditText) findViewById(R.id.locationentry);
        location = "location";
        lat = "latitude";
        lng = "longitude";
        entryMap.put(location, locationentry);


        // The Switch implementation
        nameswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    stringkey.add(name);
                    nameentry.setEnabled(true);

                } else {
                    stringkey.remove(name);
                    nameentry.setEnabled(false);

                }


            }
        });

        professionswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    stringkey.add(profession);
                    professionentry.setEnabled(true);
                } else {
                    stringkey.remove(profession);
                    professionentry.setEnabled(false);

                }

            }
        });

        instituteswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    stringkey.add(institute);
                    instituteentry.setEnabled(true);
                } else {
                    stringkey.remove(institute);
                    instituteentry.setEnabled(false);
                }

            }
        });

        interestswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    stringkey.add(interest);
                    interesteentry.setEnabled(true);

                } else {

                    stringkey.remove(interest);
                    interesteentry.setEnabled(false);
                }

            }
        });

        workstateswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    stringkey.add(workstate);
                    workstateentry.setEnabled(true);
                } else {

                    stringkey.remove(workstate);
                    workstateentry.setEnabled(false);
                }

            }
        });

        genderswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    stringkey.add(gender);
                    genderentry.setEnabled(true);
                } else {

                    stringkey.remove(gender);
                    genderentry.setEnabled(false);
                }

            }
        });

        locationswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(FindStranger.this);
                    builder.setTitle("Location choice");
                    builder.setIcon(R.drawable.map_marker);
                    builder.setMultiChoiceItems(new String[]{"All Location", "Particular Location"}, new boolean[]{false, false}, (dialog, which, isCheckeda) -> {
                        switch (which) {
                            case 0:

                                stringkey.add(location);
                                locationentry.setText(getString(R.string.alllocation));
                                locationentry.setEnabled(false);
                                dialog.dismiss();
                                break;


                            case 1:

                                stringkey.add(location);
                                locationentry.setEnabled(true);
                                dialog.dismiss();
                                break;
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create().show();


                } else {

                    stringkey.remove(location);
                    locationentry.setEnabled(false);
                }

            }
        });


        // Initialize the Bottomsheet view

        View viewsheet = findViewById(R.id.findstrangersheet);
        sheetBehavior = BottomSheetBehavior.from(viewsheet);
        findlist = (ListView) findViewById(R.id.findstrangerlistr);

        findlist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }


                v.onTouchEvent(event);
                return true;
            }
        });

        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.setPeekHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }else if(newState == BottomSheetBehavior.STATE_DRAGGING){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }


            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

       /* NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
    }

    public void userlocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // if the Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);
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

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            LocationServices.getFusedLocationProviderClient(FindStranger.this).requestLocationUpdates(locationRequest, new LocationCallback() {

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


    public void folddown(View view) {

        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    public void findstranger(View view) {

        strangersresult = new ArrayList<>();
        progresslay.setVisibility(View.VISIBLE);

        if (searchtypePref.getText().equalsIgnoreCase(getString(R.string.narrowdown))) {
            // TODO: Implement the Narrow down search here
            narrowdown();


        } else if(searchtypePref.getText().equalsIgnoreCase(getString(R.string.wildsearch))) {


            // TODO: Implement the Wild search search
            wildsearch();




        }else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Search Pattern");
            builder.setIcon(R.drawable.searchiconblue);
            builder.setMultiChoiceItems(new String[]{"Narrow Down Search","Wild Search" }, new boolean[]{false, false}, (dialog, which, isChecked) -> {
                    switch (which) {
                        case 0:

                            // TODO: Implement the Narrow down search here
                            narrowdown();
                            searchtypePref.setText(getString(R.string.narrowdown));
                            dialog.dismiss();
                            break;
                        case 1:
                            // TODO: Implement the Wild search search
                            wildsearch();
                            searchtypePref.setText(getString(R.string.wildsearch));
                            dialog.dismiss();
                            break;
                    }
                });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.create().show();


        }








    }

    public void narrowdown(){


        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get all the User on the Platform
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);
                    HashMap<String,Object> infomap =new HashMap<>();
                    if(user != null){



                        // Store the User details in a Map
                        String[] usernames = {user.getFullname(), user.getUsername()};
                        String[] userlocations = {String.valueOf(user.getLatitude()), String.valueOf(user.getLongitude()),user.getAddress(),user.getOrigin(),user.getCountry(),user.getCurrentaddress()};
                        double lati = user.getLatitude();
                        double lngi = user.getLongitude();


                        infomap.put(name,usernames);
                        infomap.put(profession,user.getProfession());
                        infomap.put(institute,user.getInstitute());
                        infomap.put(interest,user.getInterest());
                        infomap.put(gender,user.getGender());
                        infomap.put(workstate,user.getWorkstate());
                        infomap.put(location,userlocations);

                        // This checks with entry is enabled
                        for(int i=0;  i < stringkey.size();  i++){

                            String key = stringkey.get(i);


                            // Get the content of the infomap

                            if(key.equalsIgnoreCase(name)){  // the infomap is Username


                                String[] names = (String[]) infomap.get(key);
                                String fullname = names[0] != null ? names[0].toLowerCase():"unknown";
                                String username = names[1] != null ? names[1].toLowerCase():"unknown";

                                String searchname = entryMap.get(key).getText().toString().toLowerCase();

                                if(!((fullname.contains(searchname))||(username.contains(searchname)))){ break;}
                                if (i== stringkey.size()-1 && ((fullname.contains(searchname))||(username.contains(searchname)))){

                                    // TODO: In this Space I will get the current User's location and check if the stranger location
                                    // TODO: is within 20km radius around the user

                                    if(!locationswitch.isChecked()){
                                        float[] result = new float[2];
                                        Location.distanceBetween(latitude,longitude,lati,lngi,result);


                                        if(result[0] <= 3000){

                                            if(!isUserContain(strangersresult,user)){
                                                strangersresult.add(user);

                                            }

                                        }

                                    }else{

                                        if(!isUserContain(strangersresult,user)){
                                            strangersresult.add(user);

                                        }
                                    }





                                }
                            }else if(key.equalsIgnoreCase(location)){    // the infomap is Location latlng

                                String[] locations = (String[]) infomap.get(key);
                                double latit = Double.parseDouble(locations[0]);
                                double lngit = Double.parseDouble(locations[1]);
                                String address = locations[2] !=null ? locations[2].toLowerCase(): "unknown";
                                String origin =locations[3] !=null? locations[3].toLowerCase():"unknown";
                                String country =locations[4] != null? locations[4].toLowerCase():"unknown";
                                String currentaddress = locations[5] != null? locations[5].toLowerCase():"Unknown";

                                String searchaddress = entryMap.get(key).getText().toString().toLowerCase();


                                if(searchaddress.toLowerCase().equalsIgnoreCase(getString(R.string.alllocation).toLowerCase())){

                                    // This simple add the user since all location is considerd which is the same as no location
                                    if(!isUserContain(strangersresult,user)){
                                        strangersresult.add(user);

                                    }

                                }else{

                                    if(!((address.contains(searchaddress))||(origin.contains(searchaddress))||(country.contains(searchaddress))||(currentaddress.contains(searchaddress)))){break;}
                                    if((i == stringkey.size() -1) && ((address.contains(searchaddress))||(origin.contains(searchaddress))||(country.contains(searchaddress))||(currentaddress.contains(searchaddress)))){

                                        if(!isUserContain(strangersresult,user)){
                                            strangersresult.add(user);

                                        }
                                    }


                                }


                            }else{    // the infomap is String


                                String info = String.valueOf(infomap.get(key)).toLowerCase();
                                String entry = entryMap.get(key).getText().toString().toLowerCase();

                                if(!info.contains(entry)){ break; }
                                if(i == stringkey.size()-1 && info.contains(entry)){

                                    // TODO: In this Space I will get the current User's location and check if the stranger location
                                    // TODO: is within 20km radius around the user

                                    if(!locationswitch.isChecked()){
                                        float[] result = new float[2];
                                        Location.distanceBetween(latitude,longitude,lati,lngi,result);
                                        //Toast.makeText(FindStranger.this, "Location Distance: "+result[0], Toast.LENGTH_SHORT).show();


                                        if(result[0] <= 3000){

                                            if(!isUserContain(strangersresult,user)){
                                                strangersresult.add(user);

                                            }

                                        }

                                    }else{

                                        if(!isUserContain(strangersresult,user)){
                                            strangersresult.add(user);

                                        }
                                    }

                                }
                            }
                        }
                    }
                }

                FindStrangerAdapter findStrangerAdapter = new FindStrangerAdapter(FindStranger.this,strangersresult);
                findlist.setAdapter(findStrangerAdapter);
                if(findlist.getCount() != 0){

                    progresslay.setVisibility(View.GONE);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }else{

                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    progresslay.setVisibility(View.GONE);
                    Snackbar.make(progresslay,"Sorry, No Stranger was found, try again",Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void wildsearch(){

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get all the User on the Platform
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);
                    HashMap<String,Object> infomap =new HashMap<>();
                    if(user != null){


                        // Store the User details in a Map
                        String[] usernames = {user.getFullname(), user.getUsername()};
                        String[] userlocations = {String.valueOf(user.getLatitude()), String.valueOf(user.getLongitude()),user.getAddress(),user.getOrigin(),user.getCountry(),user.getCurrentaddress()};
                        double lati = user.getLatitude();
                        double lngi = user.getLongitude();

                        infomap.put(name,usernames);
                        infomap.put(profession,user.getProfession());
                        infomap.put(institute,user.getInstitute());
                        infomap.put(interest,user.getInterest());
                        infomap.put(gender,user.getGender());
                        infomap.put(workstate,user.getWorkstate());
                        infomap.put(location,userlocations);


                        // This check which entry is enabled
                        for(int i=0;  i < stringkey.size();  i++){

                            String key = stringkey.get(i);


                            // Get the content of the infomap

                            if(key.equalsIgnoreCase(name)){  // the infomap is Username


                                String[] names = (String[]) infomap.get(key);
                                String fullname = names[0] != null ? names[0].toLowerCase() : "unknown";
                                String username = names[1] != null ? names[1].toLowerCase() : "unknown";

                                String searchname = entryMap.get(key).getText().toString().toLowerCase();

                                if((fullname.contains(searchname))||(username.contains(searchname))){

                                    // TODO: In this Space I will get the current User's location and check if the stranger location
                                    // TODO: is within 20km radius around the user

                                    if(!locationswitch.isChecked()){
                                        float[] result = new float[2];
                                        Location.distanceBetween(latitude,longitude,lati,lngi,result);


                                        if(result[0] <= 3000){

                                            if(!isUserContain(strangersresult,user)){
                                                strangersresult.add(user);

                                            }

                                        }

                                    }else{

                                        if(!isUserContain(strangersresult,user)){
                                            strangersresult.add(user);

                                        }
                                    }




                                    break;
                                }





                            }else if(key.equalsIgnoreCase(location)){    // the infomap is Location latlng

                                String[] locations = (String[]) infomap.get(key);
                                double latit = Double.parseDouble(locations[0]);
                                double lngit = Double.parseDouble(locations[1]);
                                String address = locations[2] !=null ? locations[2].toLowerCase(): "unknown";
                                String origin =locations[3] !=null? locations[3].toLowerCase():"unknown";
                                String country =locations[4] != null? locations[4].toLowerCase():"unknown";
                                String currentaddress = locations[5] != null ? locations[5].toLowerCase(): "unknown";

                                String searchaddress = entryMap.get(key).getText().toString().toLowerCase();

                                if(searchaddress.toLowerCase().equalsIgnoreCase(getString(R.string.alllocation).toLowerCase())){

                                    // No location is considered

                                    if(!isUserContain(strangersresult,user)){
                                        strangersresult.add(user);

                                    }


                                }else{

                                    if((address.contains(searchaddress))||(origin.contains(searchaddress))||(country.contains(searchaddress))||(currentaddress.contains(searchaddress))){

                                        if(!isUserContain(strangersresult,user)){
                                            strangersresult.add(user);

                                        }


                                        break;
                                    }


                                }












                            }else{    // the infomap is String


                                String info = String.valueOf(infomap.get(key)).toLowerCase();
                                String entry = entryMap.get(key).getText().toString().toLowerCase();

                                if(info.contains(entry)){

                                    // TODO: In this Space I will get the current User's location and check if the stranger location
                                    // TODO: is within 30km radius around the user

                                    if(!locationswitch.isChecked()){
                                        float[] result = new float[2];
                                        Location.distanceBetween(latitude,longitude,lati,lngi,result);


                                        if(result[0] <= 3000){

                                            if(!isUserContain(strangersresult,user)){
                                                strangersresult.add(user);

                                            }

                                        }

                                    }else{

                                        if(!isUserContain(strangersresult,user)){
                                            strangersresult.add(user);

                                        }
                                    }



                                    break;

                                }
                            }
                        }
                    }
                }

                FindStrangerAdapter findStrangerAdapter = new FindStrangerAdapter(FindStranger.this,strangersresult);
                findlist.setAdapter(findStrangerAdapter);
                if(findlist.getCount() != 0){

                    progresslay.setVisibility(View.GONE);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }else{

                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    progresslay.setVisibility(View.GONE);
                    Snackbar.make(progresslay,"Sorry, No Stranger was found, try again",Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public boolean isUserContain(ArrayList<User> userlist, User uservalue){
        boolean check = false;


        for (int i = 0; i< userlist.size(); i++){


            if(userlist.get(i).getUserid().equalsIgnoreCase(uservalue.getUserid())){
                check = true;
                break;
            }

            if (i == userlist.size()-1 && !userlist.get(i).getUserid().equalsIgnoreCase(uservalue.getUserid())){
                check = false;
            }



        }



        return check ;
    }


    public void menu(View view) {

        android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(this,view);
        popupMenu.inflate(R.menu.find_stranger);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {


            switch (item.getItemId()){

                case R.id.csearchpattern:


                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setIcon(R.drawable.searchiconblue);
                    builder.setTitle("Search Pattern");

                    if (searchtypePref.getText().equalsIgnoreCase(getString(R.string.narrowdown))) {

                        builder.setMultiChoiceItems(new String[]{"Narrow Down Search","Wild Search" }, new boolean[]{true, false}, (dialog, which, isChecked) -> {
                            switch (which) {
                                case 0:

                                    searchtypePref.setText(getString(R.string.narrowdown));
                                    dialog.dismiss();
                                    break;
                                case 1:
                                    searchtypePref.setText(getString(R.string.wildsearch));
                                    dialog.dismiss();
                                    break;
                            }
                        });

                    } else if(searchtypePref.getText().equalsIgnoreCase(getString(R.string.wildsearch))) {

                        builder.setMultiChoiceItems(new String[]{"Narrow Down Search","Wild Search" }, new boolean[]{false, true}, (dialog, which, isChecked) -> {
                            switch (which) {
                                case 0:

                                    searchtypePref.setText(getString(R.string.narrowdown));
                                    dialog.dismiss();
                                    break;
                                case 1:
                                    searchtypePref.setText(getString(R.string.wildsearch));
                                    dialog.dismiss();
                                    break;
                            }
                        });


                    }else{


                        builder.setMultiChoiceItems(new String[]{"Narrow Down Search","Wild Search" }, new boolean[]{false, false}, (dialog, which, isChecked) -> {
                            switch (which) {
                                case 0:

                                    searchtypePref.setText(getString(R.string.narrowdown));
                                    dialog.dismiss();
                                    break;
                                case 1:
                                    searchtypePref.setText(getString(R.string.wildsearch));
                                    dialog.dismiss();
                                    break;
                            }
                        });
                    }



                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create().show();

                    break;
            }



        return true;});



    }

    public void back(View view) {
        onBackPressed();
    }

    public void genderclick(View view) {

        if(genderswitch.isChecked()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMultiChoiceItems(new String[]{getString(R.string.male), getString(R.string.female), getString(R.string.other)}, new boolean[]{false, false, false}, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    switch (which) {
                        case 0:
                            genderentry.setText(R.string.male);
                            dialog.dismiss();
                            break;
                        case 1:
                            genderentry.setText(R.string.female);
                            dialog.dismiss();
                            break;
                        case 2:
                            genderentry.setText(R.string.other);
                            dialog.dismiss();
                            break;
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.create().show();

        }else{

            Toast.makeText(FindStranger.this, "On the Gender Switch", Toast.LENGTH_LONG).show();
        }



    }

    public void workstateclick(View view) {

        if (workstateswitch.isChecked()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMultiChoiceItems(new String[]{getString(R.string.student), getString(R.string.professional)}, new boolean[]{false, false}, (dialog, which, isChecked) -> {
                switch (which) {
                    case 0:
                        workstateentry.setText(R.string.student);
                        dialog.dismiss();
                        break;
                    case 1:
                        workstateentry.setText(R.string.professional);
                        dialog.dismiss();
                        break;
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.create().show();
        }else{
            Toast.makeText(this, "On the WorkState Switch", Toast.LENGTH_LONG).show();
        }





    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.find_stranger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
