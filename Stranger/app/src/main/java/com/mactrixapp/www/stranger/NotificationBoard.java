package com.mactrixapp.www.stranger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.NoticeAdapter;
import com.mactrixapp.www.stranger.Model.IsListContain;
import com.mactrixapp.www.stranger.Model.IsNoticeContain;
import com.mactrixapp.www.stranger.Model.Letter;
import com.mactrixapp.www.stranger.Model.LetterFeatureHolder;
import com.mactrixapp.www.stranger.Model.Notification;
import com.mactrixapp.www.stranger.Model.Request;
import com.mactrixapp.www.stranger.Model.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView noticelist;
    private DatabaseReference requestReference;
    private DatabaseReference userReference;
    private FirebaseUser currentuser;
    private ArrayList<Notification> notifications;
    private IsNoticeContain isListContain;
    private DatabaseReference letterReference;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        noticelist = (ListView)findViewById(R.id.noticelist);
        isListContain = new IsNoticeContain();

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(currentuser.getUid());
        requestReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.request));
        letterReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.letter));

        // Get the Current user User.class
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class) != null ? dataSnapshot.getValue(User.class) : new User(currentuser.getUid(),currentuser.getEmail(),currentuser.getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        notifications = new ArrayList<>();
        registerBroadcasts();
        /*requestListener();
        letterListener();*/

        // send the start broadcast to interest service, to send this activity all the notification
        sendBroadcast(new Intent(getString(R.string.noticestart)));


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


                NoticeAdapter noticeAdapter = new NoticeAdapter(NotificationBoard.this,notifications);
                noticelist.setAdapter(noticeAdapter);

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

                        if (letter != null) {

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

                                if (distance[0] <= 30000) {
                                    // The stranger is within 30km around me

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

                NoticeAdapter noticeAdapter = new NoticeAdapter(NotificationBoard.this,notifications);
                noticelist.setAdapter(noticeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notification_board, menu);
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

    public void back(View view) {
        onBackPressed();
    }

    public void more(View view) {
    }

    BroadcastReceiver noticeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Receive the Notification Arraylist of the Interest Service

            if(intent != null) {

                notifications = intent.getParcelableArrayListExtra(getString(R.string.noticearraylist));
                if (notifications != null) {

                    Collections.sort(notifications, new Comparator<Notification>() {
                        @Override
                        public int compare(Notification o1, Notification o2) {
                            return String.valueOf(o2.getDate()).compareTo(String.valueOf(o1.getDate()));
                        }
                    });


                    NoticeAdapter noticeAdapter = new NoticeAdapter(NotificationBoard.this, notifications);
                    noticelist.setAdapter(noticeAdapter);
                }
            }

        }
    };

    public void registerBroadcasts(){

        IntentFilter noticereceivefilter = new IntentFilter(getString(R.string.noticereceive));
        registerReceiver(noticeReceiver,noticereceivefilter);

    }


}
