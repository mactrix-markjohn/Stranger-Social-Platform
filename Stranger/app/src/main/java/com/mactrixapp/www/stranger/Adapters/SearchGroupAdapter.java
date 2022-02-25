package com.mactrixapp.www.stranger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.style.IconMarginSpan;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.GroupChat;
import com.mactrixapp.www.stranger.Model.Group;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.StrangersGroup;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchGroupAdapter extends BaseAdapter {

    private FirebaseUser currentuser;
    Context context;
    ArrayList<Group> groups;
    LayoutInflater inflater;

    public SearchGroupAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
        inflater = LayoutInflater.from(context);
        currentuser= FirebaseAuth.getInstance().getCurrentUser();
    }

    class GroupHolder{


        private  CardView epcarddesign;
        private  ImageView groupaccess;
        private  CircleImageView groupphoto;
        private  TextView groupname;
        private  TextView groupmembercount;

        public GroupHolder(View v, int p){

            Group group = groups.get(p);

           groupphoto = (CircleImageView)v.findViewById(R.id.groupphoto);
           groupname = (TextView)v.findViewById(R.id.groupname);
           groupmembercount = (TextView)v.findViewById(R.id.groupmembercount);
           epcarddesign = (CardView)v.findViewById(R.id.epcardsign);
           groupaccess = (ImageView)v.findViewById(R.id.groupaccess);

            groupname.setText(group.getName());
            try{
                Glide.with(context).load(Uri.parse(group.getImageurl())).asBitmap().into(groupphoto);

            }catch (Exception e){
                groupphoto.setImageURI(Uri.parse(group.getImageurl()));
            }

            String grouptype = group.getGrouptype() != null ? group.getGrouptype() : "hagan";

            if (grouptype.equalsIgnoreCase(context.getString(R.string.strangersgroup))) {

                epcarddesign.setVisibility(View.VISIBLE);
            }else{
                epcarddesign.setVisibility(View.GONE);
            }

            String access = group.getAccess() != null ? group.getAccess() : "jfhjdfkjfkj";
            if (access.equalsIgnoreCase(context.getString(R.string.privateaccess))) {

                groupaccess.setVisibility(View.VISIBLE);
            }else{
                groupaccess.setVisibility(View.GONE);
            }

            FirebaseDatabase.getInstance().getReference().child(grouptype).child(group.getId()).child(context.getString(R.string.members)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String member = String.valueOf(dataSnapshot.getChildrenCount()) +" Member";
                    groupmembercount.setText(member);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            v.setOnClickListener(gclick ->{



                if(access.equalsIgnoreCase(context.getString(R.string.publicaccess))){

                    FirebaseDatabase.getInstance().getReference().child(grouptype).child(group.getId()).child(context.getString(R.string.members)).child(currentuser.getUid()).setValue(currentuser.getUid()).addOnSuccessListener(dd -> {

                        Toast.makeText(context, "You have joined the Group.", Toast.LENGTH_LONG).show();

                        if (grouptype.equalsIgnoreCase(context.getString(R.string.group))) {

                            // open Group activity
                            Intent intent = new Intent(context,GroupChat.class);
                            intent.putExtra(context.getString(R.string.group),group.getId());
                            context.startActivity(intent);


                        } else if (grouptype.equalsIgnoreCase(context.getString(R.string.strangersgroup))) {

                            //open Strangers Group Activity
                            Intent intent = new Intent(context,StrangersGroup.class);
                            intent.putExtra(context.getString(R.string.group),group.getId());
                            context.startActivity(intent);

                        }


                    });

                }else{

                    FirebaseDatabase.getInstance().getReference().child(grouptype).child(group.getId()).child(context.getString(R.string.request)).child(currentuser.getUid()).setValue(currentuser.getUid()).addOnSuccessListener(dsd -> Toast.makeText(context, "This group is a Private Group, You request has been sent", Toast.LENGTH_LONG).show());


                }



            });




        }
    }

    @Override
    public int getCount() {
        return groups.size();
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

            convertView = inflater.inflate(R.layout.grouplsearchlistlay,parent,false);
        }

        new GroupHolder(convertView,position);



        return convertView;
    }
}
