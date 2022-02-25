package com.mactrixapp.www.stranger.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Model.Chat;
import com.mactrixapp.www.stranger.Model.Comment;
import com.mactrixapp.www.stranger.Model.MenuModel;
import com.mactrixapp.www.stranger.Model.PlaybackStudio;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.Model.VideoStudio;
import com.mactrixapp.www.stranger.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends BaseAdapter {
    private PlaybackStudio playbackStudio;
    private Context context;
   private ArrayList<Comment> comments;
   private LayoutInflater inflater;

   private final int TEXT = 0;
   private final int IMAGE = 1;
   private final int VIDEO = 2;
   private final int AUDIO = 3;
   private final int FILE = 4;

   private int type = 0;


    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
        inflater = LayoutInflater.from(context);
        playbackStudio = new PlaybackStudio(context);
    }

    class ViewHolder{

        private CircleImageView commentphoto;
        private TextView commentfullname;
        private TextView commentusername;
        private TextView commentdate;
        private TextView commenttext;

        public ViewHolder(View v , int p) {

            Comment comment = comments.get(p);

            commentphoto =(CircleImageView) v.findViewById(R.id.commentphoto);
            commentfullname =(TextView) v.findViewById(R.id.commentfullname);
            commentusername =(TextView) v. findViewById(R.id.commentusername);
            commentdate =(TextView) v.findViewById(R.id.commentdate);
            commenttext = (TextView)v.findViewById(R.id.comment);

            commentdate.setText(comment.toDate());
            commenttext.setText(comment.getComment());

            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(comment.getUserid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    if(user != null){

                        try {
                            Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().placeholder(R.mipmap.profileavatar).into(commentphoto);
                        } catch (Exception e) {
                            commentphoto.setImageResource(R.mipmap.profileavatar);
                        }
                        commentfullname.setText(user.getFullname());
                        commentusername.setText(user.getUsername());

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


    }

    class ViewHolderImage{

        private ImageView commentimage;

        public ViewHolderImage(View v , int p) {
            Comment comment = comments.get(p);
            new ViewHolder(v,p);

            commentimage = (ImageView)v.findViewById(R.id.commentimage);

            try {
                Glide.with(context).load(Uri.parse(comment.getUri())).asBitmap().into(commentimage);
            } catch (Exception e) {
                commentimage.setImageURI(Uri.parse(comment.getUri()));
            }

            commentimage.setOnClickListener(ccc -> showfullimage(comment));

            commentimage.setOnLongClickListener(clc ->{

                MenuModel menuModel = new MenuModel(context);
                menuModel.donwloadimage(clc,comment.getUri(),"image");


                return true;
            });


        }

        public void showfullimage(Comment comment){

            Dialog fulld = new Dialog(context);
            fulld.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            fulld.setContentView(R.layout.dialog_full_image);
            fulld.show();

            ImageView cancel = (ImageView)fulld.findViewById(R.id.fullcancel);
            ImageView fullimage = (ImageView)fulld.findViewById(R.id.fullimage);

            cancel.setOnClickListener(ccc -> fulld.dismiss());

            try {
                Glide.with(context).load(Uri.parse(comment.getUri())).asBitmap().into(fullimage);
            } catch (Exception e) {
                fullimage.setImageURI(Uri.parse(comment.getUri()));
            }


        }
    }

    class ViewHolderVideo{

        private boolean prepared = false;
        private VideoView chatvideo;
        private ImageView chatvideothumbnail;
        private RelativeLayout chatvideolay;
        private RelativeLayout chatcontroller;
        private CardView playpause;
        private ImageView videobutton;
        private int count = 0;
        VideoStudio videoStudio;

        public ViewHolderVideo(View v , int p) {

            Comment comment = comments.get(p);
            videoStudio = new VideoStudio(context);
            new ViewHolder(v,p);

            chatvideo = (VideoView)v.findViewById(R.id.chatvideo);
            chatvideothumbnail = (ImageView)v.findViewById(R.id.chatvideothumnail);
            chatvideolay = (RelativeLayout)v.findViewById(R.id.chatvideolay);
            chatcontroller = (RelativeLayout)v.findViewById(R.id.videocontrollay);
            playpause = (CardView)v.findViewById(R.id.playpausecard);
            videobutton = (ImageView)v.findViewById(R.id.postvideobutton);

            chatvideothumbnail.setImageResource(R.mipmap.videoplaceh);

            chatvideolay.setOnClickListener((pvl) ->{

                if(count == 0){
                    chatcontroller.setVisibility(View.VISIBLE);
                    count = 1;
                }else{
                    chatcontroller.setVisibility(View.GONE);
                    count=0;
                }
            });

            playpause.setOnClickListener((pvb) ->{

                // Play and Pause button control

                if(!prepared){

                    videoStudio.playmedia(comment.getUri(),chatvideo);

                    chatvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {

                            prepared = true;

                        }
                    });


                    chatvideothumbnail.setVisibility(View.GONE);
                    // playmedia(post.getFileurl());
                    //chatvideo.start();


                    videobutton.setImageResource(R.drawable.pause_bottom_sm_black);



                    count = 1;
                }else {

                    if (videoStudio.isPlaying(v,chatvideo)) {

                        chatvideothumbnail.setVisibility(View.GONE);
                        videoStudio.pauseMedia(chatvideo);
                        videobutton.setImageResource(R.drawable.play_button_sm_black);

                        //prepared = false;
                    } else {
                        chatvideothumbnail.setVisibility(View.GONE);
                        videoStudio.resumeMedia(chatvideo);
                        videobutton.setImageResource(R.drawable.pause_bottom_sm_black);



                    }

                }





            });

            chatvideolay.setOnLongClickListener(cvlc ->{
                MenuModel menuModel = new MenuModel(context);
                menuModel.donwloadvideo(v,comment.getUri(),"video");
                return true;
            });


        }
    }

    class ViewHolderAudio{


        private TextView chataudioname;
        private ImageView playpause;

        public ViewHolderAudio(View v , int p) {
            Comment comment = comments.get(p);
            new ViewHolder(v,p);

            chataudioname = (TextView)v.findViewById(R.id.chataudioname);
            playpause = (ImageView)v.findViewById(R.id.playpausecard);

            chataudioname.setText(comment.getFilename());

            playpause.setOnClickListener(ppv ->{

                // Play the audio here
                if(!playbackStudio.isPrepared()){

                    playbackStudio.playMedia(comment.getUri());
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

            chataudioname.setOnLongClickListener(calc ->{

                MenuModel menuModel = new MenuModel(context);
                menuModel.donwloadaudio(v,comment.getUri(),comment.getFilename());

                return  true;
            });


        }
    }
    class ViewHolderFile{

        private TextView chatfilename;
        private ImageView downloadfile;

        public ViewHolderFile(View v , int p) {

            Comment comment = comments.get(p);

            new ViewHolder(v,p);
            chatfilename = (TextView)v.findViewById(R.id.chatfilename);
            downloadfile = (ImageView)v.findViewById(R.id.downloadfile);

            chatfilename.setText(comment.getFilename());
            downloadfile.setOnClickListener(dv ->{

                // TODO Download and open the file
                MenuModel menuModel = new MenuModel(context);
                menuModel.donwloadother(v,comment.getUri(),comment.getFilename());

            });
        }
    }


    @Override
    public int getCount() {
        return comments.size();
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



            if(viewtype(position) == TEXT){
                convertView = inflater.inflate(R.layout.comment_lay,parent,false);
                new ViewHolder(convertView,position);
            }else if(viewtype(position) == IMAGE){
                convertView = inflater.inflate(R.layout.comment_image_lay,parent,false);
                new ViewHolderImage(convertView,position);
            }else if(viewtype(position) == VIDEO){
                convertView = inflater.inflate(R.layout.comment_video_lay,parent,false);
                new ViewHolderVideo(convertView,position);
            }else if(viewtype(position) == AUDIO){
                convertView = inflater.inflate(R.layout.comment_audio_lay,parent,false);
                new ViewHolderAudio(convertView,position);
            }else if(viewtype(position) == FILE){
                convertView = inflater.inflate(R.layout.comment_other_lay,parent,false);
                new ViewHolderFile(convertView,position);
            }





        return convertView;
    }

    public int viewtype(int p){

        Comment comment = comments.get(p);

        if(comment.getType().equalsIgnoreCase(context.getString(R.string.commenttext))){
            type = TEXT;

        }else if(comment.getType().equalsIgnoreCase(context.getString(R.string.commentimage))){
            type  = IMAGE;

        }else if(comment.getType().equalsIgnoreCase(context.getString(R.string.commentvideo))){
            type = VIDEO;

        }else if(comment.getType().equalsIgnoreCase(context.getString(R.string.commentaudio))){
           type = AUDIO;
        }else if (comment.getType().equalsIgnoreCase(context.getString(R.string.commentfile))){
            type = FILE;
        }



        return type;
    }

}
