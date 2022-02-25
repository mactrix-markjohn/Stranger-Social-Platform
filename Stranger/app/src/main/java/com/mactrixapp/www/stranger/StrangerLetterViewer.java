package com.mactrixapp.www.stranger;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Model.FontModel;
import com.mactrixapp.www.stranger.Model.Letter;
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StrangerLetterViewer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView letterpaper;
    private ImageView letterbackimage;
    private CircleImageView strangerphoto;
    private String letterid;
    private Letter letter;
    private User user;
    private String strangerid;
    private ArrayList<FontModel> fontModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stranger_letter_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        letterpaper = (TextView) findViewById(R.id.letterpaper);
        letterbackimage = (ImageView)findViewById(R.id.letterbackimage);
        strangerphoto = (CircleImageView)findViewById(R.id.strangerphoto);
        
        Bundle bundle = getIntent().getBundleExtra(getString(R.string.letter));
        if(bundle != null){
            letterid = bundle.getString(getString(R.string.letter));
        }


        fontModels = new ArrayList<>();

        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"alluraregular.otf"),0,getString(R.string.alluraregular),"Allura Regular"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"bernfash.ttf"),0,getString(R.string.bernfash),"Bern Fash"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"caviardreams.ttf"),0,getString(R.string.caviardreams),"Caviar Dreams"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"dancingscriptregular.otf"),0,getString(R.string.dancingscriptregular),"Dancing Script Regular"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"e111viva.ttf"),0,getString(R.string.e111viva),"E111Viva"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"humn.ttf"),0,getString(R.string.humn),"Humn"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"kabeln.ttf"),0,getString(R.string.kabeln),"Kabeln"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"kaushanscriptregular.otf"),0,getString(R.string.kaushanscriptregular),"Kaushan Script Regular"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"ralewayregular.ttf"),0,getString(R.string.ralewayregular),"Raleway Regular"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"stacc222.ttf"),0,getString(R.string.stacc222),"Stacc222"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"typoupri.ttf"),0,getString(R.string.typoupri),"Typo Upri"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"windsong.ttf"),0,getString(R.string.windsong),"Wind Song"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"lithogrl.ttf"),0,getString(R.string.lithogrl),"Lithogrl"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"ozhandrm.ttf"),0,getString(R.string.ozhandrm),"Ozhandrm"));





        FirebaseDatabase.getInstance().getReference().child(getString(R.string.letter)).child(letterid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                letter = dataSnapshot.getValue(Letter.class);

                if (letter != null) {
                    strangerid = letter.getSenderid();
                    letterbackimage.setImageResource(letter.getLetterbackres());
                    letterpaper.setText(letter.getMessage());

                    for (FontModel model:fontModels){
                        if(model.getFontid().equalsIgnoreCase(letter.getTypefaceid())){

                            letterpaper.setTypeface(model.getTypeface());

                        }

                    }



                    FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(letter.getSenderid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            user = dataSnapshot.getValue(User.class) != null ? dataSnapshot.getValue(User.class) : new User(letter.getSenderid(), letter.getSenderfullname(),letter.getSenderusername());

                            if (user != null) {
                                try {
                                    Glide.with(StrangerLetterViewer.this).load(Uri.parse(user.getPhotoUrl())).asBitmap().placeholder(R.mipmap.strangeralone).into(strangerphoto);
                                } catch (Exception e) {
                                    strangerphoto.setImageURI(Uri.parse(user.getPhotoUrl()));
                                }

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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

       /* NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
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
        getMenuInflater().inflate(R.menu.stranger_letter_viewer, menu);
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

    public void strangerprofile(View view) {

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.strangerid), strangerid);
        Intent intent = new Intent(this, StrangerProfile.class);
        intent.putExtra(getString(R.string.strangerid), bundle);
        startActivity(intent);


    }
}
