package com.mactrixapp.www.stranger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.mactrixapp.www.stranger.Model.Post;
import com.mactrixapp.www.stranger.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridProfileAdapter extends BaseAdapter {


    Context context;
    ArrayList<Post> posts;
    LayoutInflater inflater;

    public GridProfileAdapter(Context context , ArrayList<Post> postArrayList) {

        this.context = context;
        posts = postArrayList;
        inflater = LayoutInflater.from(context);

    }






    class PostViewHolder{

        ImageView postvideothumbnail;// This previews the thumbnail of a video
        ImageView postmore; // The menu button to allow download
        ImageView postimage;// ImageView to show Post Images
        ImageView postvideobutton;//The Play and Pause button that changes from play to pause

        CircleImageView poststrangerimage;//The Stranger Profile Image

        TextView postname;// The Stranger Username;
        TextView posttext;// The TextView to display Text Post
       // TextView postcapture;//The TextView to display post Capture

        VideoView postvideoview;// The VideoView to play Video Post

        RelativeLayout postvideolay;// The Video layout, this layout be clicked to make visible the videocontrollay
        RelativeLayout videocontrollay;// This hold the playpause button that can be made visible or gone.
        RelativeLayout postmedia;// This holds all the post media, it is made gone if the filetype is none.
        RelativeLayout posttextlay;// This layout hold the Text Media
        RelativeLayout posttextback;//This layout serves the background of the Text Media(White or Vintage) paper texture

        CardView playpasuecard;//This card is the play or pause button that is clickable


        ProgressBar videoprogressbar;

        private int resumePosition;


        public PostViewHolder(View v , int position) {

            // Intialize all the views

            postimage = (ImageView) v.findViewById(R.id.postimage);
            postvideobutton = (ImageView) v.findViewById(R.id.postvideobutton);
            postvideothumbnail = (ImageView) v.findViewById(R.id.postvideothumnail);
            poststrangerimage = (CircleImageView) v.findViewById(R.id.poststrangerimage);
            // postname = (TextView) v.findViewById(R.id.postname);
            posttext = (TextView) v.findViewById(R.id.posttext);
           // postcapture = (TextView) v.findViewById(R.id.postcapture);
            postvideoview = (VideoView) v.findViewById(R.id.postvideoview);
            postvideolay = (RelativeLayout) v.findViewById(R.id.postvideolay);
            videocontrollay = (RelativeLayout) v.findViewById(R.id.videocontrollay);
            postmedia = (RelativeLayout) v.findViewById(R.id.postmedia);
            posttextlay = (RelativeLayout) v.findViewById(R.id.posttextlay);
            posttextback = (RelativeLayout) v.findViewById(R.id.posttextback);
            playpasuecard = (CardView) v.findViewById(R.id.playpausecard);
            videoprogressbar = (ProgressBar)v.findViewById(R.id.postvideoprogress);


            displaypost(v,position);
            //setUpStrangerProfile(position);

            v.setOnClickListener(vv->{

                Bundle bundle = new Bundle();
                bundle.putInt(context.getString(R.string.postclick),position);
                Intent intent = new Intent(context.getString(R.string.postclick));
                intent.putExtra(context.getString(R.string.postclick),bundle);
                context.sendBroadcast(intent);


            });



        }

        // Other inportant methods

        int count = 1;
        boolean prepared = false;


        public void displaypost(View v,int position){

            int videocount = 0;
            Post post = posts.get(position);
            String filetype = post.getType();

            // Implement the constant views which are capture and the clickable[Reply, Call, Stranger Profile, more]


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

                        postvideobutton.setImageResource(R.drawable.pause_bottomsmall);



                    }



                    count = 1;
                }else {

                    if (isPlaying(v)) {

                        postvideothumbnail.setVisibility(View.GONE);
                        pauseMedia();
                        postvideobutton.setImageResource(R.drawable.play_buttonsmall);
                        //prepared = false;
                    } else {
                        postvideothumbnail.setVisibility(View.GONE);
                        resumeMedia();
                        postvideobutton.setImageResource(R.drawable.pause_bottomsmall);

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

            postvideolay.setOnLongClickListener(pvlc ->{

                Bundle bundle = new Bundle();
                bundle.putInt(context.getString(R.string.postclick),position);
                Intent intent = new Intent(context.getString(R.string.postclick));
                intent.putExtra(context.getString(R.string.postclick),bundle);
                context.sendBroadcast(intent);

                return false;
            });
           // postcapture.setText(post.getCapture()); // The Capture text content



            //Get the File type to give direction on how to display the post


            if(filetype.equalsIgnoreCase(context.getString(R.string.image))){

                //TODO: Here we display the Image post

                // Let make visible those that need to be visible
                postmedia.setVisibility(View.VISIBLE);
                postimage.setVisibility(View.VISIBLE);

                //Let make Invisible those that need to be invisible
                postvideolay.setVisibility(View.GONE);
                posttextlay.setVisibility(View.GONE);

                //Let View the Image on the Imageview
                Glide.with(context).load(Uri.parse(post.getFileurl())).asBitmap().into(postimage);




            }else if(filetype.equalsIgnoreCase(context.getString(R.string.video))){
                //TODO: Here we display the Video post

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

                //Let make visible those that need to be visible

                postmedia.setVisibility(View.VISIBLE);
                posttextlay.setVisibility(View.VISIBLE);

                //Let make invisible those that need to be invisible
                postimage.setVisibility(View.GONE);
                postvideolay.setVisibility(View.GONE);

                posttext.setText(post.getCapture());

            }







        }

       /* public void setUpStrangerProfile(int position){

            // Here we collect the Stranger Profile Picture and Username
            Post post = posts.get(position);

            Glide.with(context).load(Uri.parse(post.getUserphotourl())).asBitmap().placeholder(R.mipmap.profileavatar).into(poststrangerimage);
            postname.setText(post.getUsername());



        }*/

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
            convertView = inflater.inflate(R.layout.gridprofilepostlay,parent,false);
        }

        new PostViewHolder(convertView,position);

        return convertView;
    }


}
