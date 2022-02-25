package com.mactrixapp.www.stranger.Model;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.VideoView;

import com.mactrixapp.www.stranger.R;

public class VideoStudio implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {


    Context context;
    public VideoStudio(Context context) {

        this.context = context;
    }

    private int resumePosition;

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

    public boolean isPlaying(View v, VideoView postvideoview){
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

            postvideoview.start();


        } catch (IllegalArgumentException e) {


            Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();
            postvideoview.start();

        } catch (Exception e) {
            Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();
            postvideoview.start();


        }
    }

    public void playmedia(Uri uri, VideoView postvideoview) {

        try {
            //Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();
            postvideoview.start();


        } catch (IllegalArgumentException e) {
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();
            postvideoview.start();

        } catch (Exception e) {
            // Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();
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

    public void stopMedia(VideoView postvideoview){
        postvideoview.stopPlayback();
    }




    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}
