package com.mactrixapp.www.stranger.Service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
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
import com.mactrixapp.www.stranger.Adapters.NoticeAdapter;
import com.mactrixapp.www.stranger.AsyncTaskPack.AsyncClass;
import com.mactrixapp.www.stranger.EPADViewer;
import com.mactrixapp.www.stranger.FindStranger;
import com.mactrixapp.www.stranger.Model.Constants;
import com.mactrixapp.www.stranger.Model.EPAds;
import com.mactrixapp.www.stranger.Model.IsNoticeContain;
import com.mactrixapp.www.stranger.Model.Letter;
import com.mactrixapp.www.stranger.Model.LetterFeatureHolder;
import com.mactrixapp.www.stranger.Model.LocationModel;
import com.mactrixapp.www.stranger.Model.Notification;
import com.mactrixapp.www.stranger.Model.Request;
import com.mactrixapp.www.stranger.Model.StrangerLocationModel;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.NotificationBoard;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.SQLLITE.GeoSQLLite;
import com.mactrixapp.www.stranger.Stranger;
import com.mactrixapp.www.stranger.StrangerProfile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class InterestService extends Service {
    private FirebaseUser currentuser;
    private DatabaseReference locationReference;
    private IsNoticeContain isListContain;
    private DatabaseReference userReference;
    private DatabaseReference requestReference;
    private DatabaseReference letterReference;
    private User user;
    private ArrayList<Notification> notifications;
    private double latitude;
    private double longitude;
    private LocationRequest locationRequest;

    // TODO: Here we will Listen to Location Node child where every user send there current location

    public InterestService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            currentuser = FirebaseAuth.getInstance().getCurrentUser();
            currentuser.getUid().toLowerCase();
        } catch (Exception e) {
            stopSelf();
        }
        locationReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.location)).child(currentuser.getUid());
        isListContain = new IsNoticeContain();
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(currentuser.getUid());
        requestReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.request));
        letterReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.letter));

        userlocation();
        registerbroadcasts();

        // Get the Current user User.class




        new AsyncClass(() -> userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class) != null ? dataSnapshot.getValue(User.class) : new User(currentuser.getUid(),currentuser.getEmail(),currentuser.getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })).execute();




        // get the notification from the database
        notifications = new ArrayList<>();

        new AsyncClass(this::requestListener).execute();
        new AsyncClass(this::letterListener).execute();
        new AsyncClass(this::locationListener).execute();
        new AsyncClass(this::getEPADs).execute();

       /* requestListener();
        letterListener();
        locationListener();
        getEPADs();*/





    }

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
        result.addOnSuccessListener(locationSettingsResponse -> {

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


        }).addOnFailureListener(e -> {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            addLocationservice();
        });




    }
    public void addLocationservice(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,new LocationCallback(){



                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);


                    Location location = locationResult.getLastLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    locationReference.setValue(new LocationModel(location.getLatitude(),location.getLongitude(),currentuser.getUid()));






                }
            },Looper.myLooper());
        }
    }

    public void requestListener(){

        // Listen to Permission Request
        requestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                // get all the request
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Request request = snapshot.getValue(Request.class);
                    if(request != null){

                        if(request.getReceiverid().equalsIgnoreCase(currentuser.getUid())){
                            // Request is receieved by me
                            Notification notice = new Notification();
                            notice.setNoticetype(getString(R.string.requestreceived));
                            notice.setSenderid(request.getSenderid());
                            notice.setReceiverid(request.getReceiverid());
                            notice.setDate(request.getDate());
                            notice.setRequest(request);

                            if(!(request.getRequestreceiverseen() != null && request.getRequestreceiverseen().equalsIgnoreCase(getString(R.string.seen)))){

                                if(!isListContain.isUserContain(notifications,notice)){

                                    notifications.add(notice);

                                }
                            }

                        }else if(request.getSenderid().equalsIgnoreCase(currentuser.getUid())){

                            // I sent a request and I received a reply [Either Accepted or Rejected]
                            if(request.getPermission() != null){


                                if(request.getPermission().equalsIgnoreCase(getString(R.string.permissiongranted))){

                                    // Permission was granted or accepted

                                    Notification notice = new Notification();
                                    notice.setNoticetype(getString(R.string.requestaccepted));
                                    notice.setSenderid(request.getSenderid());
                                    notice.setReceiverid(request.getReceiverid());
                                    notice.setDate(request.getDate());
                                    notice.setRequest(request);

                                    if(!(request.getRequestsenderseen() != null && request.getRequestsenderseen().equalsIgnoreCase(getString(R.string.seen)))){

                                        if(!isListContain.isUserContain(notifications,notice)){

                                            notifications.add(notice);

                                        } } }else if(request.getPermission().equalsIgnoreCase(getString(R.string.permissiondenied))){
                                    // Permission was denied or rejected


                                    Notification notice = new Notification();
                                    notice.setNoticetype(getString(R.string.requestrejected));
                                    notice.setSenderid(request.getSenderid());
                                    notice.setReceiverid(request.getReceiverid());
                                    notice.setRequest(request);

                                    if(!(request.getRequestsenderseen() != null && request.getRequestsenderseen().equalsIgnoreCase(getString(R.string.seen)))){

                                        if(!isListContain.isUserContain(notifications,notice)){

                                            notifications.add(notice);

                                        } } } } } }}

                Collections.sort(notifications, new Comparator<Notification>() {
                    @Override
                    public int compare(Notification o1, Notification o2) {
                        return String.valueOf(o2.getDate()).compareTo(String.valueOf(o1.getDate()));
                    }
                });


               // TODO: send the Broadcast to Notification.class
                Intent noticeintent = new Intent(getString(R.string.noticereceive));
                noticeintent.putParcelableArrayListExtra(getString(R.string.noticearraylist),notifications);
                sendBroadcast(noticeintent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void letterListener(){


        // Listen for Stranger Letter
        letterReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    if(!snapshot.hasChild(currentuser.getUid())){

                        Letter letter = snapshot.getValue(Letter.class);
                        Notification notice = new Notification();

                        if (letter != null && user != null) {

                            String name = letter.getName() != null ? letter.getName() : "jdfjdjkdfkjdfjkdfkjdfkjdfkjdf";
                            String profession = letter.getProfesssion() != null ? letter.getProfesssion() : "jfjdfjkjdfkjdfkjdfjkdf";
                            String interest = letter.getInterests() != null ? letter.getInterests() : "kjdfkjdffhdfkjdfjkdfkjdfj";
                            String institute = letter.getInstitute() != null ? letter.getInstitute() : "kjfjdfkldfkldfkldfkjldfkjldfkldf";
                            String gender = letter.getGender() != null ? letter.getGender() : "jdfkjdfkjdfjldfjfkldff";
                            String workstate = letter.getWorkstate() != null ? letter.getWorkstate() : "jdfjdfkjldfkjldfkjdfkj";
                            String locationstate = letter.getLocationstate() != null ? letter.getLocationstate() : "kjdfjkdfkjdfkjdfkjdfkjfkjdfkj";
                            double latitude = letter.getSenderlat();
                            double longitude = letter.getSenderlng();

                            ArrayList<LetterFeatureHolder> featureHolders = new ArrayList<>();
                            featureHolders.add(new LetterFeatureHolder(user.getFullname() + " " + user.getUsername(), name));
                            featureHolders.add(new LetterFeatureHolder(user.getProfession(), profession));
                            featureHolders.add(new LetterFeatureHolder(user.getInstitute(), interest));
                            featureHolders.add(new LetterFeatureHolder(user.getInstitute(), institute));
                            featureHolders.add(new LetterFeatureHolder(user.getGender(), gender));
                            featureHolders.add(new LetterFeatureHolder(user.getWorkstate(), workstate));


                            // check if the stranger is around the current user
                            if (locationstate.equalsIgnoreCase(getString(R.string.aroundme))) {

                                float[] distance = new float[2];
                                Location.distanceBetween(latitude, longitude, user.getLatitude(), user.getLongitude(), distance);

                                if (distance[0] <= 3000) {
                                    // The stranger is within 3km around me

                                    for (LetterFeatureHolder holder : featureHolders) {

                                        if(holder == null || holder.getReceiver() == null){
                                            continue;
                                        }


                                        if (holder.getReceiver().toLowerCase().contains(holder.getSender().toLowerCase())) {

                                            // The Current user has one of the features of the letter
                                            notice.setDate(letter.getDate());
                                            notice.setNoticetype(getString(R.string.letter));
                                            notice.setLetter(letter);

                                            if (!isListContain.isUserContain(notifications, notice)) {
                                                notifications.add(notice);
                                            }


                                        }

                                    }


                                }

                            } else {

                                featureHolders.add(new LetterFeatureHolder(user.getAddress() + " " + user.getCurrentaddress() + " " + user.getOrigin() + " " + user.getCountry(), locationstate));
                                for (LetterFeatureHolder holder : featureHolders) {

                                    if (holder.getReceiver().toLowerCase().contains(holder.getSender().toLowerCase())) {

                                        // The Current user has one of the features of the letter
                                        notice.setDate(letter.getDate());
                                        notice.setNoticetype(getString(R.string.letter));
                                        notice.setLetter(letter);

                                        if (!isListContain.isUserContain(notifications, notice)) {
                                            notifications.add(notice);
                                        }
                                    }

                                }
                            }
                        }
                    }

                }

                Collections.sort(notifications, new Comparator<Notification>() {
                    @Override
                    public int compare(Notification o1, Notification o2) {
                        return String.valueOf(o2.getDate()).compareTo(String.valueOf(o1.getDate()));
                    }
                });

               // TODO: send broadcast to Notification.class here
                Intent noticeintent = new Intent(getString(R.string.noticereceive));
                noticeintent.putParcelableArrayListExtra(getString(R.string.noticearraylist),notifications);
                sendBroadcast(noticeintent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void locationListener(){


        // This is the Interest Notification Listener. That listen for user that are within 200m around a user.

        FirebaseDatabase.getInstance().getReference().child(getString(R.string.location)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    LocationModel locationModel = snapshot.getValue(LocationModel.class);

                    if(locationModel != null && user != null){

                        // got the stranger latitude and longitude and the ID

                        double lat = locationModel.getLatitude();
                        double lng = locationModel.getLongitude();
                        String strangerid = locationModel.getUserid();


                        if(user.getUserid().equalsIgnoreCase(strangerid)){
                            continue;
                        }

                        // check if the user is within 500 m
                        float[] distance = new float[3];
                        Location.distanceBetween(latitude,longitude,lat,lng,distance);

                        if(distance[0] <= 500){
                            // The users here are 3km around the current user

                            String interests = user.getInterest();



                                ArrayList<String> interestlist = interestseperate(interests);


                            // here we get the details and interest of the stranger

                            FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(strangerid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    User stranger = dataSnapshot.getValue(User.class);
                                    if(stranger != null){

                                        String stratrest = stranger.getInterest() != null ? stranger.getInterest() : "kjjkjbfbhjcbvkkkjbfkjbckxjmcvkjb";

                                        ArrayList<String> interests = new ArrayList<>();// to store common interest

                                        for (String interest:interestlist){

                                            // check if the current user interest is contain in the Stranger interest list
                                            if (stratrest.toLowerCase().contains(interest.toLowerCase())) {

                                                // this stranger have the same interest as the current user


                                                interests.add(interest);


                                            }

                                        }

                                        // first join all the interest with a delimiter of "#"
                                        // send a notification to the current user in the Notification board and Device
                                        if(!interests.isEmpty()) {

                                            String commonInterest = TextUtils.join("#", interests);
                                            setupNotification(stranger, commonInterest, strangerid);

                                        }

                                    }



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }


                    }


                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupNotification(User stranger ,String interest, String strangerid) {

        // send notification to the user Notification board and device

        locationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                StrangerLocationModel model = dataSnapshot.child(strangerid).getValue(StrangerLocationModel.class);
                if (!dataSnapshot.hasChild(strangerid)) {

                    // if the stranger has not been shown to the current user before


                    Notification notice = new Notification();
                    notice.setDate(new Date().getTime());
                    notice.setReceiverid(strangerid);
                    notice.setReceivername(interest);
                    notice.setNoticetype(getString(R.string.interestuser));

                    //if(!isListContain.isUserIDContain(notifications,notice)){
                        notifications.add(notice);
                        sendnotice();
                        // TODO: send device alert Notification here
                        startNotification(stranger,interest);
                    //}

                }else if(model != null && (new GregorianCalendar().get(Calendar.DAY_OF_YEAR) - model.getDate()) > 4){

                    Notification notice = new Notification();
                    notice.setDate(new Date().getTime());
                    notice.setReceiverid(strangerid);
                    notice.setReceivername(interest);
                    notice.setNoticetype(getString(R.string.interestuser));

                    //if(!isListContain.isUserIDContain(notifications,notice)){
                        notifications.add(notice);
                        sendnotice();
                        // TODO: send device alert Notification here
                        startNotification(stranger,interest);
                    //}

                }else if(model != null && model.getDeleted().equalsIgnoreCase("false")){

                    Notification notice = new Notification();
                    notice.setDate(new Date().getTime());
                    notice.setReceiverid(strangerid);
                    notice.setNoticetype(getString(R.string.interestuser));
                    notice.setReceivername(interest);
                    //if(!isListContain.isUserIDContain(notifications,notice)){
                        notifications.add(notice);
                        sendnotice();
                        // TODO: send device alert Notification here
                        startNotification(stranger,interest);
                    //}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void startNotification(User stranger, String interest){

        /* if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){*/
        try {

                long[] vib = {1000,1100,1000,11000};

                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                String contentText = stranger.getFullname()+" shares your "+interest+" interest. Click here to view more";



                android.app.Notification.Builder builder = new android.app.Notification.Builder(this)
                        .setSmallIcon(R.drawable.about)
                        .setContentTitle("Interest Notification")
                        .setContentText(contentText)
                        .setVibrate(vib)
                        .setSound(sound)
                        .setOnlyAlertOnce(true);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.strangerid), stranger.getUserid());
                Intent intent = new Intent(this, StrangerProfile.class);
                intent.putExtra(getString(R.string.strangerid), bundle);



                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentIntent(pendingIntent);
                android.app.Notification notification = builder.build();

                if (manager != null) {
                    manager.notify(0, notification);
                }
            }catch (NullPointerException e){
                Toast.makeText(this, "Cannot send Notification", Toast.LENGTH_SHORT).show();
            }


//        /*}else{
//
//
//            try {
//
//                long[] vib = {1000,1100,1000,11000};
//
//                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//                RemoteViews expand = new RemoteViews(contexting.getPackageName(),R.layout.content_emergency_contacts);
//                // expand.setImageViewResource(R.id.backexpand,R.mipmap.kidapp);
//                expand.setTextViewText(R.id.fenceid,title);
//                expand.setTextViewText(R.id.messagegeo,contene);
//                expand.setTextViewText(R.id.advice,extra);
//                // expand.setImageViewResource(R.id.iconkid,R.drawable.alarm);
//
//
//                Icon icon = Icon.createWithResource(contexting,R.mipmap.strangeralone);
//                Notification.Builder builder = new Notification.Builder(contexting)
//                        .setSmallIcon(R.drawable.about)
//                        .setContentTitle(title)
//                        .setContentText(contene)
//                        .setLargeIcon(icon)
//                        .setVibrate(vib)
//                        .setAutoCancel(true)
//                        .setStyle(new Notification.DecoratedCustomViewStyle())
//                        .setCustomBigContentView(expand)
//                        .setSound(sound);
//                NotificationManager manager = (NotificationManager) contexting.getSystemService(Context.NOTIFICATION_SERVICE);
//                Intent intent = new Intent(contexting, EPADViewer.class);
//                Bundle bundle = new Bundle();
//                bundle.putString(contexting.getString(R.string.epadsitem),requestID);
//                intent.putExtra(contexting.getString(R.string.epadsitem),bundle);
//
//                PendingIntent pendingIntent = PendingIntent.getActivity(contexting, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                builder.setContentIntent(pendingIntent);
//                Notification notification = builder.build();
//
//                manager.notify(new Random().nextInt(), notification);
//            }catch (NullPointerException e){
//                // Toast.makeText(contexting, "Cannot send Notification", Toast.LENGTH_SHORT).show();
//            }
//
//        }*/





    }

    public void sendnotice(){

        Collections.sort(notifications, new Comparator<Notification>() {
            @Override
            public int compare(Notification o1, Notification o2) {
                return String.valueOf(o2.getDate()).compareTo(String.valueOf(o1.getDate()));
            }
        });

        // TODO: send broadcast to Notification.class here
        Intent noticeintent = new Intent(getString(R.string.noticereceive));
        noticeintent.putParcelableArrayListExtra(getString(R.string.noticearraylist),notifications);
        sendBroadcast(noticeintent);
    }


    BroadcastReceiver onNoticeStart  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Receive broadcast when notification class is started

            Intent noticeintent = new Intent(getString(R.string.noticereceive));
            noticeintent.putParcelableArrayListExtra(getString(R.string.noticearraylist),notifications);
            sendBroadcast(noticeintent);
        }
    };

    BroadcastReceiver deletebroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra(getString(R.string.deleted));
            if(bundle != null){

                int p = bundle.getInt(getString(R.string.deleted));
                notifications.remove(p);

            }
        }
    };

    public void registerbroadcasts(){

        IntentFilter noticefilter = new IntentFilter(getString(R.string.noticestart));
        registerReceiver(onNoticeStart,noticefilter);

        IntentFilter deletefilter = new IntentFilter(getString(R.string.deleted));
        registerReceiver(deletebroadcast,deletefilter);


    }



    public ArrayList<String> interestseperate(String interest){

        // Extracting all the Interest seperated by comma and storing it in a list
        ArrayList<String> interestarray = new ArrayList<>();
        int currentposition = 0;
        int storedposition = 0;
        for (int i = 0; i < interest.length();i++){

            char symbol = interest.charAt(i);

            // if the symbol is "," then the position is store and it is used to split the string
            // then that position is stored and increment by one to chater for the next interest
            // so that we can extract the next interest
            if(symbol == ','){
                currentposition = i;
                String singleinterest = interest.substring(storedposition,currentposition);
                interestarray.add(singleinterest.trim());
                storedposition = currentposition + 1;
            }else if(i == (interest.length() - 1)){

                currentposition = i;
                String singleinterest = interest.substring(storedposition,currentposition);
                interestarray.add(singleinterest.trim());

            }

        }
        return interestarray;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void getEPADs(){

        GeoSQLLite geoSQLLite = new GeoSQLLite(this,getString(R.string.epads),null,1);
        geoSQLLite.deleteAllGeofence();


        FirebaseDatabase.getInstance().getReference().child(getString(R.string.epads)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EPAds epAds = snapshot.getValue(EPAds.class);

                    if (epAds != null) {
                       // Toast.makeText(InterestService.this, "EP Ads added", Toast.LENGTH_SHORT).show();
                        geoSQLLite.addGeofence(epAds.getId(),epAds.getLatitude(),epAds.getLongitude(),epAds.getMessage(),epAds.getTitle(),epAds.getFiletype());
                    }
                }

                startService(new Intent(InterestService.this,ElectroPhyService.class));




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
