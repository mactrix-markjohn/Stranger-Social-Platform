package com.mactrixapp.www.stranger;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.sdk.AppLovinSdk;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mactrixapp.www.stranger.AsyncTaskPack.AsyncClass;
import com.mactrixapp.www.stranger.Model.Constants;
import com.mactrixapp.www.stranger.Model.Group;
import com.mactrixapp.www.stranger.Model.LocationModel;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.Service.GeoCodeIntentService;
import com.mactrixapp.www.stranger.Service.InterestService;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Stranger extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int ADD_GROUP_IMAGE = 10;
    TabLayout strangetab;
    ViewPager strangepager;
    private CircleImageView headerImage;
    private TextView headerusername;
    private TextView headerfullname;
    private FirebaseUser fireuser;
    private DatabaseReference userReference;
    private TextView naviusername;
    private GoogleApiClient googleApiclient;
    private DatabaseReference locationReference;
    private MyResultReceiver resultReceiver;
    private  HashMap<String,Object> locationmap;
    private ImageView userheaderimage;
    LocationModel locationModel;
    private LocationRequest locationRequest;
    private DatabaseReference strangergroupRef;
    private DatabaseReference groupRef;
    private ImageView previewimage;
    private Uri fileuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stranger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        AppLovinSdk.initializeSdk(this);

        // Implementing the TabLayout and ViewPager views
        initializetabpager();
        resultReceiver = new MyResultReceiver(new Handler());



        //Receive the change tab layout

        Bundle bundle = getIntent().getBundleExtra("tab");
        if(bundle != null){

            // Move the Viewpager to the right fragment page
            strangepager.setCurrentItem(bundle.getInt("tab"),true);

        }

        // start the Interest Service here
        startService(new Intent(this,InterestService.class));


        // Initialize the user profile photo, username and Full Name
        headerImage = (CircleImageView) findViewById(R.id.headerimage);
        userheaderimage = (ImageView)findViewById(R.id.userheaderimage);
        headerusername = (TextView) findViewById(R.id.headerusername);
        headerfullname = (TextView) findViewById(R.id.headerfullname);
        naviusername = (TextView) findViewById(R.id.naviusername);

        // Initialize the Firebase Database and User to get Info and the Location Reference to store the user's current location
        locationModel = new LocationModel();
        fireuser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(fireuser.getUid());
        locationReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.location)).child(fireuser.getUid());
        strangergroupRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.strangersgroup));
        groupRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.group));
        registerInternetcheck();



        // Extract the user info


        //new Thread(this::extractTheUserInfo).start();
        //new Thread(this::onlinecompute).start(); // compute the neccesary things once the user comes online ie make connection stutus online and compute time

        //onlinecompute();
        new AsyncClass(this::extractTheUserInfo).execute();
        new AsyncClass(this::onlinecompute).execute();
        new AsyncClass(this::userlocation).execute();

        //userlocation(); // user location







        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void extractTheUserInfo() {

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null) {

                   runOnUiThread(() ->{

                       try {
                           Glide.with(Stranger.this).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(headerImage);
                       } catch (Exception e) {
                           headerImage.setImageResource(R.mipmap.profileavatar);

                       }

                       try {

                           Glide.with(Stranger.this).load(Uri.parse(user.getHeaderphotourl())).asBitmap().placeholder(R.mipmap.strangeralone).into(userheaderimage);
                       } catch (Exception e) {

                           userheaderimage.setImageResource(R.mipmap.strangeralone);
                       }
                       headerusername.setText(user.getUsername());
                       naviusername.setText(user.getUsername());
                       headerfullname.setText(user.getFullname());

                   });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // Send the user location to the database node called Location
    public void googleclientlocation() {

        googleApiclient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        userlocation();
                        Snackbar.make(strangetab, "Google Client Connected", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Snackbar.make(strangetab, "Google Client Suspended", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }
                }).addOnConnectionFailedListener(connectionResult -> {


                    Snackbar.make(strangetab, "Google Client Failed to Connect", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }).build();

    }

    public void userlocation() {




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           // check if the permission is granted
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

        result.addOnCompleteListener(task -> {

            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                if (response != null) {
                    LocationSettingsStates states = response.getLocationSettingsStates();
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
                }else{

                    locationRequest = new LocationRequest();
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(5000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    addLocationservice();
                }


            } catch (ApiException e) {

                switch (e.getStatusCode()){

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:


                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(Stranger.this,REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e1) {
                           // Ignore the error
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        locationRequest = new LocationRequest();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                        addLocationservice();

                        break;
                     default:

                         locationRequest = new LocationRequest();
                         locationRequest.setInterval(10000);
                         locationRequest.setFastestInterval(5000);
                         locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                         addLocationservice();

                         break;


                }

            }


        });









    }
    public void addLocationservice(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,new LocationCallback(){



                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);


                    Location location = locationResult.getLastLocation();
                    locationmap = new HashMap<>();
                    locationmap.put(getString(R.string.latitude),location.getLatitude());
                    locationmap.put(getString(R.string.longitude),location.getLongitude());

                    if(checkInternetConnectivity()) {
                        userReference.updateChildren(locationmap);

                    }

                    // for Location Database Reference
                    //locationReference.setValue(new LocationModel(location.getLatitude(),location.getLongitude(),fireuser.getUid()));

                    // get the lat and lng
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    // Initilize the intent class for starting the intent service
                    Intent intent = new Intent(Stranger.this,GeoCodeIntentService.class);
                    intent.putExtra(Constants.LOCATION_DATA_EXTRA,latLng);
                    intent.putExtra(Constants.RECEIVER,resultReceiver);
                    startService(intent);



                }
            },Looper.myLooper());

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.stranger, menu);
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





    public void card(View view) {

        Toast.makeText(this, "it is working", Toast.LENGTH_SHORT).show();


    }

    public void navigate(View view) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public void more(View view) {

        // menu
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.inflate(R.menu.stranger);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.strangersgroup) {

                createstrangersgroupdialog();

            } else if (item.getItemId() == R.id.group) {
                creategroupdialog();

            } else if (item.getItemId() == R.id.searchgroup) {

                startActivity(new Intent(this,SearchGroup.class));


            } else if (item.getItemId() == R.id.settings) {

                // TODO: Implement settings here
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();


            }




            return false;
        });


    }

    public void creategroupdialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_group);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView cancel = (ImageView)dialog.findViewById(R.id.cancel);
        EditText groupname = (EditText)dialog.findViewById(R.id.groupnameentry);
        EditText groupdescription = (EditText)dialog.findViewById(R.id.groupdescriptionentry);
        ImageView addimage = (ImageView)dialog.findViewById(R.id.addgroupimage);
        previewimage = (ImageView)dialog.findViewById(R.id.previewgroupimage);
        CardView create = (CardView)dialog.findViewById(R.id.create);

        cancel.setOnClickListener(canclick -> dialog.dismiss());
        addimage.setOnClickListener(addclick ->{

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, ADD_GROUP_IMAGE);


        });

        create.setOnClickListener(creclick ->{


            Toast.makeText(this, "Please wait..", Toast.LENGTH_LONG).show();
            if (fileuri != null) {
                createGroup(getString(R.string.group),fileuri,groupname.getText().toString(),groupdescription.getText().toString(),dialog);
            } else {

                DatabaseReference base =FirebaseDatabase.getInstance().getReference().child(getString(R.string.group)).push();
                Group group = new Group(groupname.getText().toString(),groupdescription.getText().toString(),base.getKey(),new Date().getTime(),fireuser.getUid(),base.getPath().toString());
                group.setAccess(getString(R.string.publicaccess));
                group.setGrouptype(getString(R.string.group));
                base.setValue(group).addOnSuccessListener(success ->{
                    dialog.dismiss();
                    Toast.makeText(Stranger.this, "Group is successfully created", Toast.LENGTH_SHORT).show();
                });
                base.child(getString(R.string.members)).child(fireuser.getUid()).setValue(fireuser.getUid());
                base.child(getString(R.string.admin)).child(fireuser.getUid()).setValue(fireuser.getUid());
            }

        });




    }
    public void createstrangersgroupdialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_group);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView cancel = (ImageView)dialog.findViewById(R.id.cancel);
        EditText groupname = (EditText)dialog.findViewById(R.id.groupnameentry);
        EditText groupdescription = (EditText)dialog.findViewById(R.id.groupdescriptionentry);
        ImageView addimage = (ImageView)dialog.findViewById(R.id.addgroupimage);
        previewimage = (ImageView)dialog.findViewById(R.id.previewgroupimage);
        CardView create = (CardView)dialog.findViewById(R.id.create);

        cancel.setOnClickListener(canclick -> dialog.dismiss());
        addimage.setOnClickListener(addclick ->{

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, ADD_GROUP_IMAGE);


        });

        create.setOnClickListener(creclick ->{

            Toast.makeText(this, "Please wait..", Toast.LENGTH_LONG).show();

            if (fileuri != null) {
                createGroup(getString(R.string.strangersgroup),fileuri,groupname.getText().toString(),groupdescription.getText().toString(),dialog);
            } else {

                DatabaseReference base =FirebaseDatabase.getInstance().getReference().child(getString(R.string.strangersgroup)).push();
                Group group = new Group(groupname.getText().toString(),groupdescription.getText().toString(),base.getKey(),new Date().getTime(),fireuser.getUid(),base.getPath().toString());
                group.setAccess(getString(R.string.publicaccess));
                group.setGrouptype(getString(R.string.strangersgroup));
                base.setValue(group).addOnSuccessListener(success ->{
                    dialog.dismiss();
                    Toast.makeText(Stranger.this, "Group is successfully created", Toast.LENGTH_SHORT).show();
                });
                base.child(getString(R.string.members)).child(fireuser.getUid()).setValue(fireuser.getUid());
                base.child(getString(R.string.admin)).child(fireuser.getUid()).setValue(fireuser.getUid());
            }


        });

    }

    public void createGroup(String grouptype, Uri uri, String name , String description, Dialog dialog){

        try{

            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(grouptype).child(""+new Date().getTime());

                UploadTask uploadTask = storageReference.putStream(inputStream);

                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return storageReference.getDownloadUrl();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                      String fileurl = uri.toString();

                      DatabaseReference base =FirebaseDatabase.getInstance().getReference().child(grouptype).push();
                      Group group = new Group(name,description,base.getKey(),fileurl,new Date().getTime(),fireuser.getUid(),base.getPath().toString());
                      group.setAccess(getString(R.string.publicaccess));
                      group.setGrouptype(grouptype);
                      base.setValue(group).addOnSuccessListener(success ->{
                          dialog.dismiss();
                          Toast.makeText(Stranger.this, "Group is successfully created", Toast.LENGTH_SHORT).show();
                      });
                      base.child(getString(R.string.members)).child(fireuser.getUid()).setValue(fireuser.getUid());
                      base.child(getString(R.string.admin)).child(fireuser.getUid()).setValue(fireuser.getUid());


                    }
                });





            }


        }catch (Exception e){

            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }



    }




    public void myprofile(View view) {

        // My profile
        strangepager.setCurrentItem(4,true);

    }

    public void strangerlist(View view) {
        // Stranger List

        startActivity(new Intent(Stranger.this, StrangerList.class));

    }

    public void strangerletter(View view) {

        // Stranger Letter
        startActivity(new Intent(Stranger.this,StrangerLetter.class));
    }

    public void findstranger(View view) {

        startActivity(new Intent(Stranger.this,FindStranger.class));
    }

    public void initializetabpager(){

        strangetab = (TabLayout)findViewById(R.id.strangetab);
        strangepager = (ViewPager)findViewById(R.id.strangepager);

        strangetab.addTab(strangetab.newTab().setIcon(R.drawable.newsblue));
        strangetab.addTab(strangetab.newTab().setIcon(R.drawable.searchicon));
        strangetab.addTab(strangetab.newTab().setIcon(R.drawable.chat_bubbleblack));
        strangetab.addTab(strangetab.newTab().setIcon(R.drawable.adsicon));
        strangetab.addTab(strangetab.newTab().setIcon(R.drawable.usericon));




        PagerAdapter strangeAdapter = new StrangerViewPagerAdapter(getSupportFragmentManager(), strangetab.getTabCount());
        strangepager.setAdapter(strangeAdapter);

        strangepager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(strangetab));

        strangetab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                strangepager.setCurrentItem(tab.getPosition(),true);

                switch (tab.getPosition()){

                    case 0: tab.setIcon(R.drawable.newsblue);
                        break;
                    case 1: tab.setIcon(R.drawable.searchiconblue);
                        break;
                    case 2: tab.setIcon(R.drawable.chat_bubbleblue);
                        break;
                    case 3: tab.setIcon(R.drawable.adsiconblue);
                        break;
                    case 4: tab.setIcon(R.drawable.usericonblue);
                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


                switch (tab.getPosition()){

                    case 0: tab.setIcon(R.drawable.news);
                        break;
                    case 1: tab.setIcon(R.drawable.searchicon);
                        break;
                    case 2: tab.setIcon(R.drawable.chat_bubbleblack);
                        break;
                    case 3: tab.setIcon(R.drawable.adsicon);
                        break;
                    case 4: tab.setIcon(R.drawable.usericon);
                        break;
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    public void logout(View view) {

        SharedPref keeplogin = new SharedPref(this,getString(R.string.keeplogin));


        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        // TODO: Here, change the keeplogin to false and get the last seen time

        keeplogin.setBoolean(false);
        lastseencompute();
        startActivity(new Intent(this, Login.class));
        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
        finish();

    }

    public void lastseencompute(){

        // Here we compute the last seen time of the User
        Date date = new Date();
        long lastseen = date.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  MMMM dd, yyyy",Locale.getDefault());
        String lastseentext = getString(R.string.last_seen)+" at "+sdf.format(date);

        HashMap<String, Object> lastseentime = new HashMap<>();

        /*User user = ne-w User();
        user.setConnectionstatus(lastseentext);
        user.setLastseen(lastseen);
        lastseentime.put(fireuser.getUid(),user);*/

        lastseentime.put(getString(R.string.connectionstatus),lastseentext);
        lastseentime.put(getString(R.string.lastseen),lastseen);

        userReference.updateChildren(lastseentime).addOnFailureListener(e ->{
            userReference.child(getString(R.string.connectionstatus)).setValue(lastseentext);
            userReference.child(getString(R.string.lastseen)).setValue(lastseen);

        });



    }

    public void onlinecompute(){

        // Get the time of login and store it in the database
        Date date = new Date();
        long onlinetime = date.getTime();


        // Update the User connection status to online, once the user enters the Platform
        HashMap<String,Object> connnectionstatus = new HashMap<>();
       /* User user = new User();
        user.setConnectionstatus(getString(R.string.online));
        user.setOnlinetime(onlinetime);
        connnectionstatus.put(fireuser.getUid(),user);*/

        connnectionstatus.put(getString(R.string.connectionstatus),getString(R.string.online));
        connnectionstatus.put(getString(R.string.onlinetime),onlinetime);



        userReference.updateChildren(connnectionstatus).addOnFailureListener(fv ->{
            userReference.child(getString(R.string.connectionstatus)).setValue(getString(R.string.online));
            userReference.child(getString(R.string.onlinetime)).setValue(onlinetime);
        });

    }

    @Override
    protected void onStop() {
        /*lastseencompute();
        unregisterReceiver(internetcheck);*/
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        lastseencompute();
        unregisterReceiver(internetcheck);
        super.onDestroy();


    }

    BroadcastReceiver internetcheck = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(checkInternetConnectivity()){
                // There is Internet connection, so the user is Online
                onlinecompute();

            }else{

                // There is no Internet Connection so the user is Offline
                lastseencompute();
            }

        }
    };

    public void registerInternetcheck(){

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internetcheck,intentFilter);

    }

    public boolean checkInternetConnectivity(){

        boolean connected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        try {

            connected= connectivityManager.getActiveNetworkInfo().isConnected();

        }catch (NullPointerException e){

            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                connected = true;
            }


        }



        return connected;

    }

    public void notification(View view) {

        startActivity(new Intent(this,NotificationBoard.class));

    }

    public void profilebackdoor(View view) {


        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.strangerid), fireuser.getUid());
        Intent intent = new Intent(this, StrangerProfile.class);
        intent.putExtra(getString(R.string.strangerid), bundle);
        startActivity(intent);

    }

    public void reputation(View view) {
        // open the reputation table activity
       // Toast.makeText(this, "My Reputation", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,Reputation.class));



    }

    // Implement the receiver of the Geocode address
    class MyResultReceiver extends android.os.ResultReceiver {



        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

           // if(resultCode == Constants.SUCCESS_RESULT){

                String result = resultData != null ? resultData.getString(Constants.SENTLOCATION) : "Unknown";
                locationmap.put(getString(R.string.currentaddress),result);

                // Send the guy to the storage room; THE FIREBASE!!!

            if (userReference != null) {
                userReference.updateChildren(locationmap);
            }


            // }






        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CHECK_SETTINGS) {

                final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
                if (states.isGpsUsable()) {
                    locationRequest = new LocationRequest();
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(5000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    addLocationservice();

                }else {

                    locationRequest = new LocationRequest();
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(5000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                    addLocationservice();
                }


            } else if (requestCode == ADD_GROUP_IMAGE) {

                fileuri = data.getData();


                if (previewimage != null) {
                    try{
                        Glide.with(this).load(fileuri).asBitmap().into(previewimage);

                    }catch (Exception e){
                        previewimage.setImageURI(fileuri);
                    }
                }

            }
        }

    }
}
