package com.mactrixapp.www.stranger.Model;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Mactrix on 4/18/2018.
 */

public class PlaybackStudio implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener
        ,MediaPlayer.OnErrorListener,MediaPlayer.OnSeekCompleteListener,MediaPlayer.OnInfoListener,MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {
    MediaPlayer mediaPlayer;
    private String mediaFile;
    Context context;
    private int resumePosition;
    boolean prepared = false;

    public PlaybackStudio(Context context){
        this.context = context;
        mediaPlayer= new MediaPlayer();

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);

        mediaPlayer.reset();


        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public PlaybackStudio(Context context, int res){
        this.context = context;
        mediaPlayer = MediaPlayer.create(context,res);

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);

        mediaPlayer.reset();


        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public int getCurrentMusicTime(){
        int c=0;


        try {
            c = mediaPlayer.getCurrentPosition();

        }catch (IllegalStateException e){
            mediaPlayer = new MediaPlayer();
            // mediaPlayer.reset();
            //mediaPlayer.release();
            // Toast.makeText(this, "Error! trying to access Media in an Illegal state", Toast.LENGTH_SHORT).show();
        }
        return c;
    }

    public int getMusicDuration(){
        int d = 1;

        if(mediaPlayer!=null) {
            if (mediaPlayer.isPlaying()) {
                d = mediaPlayer.getDuration();
            }
        }
        return d;
    }

    public void reset(){
        mediaPlayer.reset();
    }

    public void playMusic(int res){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.reset();

            try {
                mediaPlayer = MediaPlayer.create(context, res);
                //mediaPlayer.prepare();
                mediaPlayer.start();

            }/*catch (IOException e){
                playMusic(res);

            }*/catch (IllegalStateException e){
                playMusic(res);
            }catch (IllegalArgumentException e){
                Toast.makeText(context, "Invalid Song, Selete another Song...", Toast.LENGTH_SHORT).show();
            }

        }else{

        }
    }



    public void playMedia(){


        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.reset();

            try {
                mediaPlayer.setDataSource(mediaFile);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (IllegalArgumentException e){
                Toast.makeText(context, "Invalid Song", Toast.LENGTH_SHORT).show();
                // skipToNext();
            }


            //mediaPlayer.start();
        }else{
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(mediaFile);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (IllegalArgumentException e){
                Toast.makeText(context, "Invalid Song", Toast.LENGTH_SHORT).show();
                // skipToNext();
            }


        }



    }

    public void setVolume(float left, float right){
        mediaPlayer.setVolume(left,right);
    }

    public void playMedia(String mediaFile){
        if(mediaFile!=null) {
            //currentIndex = index;
            this.mediaFile = mediaFile;
            mediaPlayer.reset();
            playMedia();
            /*Intent intent = new Intent("start");
            Bundle bundling = new Bundle();
            bundling.putInt("startIndex", currentIndex);
            intent.putExtra("startbundle", bundling);
            sendBroadcast(intent);
            buildNotification(MyMusicService.PlaybackStatus.PLAYING);
            updateMetaData();*/
        }else {
            Toast.makeText(context, "Selete a Song from the List", Toast.LENGTH_SHORT).show();
        }
    }



    public void stopMedia(){

        if(mediaPlayer==null) return;
        try {


            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();

            }
        }catch (IllegalStateException e){
            mediaPlayer = new MediaPlayer();
            // mediaPlayer.stop();
        }
    }
    public void pauseMedia(){

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }


    }


    public void resumeMedia(){
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }

    }

    public void seekTo(int seekedTime){
        mediaPlayer.pause();
        mediaPlayer.seekTo(seekedTime);
        mediaPlayer.start();
    }

    public boolean isPlaying(){

        boolean isPlaying=false;
        try {
            isPlaying = mediaPlayer.isPlaying();
        }catch (IllegalStateException e){
            mediaPlayer = new MediaPlayer();
            isPlaying = mediaPlayer.isPlaying();
        }catch (Exception e){
            mediaPlayer = new MediaPlayer();
        }


        return isPlaying;
    }





    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {



        Intent intent = new Intent("completesong");
        context.sendBroadcast(intent);


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mediaPlayer.start();
        prepared = true;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    public boolean isPrepared() {
        return prepared;
    }
}
