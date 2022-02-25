package com.mactrixapp.www.stranger;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.GroupMemberAdapter;
import com.mactrixapp.www.stranger.Adapters.ReputationAdapter;
import com.mactrixapp.www.stranger.Model.IsListContain;
import com.mactrixapp.www.stranger.Model.ReputationModel;
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Reputation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView reputationlist;
    private ArrayList<ReputationModel> reputations;

   // private FirebaseUser currentuser;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reputation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        reputationlist = (ListView)findViewById(R.id.reputationlist);
        reputations = new ArrayList<>();
       // currentuser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user));

        retrieveReputation();

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

/*        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
    }
    IsListContain contains;

    public void retrieveReputation(){
        contains = new IsListContain();

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);

                    if(user != null){


                        long followers =snapshot.child(getString(R.string.strangers)).getChildrenCount();
                        ReputationModel reputation = new ReputationModel(0,followers,user);
                        if(!contains.isReputationContain(reputations,reputation)){

                            reputations.add(reputation);
                        }

                    }



                }


                Collections.sort(reputations, new Comparator<ReputationModel>() {
                    @Override
                    public int compare(ReputationModel o1, ReputationModel o2) {
                        return String.valueOf(o2.getFollowers()).compareTo(String.valueOf(o1.getFollowers()));
                    }
                });


                for (int i=0; i < reputations.size();i++){

                    ReputationModel reputationModel = reputations.get(i);
                    int rank = i + 1;
                    reputationModel.setRank(rank);
                    reputations.set(i,reputationModel);


                }


                ReputationAdapter adapter = new ReputationAdapter(Reputation.this,reputations);
                reputationlist.setAdapter(adapter);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    ArrayList<ReputationModel> searchuser;
    public void showSearchDialog(){

        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_search_group);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.show();
        ImageView cancel = (ImageView)d.findViewById(R.id.cancel);
        ListView addlist = (ListView) d.findViewById(R.id.groupaddlist);
        EditText searchfield = (EditText)d.findViewById(R.id.searchfield);
        TextView searchcount = (TextView)d.findViewById(R.id.searchcount);
        ImageView search = (ImageView)d.findViewById(R.id.search);

        cancel.setOnClickListener(ccc -> d.dismiss());


        searchuser = new ArrayList<>();
        searchfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                for (int i=0; i < reputations.size(); i++){
                    String name = reputations.get(i).getUser().getFullname() != null ?  reputations.get(i).getUser().getFullname().toLowerCase() : "trhdfyshnchjhjfs" ;
                    String username = reputations.get(i).getUser().getUsername() != null ? reputations.get(i).getUser().getUsername().toLowerCase() : "fjdfjdfjfjdfnmdfkjdf";
                    String searchname = s.toString().toLowerCase();
                    ReputationModel reputation = reputations.get(i);

                    if(name.contains(searchname) || username.contains(searchname)){
                        searchuser = new ArrayList<>();

                        if (!contains.isReputationContain(searchuser,reputation)) {

                            searchuser.add(reputation);
                        }


                    }



                }

                ReputationAdapter adapter = new ReputationAdapter(Reputation.this,searchuser);
                addlist.setAdapter(adapter);
                searchcount.setText(String.valueOf(addlist.getCount()));



            }

            @Override
            public void afterTextChanged(Editable s) {



            }
        });

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
        getMenuInflater().inflate(R.menu.reputation, menu);
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

    public void cancel(View view) {
        onBackPressed();
    }

    public void search(View view) {
        showSearchDialog();
    }
}
