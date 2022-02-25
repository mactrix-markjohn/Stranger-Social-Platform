package com.mactrixapp.www.stranger.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mactrixapp.www.stranger.Model.Chat;
import com.mactrixapp.www.stranger.Model.DownloadModel;
import com.mactrixapp.www.stranger.Model.MenuModel;
import com.mactrixapp.www.stranger.Model.PlaybackStudio;
import com.mactrixapp.www.stranger.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter {


    Context context;
    ArrayList<Chat> chats;
    LayoutInflater inflater;

    private final int CHATRIGHT = 1;
    private final int CHATRIGHTIMAGE=2;
    private final int CHATRIGHTVIDEO = 3;
    private final int CHATRIGHTAUDIO = 4;
    private final int CHATRIGHTFILES = 5;
    private final int CHATLEFT = 0;
    private final int CHATLEFTIMAGE = 6;
    private final int CHATLEFTVIDEO = 7;
    private final int CHATLEFTAUDIO = 8;
    private final int CHATLEFTFILES = 9;
    private int type = 0;
    PlaybackStudio playbackStudio;

    FirebaseUser currentUser;
    private int resumePosition;
    private boolean prepared = false;
    private int count;
    MenuModel menuModel;
    private int point;

    public ChatAdapter(Context context, ArrayList<Chat> chats) {
        this.context = context;
        this.chats = chats;
        inflater = LayoutInflater.from(context);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        playbackStudio = new PlaybackStudio(context);
        menuModel = new MenuModel(context);
    }

    public ChatAdapter() {





    }


    // Normal chat
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatstatus;
        TextView chattime;
        TextView chatmessage;
        public ViewHolder(View itemView) {
            super(itemView);



            chatstatus = (TextView)itemView.findViewById(R.id.chatstatus);
            chattime = (TextView)itemView.findViewById(R.id.chattime);
            chatmessage = (TextView)itemView.findViewById(R.id.chatmessage);
        }
    }

    // Image chat
    class ViewHolderImage extends RecyclerView.ViewHolder {
        ImageView chatimage;
        ImageView chatmore;
        TextView chatstatus;
        TextView chattime;
        TextView chatmessage;
        public ViewHolderImage(View itemView) {
            super(itemView);

            chatstatus = (TextView)itemView.findViewById(R.id.chatstatus);
            chattime = (TextView)itemView.findViewById(R.id.chattime);
            chatmessage = (TextView)itemView.findViewById(R.id.chatmessage);
            chatimage = (ImageView)itemView.findViewById(R.id.chatimagea);
            chatmore = (ImageView)itemView.findViewById(R.id.chatimagemore);
        }
    }

    // Video chat
    class ViewHolderVideo extends RecyclerView.ViewHolder {
        View view;
        ImageView videobutton;
        RelativeLayout chatcontroller;
        ImageView chatmore;
        VideoView chatvideo;
        ImageView chatvideothumbnail;
        RelativeLayout chatvideolay;
        CardView playpause;
        TextView chatstatus;
        TextView chattime;
        TextView chatmessage;
        public ViewHolderVideo(View itemView) {
            super(itemView);

            chatstatus = (TextView)itemView.findViewById(R.id.chatstatus);
            chattime = (TextView)itemView.findViewById(R.id.chattime);
            chatmessage = (TextView)itemView.findViewById(R.id.chatmessage);
            chatmore = (ImageView)itemView.findViewById(R.id.chatimagemore);
            chatvideo = (VideoView)itemView.findViewById(R.id.chatvideo);
            chatvideothumbnail = (ImageView)itemView.findViewById(R.id.chatvideothumnail);
            chatvideolay = (RelativeLayout)itemView.findViewById(R.id.chatvideolay);
            chatcontroller = (RelativeLayout)itemView.findViewById(R.id.videocontrollay);
            playpause = (CardView)itemView.findViewById(R.id.playpausecard);
            videobutton = (ImageView)itemView.findViewById(R.id.postvideobutton);
            view = itemView;

        }
    }

    // Audio chat
    class ViewHolderAudio extends RecyclerView.ViewHolder {
        ImageView chatmore;
        TextView chataudioname;
        ImageView playpause;
        TextView chatstatus;
        TextView chattime;
        TextView chatmessage;
        public ViewHolderAudio(View itemView) {
            super(itemView);

            chatstatus = (TextView)itemView.findViewById(R.id.chatstatus);
            chattime = (TextView)itemView.findViewById(R.id.chattime);
            chatmessage = (TextView)itemView.findViewById(R.id.chatmessage);
            chatmore = (ImageView)itemView.findViewById(R.id.chatimagemore);
            chataudioname = (TextView)itemView.findViewById(R.id.chataudioname);
            playpause = (ImageView)itemView.findViewById(R.id.playpausecard);

        }
    }

    // File chat
    class ViewHolderFile extends RecyclerView.ViewHolder {
        ImageView chatmore;
        TextView chatfilename;
        ImageView downloadfile;
        TextView chatstatus;
        TextView chattime;
        TextView chatmessage;
        public ViewHolderFile(View itemView) {
            super(itemView);

            chatstatus = (TextView)itemView.findViewById(R.id.chatstatus);
            chattime = (TextView)itemView.findViewById(R.id.chattime);
            chatmessage = (TextView)itemView.findViewById(R.id.chatmessage);
            chatmore = (ImageView)itemView.findViewById(R.id.chatimagemore);
            chatfilename = (TextView)itemView.findViewById(R.id.chatfilename);
            downloadfile = (ImageView)itemView.findViewById(R.id.downloadfile);
        }
    }






    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        type = viewType;

        if(viewType == CHATRIGHT) {
            View view = inflater.inflate(R.layout.chatsenderright, parent, false);
            return new ChatAdapter.ViewHolder(view);

        }else if (viewType == CHATLEFT){
            View view = inflater.inflate(R.layout.chatrecieverleft, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }else if(viewType == CHATRIGHTIMAGE) {

            //right image
            View view = inflater.inflate(R.layout.chatsenderrightimage, parent, false);
            return new ChatAdapter.ViewHolderImage(view);

        }else if(viewType == CHATLEFTIMAGE){
            View view = inflater.inflate(R.layout.chatrecieverleftimage, parent, false);
            return new ChatAdapter.ViewHolderImage(view);

        }else if(viewType == CHATRIGHTVIDEO){

            View view = inflater.inflate(R.layout.chatsenderrightvideo, parent, false);
            return new ChatAdapter.ViewHolderVideo(view);

        }else if(viewType == CHATLEFTVIDEO){
            View view = inflater.inflate(R.layout.chatrecieverleftvideo, parent, false);
            return new ChatAdapter.ViewHolderVideo(view);

        }else if(viewType == CHATRIGHTAUDIO){

            View view = inflater.inflate(R.layout.chatsenderrightaudio, parent, false);
            return new ChatAdapter.ViewHolderAudio(view);

        }else if(viewType ==CHATLEFTAUDIO){

            View view = inflater.inflate(R.layout.chatrecieverleftaudio, parent, false);
            return new ChatAdapter.ViewHolderAudio(view);

        }else if(viewType ==CHATRIGHTFILES){

            View view = inflater.inflate(R.layout.chatsenderrightfiles, parent, false);
            return new ChatAdapter.ViewHolderFile(view);


        }else{

            View view = inflater.inflate(R.layout.chatrecieverleftfiles, parent, false);
            return new ChatAdapter.ViewHolderFile(view);
        }

    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        Chat chat = chats.get(position);
       // point = position;


        if(type == CHATRIGHT) {
            bindview((ChatAdapter.ViewHolder)holder,chat);

        }else if (type == CHATLEFT){

            bindview((ChatAdapter.ViewHolder)holder,chat);

        }else if(type == CHATRIGHTIMAGE) {

            bindviewImage((ChatAdapter.ViewHolderImage)holder,chat);

        }else if(type == CHATLEFTIMAGE){
            bindviewImage((ChatAdapter.ViewHolderImage)holder,chat);

        }else if(type == CHATRIGHTVIDEO){

            bindviewvideo((ChatAdapter.ViewHolderVideo)holder,chat);

        }else if(type == CHATLEFTVIDEO){
            bindviewvideo((ChatAdapter.ViewHolderVideo)holder,chat);

        }else if(type == CHATRIGHTAUDIO){

            bindviewaudio((ChatAdapter.ViewHolderAudio)holder,chat);

        }else if(type ==CHATLEFTAUDIO){

            bindviewaudio((ChatAdapter.ViewHolderAudio)holder,chat);

        }else if(type ==CHATRIGHTFILES){

            bindviewfiles((ChatAdapter.ViewHolderFile)holder,chat);


        }else{

            bindviewfiles((ChatAdapter.ViewHolderFile)holder,chat);
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {

        Chat chat = chats.get(position);


        if (chat != null && chat.getSenderid().equalsIgnoreCase(currentUser.getUid())) {

            // Right Chat

            if(chat.getFiletype().equals(context.getString(R.string.chatfiletype))){
                type =  CHATRIGHT;


            }else if(chat.getFiletype().equals(context.getString(R.string.imagefiletype))){
                type =  CHATRIGHTIMAGE;

            }else if (chat.getFiletype().equals(context.getString(R.string.videofiletype))){

                type = CHATRIGHTVIDEO;

            }else if(chat.getFiletype().equals(context.getString(R.string.audiofiletype))){

                type = CHATRIGHTAUDIO;


            }else if (chat.getFiletype().equals(context.getString(R.string.otherfiletype))){

                type = CHATRIGHTFILES;
            }



        }else if(chat != null){
            // Left Chat

            if(chat.getFiletype().equals(context.getString(R.string.chatfiletype))){
                type = CHATLEFT;


            }else if(chat.getFiletype().equals(context.getString(R.string.imagefiletype))){
                type = CHATLEFTIMAGE;

            }else if (chat.getFiletype().equals(context.getString(R.string.videofiletype))){

                type = CHATLEFTVIDEO;

            }else if(chat.getFiletype().equals(context.getString(R.string.audiofiletype))){

                type = CHATLEFTAUDIO;


            }else if (chat.getFiletype().equals(context.getString(R.string.otherfiletype))){

                type = CHATLEFTFILES;
            }


        }


        return type;
    }



    //TODO: Method to bind view
    public void bindview (ChatAdapter.ViewHolder holder, Chat chat){



        // Retrieving the date and time of the chat
        long time = chat.getDate();
        Date date = new Date(time);
        Date now = new Date();
        long today = now.getTime();
        SimpleDateFormat sd = new SimpleDateFormat("dd mm yyyy",Locale.getDefault());
        if(sd.format(date).equalsIgnoreCase(sd.format(now))){

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String chattime = sdf.format(date)+" Today";

            holder.chattime.setText(chattime);
        }else {

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  MMMM dd, yyyy", Locale.getDefault());
            String chattime = sdf.format(date);

            holder.chattime.setText(chattime);
        }


        // input the chat status{Delivered, or Seen, or Sent}
        holder.chatstatus.setText(chat.getChatstatus());


        // input the chat message
        holder.chatmessage.setText(chat.getMessage());

    }



    public void bindviewImage(ChatAdapter.ViewHolderImage holder,Chat chat){// Retrieving the date and time of the chat
        long time = chat.getDate();
        Date date = new Date(time);
        Date now = new Date();
        long today = now.getTime();
        SimpleDateFormat sd = new SimpleDateFormat("dd mm yyyy",Locale.getDefault());
        if(sd.format(date).equalsIgnoreCase(sd.format(now))){

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String chattime = sdf.format(date)+" Today";

            holder.chattime.setText(chattime);
        }else {

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  MMMM dd, yyyy", Locale.getDefault());
            String chattime = sdf.format(date);

            holder.chattime.setText(chattime);
        }
        // input the chat status{Delivered, or Seen, or Sent}
        holder.chatstatus.setText(chat.getChatstatus());


        // input the chat message
        holder.chatmessage.setText(chat.getMessage());


        holder.chatmore.setOnClickListener(vm ->{

            //TODO: download the image
            menuModel.donwloadimage(holder.chatmore,chat.getFileuri(),chat.getFilename());

        });

        // CHat image set the image uri
        try {
            Glide.with(context).load(Uri.parse(chat.getFileuri())).asBitmap().into(holder.chatimage);
        } catch (Exception e) {
            holder.chatimage.setImageURI(Uri.parse(chat.getFileuri()));
        }

        holder.chatimage.setOnClickListener(cim -> showfullimage(chat));


    }





    public void bindviewvideo(ChatAdapter.ViewHolderVideo holder, Chat chat){// Retrieving the date and time of the chat
        long time = chat.getDate();
        Date date = new Date(time);
        Date now = new Date();
        long today = now.getTime();
        SimpleDateFormat sd = new SimpleDateFormat("dd mm yyyy",Locale.getDefault());
        if(sd.format(date).equalsIgnoreCase(sd.format(now))){

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String chattime = sdf.format(date)+" Today";

            holder.chattime.setText(chattime);
        }else {

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  MMMM dd, yyyy", Locale.getDefault());
            String chattime = sdf.format(date);

            holder.chattime.setText(chattime);
        }
        // input the chat status{Delivered, or Seen, or Sent}
        holder.chatstatus.setText(chat.getChatstatus());
        // input the chat message
        holder.chatmessage.setText(chat.getMessage());


        try {
            Glide.with(context).load(Uri.parse(chat.getFileuri())).placeholder(R.mipmap.videoplaceh).into(holder.chatvideothumbnail);
        } catch (Exception e) {

            holder.chatvideothumbnail.setImageResource(R.mipmap.videoplaceh);
        }


        holder.chatmore.setOnClickListener(vm ->{


            //TODO: implemtent the download of the video
            menuModel.donwloadvideo(holder.chatmore,chat.getFileuri(),chat.getFilename());
        });


        holder.playpause.setOnClickListener((pvb) ->{

            // Play and Pause button control

            if(!prepared){

                playmedia(chat.getFileuri(),holder.chatvideo);

                    holder.chatvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {

                            prepared = true;

                        }
                    });


                    holder.chatvideothumbnail.setVisibility(View.GONE);
                    // playmedia(post.getFileurl());
                    holder.chatvideo.start();


                    if(type == CHATRIGHTVIDEO){

                        holder.videobutton.setImageResource(R.drawable.pause_bottom_sm);

                    }else if(type == CHATLEFTVIDEO){
                        holder.videobutton.setImageResource(R.drawable.pause_bottom_sm_black);

                    }

                    count = 1;
            }else {

                if (isPlaying(holder.view,holder.chatvideo)) {

                    holder.chatvideothumbnail.setVisibility(View.GONE);
                    pauseMedia(holder.chatvideo);
                    if(type == CHATRIGHTVIDEO){

                        holder.videobutton.setImageResource(R.drawable.play_button_sm);

                    }else if(type == CHATLEFTVIDEO){
                        holder.videobutton.setImageResource(R.drawable.play_button_sm_black);

                    }
                    //prepared = false;
                } else {
                    holder.chatvideothumbnail.setVisibility(View.GONE);
                    resumeMedia(holder.chatvideo);
                    if(type == CHATRIGHTVIDEO){

                        holder.videobutton.setImageResource(R.drawable.pause_bottom_sm);

                    }else if(type == CHATLEFTVIDEO){
                        holder.videobutton.setImageResource(R.drawable.pause_bottom_sm_black);

                    }

                }

            }





        });

        holder.chatvideolay.setOnClickListener((pvl) ->{

            if(count == 0){
                holder.chatcontroller.setVisibility(View.VISIBLE);
                count = 1;
            }else{
                holder.chatcontroller.setVisibility(View.GONE);
                count=0;
            }
        });




    }


    public void bindviewaudio(ChatAdapter.ViewHolderAudio holder, Chat chat){// Retrieving the date and time of the chat
        long time = chat.getDate();
        Date date = new Date(time);
        Date now = new Date();
        long today = now.getTime();
        SimpleDateFormat sd = new SimpleDateFormat("dd mm yyyy",Locale.getDefault());
        if(sd.format(date).equalsIgnoreCase(sd.format(now))){

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String chattime = sdf.format(date)+" Today";

            holder.chattime.setText(chattime);
        }else {

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  MMMM dd, yyyy", Locale.getDefault());
            String chattime = sdf.format(date);

            holder.chattime.setText(chattime);
        }


        // input the chat status{Delivered, or Seen, or Sent}
        holder.chatstatus.setText(chat.getChatstatus());
        // input the chat message
        holder.chatmessage.setText(chat.getMessage());
        holder.chataudioname.setText(chat.getFilename());

        holder.chatmore.setOnClickListener(mv ->{


            // TODO: download audio
            menuModel.donwloadaudio(holder.chatmore,chat.getFileuri(),chat.getFilename());
        });




        holder.playpause.setOnClickListener(ppv ->{




            // Play the audio here
            if(!playbackStudio.isPrepared()){

                playbackStudio.playMedia(chat.getFileuri());

                if(type == CHATRIGHTAUDIO){

                    holder.playpause.setImageResource(R.drawable.pause_bottom_sm);

                }else if(type == CHATLEFTAUDIO){
                    holder.playpause.setImageResource(R.drawable.pause_bottom_sm_black);

                }


            }else{

                if(playbackStudio.isPlaying()){

                    playbackStudio.pauseMedia();

                    if(type == CHATRIGHTAUDIO){

                        holder.playpause.setImageResource(R.drawable.play_button_sm);

                    }else if(type == CHATLEFTAUDIO){
                        holder.playpause.setImageResource(R.drawable.play_button_sm_black);

                    }

                }else{
                    playbackStudio.resumeMedia();
                    if(type == CHATRIGHTAUDIO){

                        holder.playpause.setImageResource(R.drawable.pause_bottom_sm);

                    }else if(type == CHATLEFTAUDIO){
                        holder.playpause.setImageResource(R.drawable.pause_bottom_sm_black);

                    }

                }


            }

        });





    }


    public void bindviewfiles(ChatAdapter.ViewHolderFile holder, Chat chat){// Retrieving the date and time of the chat
        long time = chat.getDate();
        Date date = new Date(time);
        Date now = new Date();
        long today = now.getTime();
        SimpleDateFormat sd = new SimpleDateFormat("dd mm yyyy",Locale.getDefault());
        if(sd.format(date).equalsIgnoreCase(sd.format(now))){

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String chattime = sdf.format(date)+" Today";

            holder.chattime.setText(chattime);
        }else {

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  MMMM dd, yyyy", Locale.getDefault());
            String chattime = sdf.format(date);

            holder.chattime.setText(chattime);
        }


        // input the chat status{Delivered, or Seen, or Sent}
        holder.chatstatus.setText(chat.getChatstatus());
        // input the chat message
        holder.chatmessage.setText(chat.getMessage());

        holder.chatfilename.setText(chat.getFilename());
        holder.chatmore.setOnClickListener(mv ->{

            // TODO: download the file
            menuModel.donwloadimage(holder.chatmore,chat.getFileuri(),chat.getFilename());

        });

        holder.downloadfile.setOnClickListener(dv ->{

            // TODO Download and open the file
            menuModel.donwloadother(holder.chatmore,chat.getFileuri(),chat.getFilename());

        });


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

    public void showfullimage(Chat chat){

        Dialog fulld = new Dialog(context);
        fulld.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fulld.setContentView(R.layout.dialog_full_image);
        fulld.show();

        ImageView cancel = (ImageView)fulld.findViewById(R.id.fullcancel);
        ImageView fullimage = (ImageView)fulld.findViewById(R.id.fullimage);

        cancel.setOnClickListener(ccc -> fulld.dismiss());

        try {
            Glide.with(context).load(Uri.parse(chat.getFileuri())).asBitmap().into(fullimage);
        } catch (Exception e) {
            fullimage.setImageURI(Uri.parse(chat.getFileuri()));
        }


    }







}
