package com.mactrixapp.www.stranger.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.method.MetaKeyKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mactrixapp.www.stranger.ChattingSpot;
import com.mactrixapp.www.stranger.Model.Chat;
import com.mactrixapp.www.stranger.Model.Like;
import com.mactrixapp.www.stranger.Model.MenuModel;
import com.mactrixapp.www.stranger.Model.Post;
import com.mactrixapp.www.stranger.Model.PostMore;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.SharedPref;
import com.mactrixapp.www.stranger.Stranger;
import com.mactrixapp.www.stranger.StrangerProfile;

import java.io.ByteArrayOutputStream;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Handler;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostListAdapter extends BaseAdapter {

    private SharedPref likewave;
    private FirebaseUser currentuser;
    Context context;
    ArrayList<PostMore> posts;
    LayoutInflater inflater;
    MenuModel menuModel;

    public PostListAdapter(Context context , ArrayList<PostMore> postArrayList) {

        this.context = context;
        posts = postArrayList;
        inflater = LayoutInflater.from(context);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        menuModel = new MenuModel(context);
        likewave = new SharedPref(context,context.getString(R.string.likewave));

    }



    class PostViewHolder{

        private TextView commentcount;
        private ImageView likeimage;
        private TextView likecount;
        private LinearLayout comment;
        private LinearLayout like;
        ImageView postvideothumbnail;// This previews the thumbnail of a video
        ImageView postmore; // The menu button to allow download
        ImageView postimage;// ImageView to show Post Images
        ImageView postvideobutton;//The Play and Pause button that changes from play to pause

        CircleImageView poststrangerimage;//The Stranger Profile Image

        TextView postname;// The Stranger Username;
        TextView origin; // The Stranger Origin
        TextView country; // The Stranger Country
        TextView posttext;// The TextView to display Text Post
        TextView postcapture;//The TextView to display post Capture

        VideoView postvideoview;// The VideoView to play Video Post

        RelativeLayout postvideolay;// The Video layout, this layout be clicked to make visible the videocontrollay
        RelativeLayout videocontrollay;// This hold the playpause button that can be made visible or gone.
        RelativeLayout postmedia;// This holds all the post media, it is made gone if the filetype is none.
        RelativeLayout posttextlay;// This layout hold the Text Media
        RelativeLayout posttextback;//This layout serves the background of the Text Media(White or Vintage) paper texture

        CardView playpasuecard;//This card is the play or pause button that is clickable

        LinearLayout reply;// Sends a message to the Stranger with the post
        LinearLayout call;// Sends the Stranger phone number to Call app
        LinearLayout strangerprofile;// Opens up the Stranger Profile Activity

        ProgressBar videoprogressbar;

        private int resumePosition;
        private int likecont = 0;
        private boolean likeclick;
        private DatabaseReference likeref;
        private boolean hasChild;


        public PostViewHolder(View v , int position) {

            // Intialize all the views

            postmore = (ImageView)v.findViewById(R.id.postmore);
            postimage = (ImageView)v.findViewById(R.id.postimage);
            postvideobutton = (ImageView)v.findViewById(R.id.postvideobutton);
            postvideothumbnail = (ImageView)v.findViewById(R.id.postvideothumnail);

            poststrangerimage = (CircleImageView)v.findViewById(R.id.poststrangerimage);

            postname  = (TextView)v.findViewById(R.id.postname);
            origin = (TextView)v.findViewById(R.id.origin);
            country = (TextView)v.findViewById(R.id.country);
            posttext = (TextView)v.findViewById(R.id.posttext);
            postcapture = (TextView)v.findViewById(R.id.postcapture);

            postvideoview = (VideoView)v.findViewById(R.id.postvideoview);

            postvideolay = (RelativeLayout)v.findViewById(R.id.postvideolay);
            videocontrollay = (RelativeLayout)v.findViewById(R.id.videocontrollay);
            postmedia = (RelativeLayout)v.findViewById(R.id.postmedia);
            posttextlay = (RelativeLayout)v.findViewById(R.id.posttextlay);
            posttextback = (RelativeLayout)v.findViewById(R.id.posttextback);

            playpasuecard = (CardView)v.findViewById(R.id.playpausecard);

            reply = (LinearLayout)v.findViewById(R.id.reply);
            call = (LinearLayout)v.findViewById(R.id.call);
            strangerprofile = (LinearLayout)v.findViewById(R.id.strangerprofile);
            like = (LinearLayout)v.findViewById(R.id.like);
            likecount = (TextView)v.findViewById(R.id.likecount);
            likeimage = (ImageView)v.findViewById(R.id.likeimage);
            comment = (LinearLayout)v.findViewById(R.id.comment);
            commentcount = (TextView)v.findViewById(R.id.commentcount);

            videoprogressbar = (ProgressBar)v.findViewById(R.id.postvideoprogress);


            displaypost(v,position);
            setUpStrangerProfile(position);

        }



        int count = 1;
        boolean prepared = false;


        public void displaypost(View v,int position){

            likeclick = false;
            int videocount = 0;
            Post post = posts.get(position).getPost();
            String postid = posts.get(position).getPostkey();
            String filetype = post.getType();

            // Implement the constant views which are capture and the clickable[Reply, Call, Stranger Profile, more, Like, Comment]

            // check for how many likes on the post

            likeref = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.post)).child(postid).child(context.getString(R.string.like));



            // update the like when users click on a post

            new Thread(() ->{

                FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.post)).child(postid).child(context.getString(R.string.like)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        likecount.setText(String.valueOf(dataSnapshot.getChildrenCount()));

                        /** the like database stores the user id of each user that clicks on it
                         * if the current user has once clicked on the post like, then the haschild boolean variable will be
                         * set to True..*/

                        if(dataSnapshot.hasChild(currentuser.getUid())){

                            likeimage.setImageResource(R.drawable.likefill);
                            hasChild = true;

                        }else{
                            likeimage.setImageResource(R.drawable.likeempty);
                            hasChild = false;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }).start();




            // implement the clicking of the like
            like.setOnClickListener(lcv ->{

                likewave.setBoolean(true);


                if(hasChild){

                    likeimage.setImageResource(R.drawable.likeempty);
                    hasChild = false;
                    new android.os.Handler().postDelayed(() -> likeref.child(currentuser.getUid()).removeValue(),500);


                }else{


                    likeimage.setImageResource(R.drawable.likefill);
                    hasChild =true;
                    new android.os.Handler().postDelayed(() -> likeref.child(currentuser.getUid()).setValue(currentuser.getUid()) ,500);


                }
                });




            // implement comment

            new Thread(() ->{


                FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.post)).child(postid).child(context.getString(R.string.comment)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ((Activity) new Stranger()).runOnUiThread(() -> commentcount.setText(String.valueOf(dataSnapshot.getChildrenCount())));


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }).start();

            comment.setOnClickListener(ccv ->{

                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.showcomment),postid);
                Intent intent = new Intent(context.getString(R.string.showcomment));
                intent.putExtra(context.getString(R.string.showcomment),bundle);
                context.sendBroadcast(intent);


            });

            reply.setOnClickListener((rv) ->{
                // TODO: Sent a Chat Messsage to the Stranger of this Post
                showreplydialog(post,filetype,v);
            });


            call.setOnClickListener((cv) ->{
                //TODO: Send the Stranger Phone Number to the call default application
                Toast.makeText(context, "Call", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+post.getPhonenum()));
                context.startActivity(intent);



            });
            strangerprofile.setOnClickListener((sv) ->{
                //TODO: Open the Stranger Profile Activity
                Toast.makeText(context, "Stranger Profile", Toast.LENGTH_SHORT).show();

                if(post.getUserid().equalsIgnoreCase(currentuser.getUid())){

                    Bundle bundlee = new Bundle();
                    bundlee.putInt("tab",4);
                    Intent intente = new Intent(context,Stranger.class);
                    intente.putExtra("tab",bundlee);
                    context.startActivity(intente);
                }else {

                    Intent intent = new Intent(context, StrangerProfile.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(context.getString(R.string.strangerid), post.getUserid());
                    Bundle activityname = new Bundle();
                    activityname.putString(context.getString(R.string.nameofactivity), context.getString(R.string.strangeract));
                    intent.putExtra(context.getString(R.string.strangerid), bundle);
                    intent.putExtra(context.getString(R.string.nameofactivity), activityname);
                    context.startActivity(intent);


                }


            });

            if(post.getUserid().equalsIgnoreCase(currentuser.getUid())){
                reply.setVisibility(View.GONE);
            }else{
                reply.setVisibility(View.VISIBLE);
            }

            postmore.setOnClickListener((pv) ->{

                //TODO: Implement a PopMenu here to allow the User to download the content of the Post. Whether Video, Image or
                // TODO: Audio(Not Conclusive)
                if(filetype.equals(context.getString(R.string.image))){

                    menuModel.donwloadimage(postmore,post.getFileurl(),post.getCapture());

                }else if(filetype.equals(context.getString(R.string.video))){
                    menuModel.donwloadvideo(postmore,post.getFileurl(),post.getCapture());

                }

            });
            postvideobutton.setOnClickListener((pvb) ->{

                // Play and Pause button control



                if(!prepared){


                    if(filetype.equalsIgnoreCase(context.getString(R.string.video))){

                        playmedia(post.getFileurl());

                        postvideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {

                                prepared = true;

                            }
                        });

                        postvideothumbnail.setVisibility(View.GONE);
                       // playmedia(post.getFileurl());
                        postvideoview.start();

                        postvideobutton.setImageResource(R.drawable.pause_bottom);



                    }



                    count = 1;
                }else {

                    if (isPlaying(v)) {

                        postvideothumbnail.setVisibility(View.GONE);
                        pauseMedia();
                        postvideobutton.setImageResource(R.drawable.play_button);
                        //prepared = false;
                    } else {
                        postvideothumbnail.setVisibility(View.GONE);
                        resumeMedia();
                        postvideobutton.setImageResource(R.drawable.pause_bottom);

                    }

                }





            });
            postvideolay.setOnClickListener((pvl) ->{

                if(count == 0){
                    videocontrollay.setVisibility(View.VISIBLE);
                    count = 1;
                }else{
                    videocontrollay.setVisibility(View.GONE);
                    count=0;
                }
            });


            postcapture.setText(post.getCapture()); // The Capture text content

            //Get the File type to give direction on how to display the post
            if(filetype.equalsIgnoreCase(context.getString(R.string.image))){

                //TODO: Here we display the Image post
                postmore.setEnabled(true);

                // Let make visible those that need to be visible
                postmedia.setVisibility(View.VISIBLE);
                postimage.setVisibility(View.VISIBLE);

                //Let make Invisible those that need to be invisible
                postvideolay.setVisibility(View.GONE);
                posttextlay.setVisibility(View.GONE);

                //Let View the Image on the Imageview
                try {
                    Glide.with(context).load(Uri.parse(post.getFileurl())).asBitmap().into(postimage);
                } catch (Exception e) {
                    postimage.setImageURI(Uri.parse(post.getFileurl()));
                }

                postimage.setOnClickListener(pic -> showfullimage(post));


            }else if(filetype.equalsIgnoreCase(context.getString(R.string.video))){
                //TODO: Here we display the Video post
                postmore.setEnabled(true);

                // Let Make visible those that need to be visible
                postmedia.setVisibility(View.VISIBLE);
                postvideolay.setVisibility(View.VISIBLE);
                postvideothumbnail.setVisibility(View.VISIBLE);
                videocontrollay.setVisibility(View.VISIBLE);

                //let make invisible those that need to be invisible
                postimage.setVisibility(View.GONE);
                posttextlay.setVisibility(View.GONE);

                // Let render the Video in the videoview

                try {
                    Glide.with(context).load(Uri.parse(post.getVideothumbnailurl())).placeholder(R.mipmap.videoplaceh).into(postvideothumbnail);
                } catch (Exception e) {
                    Glide.with(context).load(R.mipmap.videoplaceh).into(postvideothumbnail);
                }





            }else if(filetype.equalsIgnoreCase(context.getString(R.string.text))){
                //TODO: Here we display the text post
                postmore.setEnabled(false);

                //Let make visible those that need to be visible
                postmedia.setVisibility(View.VISIBLE);
                posttextlay.setVisibility(View.VISIBLE);

                //Let make invisible those that need to be invisible
                postimage.setVisibility(View.GONE);
                postvideolay.setVisibility(View.GONE);

                // Let implement the Text Post
                if(post.getPapertype().equalsIgnoreCase(context.getString(R.string.whiteback))){

                    // White paper background
                    posttextback.setBackgroundResource(R.mipmap.whitepaperback);
                }else{

                    // Vintage paper background
                    posttextback.setBackgroundResource(R.mipmap.vintagepaper);
                }

                posttext.setText(post.getTextmessage());


            }else{

                //TODO: Here we display only the capture because the file type is None

                //Let Invisible the PostMedia layout
                postmedia.setVisibility(View.GONE);
                postmore.setEnabled(false);

            }







        }


        // show the dialog
        Dialog sd;
        public void showreplydialog(Post post, String filetype, View v){
            sd = new Dialog(context);
            sd.setContentView(R.layout.dialog_reply_chat);
            sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            sd.show();

            CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
            CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
            ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
            EditText chatype = (EditText)sd.findViewById(R.id.chattype);

            cancel.setOnClickListener(c -> sd.dismiss());

            rightclick.setOnClickListener(rcv ->{
                sendreply(post,filetype,sd,chatype);


            });



        }

        public void sendreply(Post post, String filetype, Dialog sd, EditText chatype){

            Date date = new Date();
            Chat chat= new Chat();
            chat.setReceiverid(post.getUserid());
            chat.setSenderid(currentuser.getUid());
            chat.setDate(date.getTime());
            chat.setChatstatus(context.getString(R.string.delivered));


            if(filetype.equalsIgnoreCase(context.getString(R.string.text))){

                // when reply chat is text


                chat.setFiletype(context.getString(R.string.chatfiletype));
                chat.setMessage(post.getTextmessage());
               DatabaseReference chatting = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                        .child(currentuser.getUid()).child(context.getString(R.string.chat)).child(post.getUserid()).push();

               String key = chatting.getKey() != null ? chatting.getKey() : "hbfbbshsbasdg";


               DatabaseReference strangerchatReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                        .child(post.getUserid()).child(context.getString(R.string.chat)).child(currentuser.getUid())
                        .child(key);

               chat.setChatid(chatting.getKey());
               chatting.setValue(chat)
                .addOnSuccessListener(vvvv ->{

                    chat.setMessage(chatype.getText().toString());

                    DatabaseReference chating =FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                            .child(currentuser.getUid()).child(context.getString(R.string.chat)).child(post.getUserid()).push();


                    String keys = chating.getKey() != null ? chating.getKey() : "hbfbbshsbasdg";


                    DatabaseReference strangerchatReferences = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                            .child(post.getUserid()).child(context.getString(R.string.chat)).child(currentuser.getUid())
                            .child(keys);

                    chat.setChatid(chating.getKey());
                    chating.setValue(chat)
                            .addOnSuccessListener(vvv -> {Toast.makeText(context, "Replied", Toast.LENGTH_SHORT).show(); sd.dismiss();});


                    strangerchatReferences.setValue(chat);


                });
               strangerchatReference.setValue(chat);




            }else if(filetype.equalsIgnoreCase(context.getString(R.string.none))){

                // when reply chat is just a capture

                chat.setFiletype(context.getString(R.string.chatfiletype));
                chat.setMessage(post.getCapture());
                DatabaseReference chatss = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                        .child(currentuser.getUid()).child(context.getString(R.string.chat)).child(post.getUserid()).push();

                String key = chatss.getKey() != null ? chatss.getKey() : "hbfbbshsbasdg";

                DatabaseReference strangerchatReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                        .child(post.getUserid()).child(context.getString(R.string.chat)).child(currentuser.getUid())
                        .child(key);



                chat.setChatid(chatss.getKey());
                chatss.setValue(chat)
                        .addOnSuccessListener(vvvv ->{

                            chat.setMessage(chatype.getText().toString());

                            DatabaseReference chattsin = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                                    .child(currentuser.getUid()).child(context.getString(R.string.chat)).child(post.getUserid()).push();


                            String keys = chattsin.getKey() != null ? chattsin.getKey() : "hbfbbshsbasdg";

                            DatabaseReference strangerchatReferences = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                                    .child(post.getUserid()).child(context.getString(R.string.chat)).child(currentuser.getUid())
                                    .child(keys);


                            chat.setChatid(chattsin.getKey());
                            chattsin.setValue(chat)
                                    .addOnSuccessListener(vvv -> {Toast.makeText(context, "Replied", Toast.LENGTH_SHORT).show();sd.dismiss();});

                            strangerchatReferences.setValue(chat);


                        });

                strangerchatReference.setValue(chat);



            }else if(filetype.equalsIgnoreCase(context.getString(R.string.image))){

                // when reply chat is image


                chat.setFiletype(context.getString(R.string.imagefiletype));
                chat.setMessage(chatype.getText().toString());
                chat.setFileuri(post.getFileurl());

                DatabaseReference chating = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                        .child(currentuser.getUid()).child(context.getString(R.string.chat)).child(post.getUserid()).push();


                String key = chating.getKey() != null ? chating.getKey() : "hbfbbshsbasdg";

                DatabaseReference strangerchatReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                        .child(post.getUserid()).child(context.getString(R.string.chat)).child(currentuser.getUid())
                        .child(key);


                chat.setChatid(chating.getKey());
                chating.setValue(chat)
                        .addOnSuccessListener(vvv -> {Toast.makeText(context, "Replied", Toast.LENGTH_SHORT).show();sd.dismiss();});


                strangerchatReference.setValue(chat);

            }else if(filetype.equalsIgnoreCase(context.getString(R.string.video))){


                // when the reply is a video

                chat.setFiletype(context.getString(R.string.videofiletype));
                chat.setMessage(chatype.getText().toString());
                chat.setFileuri(post.getFileurl());

                DatabaseReference chating = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                        .child(currentuser.getUid()).child(context.getString(R.string.chat)).child(post.getUserid()).push();


                String key = chating.getKey() != null ? chating.getKey() : "hbfbbshsbasdg";

                DatabaseReference strangerchatReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user))
                        .child(post.getUserid()).child(context.getString(R.string.chat)).child(currentuser.getUid())
                        .child(key);




                chat.setChatid(chating.getKey());
                chating.setValue(chat)
                        .addOnSuccessListener(vvv -> {Toast.makeText(context, "Replied", Toast.LENGTH_SHORT).show();
                        sd.dismiss();});

                strangerchatReference.setValue(chat);

            }


        }

        public void showfullimage(Post post){

            Dialog fulld = new Dialog(context);
            fulld.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            fulld.setContentView(R.layout.dialog_full_image);
            fulld.show();

            ImageView cancel = (ImageView)fulld.findViewById(R.id.fullcancel);
            ImageView fullimage = (ImageView)fulld.findViewById(R.id.fullimage);

            cancel.setOnClickListener(ccc -> fulld.dismiss());

            try {
                Glide.with(context).load(Uri.parse(post.getFileurl())).asBitmap().into(fullimage);
            } catch (Exception e) {
                fullimage.setImageURI(Uri.parse(post.getFileurl()));
            }


        }

        public void setUpStrangerProfile(int position){

            // Here we collect the Stranger Profile Picture and Username
            Post post = posts.get(position).getPost();
           // postname.setText(post.getUsername());

            Date date = new Date(post.getDate());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  MMM dd, yyyy",Locale.getDefault());
            String datestring = sdf.format(date);

            poststrangerimage.setOnClickListener(psi ->{

                if(post.getUserid().equalsIgnoreCase(currentuser.getUid())){

                    Bundle bundlee = new Bundle();
                    bundlee.putInt("tab",4);
                    Intent intente = new Intent(context,Stranger.class);
                    intente.putExtra("tab",bundlee);
                    context.startActivity(intente);
                }else {

                    Intent intent = new Intent(context, StrangerProfile.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(context.getString(R.string.strangerid), post.getUserid());
                    Bundle activityname = new Bundle();

                    activityname.putString(context.getString(R.string.nameofactivity), context.getString(R.string.strangeract));
                    intent.putExtra(context.getString(R.string.strangerid), bundle);
                    intent.putExtra(context.getString(R.string.nameofactivity), activityname);
                    context.startActivity(intent);


                }

            });

                FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(post.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        if(user != null){
                            try {
                                Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(poststrangerimage);
                            } catch (Exception e) {
                                poststrangerimage.setImageResource(R.mipmap.profileavatar);
                            }

                            postname.setText(user.getUsername());
                            country.setText(datestring);
                            origin.setText(user.getOrigin()+" "+user.getCountry());
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






        }

        public boolean isPlaying(View v){
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


        public void playmedia(String data) {

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

            postvideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                        @Override
                        public void onBufferingUpdate(MediaPlayer mp, int percent) {
                            if(percent < 100){

                                videoprogressbar.setVisibility(View.VISIBLE);

                            }else{

                                videoprogressbar.setVisibility(View.INVISIBLE);

                            }
                        }
                    });
                }
            });



        }

        public void playmedia(Uri uri) {

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

    }


    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.postlayout,parent,false);
        }

        new PostViewHolder(convertView,position);

        return convertView;
    }



}
