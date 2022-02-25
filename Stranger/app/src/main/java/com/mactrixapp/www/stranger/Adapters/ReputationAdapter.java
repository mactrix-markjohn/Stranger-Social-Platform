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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mactrixapp.www.stranger.Model.ReputationModel;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.Stranger;
import com.mactrixapp.www.stranger.StrangerProfile;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReputationAdapter extends BaseAdapter {

    private FirebaseUser currentuser;
    private Context context;
    private ArrayList<ReputationModel> reputations;
    LayoutInflater inflater;

    public ReputationAdapter(Context context, ArrayList<ReputationModel> reputations) {
        this.context = context;
        this.reputations = reputations;
        inflater = LayoutInflater.from(context);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
    }


    class ChildHolder{

        TextView rank;
        TextView followers;
        CircleImageView photo;
        TextView name;
        TextView username;


        public ChildHolder(View v, int p){

            ReputationModel reputation = reputations.get(p);


            rank = v.findViewById(R.id.rank);
            followers = v.findViewById(R.id.followers);
            photo = v.findViewById(R.id.photo);
            name = v.findViewById(R.id.name);
            username = v.findViewById(R.id.username);

            rank.setText(String.valueOf(reputation.getRank()));
            followers.setText(String.valueOf(reputation.getFollowers()));

            User user = reputation.getUser();
            name.setText(user.getFullname());
            username.setText(user.getUsername());

            try{
                Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(photo);

            }catch (Exception e){
                photo.setImageResource(R.mipmap.profileavatar);
            }





            v.setOnClickListener(rvc ->{

                try {
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
                } catch (Exception e) {
                    Toast.makeText(context, "This Stranger's account is improperly set up", Toast.LENGTH_SHORT).show();
                }

            });




        }

    }

    @Override
    public int getCount() {
        return reputations.size();
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

            convertView = inflater.inflate(R.layout.reputationchildlay,parent,false);

        }

        new ChildHolder(convertView,position);


        return convertView;
    }
}
