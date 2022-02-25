package com.mactrixapp.www.stranger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.mactrixapp.www.stranger.ChattingSpot;
import com.mactrixapp.www.stranger.GroupChat;
import com.mactrixapp.www.stranger.Model.ChatUserModel;
import com.mactrixapp.www.stranger.Model.Group;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.StrangersGroup;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatlistAdapter extends BaseAdapter {
    Context context;
    ArrayList<ChatUserModel> users;
    LayoutInflater inflater;

    public ChatlistAdapter(Context context, ArrayList<ChatUserModel> users) {
        this.context = context;
        this.users = users;
        inflater = LayoutInflater.from(context);
    }



    class ViewKeeper{


        private RelativeLayout connect;
        private TextView chatcount;
        private DatabaseReference chatreference;
        private TextView chatusername;
        private TextView chatlastmessage;
        private TextView connectionstatus;
        private ImageView connectioncolor;
        private CircleImageView chatphoto;

        public ViewKeeper(View v, int p){

            ChatUserModel chatuser = users.get(p);
            User user = chatuser.getUser();
            String lastmessage = chatuser.getLastmessage();

            chatphoto = (CircleImageView)v.findViewById(R.id.chatlistphoto);
            chatusername = (TextView)v.findViewById(R.id.chatusername);
            chatlastmessage = (TextView)v.findViewById(R.id.chatlastmessage);
            connectionstatus = (TextView)v.findViewById(R.id.connectionstatus);
            connectioncolor = (ImageView)v.findViewById(R.id.connectioncolor);
            chatcount = (TextView)v.findViewById(R.id.chatinfo);
            connect = (RelativeLayout)v.findViewById(R.id.connect);

            // Enter the necessary info
            try {
                Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(chatphoto);
            } catch (Exception e) {
                chatphoto.setImageResource(R.mipmap.profileavatar);
            }
            chatusername.setText(user.getUsername());
            connectionstatus.setText(user.getConnectionstatus());
            if(user.getConnectionstatus().equals(context.getString(R.string.online))){
                connectioncolor.setBackgroundResource(R.drawable.greencircle);

            }else{

                connectioncolor.setBackgroundResource(R.drawable.greycircle);
            }

            chatlastmessage.setText(lastmessage);

            if (chatuser.getChatcount() <= 0) {
              chatcount.setVisibility(View.GONE);
            }else{
                chatcount.setText(String.valueOf(chatuser.getChatcount()));
                chatcount.setVisibility(View.VISIBLE);
            }

            v.setOnClickListener(vv ->{

                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.strangerid),user.getUserid());
                Intent intent = new Intent(context, ChattingSpot.class);
                intent.putExtra(context.getString(R.string.strangerid),bundle);
                context.startActivity(intent);


            });



        }

    }


    class ViewGroup{


        private RelativeLayout connecte;
        private  TextView groupname;
        private TextView grouplastmessage;
        private CircleImageView groupphoto;
        private TextView chatcount;

        public ViewGroup(View v, int p){
            ChatUserModel model = users.get(p);
            Group group = model.getGroup();

            groupname = (TextView)v.findViewById(R.id.groupname);
            grouplastmessage = (TextView)v.findViewById(R.id.grouplastmessage);
            groupphoto = (CircleImageView)v.findViewById(R.id.groupphoto);
            chatcount = (TextView)v.findViewById(R.id.chatinfo);
            connecte = (RelativeLayout)v.findViewById(R.id.connecte);

            grouplastmessage.setText(model.getLastmessage());
            groupname.setText(group.getName());

            if (model.getChatcount() <= 0) {
                chatcount.setVisibility(View.GONE);
            }else{
                chatcount.setText(String.valueOf(model.getChatcount()));
                chatcount.setVisibility(View.VISIBLE);
            }

            try{
                Glide.with(context).load(Uri.parse(group.getImageurl())).asBitmap().into(groupphoto);

            }catch (Exception e){
                groupphoto.setImageURI(Uri.parse(group.getImageurl()));
            }






            v.setOnClickListener(vclick ->{

                if (model.getChattype().equalsIgnoreCase(context.getString(R.string.group))) {

                    // open Group activity
                    Intent intent = new Intent(context,GroupChat.class);
                    intent.putExtra(context.getString(R.string.group),group.getId());
                    context.startActivity(intent);


                } else if (model.getChattype().equalsIgnoreCase(context.getString(R.string.strangersgroup))) {

                    //open Strangers Group Activity
                    Intent intent = new Intent(context,StrangersGroup.class);
                    intent.putExtra(context.getString(R.string.group),group.getId());
                    context.startActivity(intent);

                }


            });

        }

    }



    @Override
    public int getCount() {
        return users.size();
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
    public View getView(int position, View convertView, android.view.ViewGroup parent) {
        ChatUserModel model = users.get(position);

        if (model.getChattype().equalsIgnoreCase(context.getString(R.string.chat))) {

            convertView = inflater.inflate(R.layout.chatlistlayout,parent,false);
            new ViewKeeper(convertView,position);


        } else if (model.getChattype().equalsIgnoreCase(context.getString(R.string.group))) {

            convertView = inflater.inflate(R.layout.grouplistlay,parent,false);
            new ViewGroup(convertView,position);


        } else if (model.getChattype().equalsIgnoreCase(context.getString(R.string.strangersgroup))) {

            convertView = inflater.inflate(R.layout.strangersgrouplistlay,parent,false);
            new ViewGroup(convertView,position);

        }



        return convertView;
    }
}
