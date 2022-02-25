package com.mactrixapp.www.stranger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.Stranger;
import com.mactrixapp.www.stranger.StrangerProfile;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdminAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> users;
    private LayoutInflater inflater;
    private FirebaseUser currentuser;

    public GroupAdminAdapter(Context context, ArrayList<String> users) {
        this.context = context;
        this.users = users;
        inflater = LayoutInflater.from(context);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
    }

    class ViewHold {


        private TextView username;
        private TextView userfullname;
        private TextView profession;
        private TextView gender;
        private TextView workstate;
        private CircleImageView profilephoto;

        public ViewHold(View v, int p) {

            profilephoto = (CircleImageView)v.findViewById(R.id.profilephoto);
            userfullname = (TextView)v.findViewById(R.id.userfullname);
            username = (TextView)v.findViewById(R.id.username);
            profession = (TextView)v.findViewById(R.id.profession);
            gender = (TextView)v.findViewById(R.id.gender);
            workstate= (TextView)v.findViewById(R.id.workstate);

            String userid = users.get(p);


            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(userid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    if(user != null){

                        try {
                            Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(profilephoto);
                        } catch (Exception e) {
                            profilephoto.setImageResource(R.mipmap.profileavatar);
                        }


                        userfullname.setText(user.getFullname());
                        username.setText(user.getUsername());
                        profession.setText(user.getProfession());
                        String gendery = user.getGender() != null ? user.getGender().toLowerCase(): user.getGender();
                        gender.setText(gendery);
                        String work = user.getWorkstate() != null ? user.getWorkstate().toLowerCase() : user.getWorkstate();
                        workstate.setText(work);



                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




           /* v.setOnClickListener(fsv ->{

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


            });*/


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
            convertView = inflater.inflate(R.layout.find_stranger_list_lay,parent,false);
        }

        new ViewHold(convertView,position);
        return convertView;
    }
}
