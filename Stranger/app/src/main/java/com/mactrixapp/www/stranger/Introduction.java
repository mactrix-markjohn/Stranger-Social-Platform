package com.mactrixapp.www.stranger;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Introduction extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    SharedPref keeplogin;
    FirebaseAuth userAuth;
    DatabaseReference userreference;
    FirebaseUser fireuser;
    SharedPref introoncePref;

    RelativeLayout relateview;
    private SharedPref emailpref;
    private SharedPref passpref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Initialize the neccessary things

        keeplogin = new SharedPref(this,getString(R.string.keeplogin));
        userAuth = FirebaseAuth.getInstance();
        userreference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user));
        introoncePref = new SharedPref(this,getString(R.string.introonce));
        relateview = (RelativeLayout)findViewById(R.id.relatevieww);
        emailpref = new SharedPref(this, getString(R.string.emailpref));
        passpref = new SharedPref(this,getString(R.string.passpref));



        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO},10);
           // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},10);

        }else {


            new Handler().postDelayed(this::introdirection, 2000);
           // new Handler().postDelayed(this::repute, 3000);

        }




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

        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            new Handler().postDelayed(this::introdirection, 4000);
        }

    }

    public void repute(){

        startActivity(new Intent(this,Reputation.class));
    }

    public void introdirection(){
        String userID;

        if(keeplogin.getBoolean()){

            if(checkInternetConnectivity()){



                userAuth.signInWithEmailAndPassword(emailpref.getText(),passpref.getText()).addOnSuccessListener((authResult) ->{

                    startActivity(new Intent(Introduction.this,Stranger.class));
                    //startActivity(new Intent(Introduction.this,Reputation.class));

                    Toast.makeText(this, "Welcome to Stranger", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener((e) ->{

                    startActivity(new Intent(this, Login.class));
                    finish();

                });



            }else{
                registerInternetBroadcast();
            }


        }else {

            if (checkInternetConnectivity()) {


            try {


                introoncePref.setInt(1);
                fireuser = userAuth.getCurrentUser();
                userID = fireuser.getUid();

                userreference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (introoncePref.getInt() == 1) {


                            if (dataSnapshot.hasChild(userID)) {

                                // TODO: if things does not work out, implement the keeplogin here...

                                Toast.makeText(Introduction.this, "Please Login", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Introduction.this, Login.class));
                                try {
                                    unregisterReceiver(internetReceiver);
                                } catch (Exception e) {
                                    String nul = "null";
                                }
                                finish();

                            } else {

                                Toast.makeText(Introduction.this, "SignUp ", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Introduction.this, SignUp.class));

                                finish();
                            }

                        }
                        introoncePref.setInt(0);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            } catch (Exception e) {
                Toast.makeText(this, "Welcome, Please SignUp", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Introduction.this, SignUp.class));
                finish();
            }

        }else{

                registerInternetBroadcast();
                //Toast.makeText(this, "There is No Connectivity, Please turn on your Internet", Toast.LENGTH_LONG).show();

            }

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
        getMenuInflater().inflate(R.menu.introduction, menu);
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

    BroadcastReceiver internetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


           if(checkInternetConnectivity()) {

               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                   Snackbar.make(relateview, "Connection is Online", Snackbar.LENGTH_LONG)
                           .setActionTextColor(getColor(R.color.appgreen)).setAction("Action", null).show();
               } else {

                   Toast.makeText(context, "Connection is office", Toast.LENGTH_SHORT).show();

               }
               introdirection();
               unregisterReceiver(internetReceiver);

           }else{

               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {



                   Snackbar.make(relateview, "Connection is Offline", Snackbar.LENGTH_LONG)
                           .setActionTextColor(getColor(R.color.red)).setAction("Action", null).show();
               } else {

                   Toast.makeText(context, "Connection is Offline", Toast.LENGTH_SHORT).show();

               }

           }


        }
    };

    public void registerInternetBroadcast(){

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internetReceiver,intentFilter);


    }

}
