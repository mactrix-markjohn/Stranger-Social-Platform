package com.mactrixapp.www.stranger;

import android.icu.util.Freezable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.SearchGroupAdapter;
import com.mactrixapp.www.stranger.Model.Group;
import com.mactrixapp.www.stranger.Model.IsListContain;

import java.util.ArrayList;

public class SearchGroup extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText searchfield;
    private TextView searchcount;
    private ListView searchlist;
    private ArrayList<Group> groups;
    private DatabaseReference groupReference;
    private DatabaseReference stranGroupRef;
    private IsListContain contain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        searchfield = (EditText)findViewById(R.id.searchfield);
        searchcount = (TextView)findViewById(R.id.searchcount);
        searchlist = (ListView)findViewById(R.id.searchlist);

        contain = new IsListContain();

        groupReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.group));
        stranGroupRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.strangersgroup));
        groups = new ArrayList<>();


        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Group group = snapshot.getValue(Group.class);

                    if(group != null){
                        group.setGrouptype(getString(R.string.group));

                        String groupname = group.getName().toLowerCase();



                            if (!contain.isGroupContain(groups,group)) {
                                groups.add(group);
                            }



                    }

                }

                SearchGroupAdapter groupAdapter = new SearchGroupAdapter(SearchGroup.this,groups);
                searchlist.setAdapter(groupAdapter);
                searchcount.setText(String.valueOf(searchlist.getCount()));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        stranGroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Group group = snapshot.getValue(Group.class);

                    if(group != null){
                        group.setGrouptype(getString(R.string.strangersgroup));

                        String groupname = group.getName().toLowerCase();



                            if (!contain.isGroupContain(groups,group)) {
                                groups.add(group);
                            }



                    }

                }

                SearchGroupAdapter groupAdapter = new SearchGroupAdapter(SearchGroup.this,groups);
                searchlist.setAdapter(groupAdapter);
                searchcount.setText(String.valueOf(searchlist.getCount()));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        searchfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                groups = new ArrayList<>();

                groupReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String searchname = s.toString().toLowerCase();

                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                            Group group = snapshot.getValue(Group.class);

                            if(group != null){
                                group.setGrouptype(getString(R.string.group));

                                String groupname = group.getName().toLowerCase();

                                if (groupname.contains(searchname)) {

                                    if (!contain.isGroupContain(groups,group)) {
                                        groups.add(group);
                                    }

                                }

                            }

                        }

                        SearchGroupAdapter groupAdapter = new SearchGroupAdapter(SearchGroup.this,groups);
                        searchlist.setAdapter(groupAdapter);
                        searchcount.setText(String.valueOf(searchlist.getCount()));


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                stranGroupRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String searchname = s.toString().toLowerCase();

                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                            Group group = snapshot.getValue(Group.class);

                            if(group != null){
                                group.setGrouptype(getString(R.string.strangersgroup));

                                String groupname = group.getName().toLowerCase();

                                if (groupname.contains(searchname)) {

                                    if (!contain.isGroupContain(groups,group)) {
                                        groups.add(group);
                                    }

                                }

                            }

                        }

                        SearchGroupAdapter groupAdapter = new SearchGroupAdapter(SearchGroup.this,groups);
                        searchlist.setAdapter(groupAdapter);
                        searchcount.setText(String.valueOf(searchlist.getCount()));


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void afterTextChanged(Editable s) {

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
        getMenuInflater().inflate(R.menu.search_group, menu);
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
}
