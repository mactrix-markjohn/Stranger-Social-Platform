package com.mactrixapp.www.stranger;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.AnalysAdapter;
import com.mactrixapp.www.stranger.Adapters.EpAdsAdapter;
import com.mactrixapp.www.stranger.Model.EPAds;
import com.mactrixapp.www.stranger.Model.IsListContain;
import com.mactrixapp.www.stranger.Model.MenuModel;
import com.mactrixapp.www.stranger.Model.PairValueHolder;
import com.mactrixapp.www.stranger.Model.PlaybackStudio;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.Model.VideoStudio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class EPADViewer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String epadsId = "empty";
    private DatabaseReference epadReference;

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
    private TextView epadvenue;
    private TextView eptitle;
    private TextView epmessage;

    private String filetype;
    private EPAds epAds;
    private CircleImageView epviewimage;
    private TextView epviewname;

    private String senderid;
    private FirebaseUser currentuser;
    private RelativeLayout analysislay;
    private TextView viewers;
    private IsListContain contains;
    private ArrayList<User> users;
    private ListView regionlist;
    private ListView workstatelist;
    private ListView profeesionlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epadviewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // initialize the views
        // media players
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        epadReference =  FirebaseDatabase.getInstance().getReference().child(getString(R.string.epads));
        playstudio = new PlaybackStudio(this);
        contains = new IsListContain();
        users = new ArrayList<>();
        videostudio = new VideoStudio(this);
        filetype = getString(R.string.none);

        epviewimage = (CircleImageView)findViewById(R.id.epviewimage);
        epviewname = (TextView)findViewById(R.id.epviewname);
        eptitle = (TextView)findViewById(R.id.eptitle);
        epmessage = (TextView)findViewById(R.id.epmessage);
        epadvenue = (TextView)findViewById(R.id.epadvenue);

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

        // Analysis Layout
        analysislay = (RelativeLayout)findViewById(R.id.analysislay);
        viewers = (TextView)findViewById(R.id.viewers);
        regionlist = (ListView)findViewById(R.id.regionlist);
        profeesionlist = (ListView)findViewById(R.id.professionlist);
        workstatelist  = (ListView)findViewById(R.id.workstatelist);




        // get the EPAd key
        Bundle bundle  = getIntent().getBundleExtra(getString(R.string.epadsitem));
        if (bundle != null) {
            epadsId = bundle.getString(getString(R.string.epadsitem)) != null ? bundle.getString(getString(R.string.epadsitem)) : "hbhidsiuisiusghiudfsghl";
        }

        // extract the neccessary details
        epadReference.child(Objects.requireNonNull(epadsId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                epAds = dataSnapshot.getValue(EPAds.class);

                if (epAds != null) {

                    senderid = epAds.getSenderid();
                    sendersInfo(senderid);

                    analysisSheet(senderid);

                    if (epAds.getFiletype().equals(getString(R.string.none))) {
                        showmessage(epAds);

                    } else if (epAds.getFiletype().equals(getString(R.string.image))) {
                        showimage(epAds);
                    } else if (epAds.getFiletype().equals(getString(R.string.video))) {
                        showvideo(epAds);
                    } else if (epAds.getFiletype().equals(getString(R.string.audio))) {
                        showaudio(epAds);
                    } else if (epAds.getFiletype().equals(getString(R.string.other))) {
                        showfile(epAds);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // This algorithm stores the unique users that view this EPAd. it show the count to the sender user
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(currentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if(user != null){

                    if(!user.getUserid().equalsIgnoreCase(senderid)){

                       epadReference.child(epadsId).child(getString(R.string.views)).child(user.getUserid()).setValue(user);

                    }


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
    }

    public void analysisSheet(String senderid){

        if(currentuser.getUid().equalsIgnoreCase(senderid)) {
            analysislay.setVisibility(View.VISIBLE);

            epadReference.child(epadsId).child(getString(R.string.views)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    long views = dataSnapshot.getChildrenCount();
                    viewers.setText(String.valueOf(views));

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                        User user = snapshot.getValue(User.class);

                        if (user != null) {

                            if (!contains.isUserContain(users,user)) {

                                users.add(user);
                            }


                        }
                    }





                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    public void regioncount(ArrayList<User> users){
        Map<String,Long> regions = new HashMap<>();

        for (User user : users){

            String region = user.getOrigin();
            if (regions.containsKey(region)) { // if it contains the key

                long value = regions.get(region); // it gets the value
                value++; // increment the value
                regions.put(region,value); // update the value count


            }else{

                long value = 1; // make value 1
                regions.put(region,value); // and add to the map

            }

        }

        ArrayList<PairValueHolder> valuesList = new ArrayList<>();
        Set key = regions.keySet();


        for (String keyly: regions.keySet()){

            long count = regions.get(keyly);
            PairValueHolder valueHolder = new PairValueHolder(keyly,count);
            valuesList.add(valueHolder);


        }

        AnalysAdapter analysAdapter = new AnalysAdapter(EPADViewer.this,valuesList);
        regionlist.setAdapter(analysAdapter);



    }
    public void professioncount(){

        Map<String,Long> regions = new HashMap<>();

        for (User user : users){

            String region = user.getProfession();
            if (regions.containsKey(region)) { // if it contains the key

                long value = regions.get(region); // it gets the value
                value++; // increment the value
                regions.put(region,value); // update the value count

            }else{

                long value = 1; // make value 1
                regions.put(region,value); // and add to the map

            }

        }

        ArrayList<PairValueHolder> valuesList = new ArrayList<>();
        Set key = regions.keySet();


        for (String keyly: regions.keySet()){

            long count = regions.get(keyly);
            PairValueHolder valueHolder = new PairValueHolder(keyly,count);
            valuesList.add(valueHolder);


        }

        AnalysAdapter analysAdapter = new AnalysAdapter(EPADViewer.this,valuesList);
        profeesionlist.setAdapter(analysAdapter);

    }
    public void statuscount(){
        Map<String,Long> regions = new HashMap<>();

        for (User user : users){

            String region = user.getWorkstate();
            if (regions.containsKey(region)) { // if it contains the key

                long value = regions.get(region); // it gets the value
                value++; // increment the value
                regions.put(region,value); // update the value count

            }else{

                long value = 1; // make value 1
                regions.put(region,value); // and add to the map

            }

        }

        ArrayList<PairValueHolder> valuesList = new ArrayList<>();
        Set key = regions.keySet();


        for (String keyly: regions.keySet()){

            long count = regions.get(keyly);
            PairValueHolder valueHolder = new PairValueHolder(keyly,count);
            valuesList.add(valueHolder);


        }

        AnalysAdapter analysAdapter = new AnalysAdapter(EPADViewer.this,valuesList);
        workstatelist.setAdapter(analysAdapter);
    }

    public void sendersInfo(String senderid){

        FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(senderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null) {

                    epviewname.setText(user.getFullname());
                    try {
                        Glide.with(EPADViewer.this).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(epviewimage);
                    } catch (Exception e) {
                        epviewimage.setImageURI(Uri.parse(user.getPhotoUrl()));
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void showmessage(EPAds epAds){

        eptitle.setText(epAds.getTitle());
        epmessage.setText(epAds.getMessage());
        epadvenue.setText(epAds.getVenue());
    }

    public void showimage(EPAds epAds){

        showmessage(epAds);
        epimage.setVisibility(View.VISIBLE);
        epvideolay.setVisibility(View.GONE);
        epaudiocard.setVisibility(View.GONE);
        epfilecard.setVisibility(View.GONE);

        try{
            Glide.with(this).load(Uri.parse(epAds.getFileurl())).asBitmap().into(epimage);
        }
        catch (Exception e){
            epimage.setImageURI(Uri.parse(epAds.getFileurl()));
        }
    }
    public void showvideo(EPAds epAds){

        showmessage(epAds);
        epvideolay.setVisibility(View.VISIBLE);
        epimage.setVisibility(View.GONE);
        epaudiocard.setVisibility(View.GONE);
        epfilecard.setVisibility(View.GONE);
        videostudio.playmedia(Uri.parse(epAds.getFileurl()),epvideoview);
    }
    public void showaudio(EPAds epAds){

        showmessage(epAds);
        epaudiocard.setVisibility(View.VISIBLE);
        epimage.setVisibility(View.GONE);
        epvideolay.setVisibility(View.GONE);
        epfilecard.setVisibility(View.GONE);
        String audioname = epAds.getAudioname();//audioname(Uri.parse(epAds.getFileurl()));
        epaudioname.setText(audioname);
       // playstudio.playMedia(epAds.getFileurl());
    }
    public void showfile(EPAds epAds){

        showmessage(epAds);
        epfilecard.setVisibility(View.VISIBLE);
        epimage.setVisibility(View.GONE);
        epvideolay.setVisibility(View.GONE);
        epaudiocard.setVisibility(View.GONE);
        String filename = epAds.getFilename();//filename(Uri.parse(epAds.getFileurl()));
        epfilename.setText(filename);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

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
        getMenuInflater().inflate(R.menu.epadviewer, menu);
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

    public void back(View view) {
        onBackPressed();
    }

    public void playpause(View view) {

        if (!playstudio.isPrepared()) {

            playstudio.playMedia(epAds.getFileurl());
            playpausecard.setImageResource(R.drawable.pause_bottom_sm);

        }else{

            if (playstudio.isPlaying()) {
                playstudio.pauseMedia();
                playpausecard.setImageResource(R.drawable.play_button_sm);

            }else{

                playstudio.resumeMedia();
                playpausecard.setImageResource(R.drawable.pause_bottom_sm);

            }

        }
    }

    public void createEPAD(View view) {

        startActivity(new Intent(this,NewEPAds.class));
    }

    public void download(View view) {

        Toast.makeText(this, "Please wait", Toast.LENGTH_LONG).show();

        if (epAds != null) {


            if (epAds.getFiletype().equals(getString(R.string.none))) {

                Toast.makeText(this, "Sorry, there is nothing to download", Toast.LENGTH_LONG).show();

            } else if (epAds.getFiletype().equals(getString(R.string.image))) {
                new MenuModel(this).donwloadimage(view,epAds.getFileurl(),"epadImage");

            } else if (epAds.getFiletype().equals(getString(R.string.video))) {

                new MenuModel(this).donwloadvideo(view,epAds.getFileurl(),"epadVideo");


            } else if (epAds.getFiletype().equals(getString(R.string.audio))) {

                new MenuModel(this).donwloadaudio(view,epAds.getFileurl(),epaudioname.getText().toString());


            } else if (epAds.getFiletype().equals(getString(R.string.other))) {

                new MenuModel(this).donwloadother(view,epAds.getFileurl(),epfilename.getText().toString());


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

    public void epprofile(View view) {

        if(senderid.equals(currentuser.getUid())){

            Bundle bundlee = new Bundle();
            bundlee.putInt("tab",4);
            Intent intente = new Intent(this,Stranger.class);
            intente.putExtra("tab",bundlee);
            startActivity(intente);

        }else {

            Intent intent = new Intent(EPADViewer.this, StrangerProfile.class);
            Bundle bundle = new Bundle();
            bundle.putString(EPADViewer.this.getString(R.string.strangerid), senderid);
            intent.putExtra(EPADViewer.this.getString(R.string.strangerid), bundle);
            startActivity(intent);

        }
    }
}
