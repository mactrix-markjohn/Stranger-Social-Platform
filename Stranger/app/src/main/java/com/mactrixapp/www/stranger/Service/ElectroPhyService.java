package com.mactrixapp.www.stranger.Service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.BroadcastReceiver.GeoReceiver;
import com.mactrixapp.www.stranger.BroadcastReceiver.RestartReceiver;
import com.mactrixapp.www.stranger.FindStranger;
import com.mactrixapp.www.stranger.Model.EPAds;
import com.mactrixapp.www.stranger.Model.LocationModel;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.SQLLITE.GeoSQLLite;

import java.util.ArrayList;
import java.util.List;

public class ElectroPhyService extends Service {
    private Location location;
    private double latitude;
    private double longitude;
    private FirebaseUser currentuser;
    private EPAds epAds;
    private GeoSQLLite geoLite;
    private Cursor cursor;
    private List<Geofence> geofenceArray;
    private LocationRequest locationRequest;


    //TODO: Here we are going to Implement the Geofence Listening Callbacks


    public ElectroPhyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        userlocation(); // start monitoring location
        geofenceArray = new ArrayList<>();


        try {
            currentuser = FirebaseAuth.getInstance().getCurrentUser();
        } catch (Exception e) {
            stopSelf();
        }
        geoLite =  new GeoSQLLite(this,getString(R.string.epads),null,1);
        cursor = geoLite.getAllGeofence();


        try {


            if (cursor != null && cursor.getCount() > 0) {
                //Toast.makeText(this, "There is item in cursor", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(this::startEPADListening,20000);
                //Toast.makeText(this, "it made it to the other side", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            cursor = geoLite.getAllGeofence();
            stopSelf();
        }

        // Start adding and listening to Geofence








    }


    /*public void getEPADs(){

        GeoSQLLite geoSQLLite = new GeoSQLLite(this,getString(R.string.epads),null,1);

        FirebaseDatabase.getInstance().getReference().child(getString(R.string.epads)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                geoSQLLite.deleteAllGeofence();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EPAds epAds = snapshot.getValue(EPAds.class);

                    if (epAds != null) {

                        geoSQLLite.addGeofence(epAds.getId(),epAds.getLatitude(),epAds.getLongitude(),epAds.getMessage(),epAds.getTitle(),epAds.getFiletype());
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/

    public void userlocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // check if the permission is granted

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


            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {

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

    public void startEPADListening(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // check if the permission is granted

            return;
        }

        if(cursor != null && cursor.getCount() > 0){

            for(int i = 0;i <cursor.getCount();i++){

                // Extracting all the Geofences location registered in the database
                cursor.moveToPosition(i);
                double latitiud = cursor.getDouble(cursor.getColumnIndex(GeoSQLLite.LATITUDE));
                double longitud = cursor.getDouble(cursor.getColumnIndex(GeoSQLLite.LONGITIUDE));
                String requestID = cursor.getString(cursor.getColumnIndex(GeoSQLLite.GEOID));
                if(requestID == null){
                    requestID = "alatzone";
                }

                // Finding all the Geofences in the Set out Circumference of about 50,000 meters

                float[] distance = new float[2];

                Location.distanceBetween(latitiud, longitud, latitude, longitude, distance);

                if(distance[0] <= 5000){

                    // Inside the Circumference or perimeter


                    // TODO: Setting up the Geofence of the within the 50,000 meter radius perimeter
                    Geofence geofence = new Geofence.Builder()
                            .setCircularRegion(latitiud,longitud,300)
                            .setLoiteringDelay(300000)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_DWELL)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setRequestId(requestID)
                            .build();
                    geofenceArray.add(geofence);




                }else if(latitude == 0){

                    Geofence geofence = new Geofence.Builder()
                            .setCircularRegion(latitiud,longitud,300)
                            .setLoiteringDelay(300000)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_DWELL)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setRequestId(requestID)
                            .build();
                    geofenceArray.add(geofence);
                }
            }
            try {
                GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                        .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_DWELL)
                        .addGeofences(geofenceArray)
                        .build();


                Intent intent = new Intent(this,GeoReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                LocationServices.getGeofencingClient(this).addGeofences(geofencingRequest,pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {




                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });
            } catch (Exception e) {
                stopSelf();
            }

            // TODO: Please remember to add the onSuccussListener once we have resolved the issue of Task Class in this Android Studio


        }





    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sendBroadcast(new Intent(this, RestartReceiver.class));
    }
}
