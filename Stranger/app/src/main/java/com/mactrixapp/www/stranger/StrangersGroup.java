package com.mactrixapp.www.stranger;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.mactrixapp.www.stranger.Adapters.ChatAdapter;
import com.mactrixapp.www.stranger.Adapters.StrangerGroupAdapter;
import com.mactrixapp.www.stranger.Model.Chat;
import com.mactrixapp.www.stranger.Model.Group;
import com.mactrixapp.www.stranger.Model.IsListContain;
import com.mactrixapp.www.stranger.Model.PlaybackStudio;
import com.mactrixapp.www.stranger.Model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StrangersGroup extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int ADD_PHOTO = 10;
    private static final int ADD_FILE = 20;
    private RecyclerView chatview;
    private CircleImageView chatuserphoto;
    private TextView chatusername;
    private EditText chatype;
    //private String strangerid;
    private String activityname;
    private FirebaseUser currentUser;
    private DatabaseReference strangerReference;
    private DatabaseReference userReference;
    private DatabaseReference chatReference;
    private User stranger;
    private String userid;
    private String filetype;
    private TextView interestfloat;
    private TextView lastseentext;
    private ImageView connectioncolor;
    private Uri fileuri;
    private StorageReference chatstorage;
    private int resumePosition;
    private boolean prepared;
    private int count;
    private String groupid;
    private DatabaseReference groupReference;
    private SharedPref lastChatPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strangers_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // initialize the views
        chatview = (RecyclerView)findViewById(R.id.chatview);
        chatuserphoto  = (CircleImageView)findViewById(R.id.chatuserphoto);
        chatusername = (TextView)findViewById(R.id.chatusername);
        interestfloat = (TextView)findViewById(R.id.interestfloat);
        lastseentext = (TextView)findViewById(R.id.chatuserfullname);
        connectioncolor = (ImageView)findViewById(R.id.connectioncolor);
        chatype = (EditText)findViewById(R.id.chattype);
        filetype = getString(R.string.chatfiletype);

        groupid = getIntent().getStringExtra(getString(R.string.group));
        if (groupid == null){
            onBackPressed();
        }



        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userid = currentUser.getUid();
        }

        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(currentUser.getUid());
        chatstorage = FirebaseStorage.getInstance().getReference().child(getString(R.string.strangersgroup));
        String key = currentUser.getUid().concat(groupid);
        lastChatPref = new SharedPref(this,key);
        groupReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.strangersgroup)).child(groupid);
        chatReference = groupReference.child(getString(R.string.chat));
        receivemessage();
        setGroupInfo();













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

    public void setGroupInfo(){

        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Group group = dataSnapshot.getValue(Group.class);
                if (group != null) {

                    chatusername.setText(group.getName());
                    interestfloat.setText(group.getDescription());
                    try {
                        Glide.with(StrangersGroup.this).load(Uri.parse(group.getImageurl())).asBitmap().into(chatuserphoto);
                    } catch (Exception e) {
                        chatuserphoto.setImageURI(Uri.parse(group.getImageurl()));
                    }

                    FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(group.getCreatorid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            User user = dataSnapshot.getValue(User.class);
                            if(user != null){

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a  EE dd MMM, yyyy ", Locale.getDefault());
                                String date = simpleDateFormat.format(new Date(group.getDate()));

                                String create = "Created by "+user.getUsername()+" \nat "+date;
                                lastseentext.setText(create);
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

    }

    Chat chat;
    StorageReference chatstore;
    public void sendmessage(){

        String message = chatype.getText().toString();
        chatype.setText("");

        new Thread(() ->{
            //Get Date
            Date date = new Date();
            chat= new Chat();
            chat.setReceiverid(currentUser.getUid());
            chat.setSenderid(currentUser.getUid());
            chat.setMessage(message);
            chat.setDate(date.getTime());
            chat.setChatstatus(getString(R.string.delivered));
            // chat type
            chat.setFiletype(getString(R.string.chatfiletype));
            DatabaseReference chatinput = chatReference.push();
            chat.setChatid(chatinput.getKey());
            chatinput.setValue(chat).addOnSuccessListener(vv -> filetype = getString(R.string.chatfiletype));





        }).start();




    }
    private void receivemessage() {
        ArrayList<Chat> chats = new ArrayList<>();
        IsListContain iscontain = new IsListContain();

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat != null) {
                        if (!iscontain.isChatContain(chats,chat)) {

                            chats.add(chat);
                            lastChatPref.setLong(chat.getDate());
                        }

                    }

                }

                StrangerGroupAdapter strangersAdapter = new StrangerGroupAdapter(StrangersGroup.this,chats);
                chatview.setAdapter(strangersAdapter);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        getMenuInflater().inflate(R.menu.strangers_group, menu);
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

    public void menu(View view) {
    }

    public void back(View view) {
        onBackPressed();
    }

    public void groupprofile(View view) {

        Intent intent = new Intent(this,StrangersGroupProfile.class);
        intent.putExtra(getString(R.string.group),groupid);
        startActivity(intent);
    }

    public void send(View view) {
        if(filetype.equalsIgnoreCase(getString(R.string.chatfiletype))){
            sendmessage();
        }
    }



    public void addphoto(View view) {
        showdialogimagechoose();
    }

    public void addfile(View view) {

        Intent intent  = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        try {
            startActivityForResult(Intent.createChooser(intent,"Select File Manager"),ADD_FILE);
        } catch (Exception e) {
            Toast.makeText(this, "No App can perform this task", Toast.LENGTH_SHORT).show();
        }

    }

    public void scrolldown(View view) {
    }

    boolean othercheck = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String imagetypes[] = {".jpg",".jpeg",".png","image"};
        String videotypes[] = {".mp4",".3gp","video"};
        String aufiotype[]  = {".mp3",".m4a",".aac",".3gpp","audio"};
        String othertype[] = {".pdf",".html",".docx",".doc",".apk",".txt",".ppt",".pptx",".xlnx",".exe",".py",".java"};



        if (data != null) {
            if(requestCode == ADD_PHOTO){

                if(resultCode == RESULT_OK){

                    // Receive the photo taken by the Camera Source


                    fileuri = data.getData();
                    if (fileuri != null) {
                        Toast.makeText(this, ""+fileuri.getLastPathSegment(), Toast.LENGTH_SHORT).show();

                        String path = data.getData().getPath() != null ? data.getData().getPath() : ".jpg";
                        //String format = path.substring(path.lastIndexOf("."),path.length());
                        //Toast.makeText(this, ""+format, Toast.LENGTH_SHORT).show();


                        if (path.contains("image")) {
                            filetype = getString(R.string.imagefiletype);
                            showdialogimage();


                        }else if(path.contains("video")){
                            filetype = getString(R.string.videofiletype);
                            showdialogvideo();
                        }


                    }else{

                        // Camera intent

                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap image = (Bitmap) extras.get("data");
                            filetype = getString(R.string.imagefiletype);
                            showdialogbitmap(image);
                        }


                    }


                }else{ Toast.makeText(this, "Failed to Add Photo", Toast.LENGTH_SHORT).show(); }


            }else if(requestCode == ADD_FILE){

                if(resultCode == RESULT_OK){

                    // Receive the File from the File manager
                    fileuri= data.getData();
                    if (fileuri != null) {


                        String path = fileuri.toString() != null ? fileuri.toString() : ".unknown";
                        //Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();
                        String format= path.substring(path.lastIndexOf("."),path.length());
                        Toast.makeText(this, ""+format+" ::::"+path, Toast.LENGTH_SHORT).show();

                        if(path.contains("image")){

                            filetype = getString(R.string.imagefiletype);
                            showdialogimage();
                            othercheck = false;

                        } else if(path.contains("video")){
                            filetype = getString(R.string.videofiletype);
                            showdialogvideo();
                            othercheck = false;

                        } else if(path.contains("audio")){
                            filetype = getString(R.string.audiofiletype);
                            showdialogaudio();
                            othercheck = false;

                        } else {

                            filetype = getString(R.string.otherfiletype);
                            showdialogfile();
                            othercheck = false;
                        }
                    }


                }else{

                    Toast.makeText(this, "Failed to Add File", Toast.LENGTH_SHORT).show();
                    filetype = getString(R.string.chatfiletype);

                }


            }
        } else {

            Toast.makeText(this, "Failed to add file", Toast.LENGTH_SHORT).show();
            filetype = getString(R.string.chatfiletype);
        }


    }


    private void sendmedia(EditText chatype) {

        //Get Date
        Date date = new Date();
        chat= new Chat();
        chat.setReceiverid(currentUser.getUid());
        chat.setSenderid(currentUser.getUid());
        chat.setMessage(chatype.getText().toString());
        chat.setDate(date.getTime());
        chat.setChatstatus(getString(R.string.delivered));


        chat.setFiletype(filetype);

        // store the file using inputstream
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileuri);

            if(inputStream != null){

                chatstore = chatstorage.child(""+chat.getDate());

                UploadTask uploadTask = chatstore.putStream(inputStream);

                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return chatstore.getDownloadUrl() ;
                    }
                }).addOnSuccessListener(uri -> {

                    chat.setFileuri(uri.toString());
                    DatabaseReference chatinput = chatReference.push();
                    chat.setChatid(chatinput.getKey());
                    chatinput.setValue(chat).addOnSuccessListener(vvv -> {filetype = getString(R.string.chatfiletype);

                        sd.dismiss();
                    });


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StrangersGroup.this, "Unable to send file, try again", Toast.LENGTH_SHORT).show();
                        DatabaseReference chatinput = chatReference.push();
                        chat.setChatid(chatinput.getKey());
                        chatinput.setValue(chat).addOnSuccessListener(vvv -> filetype = getString(R.string.chatfiletype));

                    }
                });




            }


        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        chatype.setText("");

    }
    private void sendbitmap(EditText chatype, Bitmap bitmap) {

        //Get Date
        Date date = new Date();
        chat= new Chat();
        chat.setReceiverid(currentUser.getUid());
        chat.setSenderid(currentUser.getUid());
        chat.setMessage(chatype.getText().toString());
        chat.setDate(date.getTime());
        chat.setChatstatus(getString(R.string.delivered));


        chat.setFiletype(filetype);

        // store the file using inputstream
        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,40,outputStream);



            chatstore = chatstorage.child(""+chat.getDate());

            UploadTask uploadTask = chatstore.putBytes(outputStream.toByteArray());

            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return chatstore.getDownloadUrl() ;
                }
            }).addOnSuccessListener(uri -> {

                chat.setFileuri(uri.toString());
                DatabaseReference chatinput = chatReference.push();
                chat.setChatid(chatinput.getKey());
                chatinput.setValue(chat).addOnSuccessListener(vvv -> {filetype = getString(R.string.chatfiletype);

                    sd.dismiss();
                });


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(StrangersGroup.this, "Unable to send file, try again", Toast.LENGTH_SHORT).show();
                    DatabaseReference chatinput = chatReference.push();
                    chat.setChatid(chatinput.getKey());
                    chatinput.setValue(chat).addOnSuccessListener(vvv -> filetype = getString(R.string.chatfiletype));

                }
            });







        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        chatype.setText("");

    }
    private void sendmedia(EditText chatype, String name) {

        //Get Date
        Date date = new Date();
        chat= new Chat();
        chat.setReceiverid(currentUser.getUid());
        chat.setSenderid(currentUser.getUid());
        chat.setMessage(chatype.getText().toString());
        chat.setDate(date.getTime());
        chat.setFilename(name);
        chat.setChatstatus(getString(R.string.delivered));


        chat.setFiletype(filetype);

        // store the file using inputstream
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileuri);

            if(inputStream != null){

                chatstore = chatstorage.child(""+chat.getDate());

                UploadTask uploadTask = chatstore.putStream(inputStream);

                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return chatstore.getDownloadUrl() ;
                    }
                }).addOnSuccessListener(uri -> {

                    chat.setFileuri(uri.toString());
                    DatabaseReference chatinput = chatReference.push();
                    chat.setChatid(chatinput.getKey());
                    chatinput.setValue(chat).addOnSuccessListener(vvv -> {filetype = getString(R.string.chatfiletype);

                        sd.dismiss();
                    });


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StrangersGroup.this, "Unable to send file, try again", Toast.LENGTH_SHORT).show();
                        DatabaseReference chatinput = chatReference.push();
                        chat.setChatid(chatinput.getKey());
                        chatinput.setValue(chat).addOnSuccessListener(vvv -> filetype = getString(R.string.chatfiletype));

                    }
                });




            }


        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        chatype.setText("");

    }




    Dialog sd;
    public void showdialogimage(){

        sd = new Dialog(this);
        sd.setContentView(R.layout.dialog_chat_image);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
        EditText chatype = (EditText)sd.findViewById(R.id.chattype);
        ImageView chatimagea = (ImageView)sd.findViewById(R.id.chatimagea);

        Glide.with(this).load(fileuri).asBitmap().into(chatimagea);

        leftclick.setOnClickListener(lcv ->{

            // open document to select files

            Intent intent  = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            try {
                startActivityForResult(Intent.createChooser(intent,"Select File Manager"),ADD_FILE);
                sd.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, "No App can perform this task", Toast.LENGTH_SHORT).show();
            }
        });
        rightclick.setOnClickListener(rcv ->{

            // send the chat
            sendmedia(chatype);
            Toast.makeText(this, "Please wait, this might take some seconds", Toast.LENGTH_SHORT).show();
            sd.dismiss();
        });

        cancel.setOnClickListener(cv -> sd.dismiss());

    }


    public void showdialogbitmap(Bitmap bitmap){

        sd = new Dialog(this);
        sd.setContentView(R.layout.dialog_chat_image);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
        EditText chatype = (EditText)sd.findViewById(R.id.chattype);
        ImageView chatimagea = (ImageView)sd.findViewById(R.id.chatimagea);



        chatimagea.setImageBitmap(bitmap);

        leftclick.setOnClickListener(lcv ->{

            // open document to select files

            Intent intent  = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            try {
                startActivityForResult(Intent.createChooser(intent,"Select File Manager"),ADD_FILE);
                sd.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, "No App can perform this task", Toast.LENGTH_SHORT).show();
            }
        });
        rightclick.setOnClickListener(rcv ->{

            // send the chat
            sendbitmap(chatype, bitmap);
            Toast.makeText(this, "Please wait, this might take some seconds", Toast.LENGTH_LONG).show();
            sd.dismiss();
        });

        cancel.setOnClickListener(cv -> sd.dismiss());

    }


    public void showdialogvideo(){

        sd = new Dialog(this);
        sd.setContentView(R.layout.dialog_chat_video);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
        EditText chatype = (EditText)sd.findViewById(R.id.chattype);
        CardView playpause = (CardView)sd.findViewById(R.id.playpausecard);
        ImageView videobutton = (ImageView)sd.findViewById(R.id.postvideobutton);
        RelativeLayout videolay = (RelativeLayout)sd.findViewById(R.id.chatvideolay);
        RelativeLayout vontroller = (RelativeLayout)sd.findViewById(R.id.videocontrollay);
        VideoView chatvideo = (VideoView)sd.findViewById(R.id.chatvideo);

        playmedia(fileuri,chatvideo);

        playpause.setOnClickListener((pvb) ->{

            // Play and Pause button control

            if(!prepared){

                playmedia(fileuri,chatvideo);

                chatvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        prepared = true;

                    }
                });

                // playmedia(post.getFileurl());
                chatvideo.start();
                videobutton.setImageResource(R.drawable.pause_bottom_sm);
                count = 1;

            }else {

                if (isPlaying(sd,chatvideo)) {

                    pauseMedia(chatvideo);
                    videobutton.setImageResource(R.drawable.play_button_sm);


                    //prepared = false;
                } else {

                    resumeMedia(chatvideo);
                    videobutton.setImageResource(R.drawable.pause_bottom_sm);

                }

            }





        });

        videolay.setOnClickListener((pvl) ->{

            if(count == 0){
                vontroller.setVisibility(View.VISIBLE);
                count = 1;
            }else{
                vontroller.setVisibility(View.GONE);
                count=0;
            }
        });


        rightclick.setOnClickListener(rcv ->{

            // send the chat
            sendmedia(chatype);
            Toast.makeText(this, "Please wait, this might take some seconds", Toast.LENGTH_LONG).show();
            sd.dismiss();
        });

        leftclick.setOnClickListener(lcv ->{

            Intent intent  = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            try {
                startActivityForResult(Intent.createChooser(intent,"Select File Manager"),ADD_FILE);
                sd.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, "No App can perform this task", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(cv -> {
            chatvideo.stopPlayback();
            sd.dismiss();});

    }

    String titile;
    String songname;
    public void showdialogaudio(){

        PlaybackStudio playbackStudio = new PlaybackStudio(this);

        sd = new Dialog(this);
        sd.setContentView(R.layout.dialog_chat_audio);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
        ImageView playpause = (ImageView)sd.findViewById(R.id.playpausecard);
        TextView audioname = (TextView)sd.findViewById(R.id.chataudioname);

        Cursor cursor =getContentResolver().query(fileuri,null,null,null,null);

        if(cursor != null){

            if(cursor.getCount() > 0){
                cursor.moveToFirst();

                titile = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                songname = titile.substring(titile.lastIndexOf("/")+1,titile.length());
            }

        }

        audioname.setText(songname);

        playpause.setOnClickListener(pcv ->{



            // Play the audio here
            if(!playbackStudio.isPrepared()){

                playbackStudio.playMedia(titile);
                playpause.setImageResource(R.drawable.pause_bottom_sm_black);


            }else{

                if(playbackStudio.isPlaying()){

                    playbackStudio.pauseMedia();
                    playpause.setImageResource(R.drawable.play_button_sm_black);

                }else{
                    playbackStudio.resumeMedia();
                    playpause.setImageResource(R.drawable.pause_bottom_sm_black);

                }


            }

        });


        leftclick.setOnClickListener(lcv ->{

            Intent intent  = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            try {
                startActivityForResult(Intent.createChooser(intent,"Select File Manager"),ADD_FILE);
                sd.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, "No App can perform this task", Toast.LENGTH_SHORT).show();
            }

        });
        EditText chatype = (EditText)sd.findViewById(R.id.chattype);
        rightclick.setOnClickListener(rcv ->{

            // send the chat
            sendmedia(chatype,songname);
            Toast.makeText(this, "Please wait, this might take some seconds", Toast.LENGTH_SHORT).show();
            sd.dismiss();
        });
        cancel.setOnClickListener(cv -> {
            playbackStudio.stopMedia();
            sd.dismiss();});

    }

    public void showdialogfile(){

        sd = new Dialog(this);
        sd.setContentView(R.layout.dialog_chat_files);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
        ImageView download = (ImageView)sd.findViewById(R.id.downloadfile);
        TextView filename = (TextView)sd.findViewById(R.id.chatfilename);

        String path = fileuri.toString() != null ? fileuri.toString() : " /Unknown.";
        String name = path.substring(path.lastIndexOf("%")+1,path.length());
        filename.setText(name);



        download.setOnClickListener(dcv ->{
            // TODO: Open the file with reasonable App


        });

        leftclick.setOnClickListener(lcv ->{

            Intent intent  = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            try {
                startActivityForResult(Intent.createChooser(intent,"Select File Manager"),ADD_FILE);
                sd.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, "No App can perform this task", Toast.LENGTH_SHORT).show();
            }
        });


        EditText chatype = (EditText)sd.findViewById(R.id.chattype);

        rightclick.setOnClickListener(rcv ->{

            // send the chat
            sendmedia(chatype,name);
            Toast.makeText(this, "Please wait, this might take some seconds", Toast.LENGTH_SHORT).show();
            sd.dismiss();
        });


        cancel.setOnClickListener(cv -> sd.dismiss());

    }

    public boolean isPlaying(Dialog v, VideoView postvideoview){
        boolean isPlaying=false;
        try {
            isPlaying = postvideoview.isPlaying();
        }catch (IllegalStateException e){
            postvideoview = (VideoView)v.findViewById(R.id.postvideoview);
            isPlaying = postvideoview.isPlaying();
        }catch (Exception e){
            postvideoview = (VideoView)v.findViewById(R.id.postvideoview);
        }


        return isPlaying;
    }


    public void playmedia(String data, VideoView postvideoview) {

        try {
            Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();

            //postvideoview.start();


        } catch (IllegalArgumentException e) {


            Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();

        } catch (Exception e) {
            Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();
            // postvideoview.start();


        }
    }

    public void playmedia(Uri uri, VideoView postvideoview) {

        try {
            //Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.start();


        } catch (IllegalArgumentException e) {

        } catch (Exception e) {
            // Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.start();


        }
    }

    public void pauseMedia(VideoView postvideoview){

        if (postvideoview.isPlaying()) {
            postvideoview.pause();
            resumePosition = postvideoview.getCurrentPosition();
        }

        //Intent intent = new Intent("pause");
        //sendBroadcast(intent);


    }

    public void resumeMedia(VideoView postvideoview){

        if (!postvideoview.isPlaying()) {
            postvideoview.seekTo(resumePosition);
            postvideoview.start();
        }



    }
    public void showdialogimagechoose(){

        sd = new Dialog(this);
        sd.setContentView(R.layout.dialog_camera_video);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);

        leftclick.setOnClickListener(lcv ->{

            // open document to select files

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(Intent.createChooser(intent,"Select Camera Source"),ADD_PHOTO);
            } catch (Exception e) {
                Toast.makeText(this, "You do not have any Camera Source on this Device", Toast.LENGTH_SHORT).show();
            }
        });
        rightclick.setOnClickListener(rcv ->{

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            try {
                startActivityForResult(Intent.createChooser(intent,"Select Camera Source"),ADD_PHOTO);
            } catch (Exception e) {
                Toast.makeText(this, "You do not have any Camera Source on this Device", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(cv -> sd.dismiss());

    }
}
