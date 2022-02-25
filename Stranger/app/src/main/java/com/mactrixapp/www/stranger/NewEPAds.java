package com.mactrixapp.www.stranger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mactrixapp.www.stranger.Model.Chat;
import com.mactrixapp.www.stranger.Model.Constants;
import com.mactrixapp.www.stranger.Model.EPAds;
import com.mactrixapp.www.stranger.Model.PlaybackStudio;
import com.mactrixapp.www.stranger.Model.VideoStudio;
import com.mactrixapp.www.stranger.Service.GeoCodeIntentService;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

public class NewEPAds extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int VIDEO_CODE = 20;
    private static final int IMAGE_CODE = 10;
    private static final int FILE_CODE = 30;
    private EditText eptitle;
    private EditText epmessage;
    private TextView eplatitude;
    private TextView eplongitude;
    private TextView epaddress;
    private MyResultReceiver resultReceiver;
    private Location location;
    private String filetype;
    private Uri fileuri;
    private DatabaseReference epadReference;
    private FirebaseUser currentuser;
    private StorageReference storageReference;
    private ImageView epimage;
    private RelativeLayout epmedia;
    private VideoView epvideoview;
    private ImageView epvideobutton;
    private RelativeLayout epvideolay;
    private RelativeLayout epvideocontroller;
    private CardView epaudiocard;
    private TextView epaudioname;
    private ImageView playpausecard;
    private CardView epfilecard;
    private TextView epfilename;
    private PlaybackStudio playstudio;
    private VideoStudio videostudio;
    private EditText epadvenue;
    private String titile;
    private String songname;
    private String audioname;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_epads);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // media players
        playstudio = new PlaybackStudio(this);
        videostudio = new VideoStudio(this);
        filetype = getString(R.string.none);

        eptitle = (EditText)findViewById(R.id.eptitle);
        epmessage = (EditText)findViewById(R.id.epmessage);
        epadvenue = (EditText)findViewById(R.id.epadvenue);
        eplatitude = (TextView)findViewById(R.id.eplatitude);
        eplongitude = (TextView)findViewById(R.id.eplongitude);
        epaddress = (TextView) findViewById(R.id.epaddress);
        epmedia = (RelativeLayout)findViewById(R.id.epmedia);

        // image lay
        epimage = (ImageView)findViewById(R.id.epimage);

        // video lay
        epvideolay = (RelativeLayout)findViewById(R.id.eppostvideolay);
        epvideoview = (VideoView)findViewById(R.id.postvideoview);
        epvideobutton = (ImageView)findViewById(R.id.postvideobutton);
        epvideocontroller = (RelativeLayout)findViewById(R.id.epvideocontroller);

        // audio lay
        epaudiocard = (CardView)findViewById(R.id.epaudiocard);
        epaudioname = (TextView)findViewById(R.id.epaudioname);
        playpausecard = (ImageView)findViewById(R.id.playpausecard);

        // file lay
        epfilecard = (CardView)findViewById(R.id.epfilecard);
        epfilename = (TextView)findViewById(R.id.epfilename);

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        epadReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.epads));
        storageReference = FirebaseStorage.getInstance().getReference().child(getString(R.string.epads));

        resultReceiver = new MyResultReceiver(new Handler());
        userlocation();







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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    public void postepad(View view) {

        // TODO: More things will happen here
        Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show();

        EPAds epAds = new EPAds();
        DatabaseReference epadBase = epadReference.push();
        epAds.setId(epadBase.getKey());
        epAds.setDate(new Date().getTime());
        epAds.setSenderid(currentuser.getUid());
        epAds.setAddress(epaddress.getText().toString());
        epAds.setVenue(epadvenue.getText().toString());
        epAds.setLatitude(Double.valueOf(eplatitude.getText().toString()));
        epAds.setLongitude(Double.valueOf(eplongitude.getText().toString()));
        epAds.setMessage(epmessage.getText().toString());
        epAds.setAudioname(audioname);
        epAds.setFilename(filename);
        epAds.setTitle(eptitle.getText().toString());
        epAds.setFiletype(filetype);
        if (filetype.equalsIgnoreCase(getString(R.string.none))) {
            epadBase.setValue(epAds).addOnSuccessListener(svv ->{
                Toast.makeText(this, "EP Ads Sucessfully Uploaded", Toast.LENGTH_SHORT).show();
                finish();
            });
        }else{
            sendmedia(fileuri,epAds,epadBase);
        }





    }

    public void sendmedia(Uri fileuri, EPAds chat, DatabaseReference chatinput ){
        //Get Date


        // store the file using inputstream
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileuri);

            if(inputStream != null){

                StorageReference chatstore = storageReference.child(""+chat.getDate());
                UploadTask uploadTask = chatstore.putStream(inputStream);

                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return chatstore.getDownloadUrl() ;
                    }
                }).addOnSuccessListener(uri -> {

                    chat.setFileurl(uri.toString());
                    chatinput.setValue(chat).addOnSuccessListener(vvv -> {filetype = getString(R.string.none);
                        Toast.makeText(this, "EP Ads Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        finish();
                    });


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(NewEPAds.this, "Unable to send file, try again", Toast.LENGTH_SHORT).show();
                        chatinput.setValue(chat).addOnSuccessListener(vvv -> {filetype = getString(R.string.none);
                            Toast.makeText(NewEPAds.this, "EP Ads Successfully Uploaded", Toast.LENGTH_SHORT).show();
                            finish();
                        });

                    }
                });

            }


        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }


    }



    public void openmap(View view) {
    }

    public void addphoto(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_CODE);
        filetype = getString(R.string.image);

    }

    public void addvideo(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent,VIDEO_CODE);
        filetype = getString(R.string.video);

    }

    public void addfile(View view) {

        Intent intent  = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        try {
            startActivityForResult(Intent.createChooser(intent,"Select File Manager"),FILE_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "No App can perform this task", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){

            fileuri = data.getData();

            if(resultCode == RESULT_OK && fileuri != null){



                if (requestCode == IMAGE_CODE) {
                    // image data
                    filetype = getString(R.string.image);

                    epmedia.setVisibility(View.VISIBLE);
                    epimage.setVisibility(View.VISIBLE);
                    epvideolay.setVisibility(View.GONE);
                    epaudiocard.setVisibility(View.GONE);
                    epfilecard.setVisibility(View.GONE);

                    try{
                        Glide.with(this).load(fileuri).asBitmap().into(epimage);
                    }
                    catch (Exception e){
                        epimage.setImageURI(fileuri);
                    }



                } else if (requestCode == VIDEO_CODE) {
                    // video data
                    filetype = getString(R.string.video);
                    epmedia.setVisibility(View.VISIBLE);
                    epvideolay.setVisibility(View.VISIBLE);
                    epimage.setVisibility(View.GONE);
                    epaudiocard.setVisibility(View.GONE);
                    epfilecard.setVisibility(View.GONE);
                    videostudio.playmedia(fileuri,epvideoview);

                } else if (requestCode == FILE_CODE) {
                    // file data

                    String path = fileuri.toString();

                    if (path.contains("image")) {
                        filetype = getString(R.string.image);
                        epmedia.setVisibility(View.VISIBLE);
                        epimage.setVisibility(View.VISIBLE);
                        epvideolay.setVisibility(View.GONE);
                        epaudiocard.setVisibility(View.GONE);
                        epfilecard.setVisibility(View.GONE);
                        try{
                            Glide.with(this).load(fileuri).asBitmap().into(epimage);
                        }
                        catch (Exception e){
                            epimage.setImageURI(fileuri);
                        }

                    } else if (path.contains("video")) {
                        filetype = getString(R.string.video);
                        // video data
                        filetype = getString(R.string.video);
                        epmedia.setVisibility(View.VISIBLE);
                        epvideolay.setVisibility(View.VISIBLE);
                        epimage.setVisibility(View.GONE);
                        epaudiocard.setVisibility(View.GONE);
                        epfilecard.setVisibility(View.GONE);
                        videostudio.playmedia(fileuri,epvideoview);

                    } else if (path.contains("audio")) {
                        filetype = getString(R.string.audio);

                        epmedia.setVisibility(View.VISIBLE);
                        epaudiocard.setVisibility(View.VISIBLE);
                        epimage.setVisibility(View.GONE);
                        epvideolay.setVisibility(View.GONE);
                        epfilecard.setVisibility(View.GONE);
                        audioname = audioname(fileuri);
                        epaudioname.setText(audioname);
                        playstudio.playMedia(fileuri.toString());

                    }else{

                        filetype = getString(R.string.other);

                        epmedia.setVisibility(View.VISIBLE);
                        epfilecard.setVisibility(View.VISIBLE);
                        epimage.setVisibility(View.GONE);
                        epvideolay.setVisibility(View.GONE);
                        epaudiocard.setVisibility(View.GONE);
                        filename = filename(fileuri);
                        epfilename.setText(filename);

                    }

                }
            }
        }
    }

    public String filename(Uri fileuri){
        String path = fileuri.toString() != null ? fileuri.toString() : " /Unknown.";
        String name = path.substring(path.lastIndexOf("%")+1,path.length());
        return name;

    }
    public String audioname(Uri fileuri){

        String titile = "jfjjda";
        String songname = "unknamsdf";

        Cursor cursor =getContentResolver().query(fileuri,null,null,null,null);

        if(cursor != null){

            if(cursor.getCount() > 0){
                cursor.moveToFirst();

                titile = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                songname = titile.substring(titile.lastIndexOf("/")+1,titile.length());
            }

        }
        return songname;
    }

    public void playpause(View view) {


        Cursor cursor = getContentResolver().query(fileuri,null,null,null,null);

        if(cursor != null){

            if(cursor.getCount() > 0){
                cursor.moveToFirst();

                titile = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                songname = titile.substring(titile.lastIndexOf("/")+1,titile.length());
            }

        }



        if (!playstudio.isPrepared()) {

            playstudio.playMedia(titile);
            playpausecard.setImageResource(R.drawable.pause_bottom_sm_black);

        }else{

            if (playstudio.isPlaying()) {
                playstudio.pauseMedia();
                playpausecard.setImageResource(R.drawable.play_button_sm_black);

            }else{

                playstudio.resumeMedia();
                playpausecard.setImageResource(R.drawable.pause_bottom_sm_black);

            }

        }
    }

    public void videobutton(View view) {

        if(videostudio.isPlaying(epvideolay,epvideoview)){

            videostudio.pauseMedia(epvideoview);
            epvideobutton.setImageResource(R.drawable.play_button_sm_black);


        }else{

            videostudio.resumeMedia(epvideoview);
            epvideobutton.setImageResource(R.drawable.pause_bottom_sm_black);

        }


    }

    int count = 0;
    public void epvideolay(View view) {

        if(count == 0){
            epvideocontroller.setVisibility(View.INVISIBLE);

            count = 1;
        }else{
            epvideocontroller.setVisibility(View.VISIBLE);
            count = 0;
        }



    }

    public void cancelmedia(View view) {

        epmedia.setVisibility(View.GONE);
        if(playstudio.isPrepared()){
            playstudio.reset();
        }
        videostudio.stopMedia(epvideoview);
        filetype = getString(R.string.none);

    }

    public void mylocation(View view) {

        // get the lat and lng
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

        // Initilize the intent class for starting the intent service
        Intent intent = new Intent(NewEPAds.this,GeoCodeIntentService.class);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,latLng);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        startService(intent);

        eplatitude.setText(String.valueOf(location.getLatitude()));
        eplongitude.setText(String.valueOf(location.getLongitude()));

    }

    public void back(View view) {
        onBackPressed();
    }


    public void userlocation() {




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // check if the permission is granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);

            return;
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,new LocationCallback(){



            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);


                location = locationResult.getLastLocation();



                // for Location Database Reference
                //locationReference.setValue(new LocationModel(location.getLatitude(),location.getLongitude(),fireuser.getUid()));




            }
        },Looper.myLooper());






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
        getMenuInflater().inflate(R.menu.new_epads, menu);
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

            epaddress.setText(result);





            // }






        }
    }


}
