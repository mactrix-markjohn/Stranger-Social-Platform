package com.mactrixapp.www.stranger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.Stranger;
import com.mactrixapp.www.stranger.StrangerProfile;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StrangerListAdapter extends BaseAdapter {

    private final DatabaseReference friendReference;
    private DatabaseReference userReference;
    private FirebaseUser currentuser;
    Context context;
    ArrayList<User> users;
    LayoutInflater inflater;
    User thisuser;

    public StrangerListAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        inflater = LayoutInflater.from(context);

        // This is for the current user
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(currentuser.getUid());
        friendReference = userReference.child(context.getString(R.string.friends));
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                thisuser = dataSnapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }



    class ViewHolder{

        private TextView unstrangertext;
        private DatabaseReference strangerReference;
        private CardView unstranger;
        private CircleImageView userphoto;
        private TextView userfullname;
        private TextView username;
        boolean clicked;



        public ViewHolder(View v, int p) {


            // initialize the neccessary things
            userphoto = (CircleImageView)v.findViewById(R.id.profilephoto);
            userfullname = (TextView)v.findViewById(R.id.userfullname);
            username = (TextView)v.findViewById(R.id.username);
            unstranger = (CardView)v.findViewById(R.id.unstrangerclick);
            unstrangertext  = (TextView)v.findViewById(R.id.unstrangerclicktext);

            User user = users.get(p);
            strangerReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(user.getUserid()).child(context.getString(R.string.strangers));

            userfullname.setText(user.getFullname());
            username.setText(user.getUsername());
            try {
                Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(userphoto);
            } catch (Exception e) {
                userphoto.setImageResource(R.mipmap.profileavatar);
            }

            friendReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot shot: dataSnapshot.getChildren()){
                        User frienduser = shot.getValue(User.class);

                        if(frienduser != null){


                            if(frienduser.getUserid().equals(user.getUserid())){

                                // change the unstranger to white
                                unstranger.setCardBackgroundColor(0xffffffff);
                                unstranger.setBackgroundColor(0xffffffff);
                                unstrangertext.setTextColor(0xdd00aaff);
                                unstrangertext.setText(context.getString(R.string.unstrangered));
                                clicked = true;

                            }


                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            clicked = false;
            unstranger.setOnClickListener(view ->{

                if(clicked){

                    unstranger.setCardBackgroundColor(0xdd00aaff);
                    unstranger.setBackgroundColor(0xdd00aaff);
                    unstrangertext.setText(context.getString(R.string.unstranger));
                    unstrangertext.setTextColor(Color.WHITE);


                    friendReference.child(user.getUserid()).removeValue().addOnSuccessListener(vv -> Toast.makeText(context, "You have Strangered the Stranger", Toast.LENGTH_SHORT).show());
                    strangerReference.child(thisuser.getUserid()).removeValue().addOnSuccessListener(vvv -> Toast.makeText(context, "Strangered", Toast.LENGTH_SHORT).show());


                    clicked = false;
                }else{


                    // if the user click on Unstranger, we will upload the stranger User.class to current user Friends node
                    // and then we will upload the currentUser User.class to the Strangers node of the Stranger

                    unstranger.setCardBackgroundColor(0xffffffff);
                    unstranger.setBackgroundColor(0xffffffff);
                    unstrangertext.setTextColor(0xdd00aaff);
                    unstrangertext.setText(context.getString(R.string.unstrangered));

                    friendReference.child(user.getUserid()).setValue(user).addOnSuccessListener(vv -> Toast.makeText(context, "You have UnStrangered the Stranger", Toast.LENGTH_SHORT).show());
                    strangerReference.child(thisuser.getUserid()).setValue(thisuser).addOnSuccessListener(vvc -> Toast.makeText(context, "UnStrangered", Toast.LENGTH_SHORT).show());




                    clicked = true;
                }
                // Handle Unstranger onClick here
            });

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
            convertView = inflater.inflate(R.layout.fragment_strangers_list_lay,parent,false);

        }

        new ViewHolder(convertView,position);


        return convertView;
    }
}
