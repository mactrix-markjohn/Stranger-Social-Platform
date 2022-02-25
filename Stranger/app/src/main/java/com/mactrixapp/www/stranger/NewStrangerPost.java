package com.mactrixapp.www.stranger;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mactrixapp.www.stranger.Model.Post;
import com.mactrixapp.www.stranger.Model.User;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**Here in this Class, we are going o collect*/




public class NewStrangerPost extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int FILE_CODE = 10 ;
    private CircleImageView newpostprofileImage;
    private TextView postusername;
    private RelativeLayout newpostlayout;
    private ImageView newpostimage;
    private RelativeLayout postvideolay;
    private VideoView postvideoview;
    private RelativeLayout playpauselay;
    private RelativeLayout posttextlay;
    private RelativeLayout whitevintageback;
    private EditText posttext;
    private FirebaseUser fireuser;
    private DatabaseReference userReference;
    private Post post;
    private String filetype;
    private String textback;
    private Uri fileuri;
    private EditText postcapturetext;
    private int resumePosition;
    private ImageView postvideobutton;
    private DatabaseReference postReference;
    private StorageReference postStorage;
    private ImageView backimage;
    private MultiAutoCompleteTextView postinterestfield;
    private DatabaseReference interestReference;
    private ArrayList<String> interests;
    private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stranger_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        filetype = getString(R.string.none);

        // View at the head of the Post
        newpostprofileImage = (CircleImageView)findViewById(R.id.newpostprofilepic);
        postusername = (TextView)findViewById(R.id.postusername);

        // View of the post interest

        postinterestfield = (MultiAutoCompleteTextView)findViewById(R.id.postinterestfield);


        // View at the Post media layout
        newpostlayout = (RelativeLayout)findViewById(R.id.newpostlayout);// Hold all the media video, make visible or gone
        newpostimage = (ImageView)findViewById(R.id.newpostimage); // The ImageView to hold images
        postvideolay = (RelativeLayout)findViewById(R.id.postvideolay); // Make this visible and gone at time due, make visible or gone
        postvideoview = (VideoView) findViewById(R.id.postvideoview); // The videoview to play video
        playpauselay = (RelativeLayout)findViewById(R.id.playpauselay); // The layout to hold the play and pasue button
        posttextlay = (RelativeLayout)findViewById(R.id.posttextlay);// Layout to hold the Text Media, Make Visible or gone
        whitevintageback = (RelativeLayout)findViewById(R.id.whitevintageback);// Change text back to white or vintage paper texture
        posttext = (EditText)findViewById(R.id.posttext); // Edittext to input text
        postcapturetext = (EditText)findViewById(R.id.postcapturetext);// Edittext to input Capture;
        postvideobutton = (ImageView)findViewById(R.id.postvideobutton);// PlayPause button
        backimage = (ImageView)findViewById(R.id.backimage);

        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewStrangerPost.this, Stranger.class));
            }
        });

        // Initialize the Database and Post Model
        fireuser = FirebaseAuth.getInstance().getCurrentUser();
        postReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.post));
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user));
        postStorage = FirebaseStorage.getInstance().getReference().child(getString(R.string.post));
        interestReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.interestreference));
        post = new Post();


        interestRetrieve();

        userReference.child(fireuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    postusername.setText(user.getUsername());
                    try {
                        Glide.with(NewStrangerPost.this).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(newpostprofileImage);
                    } catch (Exception e) {
                        newpostprofileImage.setImageResource(R.mipmap.profileavatar);
                    }


                    // Save the User info into Post
                    post.setUsername(user.getUsername());
                    post.setUserid(user.getUserid());
                    post.setUserphotourl(user.getPhotoUrl());
                    post.setPhonenum(user.getPhoneNumber());
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

    private void interestRetrieve(){


        interests  = new ArrayList<>();


        interestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    //retreive all the interest in the database
                    String interest = snapshot.getValue(String.class);

                    // check if the Interest type by the user is contain in the Database Interest
                    if (interest != null) {


                        // check if the interest has been added before
                        if(!interests.contains(interest)){

                            interests.add(interest);
                            Toast.makeText(NewStrangerPost.this, "Interest is added", Toast.LENGTH_SHORT).show();

                        }

                    }


                }

                //arrayAdapter.notifyDataSetChanged();
                //adapter.notifyDataSetChanged();


                String[] interestarray = new String[interests.size()];

                // fill up the Interest Array
                for (int i = 0; i < interestarray.length;i++){


                    interestarray[i] = interests.get(i);

                }



                // arrayAdapter = new ArrayAdapter<>(EditProfile.this,R.layout.interestlistlayoutprofile,R.id.interesttext,interestarray);
                adapter = new ArrayAdapter(NewStrangerPost.this, android.R.layout.simple_dropdown_item_1line,interestarray);

                postinterestfield.setAdapter(adapter);
                postinterestfield.setThreshold(1);
                postinterestfield.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void interestAddition(String interest){

        interestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(interest)){


                    interestReference.child(interest).setValue(interest);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void addInterest(){

        String interestss = postinterestfield.getText().toString();

        String[] interestarray = interestss.split(",");

        for (String s : interestarray){


            interestAddition(s);
        }

    }





    // Storage reference to hold the Image and video reference
    StorageReference posting;

    public void share(View view) {

        // add the newly enter interest of the user

        if(TextUtils.isEmpty(postinterestfield.getText().toString())){
            Toast.makeText(this, "You Interest field is empty. Please fill it", Toast.LENGTH_LONG).show();
            return;
        }else{
            addInterest();

        }



        Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show();

        // Added the File type, Interest and Capture text [The constant input]
        post.setType(filetype);
        post.setCapture(postcapturetext.getText().toString());
        post.setInterest(postinterestfield.getText().toString());


        // Added the time of the post
        Date date = new Date();
        long time = date.getTime();
        post.setDate(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH.mm.ss dd.MM.yyyy ",Locale.getDefault());
        String period = simpleDateFormat.format(date);


        // if the file type is None. We store the capture alone
        if(filetype.equalsIgnoreCase(getString(R.string.none))){

            DatabaseReference base = postReference.push();
            post.setPostid(base.getKey());
            base.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(NewStrangerPost.this, "You have succesfully Posted.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(NewStrangerPost.this,Stranger.class));
                            finish();

                        }
                    });

        }

        // if the filetype is text we collect the paper type and text message
        if(filetype.equalsIgnoreCase(getString(R.string.text))){
            post.setPapertype(textback);
            post.setTextmessage(posttext.getText().toString());

            DatabaseReference base = postReference.push();
            post.setPostid(base.getKey());
            base.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(NewStrangerPost.this, "You have succesfully Posted.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(NewStrangerPost.this,Stranger.class));
                            finish();

                        }
                    });


        }

        // if the file type is Image, Store the Image to Firebase Storage and save the Url in the Database
        if(filetype.equalsIgnoreCase(getString(R.string.image)) ){


            try {

                InputStream inputStream = getContentResolver().openInputStream(fileuri);


                if (inputStream != null) {

                    posting =postStorage.child(getString(R.string.image)).child(period);
                    UploadTask uploadTask = posting.putStream(inputStream);

                    Task<Uri> task  = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {


                            // Return the Download Url of the Image
                            return posting.getDownloadUrl();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = uri.toString();
                            post.setFileurl(url);


                            // Store the Post class in the Database


                            DatabaseReference base = postReference.push();
                            post.setPostid(base.getKey());
                            base.setValue(post)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(NewStrangerPost.this, "You have succesfully Posted.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(NewStrangerPost.this,Stranger.class));
                                            finish();

                                        }
                                    });

                        }
                    });
                }else{

                    Toast.makeText(this, "Select a Image from your Gallery", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }


        // if the file type is Video Store the video to Firebase Storage and save the Url in the Database
        if (filetype.equalsIgnoreCase(getString(R.string.video))) {

            /*int duration = postvideoview.getDuration()/1000;
            if(duration <= 60){*/

                try {

                    InputStream inputStream = getContentResolver().openInputStream(fileuri);


                    if (inputStream != null) {

                        posting = postStorage.child(getString(R.string.video)).child(period);
                        UploadTask uploadTask = posting.putStream(inputStream);
                        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {


                                // Return the Download Url of the Video
                                return posting.getDownloadUrl();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString() ;
                                post.setFileurl(url);

                                // Store the bitmap here

                                storevideothumbnail();




                            }
                        });
                    } else {

                        Toast.makeText(this, "Please select a Video from your Gallery", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }




        }

    }

    StorageReference postthumb;
    Bitmap bitmape;
    public void storevideothumbnail(){


        try {
           /* MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(fileuri.toString(),new HashMap<String,String>());
            bitmap = metadataRetriever.getFrameAtTime(1000);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,30,byteArrayOutputStream);
            imagebyte = byteArrayOutputStream.toByteArray();*/

           Cursor cursor = getContentResolver().query(fileuri,null,null,null,null);

           if(cursor != null){
               cursor.moveToFirst();
               String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
               bitmap = ThumbnailUtils.createVideoThumbnail(path,MediaStore.Video.Thumbnails.MINI_KIND);
               ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
               bitmap.compress(Bitmap.CompressFormat.JPEG,30,byteArrayOutputStream);
               imagebyte = byteArrayOutputStream.toByteArray();
           }else{

               bitmap = ThumbnailUtils.createVideoThumbnail(fileuri.toString(),MediaStore.Video.Thumbnails.MINI_KIND);
               ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
               bitmap.compress(Bitmap.CompressFormat.JPEG,30,byteArrayOutputStream);
               imagebyte = byteArrayOutputStream.toByteArray();
           }






            /*playmedia(fileuri);*/
        } catch (Exception e) {

            bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.videoplaceh);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,30,byteArrayOutputStream);
            imagebyte = byteArrayOutputStream.toByteArray();

           /* playmedia(fileuri);*/

        }


        Date date = new Date();
        long time = date.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH.mm.ss dd.MM.yyyy ",Locale.getDefault());
        String period = simpleDateFormat.format(date);

        postthumb = postStorage.child(getString(R.string.videothumbnail)).child(period);

        UploadTask uploadTask = postthumb.putBytes(imagebyte);
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return postthumb.getDownloadUrl();
            }
        }).addOnSuccessListener((uri) ->{


            post.setVideothumbnailurl(uri.toString());


            // Store the Post class in the Database
            DatabaseReference base = postReference.push();
            post.setPostid(base.getKey());
            base.setValue(post)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NewStrangerPost.this, "You have Successfully Posted  your Content", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(NewStrangerPost.this,Stranger.class));
                            finish();

                        }
                    });


        });




    }

    public void back(View view) {

        startActivity(new Intent(this,Stranger.class));


    }


    public void playpause(View view) {


        // Play and Pause button control
        if(isPlaying()){

            pauseMedia();
            postvideobutton.setImageResource(R.drawable.play_button);

        }else{

           resumeMedia();
           postvideobutton.setImageResource(R.drawable.pause_bottom);

        }



    }

    public boolean isPlaying(){
        boolean isPlaying=false;
        try {
            isPlaying = postvideoview.isPlaying();
        }catch (IllegalStateException e){
            postvideoview = (VideoView)findViewById(R.id.postvideoview);
            isPlaying = postvideoview.isPlaying();
        }catch (Exception e){
            postvideoview = (VideoView)findViewById(R.id.postvideoview);
        }


        return isPlaying;
    }

    public void addtext(View view) {

        filetype = getString(R.string.text);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Text Background..");
        builder.setIcon(R.drawable.feather_pen);
        builder.setMultiChoiceItems(new String[]{"White Paper Texture", "Vintage Paper Texture"}, new boolean[]{false,false}, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                // Select either White or vintage

                if(isChecked) {
                    switch (which) {
                        // Set the background to white texture or vintage texture
                        case 0:
                            textback = getString(R.string.whiteback);
                            newpostimage.setVisibility(View.GONE);
                            postvideolay.setVisibility(View.GONE);
                            newpostlayout.setVisibility(View.VISIBLE);
                            posttextlay.setVisibility(View.VISIBLE);
                            whitevintageback.setBackgroundResource(R.mipmap.whitepaperback);
                            posttext.requestFocus();
                            dialog.dismiss();
                            break;
                        case 1:
                            textback = getString(R.string.vintageback);
                            newpostlayout.setVisibility(View.VISIBLE);
                            posttextlay.setVisibility(View.VISIBLE);
                            whitevintageback.setBackgroundResource(R.mipmap.vintagepaper);
                            posttext.requestFocus();
                            dialog.dismiss();
                            break;


                    }
                }
            }
        });

        builder.setNegativeButton("Cancel",(dialog, which) -> dialog.cancel());


        builder.create().show();







    }

    public void cancelpostmedia(View view) {


        if(filetype.equalsIgnoreCase(getString(R.string.video))){
            if(isPlaying()) {
                postvideoview.stopPlayback();
                postvideoview.suspend();
            }
        }

        filetype = getString(R.string.none);
        newpostlayout.setVisibility(View.GONE);
        posttext.setText("");


    }

    public void addvideo(View view) {


      /*  AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("");
        builder.setPositiveButton("Continue",(dialog, which) -> {


        });
        builder.setNegativeButton("Cancel",(dialog, which) -> dialog.cancel());
        builder.create().show();*/

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent,FILE_CODE);
        filetype = getString(R.string.video);





    }

    public void addphoto(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,FILE_CODE);
        filetype = getString(R.string.image);



    }

    Bitmap bitmap;
    byte[] imagebyte;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FILE_CODE){
            if(resultCode == RESULT_OK){


                fileuri = data.getData();

                if(filetype.equalsIgnoreCase(getString(R.string.image))){

                    // TODO: if the file type is an Image
                    // Here we simply preview the image
                    // The storage of the image will be done in the share button callback method

                    newpostlayout.setVisibility(View.VISIBLE);
                    newpostimage.setVisibility(View.VISIBLE);
                    postvideolay.setVisibility(View.GONE);
                    posttextlay.setVisibility(View.GONE);

                    // preview the image in the imageview
                    Glide.with(NewStrangerPost.this).load(fileuri).asBitmap().into(newpostimage);

                } else if(filetype.equalsIgnoreCase(getString(R.string.video))){


                        // TODO: if the file type is a video
                        // Here we simply preview the video
                        // The storage of the image will be done in the share button callback method

                        newpostlayout.setVisibility(View.VISIBLE);
                        postvideolay.setVisibility(View.VISIBLE);
                        newpostimage.setVisibility(View.GONE);
                        posttextlay.setVisibility(View.GONE);

                        // Play the video and then pause in case the user does not want to play the video

                        // TODO: Remember to extract the Thumbnail of every video and store if with the video
                        // TODO: So a variable in the Post.class will be name videothumbnailUri will be decleared in the class


                    playmedia(fileuri);

                }



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
        getMenuInflater().inflate(R.menu.new_stranger_post, menu);
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


    public void playmedia(String data) {

        try {
            Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.start();


        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Corrupted File", Toast.LENGTH_SHORT).show();
            onBackPressed();
        } catch (Exception e) {
            Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.start();


        }
    }

    int duration = 100;
    public int getVideoDuration(Uri uri){

        try{

            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();
            duration = postvideoview.getDuration();

        }catch (Exception e){
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();

            duration = postvideoview.getDuration();
        }
        return duration;
    }

    public void playmedia(Uri uri) {

        try {
            //Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.start();




        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Corrupted File", Toast.LENGTH_SHORT).show();
            onBackPressed();
        } catch (Exception e) {
           // Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.start();


        }
    }

    public void pauseMedia(){

        if (postvideoview.isPlaying()) {
            postvideoview.pause();
            resumePosition = postvideoview.getCurrentPosition();
        }

        //Intent intent = new Intent("pause");
        //sendBroadcast(intent);


    }

    public void resumeMedia(){

        if (!postvideoview.isPlaying()) {
            postvideoview.seekTo(resumePosition);
            postvideoview.start();
        }



    }


    int count = 0;
    public void showvideocontrol(View view) {

        if(count == 0){
            playpauselay.setVisibility(View.VISIBLE);
            count = 1;
        }else{
            playpauselay.setVisibility(View.GONE);
            count=0;
        }



    }

    public void bac(View view) {

        startActivity(new Intent(this,Stranger.class));
    }
}
