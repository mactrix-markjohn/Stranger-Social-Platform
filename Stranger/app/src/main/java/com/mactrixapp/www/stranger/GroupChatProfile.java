package com.mactrixapp.www.stranger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
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
import com.mactrixapp.www.stranger.Adapters.GroupAdminAdapter;
import com.mactrixapp.www.stranger.Adapters.GroupMemberAdapter;
import com.mactrixapp.www.stranger.Adapters.GroupRequestAdapter;
import com.mactrixapp.www.stranger.Model.Group;
import com.mactrixapp.www.stranger.Model.IsListContain;
import com.mactrixapp.www.stranger.Model.User;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupChatProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CHANGE_GROUP_PHOTO = 1;
    private ImageView groupphoto;
    private EditText groupname;
    private TextView groupcreator;
    private EditText groupdescription;
    private TextView groupaccess;
    private EditText groupinvitelink;
    private TextView requestcount;
    private ListView groupjoinlist;
    private TextView admincount;
    private ListView groupadminlist;
    private TextView membercount;
    private ListView groupmemberlist;
    private String groupid;
    private DatabaseReference groupReference;
    private FirebaseUser currentuser;
    private Group group;
    private ArrayList<String> requestArray;
    private ArrayList<String> memberArray;
    private ArrayList<String> adminArray;
    private ArrayList<String> adminIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // get the Group id
        groupid = getIntent().getStringExtra(getString(R.string.group));
        if (groupid == null){
            onBackPressed();
        }

        // get the neccessary database reference
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        groupReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.group)).child(groupid);



        // Group profile header details
        groupphoto = (ImageView)findViewById(R.id.groupphoto);
        groupname = (EditText)findViewById(R.id.groupname);
        groupcreator = (TextView)findViewById(R.id.groupcreator);

        // group description and access
        groupdescription = (EditText)findViewById(R.id.groupdescription);
        groupaccess = (TextView)findViewById(R.id.groupaccess);
        groupinvitelink = (EditText)findViewById(R.id.groupinvitelink);

        // lists and its item count textview
        requestcount = (TextView)findViewById(R.id.requestcount);
        groupjoinlist = (ListView)findViewById(R.id.groupjoinlist);

        admincount = (TextView)findViewById(R.id.admincount);
        groupadminlist = (ListView)findViewById(R.id.groupadminlist);

        membercount = (TextView)findViewById(R.id.membercount);
        groupmemberlist = (ListView)findViewById(R.id.groupmemberlist);


        // set up profile details and handle list item click
        setUpProfile();
        handleListItemClick();

















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


    public void setUpProfile(){

        IsListContain iscontain = new IsListContain();

        // group info
        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                group = dataSnapshot.getValue(Group.class);
                if (group != null) {


                    groupname.setText(group.getName());
                    groupinvitelink.setText(group.getInvitelink());
                    groupaccess.setText(group.getAccess());
                    groupdescription.setText(group.getDescription());
                    try {
                        Glide.with(GroupChatProfile.this).load(Uri.parse(group.getImageurl())).asBitmap().into(groupphoto);
                    } catch (Exception e) {
                        groupphoto.setImageResource(R.mipmap.original);
                    }
                    FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(group.getCreatorid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            User user = dataSnapshot.getValue(User.class);
                            if(user != null){

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a  EE dd MMM, yyyy ", Locale.getDefault());
                                String date = simpleDateFormat.format(new Date(group.getDate()));

                                String create = "Created by "+user.getUsername()+" \nat "+date;
                                groupcreator.setText(create);
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


        // list of all Request to join group
        requestArray = new ArrayList<>();
        groupReference.child(getString(R.string.request)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    try {
                        String strangerid = snapshot.getValue(String.class) != null ? snapshot.getValue(String.class) : "hallla";
                        if (!requestArray.contains(strangerid)) {

                            requestArray.add(strangerid);
                        }
                    } catch (Exception e) {
                        Object hash = snapshot.getValue();

                    }

                }

                GroupRequestAdapter requestAdapter = new GroupRequestAdapter(GroupChatProfile.this,requestArray);
                groupjoinlist.setAdapter(requestAdapter);
                requestcount.setText(String.valueOf(groupjoinlist.getCount()));
                if (groupjoinlist.getCount()>0) {
                    groupjoinlist.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // list of all admin
         adminArray = new ArrayList<>();
         adminIdArray = new ArrayList<>();
        groupReference.child(getString(R.string.admin)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot:dataSnapshot.getChildren()){


                    String strangerid = snapshot.getValue(String.class) != null ? snapshot.getValue(String.class) : "hallla";

                    if (!adminArray.contains(strangerid)) {

                        adminArray.add(strangerid);
                        adminIdArray.add(strangerid);

                    }
                }

                GroupAdminAdapter adminAdapter = new GroupAdminAdapter(GroupChatProfile.this,adminArray);
                groupadminlist.setAdapter(adminAdapter);
                admincount.setText(String.valueOf(groupadminlist.getCount()));
                if (groupadminlist.getCount()>0) {
                    groupadminlist.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // list of all members
        memberArray = new ArrayList<>();
        groupReference.child(getString(R.string.members)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                   String strangerid = snapshot.getValue(String.class) != null ? snapshot.getValue(String.class) : "hallla";

                    if (!memberArray.contains(strangerid)) {

                        memberArray.add(strangerid);
                    }

                }

                GroupMemberAdapter memberAdapter = new GroupMemberAdapter(GroupChatProfile.this,memberArray);
                groupmemberlist.setAdapter(memberAdapter);
                membercount.setText(String.valueOf(groupmemberlist.getCount()));
                if (groupmemberlist.getCount()>0) {
                    groupmemberlist.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    public void handleListItemClick(){

        groupjoinlist.setOnItemClickListener((parent, view, position, id) -> {
            String strangerid = requestArray.get(position);


            if (adminIdArray.contains(currentuser.getUid())) {


                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatProfile.this);

                builder.setPositiveButton("Grant Permission", (dialog, which) -> {

                    addMember(strangerid);
                    removeRequest(strangerid);


                });

                builder.setNegativeButton("Denial Permission", (dialog, which) -> {

                    removeRequest(strangerid);

                });

                builder.setNeutralButton("Stranger Profile",(dialog, which) -> {

                    openStrangerProfile(strangerid);
                });

                builder.create().show();


            }else{

                Toast.makeText(this, "This is for Admins Only", Toast.LENGTH_LONG).show();
            }







        });


        groupadminlist.setOnItemClickListener((parent, view, position, id) -> {

            String strangerid = adminArray.get(position);


            if (currentuser.getUid().equalsIgnoreCase(group.getCreatorid())) {

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatProfile.this);

                builder.setPositiveButton("Remove Stranger", (dialog, which) -> {

                    removeAdmin(strangerid);

                });

                builder.setNeutralButton("Stranger Profile",(dialog, which) -> {

                    openStrangerProfile(strangerid);
                });

                builder.create().show();


            }else{

                Toast.makeText(this, "Sorry you are not the Creator of this Group", Toast.LENGTH_LONG).show();
            }




        });

        groupmemberlist.setOnItemClickListener((parent, view, position, id) -> {

            String strangerid = memberArray.get(position);




                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatProfile.this);

                builder.setPositiveButton("Make Stranger Admin", (dialog, which) -> {

                    if (adminIdArray.contains(currentuser.getUid())) {

                        addAdmin(strangerid);

                    }else{

                        Toast.makeText(this, "This is for Admins Only", Toast.LENGTH_LONG).show();
                    }



                });

                builder.setNegativeButton("Remove Stranger", (dialog, which) -> {

                    if (adminIdArray.contains(currentuser.getUid())) {

                        removeMember(strangerid);

                    }else{

                        Toast.makeText(this, "This is for Admins Only", Toast.LENGTH_LONG).show();
                    }

                });

                builder.setNeutralButton("Stranger Profile",(dialog, which) -> {

                    openStrangerProfile(strangerid);
                });

                builder.create().show();




        });

    }


    public void exitgroup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatProfile.this);

        builder.setMessage("Are you sure you want to do this?");

        builder.setPositiveButton("Yes", (dialog, which) -> {

            removeMember(currentuser.getUid());
            finish();

        });

        builder.setNegativeButton("No", (dialog, which) -> {

            dialog.cancel();

        });


        builder.create().show();



    }

    public void addmember(View view) {

        if (adminIdArray.contains(currentuser.getUid())) {

            showaddDialog();
        }else{

            Toast.makeText(this, "This is for Admins Only", Toast.LENGTH_LONG).show();

        }

    }

    public void searchmember(View view) {

        showsearchDialog();

    }


    public void showaddDialog(){
        IsListContain contain = new IsListContain();
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_add_group);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.show();

        ImageView cancel = (ImageView)d.findViewById(R.id.cancel);
        ListView addlist = (ListView) d.findViewById(R.id.groupaddlist);

        cancel.setOnClickListener(ccc -> d.dismiss());

        ArrayList<String> friendarray = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(currentuser.getUid()).child(getString(R.string.friends)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);
                    if(user != null){

                        if(!friendarray.contains(user.getUserid())){
                            friendarray.add(user.getUserid());
                        }


                    }

                }

                GroupMemberAdapter memberAdapter = new GroupMemberAdapter(GroupChatProfile.this,friendarray);
                addlist.setAdapter(memberAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        addlist.setOnItemClickListener((parent, view, position, id) -> {


            String strangerid = friendarray.get(position);
            addMember(strangerid);
            d.dismiss();



        });


    }
    public void showsearchDialog(){
        IsListContain contain = new IsListContain();

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


        ArrayList<String> searchuser = new ArrayList<>();
        searchfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                groupReference.child(getString(R.string.members)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            String strangerid = snapshot.getValue(String.class);

                            FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(strangerid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    User user = dataSnapshot.getValue(User.class);
                                    if(user != null){


                                        String username = user.getUsername().toLowerCase();
                                        String fullname = user.getFullname().toLowerCase();
                                        String searchname = s.toString().toLowerCase();

                                        if (username.contains(searchname) || fullname.contains(searchname)) {


                                            if(!searchuser.contains(user.getUserid())){
                                                searchuser.add(user.getUserid());
                                            }


                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });






                        }


                        GroupMemberAdapter memberAdapter = new GroupMemberAdapter(GroupChatProfile.this,searchuser);
                        addlist.setAdapter(memberAdapter);
                        searchcount.setText(String.valueOf(addlist.getCount()));

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

        addlist.setOnItemClickListener((parent, view, position, id) -> {

            String strangerid = searchuser.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatProfile.this);
            builder.setPositiveButton("Make Stranger Admin", (dialog, which) -> {
                if (adminIdArray.contains(currentuser.getUid())) {

                    addAdmin(strangerid);

                }else{

                    Toast.makeText(this, "This is for Admins Only", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Remove Stranger", (dialog, which) -> {

                if (adminIdArray.contains(currentuser.getUid())) {

                    removeMember(strangerid);

                }else{

                    Toast.makeText(this, "This is for Admins Only", Toast.LENGTH_LONG).show();
                }

            });
            builder.setNeutralButton("Stranger Profile",(dialog, which) -> {

                openStrangerProfile(strangerid);
            });
            builder.create().show();


        });





    }




    public void grantallrequest(View view) {

        if (adminIdArray.contains(currentuser.getUid())) {


            for(String strangerid : requestArray){

                addMember(strangerid);
                removeRequest(strangerid);



            }


        }else{

            Toast.makeText(this, "This is for Admins Only", Toast.LENGTH_LONG).show();
        }



    }

    public void editgroupdescription(View view) {


        if (adminIdArray.contains(currentuser.getUid())) {

            AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatProfile.this);

            builder.setPositiveButton("Save", (dialog, which) -> {

                changegroupinfo(getString(R.string.description),groupdescription.getText().toString());

            });

            builder.setNeutralButton("Edit",(dialog, which) -> {

                groupdescription.setEnabled(true);

            });

            builder.create().show();


        }else{
            Toast.makeText(this, "This is for Admins only", Toast.LENGTH_LONG).show();

        }





    }

    public void editgroupname(View view) {


        if (adminIdArray.contains(currentuser.getUid())) {

            AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatProfile.this);

            builder.setPositiveButton("Save", (dialog, which) -> {

                changegroupinfo(getString(R.string.name),groupname.getText().toString());

            });

            builder.setNeutralButton("Edit",(dialog, which) -> {

                groupname.setEnabled(true);
            });

            builder.create().show();

        }else {

            Toast.makeText(this, "This is for Admins Only", Toast.LENGTH_LONG).show();
        }



    }

    public void changegrouphoto(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,CHANGE_GROUP_PHOTO);


    }

    public void back(View view) {

        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == CHANGE_GROUP_PHOTO) {

                Uri uri = data.getData();

                if (uri != null) {

                    changephoto(uri,getString(R.string.group));
                }

            }


        }



    }


    public void changephoto(Uri uri,String grouptype){


        groupphoto.setImageURI(uri);

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

                        changegroupinfo(getString(R.string.imageurl),fileurl);

                    }
                });





            }


        }catch (Exception e){

            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }



    }
    public void changegroupinfo(String childname, String value){


        Map<String,Object> updateMap = new HashMap<>();
        updateMap.put(childname,value);
        groupReference.updateChildren(updateMap);


    }

    public void addMember(String strangerid){

        groupReference.child(getString(R.string.members)).child(strangerid).setValue(strangerid).addOnSuccessListener( succ -> Toast.makeText(this, "Stranger added successfully", Toast.LENGTH_SHORT).show());



    }

    public void addAdmin(String strangerid){

        groupReference.child(getString(R.string.admin)).child(strangerid).setValue(strangerid).addOnSuccessListener( succ -> Toast.makeText(this, "Stranger added successfully", Toast.LENGTH_SHORT).show());



    }
    public void removeMember(String strangerid){

        groupReference.child(getString(R.string.members)).child(strangerid).removeValue().addOnSuccessListener(succ -> Toast.makeText(this, "Removed Stranger", Toast.LENGTH_SHORT).show());

    }
    public void removeAdmin(String strangerid){

        groupReference.child(getString(R.string.admin)).child(strangerid).removeValue().addOnSuccessListener(succ -> Toast.makeText(this, "Removed Stranger", Toast.LENGTH_SHORT).show());

    }
    public void removeRequest(String strangerid){

        groupReference.child(getString(R.string.request)).child(strangerid).removeValue().addOnSuccessListener(succ -> Toast.makeText(this, "Removed Stranger", Toast.LENGTH_SHORT).show());

    }

    public void openStrangerProfile(String strangerid){

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.strangerid), strangerid);
        Intent intent = new Intent(this, StrangerProfile.class);
        intent.putExtra(getString(R.string.strangerid), bundle);
        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.group_chat_profile, menu);
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


    public void groupaccess(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMultiChoiceItems(new String[]{"Public", "Private"}, new boolean[]{false, false}, (dialog, which, isChecked) -> {
            switch (which) {
                case 0:
                  groupaccess.setText("Public");
                  changegroupinfo(getString(R.string.access),getString(R.string.publicaccess));
                    dialog.dismiss();
                    break;
                case 1:
                    groupaccess.setText("Private");
                    changegroupinfo(getString(R.string.access),getString(R.string.privateaccess));
                    dialog.dismiss();
                    break;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();



    }
}
