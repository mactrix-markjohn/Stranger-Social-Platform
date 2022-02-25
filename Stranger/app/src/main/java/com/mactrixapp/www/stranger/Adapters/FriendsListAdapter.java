package com.mactrixapp.www.stranger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.Stranger;
import com.mactrixapp.www.stranger.StrangerProfile;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsListAdapter extends BaseAdapter {

    Context context;
    ArrayList<User> users;
    LayoutInflater inflater;
    FirebaseUser currentuser;

    public FriendsListAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        inflater = LayoutInflater.from(context);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
    }



    class ViewHolder{

        private CircleImageView userphoto;
        private TextView userfullname;
        private TextView username;

        public ViewHolder(View v, int p) {

            userphoto = (CircleImageView)v.findViewById(R.id.profilephoto);
            userfullname = (TextView)v.findViewById(R.id.userfullname);
            username = (TextView)v.findViewById(R.id.username);


            userphoto = (CircleImageView)v.findViewById(R.id.profilephoto);
            userfullname = (TextView)v.findViewById(R.id.userfullname);
            username = (TextView)v.findViewById(R.id.username);

            User user = users.get(p);

            userfullname.setText(user.getFullname());
            username.setText(user.getUsername());
            try {
                Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(userphoto);
            } catch (Exception e) {
                userphoto.setImageResource(R.mipmap.profileavatar);
            }


            v.setOnClickListener(views ->{

                // Handle list onclick here

                if(user.getUserid().equals(currentuser.getUid())){
                    Bundle bundlee = new Bundle();
                    bundlee.putInt("tab",4);
                    Intent intente = new Intent(context,Stranger.class);
                    intente.putExtra("tab",bundlee);
                    context.startActivity(intente);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString(context.getString(R.string.strangerid), user.getUserid());
                    Intent intent = new Intent(context, StrangerProfile.class);
                    intent.putExtra(context.getString(R.string.strangerid), bundle);
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.fragment_friends_list_lay,parent,false);

        }

        new ViewHolder(convertView,position);


        return convertView;
    }
}
