package com.mactrixapp.www.stranger.Model;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.mactrixapp.www.stranger.Adapters.CommentAdapter;
import com.mactrixapp.www.stranger.ChattingSpot;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.SharedPref;
import com.mactrixapp.www.stranger.Stranger;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class OpenComment {

    private static final int ADD_FILE =10 ;
    private Context context;
    private ImageView commentdown;
    private ListView commentlist;
    private CardView media;
    private CardView file;
    private EditText commentype;
    private CardView send;
    private BottomSheetBehavior<View> sheetBehavior;
    private String postid;
    private DatabaseReference commentReference;
    private String commenttype;
    private Comment comment;
    private FirebaseUser currentuser;
    private StorageReference commentStorage;
    private StorageReference commentstore;
    private boolean prepared;
    private int count;
    SharedPref likewave;
    //private Uri fileuri;


    public OpenComment() {
    }

    public OpenComment(Context context) {
        this.context = context;

    }

    public void setUpComment(){

        likewave = new SharedPref(context,context.getString(R.string.likewave));
        currentuser =FirebaseAuth.getInstance().getCurrentUser();
        commentStorage = FirebaseStorage.getInstance().getReference().child(context.getString(R.string.comment));

        sheetBehavior.setPeekHeight(CardView.LayoutParams.MATCH_PARENT);
        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {


                if(newState == BottomSheetBehavior.STATE_DRAGGING){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }


               /* if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }*/

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        send.setOnClickListener(sv -> sendcomment());
        media.setOnClickListener(mv -> showdialogimagechoose());
        file.setOnClickListener(fv ->{

            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.addfile),20);
            Intent intent = new Intent(context.getString(R.string.broadcastactivity));
            intent.putExtra(context.getString(R.string.addfile),bundle);
            context.sendBroadcast(intent);

        });
        commentdown.setOnClickListener(cdv -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));

    }

    public void sendcomment(){

        //Get Date
        likewave.setBoolean(true);
        Date date = new Date();
        comment= new Comment();
        comment.setUserid(currentuser.getUid());
        comment.setComment(commentype.getText().toString());
        comment.setDate(date.getTime());
        comment.setType(context.getString(R.string.commenttext));
        DatabaseReference chatinput = commentReference.push();
        comment.setCommentid(chatinput.getKey());
        chatinput.setValue(comment).addOnSuccessListener(vv -> commenttype = context.getString(R.string.commenttext));
        commentype.setText("");
    }

    public void sendconstants(Comment comment ,EditText commentype){
        likewave.setBoolean(true);
        Date date = new Date();
        comment.setUserid(currentuser.getUid());
        comment.setComment(commentype.getText().toString());
        comment.setDate(date.getTime());

    }




    public void receivecomment(){


        new Thread(() -> commentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Comment> comments = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Comment comment = snapshot.getValue(Comment.class);
                    if(comment != null) {

                        comments.add(comment);

                    }

                }

                ((Activity) new Stranger()).runOnUiThread(() -> {



                    CommentAdapter commentAdapter = new CommentAdapter(context,comments);
                    commentlist.setAdapter(commentAdapter);


                });






            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })).start();




    }

    public void openComment(String postid){
        commentype.setVisibility(View.VISIBLE);
        commentype.setEnabled(true);
        commentype.setFocusable(true);
        commentype.requestFocus();
        commentReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.post)).child(postid).child(context.getString(R.string.comment));
        receivecomment();
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    public void sendmessage(){



    }


    public void sendmedia(EditText commentype, Uri fileuri){
        //Get Date

        comment= new Comment();
        sendconstants(comment,commentype);
        comment.setType(commenttype);

        // store the file using inputstream
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileuri);

            if(inputStream != null){

                commentstore = commentStorage.child(""+comment.getDate());

                UploadTask uploadTask = commentstore.putStream(inputStream);

                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return commentstore.getDownloadUrl() ;
                    }
                }).addOnSuccessListener(uri -> {

                    comment.setUri(uri.toString());
                    DatabaseReference chatinput = commentReference.push();
                    comment.setCommentid(chatinput.getKey());
                    chatinput.setValue(comment).addOnSuccessListener(vvv -> {commenttype = context.getString(R.string.commenttext);

                        sd.dismiss();
                    });


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Unable to send file, try again", Toast.LENGTH_SHORT).show();
                        DatabaseReference chatinput = commentReference.push();
                        comment.setCommentid(chatinput.getKey());
                        chatinput.setValue(comment).addOnSuccessListener(vvv -> commenttype = context.getString(R.string.commenttext));

                    }
                });




            }


        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        commentype.setText("");

    }

    public void sendmedia(EditText chatype, String name, Uri fileuri){
        comment= new Comment();
        sendconstants(comment,chatype);
        comment.setFilename(name);


        comment.setType(commenttype);

        // store the file using inputstream
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileuri);

            if(inputStream != null){

                commentstore = commentStorage.child(""+comment.getDate());

                UploadTask uploadTask = commentstore.putStream(inputStream);

                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return commentstore.getDownloadUrl() ;
                    }
                }).addOnSuccessListener(uri -> {

                    comment.setUri(uri.toString());
                    DatabaseReference chatinput = commentReference.push();
                    comment.setCommentid(chatinput.getKey());
                    chatinput.setValue(comment).addOnSuccessListener(vvv -> {commenttype = context.getString(R.string.commenttext);

                        sd.dismiss();
                    });


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Unable to send file, try again", Toast.LENGTH_SHORT).show();
                        DatabaseReference chatinput = commentReference.push();
                        comment.setCommentid(chatinput.getKey());
                        chatinput.setValue(comment).addOnSuccessListener(vvv -> commenttype = context.getString(R.string.commenttext));

                    }
                });




            }


        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        chatype.setText("");

    }

    public void sendbitmap(EditText chatype, Bitmap bitmap){
        //Get Date
        comment= new Comment();
        sendconstants(comment,chatype);




        comment.setType(commenttype);

        // store the file using inputstream
        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,40,outputStream);



            commentstore = commentStorage.child(""+comment.getDate());

            UploadTask uploadTask = commentstore.putBytes(outputStream.toByteArray());

            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return commentstore.getDownloadUrl() ;
                }
            }).addOnSuccessListener(uri -> {

                comment.setUri(uri.toString());
                DatabaseReference chatinput = commentReference.push();
                comment.setCommentid(chatinput.getKey());
                chatinput.setValue(comment).addOnSuccessListener(vvv -> {commenttype = context.getString(R.string.commenttext);

                    sd.dismiss();
                });


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Unable to send file, try again", Toast.LENGTH_SHORT).show();
                    DatabaseReference chatinput = commentReference.push();
                    comment.setCommentid(chatinput.getKey());
                    chatinput.setValue(comment).addOnSuccessListener(vvv -> commenttype = context.getString(R.string.chatfiletype));

                }
            });







        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        chatype.setText("");

    }

    Dialog sd;
    public void showdialogimage(Uri fileuri){

        sd = new Dialog(context);
        sd.setContentView(R.layout.dialog_chat_image);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        commenttype = context.getString(R.string.commentimage);

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
        EditText commentypee = (EditText)sd.findViewById(R.id.chattype);
        ImageView chatimagea = (ImageView)sd.findViewById(R.id.chatimagea);

        Glide.with(context).load(fileuri).asBitmap().into(chatimagea);

        leftclick.setOnClickListener(lcv ->{

            // open document to select files

            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.addfile),20);
            Intent intent = new Intent(context.getString(R.string.broadcastactivity));
            intent.putExtra(context.getString(R.string.addfile),bundle);
            context.sendBroadcast(intent);

            sd.dismiss();

        });
        rightclick.setOnClickListener(rcv ->{

            // send the chat
            sendmedia(commentypee,fileuri);
            Toast.makeText(context, "Please wait, this might take some seconds", Toast.LENGTH_SHORT).show();
            sd.dismiss();
        });

        cancel.setOnClickListener(cv -> sd.dismiss());

    }

    public void showdialogimagechoose(){

        sd = new Dialog(context);
        sd.setContentView(R.layout.dialog_camera_video);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);

        leftclick.setOnClickListener(lcv ->{

            // open document to select files

            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.addfile),10);
            Intent intent = new Intent(context.getString(R.string.broadcastactivity));
            intent.putExtra(context.getString(R.string.addfile),bundle);
            context.sendBroadcast(intent);
            sd.dismiss();


        });
        rightclick.setOnClickListener(rcv ->{

            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.addfile),15);
            Intent intent = new Intent(context.getString(R.string.broadcastactivity));
            intent.putExtra(context.getString(R.string.addfile),bundle);
            context.sendBroadcast(intent);
            sd.dismiss();
        });

        cancel.setOnClickListener(cv -> sd.dismiss());

    }


    public void showdialogbitmap(Bitmap bitmap){

        sd = new Dialog(context);
        sd.setContentView(R.layout.dialog_chat_image);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        commenttype = context.getString(R.string.commentimage);

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
        EditText chatype = (EditText)sd.findViewById(R.id.chattype);
        ImageView chatimagea = (ImageView)sd.findViewById(R.id.chatimagea);



        chatimagea.setImageBitmap(bitmap);

        leftclick.setOnClickListener(lcv ->{

            // open document to select files

            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.addfile),20);
            Intent intent = new Intent(context.getString(R.string.broadcastactivity));
            intent.putExtra(context.getString(R.string.addfile),bundle);
            context.sendBroadcast(intent);
            sd.dismiss();
        });
        rightclick.setOnClickListener(rcv ->{

            // send the chat
            sendbitmap(chatype, bitmap);
            Toast.makeText(context, "Please wait, this might take some seconds", Toast.LENGTH_LONG).show();
            sd.dismiss();
        });

        cancel.setOnClickListener(cv -> sd.dismiss());

    }


    public void showdialogvideo(Uri fileuri){

        VideoStudio videoStudio = new VideoStudio(context);

        sd = new Dialog(context);
        sd.setContentView(R.layout.dialog_chat_video);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();
        prepared = false;

        commenttype = context.getString(R.string.commentvideo);

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
        EditText chatype = (EditText)sd.findViewById(R.id.chattype);
        CardView playpause = (CardView)sd.findViewById(R.id.playpausecard);
        ImageView videobutton = (ImageView)sd.findViewById(R.id.postvideobutton);
        RelativeLayout videolay = (RelativeLayout)sd.findViewById(R.id.chatvideolay);
        RelativeLayout vontroller = (RelativeLayout)sd.findViewById(R.id.videocontrollay);
        VideoView chatvideo = (VideoView)sd.findViewById(R.id.chatvideo);

        videoStudio.playmedia(fileuri,chatvideo);

        playpause.setOnClickListener((pvb) ->{

            // Play and Pause button control

            if(!prepared){

                videoStudio.playmedia(fileuri,chatvideo);

                chatvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        prepared = true;

                    }
                });

                // playmedia(post.getFileurl());
               // chatvideo.start();
                videobutton.setImageResource(R.drawable.pause_bottom_sm);
                count = 1;

            }else {

                if (videoStudio.isPlaying(sd,chatvideo)) {

                    videoStudio.pauseMedia(chatvideo);
                    videobutton.setImageResource(R.drawable.play_button_sm);


                    //prepared = false;
                } else {

                    videoStudio.resumeMedia(chatvideo);
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
            sendmedia(chatype,fileuri);
            Toast.makeText(context, "Please wait, this might take some seconds", Toast.LENGTH_LONG).show();
            sd.dismiss();
        });

        leftclick.setOnClickListener(lcv ->{

            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.addfile),20);
            Intent intent = new Intent(context.getString(R.string.broadcastactivity));
            intent.putExtra(context.getString(R.string.addfile),bundle);
            context.sendBroadcast(intent);
            sd.dismiss();
        });

        cancel.setOnClickListener(cv -> {
            chatvideo.stopPlayback();
            sd.dismiss();});

    }

    String titile;
    String songname;
    public void showdialogaudio(Uri fileuri){

        PlaybackStudio playbackStudio = new PlaybackStudio(context);

        sd = new Dialog(context);
        sd.setContentView(R.layout.dialog_chat_audio);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        commenttype = context.getString(R.string.commentaudio);

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
        ImageView playpause = (ImageView)sd.findViewById(R.id.playpausecard);
        TextView audioname = (TextView)sd.findViewById(R.id.chataudioname);

        Cursor cursor = context.getContentResolver().query(fileuri,null,null,null,null);

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

            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.addfile),20);
            Intent intent = new Intent(context.getString(R.string.broadcastactivity));
            intent.putExtra(context.getString(R.string.addfile),bundle);
            context.sendBroadcast(intent);
            sd.dismiss();

        });
        EditText chatype = (EditText)sd.findViewById(R.id.chattype);
        rightclick.setOnClickListener(rcv ->{

            // send the chat
            sendmedia(chatype,songname,fileuri);
            Toast.makeText(context, "Please wait, this might take some seconds", Toast.LENGTH_SHORT).show();
            sd.dismiss();
        });
        cancel.setOnClickListener(cv -> {
            playbackStudio.stopMedia();
            sd.dismiss();});

    }

    public void showdialogfile(Uri fileuri){

        sd = new Dialog(context);
        sd.setContentView(R.layout.dialog_chat_files);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();


        commenttype = context.getString(R.string.commentfile);

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

            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.addfile),20);
            Intent intent = new Intent(context.getString(R.string.broadcastactivity));
            intent.putExtra(context.getString(R.string.addfile),bundle);
            context.sendBroadcast(intent);
            sd.dismiss();
        });


        EditText chatype = (EditText)sd.findViewById(R.id.chattype);

        rightclick.setOnClickListener(rcv ->{

            // send the chat
            sendmedia(chatype,name,fileuri);
            Toast.makeText(context, "Please wait, this might take some seconds", Toast.LENGTH_SHORT).show();
            sd.dismiss();
        });


        cancel.setOnClickListener(cv -> sd.dismiss());

    }

    public OpenComment(Context context, ImageView commentdown, ListView commentlist, CardView media, CardView file, EditText commentype, CardView send) {
        this.context = context;
        this.commentdown = commentdown;
        this.commentlist = commentlist;
        this.media = media;
        this.file = file;
        this.commentype = commentype;
        this.send = send;
    }

    public OpenComment(Context context, ImageView commentdown, ListView commentlist, CardView media, CardView file, EditText commentype, CardView send, BottomSheetBehavior<View> sheetBehavior) {
        this.context = context;
        this.commentdown = commentdown;
        this.commentlist = commentlist;
        this.media = media;
        this.file = file;
        this.commentype = commentype;
        this.send = send;
        this.sheetBehavior = sheetBehavior;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public BottomSheetBehavior<View> getSheetBehavior() {
        return sheetBehavior;
    }

    public void setSheetBehavior(BottomSheetBehavior<View> sheetBehavior) {
        this.sheetBehavior = sheetBehavior;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ImageView getCommentdown() {
        return commentdown;
    }

    public void setCommentdown(ImageView commentdown) {
        this.commentdown = commentdown;
    }

    public ListView getCommentlist() {
        return commentlist;
    }

    public void setCommentlist(ListView commentlist) {
        this.commentlist = commentlist;
    }

    public CardView getMedia() {
        return media;
    }

    public void setMedia(CardView media) {
        this.media = media;
    }

    public CardView getFile() {
        return file;
    }

    public void setFile(CardView file) {
        this.file = file;
    }

    public EditText getCommentype() {
        return commentype;
    }

    public void setCommentype(EditText commentype) {
        this.commentype = commentype;
    }

    public CardView getSend() {
        return send;
    }

    public void setSend(CardView send) {
        this.send = send;
    }

   /* public Uri getFileuri() {
        return fileuri;
    }

    public void setFileuri(Uri fileuri) {
        this.fileuri = fileuri;
    }*/
}
