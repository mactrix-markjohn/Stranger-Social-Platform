package com.mactrixapp.www.stranger.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.mactrixapp.www.stranger.Model.Request;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.Stranger;
import com.mactrixapp.www.stranger.StrangerProfile;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class InterestSearchAdapter extends BaseAdapter {

    private FirebaseUser currentuser;
    private DatabaseReference userRef;
    private DatabaseReference requestReference;
    private Context context;
    private ArrayList<User> users;
    private LayoutInflater inflater;
    private  User userCurrent;

    public InterestSearchAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        inflater = LayoutInflater.from(context);
        requestReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.request));

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(currentuser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                userCurrent = dataSnapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class ViewHold {


        /*//private  TextView currentlocation;
        //private  CardView mpcard;*/
        private TextView username;
        private TextView userfullname;
        private TextView profession;
        private TextView gender;
        private TextView workstate;
        private CircleImageView profilephoto;
        private Dialog sd;

        public ViewHold(View v, int p) {

            profilephoto = (CircleImageView)v.findViewById(R.id.profilephoto);
            userfullname = (TextView)v.findViewById(R.id.userfullname);
            username = (TextView)v.findViewById(R.id.username);
            profession = (TextView)v.findViewById(R.id.profession);
            gender = (TextView)v.findViewById(R.id.gender);
            workstate= (TextView)v.findViewById(R.id.workstate);
           /* currentlocation = (TextView)v.findViewById(R.id.currentlocation);
            mpcard = (CardView)v.findViewById(R.id.mpcard);*/

            User user = users.get(p);

            try {
                Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(profilephoto);
            } catch (Exception e) {
                profilephoto.setImageResource(R.mipmap.profileavatar);
            }


                userfullname.setText(user.getFullname());
                username.setText(user.getUsername());
                profession.setText(user.getProfession());
               /* currentlocation.setText(user.getCurrentaddress());*/
               String gendery = user.getGender() != null ? user.getGender().toLowerCase(): user.getGender();
               gender.setText(gendery);
               String work = user.getWorkstate() != null ? user.getWorkstate().toLowerCase() : user.getWorkstate();
                workstate.setText(work);

               /* mpcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // open map here
                    }
                });*/


            v.setOnClickListener(fsv ->{

                // Handle list onclick here
                // TODO: Handle the show dialog here


                showdialog(user);



            });


        }


        public void showdialog(User user){

            sd = new Dialog(context);
            sd.setContentView(R.layout.dialog_sent_permission);
            sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            sd.show();

            CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
            CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
            ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);

            leftclick.setOnClickListener(lcv ->{

                // Start the Stranger Profile activity
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
            rightclick.setOnClickListener(rcv ->{

                if(user.getUserid().equals(currentuser.getUid())){

                    Toast.makeText(context, "You can not send Permission Request to yourself", Toast.LENGTH_LONG).show();

                }else {
                    sendrequest(user);
                }



            });
            cancel.setOnClickListener(cv -> sd.dismiss());

        }

        public void sendrequest(User user){


            // Send the request for permission to view the Stranger's Location
            Request request = new Request();
            request.setDate(new Date().getTime());

            request.setReceiverid(user.getUserid());
            request.setReceivername(user.getFullname());
            request.setReceiverphotourl(user.getPhotoUrl());
            request.setReceiverusername(user.getUsername());

            request.setSenderid(userCurrent.getUserid());
            request.setSendername(userCurrent.getFullname());
            request.setSenderusername(userCurrent.getUsername());
            request.setSenderphotourl(userCurrent.getPhotoUrl());

            DatabaseReference push = requestReference.push();
            request.setRequestkey(push.getKey());

            push.setValue(request).addOnSuccessListener((ss) ->{

                Toast.makeText(context, "Permission Request has been Sent", Toast.LENGTH_LONG).show();

            }).addOnFailureListener((fv) ->{

                Toast.makeText(context, "Sorry, Something went wrong, Please resend Request", Toast.LENGTH_LONG).show();

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
            convertView = inflater.inflate(R.layout.interest_search_list_lay,parent,false);
        }


        new ViewHold(convertView,position);



        return convertView;
    }
}
