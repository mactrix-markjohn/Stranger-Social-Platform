package com.mactrixapp.www.stranger.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.mactrixapp.www.stranger.Model.Letter;
import com.mactrixapp.www.stranger.Model.Notification;
import com.mactrixapp.www.stranger.Model.Request;
import com.mactrixapp.www.stranger.Model.StrangerLocationModel;
import com.mactrixapp.www.stranger.Model.User;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.StrangerLetterViewer;
import com.mactrixapp.www.stranger.StrangerProfile;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class NoticeAdapter extends BaseAdapter {

    private FirebaseUser currentuser;
    private DatabaseReference requestReference;
    Context context;
    ArrayList<Notification> notifications;
    LayoutInflater inflater;

    public NoticeAdapter(Context context, ArrayList<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
        inflater = LayoutInflater.from(context);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        requestReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.request));

    }

    // view holder for Request received
    class ReceivedHolder{

        private TextView noticedate;
        private TextView username;
        private ImageView more;
        private ImageView userphoto;
        private Request notice;

        public ReceivedHolder(View v , int p) {

             notice = notifications.get(p).getRequest();

            userphoto = (ImageView)v.findViewById(R.id.userphoto);
            username = (TextView)v.findViewById(R.id.username);
            more = (ImageView)v.findViewById(R.id.more);

            noticedate = (TextView)v.findViewById(R.id.noticetime);
            noticedate.setText(new SimpleDateFormat("hh:mm a  E dd MMM, yyyy",Locale.getDefault()).format(new Date(notice.getDate())));



            more.setOnClickListener(m ->{

                PopupMenu popupMenu = new PopupMenu(context,m);
                popupMenu.inflate(R.menu.notification_list);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.delete:
                                // delete the request from the Arraylist and Database by making it seen for the request receiver
                                notifications.remove(p);
                                Bundle bundle = new Bundle();
                                bundle.putInt(context.getString(R.string.deleted),p);
                                Intent intent = new Intent(context.getString(R.string.deleted));
                                intent.putExtra(context.getString(R.string.deleted),bundle );
                                context.sendBroadcast(intent);
                                deleterequest(notice);

                                break;
                        }


                        return false;
                    }
                });

            });

            v.setOnClickListener(vc -> showdialog(notice));

            // The name of the sender is seen here, the user is receiving a request

            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(notice.getSenderid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null){

                        try {
                            Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().placeholder(R.mipmap.strangeralone).into(userphoto);
                        } catch (Exception e) {
                            userphoto.setImageResource(R.mipmap.profileavatar);
                        }
                        username.setText(user.getFullname());


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }

        public void showdialog(Request notice){

            Dialog sd = new Dialog(context);
            sd.setContentView(R.layout.dialog_receive_permission);
            sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            sd.show();

            CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
            CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
            ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
            CardView topclick = (CardView)sd.findViewById(R.id.topclick);
            TextView username = (TextView)sd.findViewById(R.id.username);

            username.setText(notice.getSendername());

            topclick.setOnClickListener(tcv ->{

                // Start the Stranger Profile activity
                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.strangerid),notice.getSenderid());

                Intent intent = new Intent(context,StrangerProfile.class);
                intent.putExtra(context.getString(R.string.strangerid),bundle);
                context.startActivity(intent);

            });

            leftclick.setOnClickListener(lcv ->{

//               /* // reject the request
//                DatabaseReference request = requestReference.child(notice.getRequestkey());
//
//                //Update the Permission child to reject
//                HashMap<String,Object> permission = new HashMap<>();
//                permission.put(context.getString(R.string.permission),context.getString(R.string.permissiondenied));
//                request.updateChildren(permission);
//                Toast.makeText(context, "reject", Toast.LENGTH_SHORT).show();*/


                // reject the request


                // reject the request
                requestReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Request requesting = snapshot.getValue(Request.class);

                            if(requesting != null){

                                if(requesting.getReceiverid().equalsIgnoreCase(notice.getReceiverid()) && requesting.getSenderid().equalsIgnoreCase(notice.getSenderid())){

                                    //Update the Permission child to reject
                                    HashMap<String,Object> permission = new HashMap<>();
                                    permission.put(context.getString(R.string.permission),context.getString(R.string.permissiondenied));
                                    snapshot.getRef().updateChildren(permission).addOnSuccessListener(sv ->{
                                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                                    });;


                                }


                            }



                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            });

            rightclick.setOnClickListener(rcv ->{


               /* // accept the request
                DatabaseReference request = requestReference.child(notice.getRequestkey());

                //Update the Permission child to reject
                HashMap<String,Object> permission = new HashMap<>();
                permission.put(context.getString(R.string.permission),context.getString(R.string.permissiongranted));
                request.updateChildren(permission);*/




               // accept the request
                requestReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Request requesting = snapshot.getValue(Request.class);

                            if(requesting != null){

                                if(requesting.getReceiverid().equalsIgnoreCase(notice.getReceiverid()) && requesting.getSenderid().equalsIgnoreCase(notice.getSenderid())){

                                    HashMap<String,Object> permission = new HashMap<>();
                                    permission.put(context.getString(R.string.permission),context.getString(R.string.permissiongranted));
                                    snapshot.getRef().updateChildren(permission).addOnSuccessListener(sv ->{
                                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
                                    });;


                                }


                            }



                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            });


            cancel.setOnClickListener(cv -> sd.dismiss());

        }

        public void deleterequest(Request notice){


           /* DatabaseReference request = requestReference.child(notice.getRequestkey());

            // found the request value so we have to update it to seen by who is asking for it
            HashMap<String , Object> receiverseen = new HashMap<>();
            receiverseen.put(context.getString(R.string.requestreceiverseen),context.getString(R.string.seen));
            request.updateChildren(receiverseen);*/

            requestReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Request requesting = snapshot.getValue(Request.class);

                        if(requesting != null){

                            if(requesting.getReceiverid().equalsIgnoreCase(notice.getReceiverid()) && requesting.getSenderid().equalsIgnoreCase(notice.getSenderid())){

                                HashMap<String , Object> receiverseen = new HashMap<>();
                                receiverseen.put(context.getString(R.string.requestreceiverseen),context.getString(R.string.seen));
                                snapshot.getRef().updateChildren(receiverseen).addOnSuccessListener(sv ->{
                                    Toast.makeText(context, "You have deleted the Request", Toast.LENGTH_SHORT).show();
                                });



                            }


                        }



                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });






        }


    }

    // view holder for Request accepted
    class AcceptedHolder{

        private TextView noticedate;
        private TextView username;
        private ImageView more;
        private ImageView userphoto;


        public AcceptedHolder(View v, int p) {

            Request notice = notifications.get(p).getRequest();

            userphoto = (ImageView)v.findViewById(R.id.userphoto);
            username = (TextView)v.findViewById(R.id.username);
            more = (ImageView)v.findViewById(R.id.more);

            noticedate = (TextView)v.findViewById(R.id.noticetime);
            noticedate.setText(new SimpleDateFormat("hh:mm a  E dd MMM, yyyy",Locale.getDefault()).format(new Date(notice.getDate())));

            more.setOnClickListener(m ->{

                PopupMenu popupMenu = new PopupMenu(context,m);
                popupMenu.inflate(R.menu.notification_list);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.delete:
                                // delete the request from the Arraylist and Database by making it seen for the request receiver
                                notifications.remove(p);
                                Bundle bundle = new Bundle();
                                bundle.putInt(context.getString(R.string.deleted),p);
                                Intent intent = new Intent(context.getString(R.string.deleted));
                                intent.putExtra(context.getString(R.string.deleted),bundle );
                                context.sendBroadcast(intent);
                                deleterequest(notice);

                                break;
                        }


                        return false;
                    }
                });


            });
            v.setOnClickListener(vc ->{showdialog(notice);});

            // The sender of the request receives this notice, so name is that of the Receiver of the Request

            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(notice.getReceiverid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null){

                        try {
                            Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().placeholder(R.mipmap.strangeralone).into(userphoto);
                        } catch (Exception e) {
                            userphoto.setImageResource(R.mipmap.profileavatar);
                        }
                        username.setText(user.getFullname());


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }

        String location;

        public void showdialog(Request notice){

            Dialog sd = new Dialog(context);
            sd.setContentView(R.layout.dialog_accepted_permission);
            sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            sd.show();

            CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
            CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
            ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
            TextView requestname = (TextView)sd.findViewById(R.id.requestname);
            TextView currentlocation = (TextView)sd.findViewById(R.id.currentlocation);
             location= " ";

            requestname.setText(notice.getReceivername());

            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(notice.getReceiverid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        currentlocation.setText(user.getCurrentaddress());
                        location = "http://maps.google.com/maps?q="+user.getLatitude()+","+user.getLongitude();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            leftclick.setOnClickListener(lcv ->{

                //TODO:  Open map here
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(location));
                context.startActivity(intent);




            });

            rightclick.setOnClickListener(rcv ->{


                // Start the Stranger Profile activity
                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.strangerid),notice.getReceiverid());

                Intent intent = new Intent(context,StrangerProfile.class);
                intent.putExtra(context.getString(R.string.strangerid),bundle);
                context.startActivity(intent);
            });


            cancel.setOnClickListener(cv -> sd.dismiss());

        }


        public void deleterequest(Request notice){


           /* DatabaseReference request = requestReference.child(notice.getRequestkey());

            // found the request value so we have to update it to seen by who is asking for it
            HashMap<String , Object> receiverseen = new HashMap<>();
            receiverseen.put(context.getString(R.string.requestsenderseen),context.getString(R.string.seen));
            request.updateChildren(receiverseen);*/


            requestReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Request requesting = snapshot.getValue(Request.class);

                        if(requesting != null){

                            if(requesting.getReceiverid().equalsIgnoreCase(notice.getReceiverid()) && requesting.getSenderid().equalsIgnoreCase(notice.getSenderid())){

                                // found the request value so we have to update it to seen by who is asking for it
                                HashMap<String , Object> receiverseen = new HashMap<>();
                                receiverseen.put(context.getString(R.string.requestsenderseen),context.getString(R.string.seen));
                                snapshot.getRef().updateChildren(receiverseen).addOnSuccessListener(sv ->{
                                    Toast.makeText(context, "You have deleted the Request", Toast.LENGTH_SHORT).show();
                                });



                            }


                        }



                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    // view holder for Request rejected
    class RejectedHolder{

        private TextView noticedate;
        private TextView username;
        private ImageView more;
        private ImageView userphoto;
        private User currentuser;
        private User strangeruser;

        public RejectedHolder(View v, int p) {

            Request notice = notifications.get(p).getRequest();

            userphoto = (ImageView)v.findViewById(R.id.userphoto);
            username = (TextView)v.findViewById(R.id.username);
            more = (ImageView)v.findViewById(R.id.more);

            noticedate = (TextView)v.findViewById(R.id.noticetime);
            noticedate.setText(new SimpleDateFormat("hh:mm a  E dd MMM, yyyy",Locale.getDefault()).format(new Date(notice.getDate())));


            more.setOnClickListener(m ->{

                PopupMenu popupMenu = new PopupMenu(context,m);
                popupMenu.inflate(R.menu.notification_list);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.delete:
                                // delete the request from the Arraylist and Database by making it seen for the request receiver

                                notifications.remove(p);
                                Bundle bundle = new Bundle();
                                bundle.putInt(context.getString(R.string.deleted),p);
                                Intent intent = new Intent(context.getString(R.string.deleted));
                                intent.putExtra(context.getString(R.string.deleted),bundle );
                                context.sendBroadcast(intent);
                                deleterequest(notice);

                                break;
                        }


                        return false;
                    }
                });
            });
            v.setOnClickListener(vc -> showdialog(notice));

            // The sender of the request receives this notice, so name is that of the Receiver of the Request



            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(notice.getReceiverid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {

                        strangeruser = user;
                        try {
                            Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().placeholder(R.mipmap.strangeralone).into(userphoto);
                        } catch (Exception e) {
                            userphoto.setImageResource(R.mipmap.profileavatar);
                        }
                        username.setText(user.getFullname());

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(notice.getSenderid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {

                        currentuser = user;

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public void showdialog(Request notice){

            Dialog sd = new Dialog(context);
            sd.setContentView(R.layout.dialog_rejected_permission);
            sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            sd.show();

            CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
            CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
            ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
            TextView requestname = (TextView)sd.findViewById(R.id.requestname);

            requestname.setText(notice.getReceivername());



            leftclick.setOnClickListener(lcv ->{

                // Resend Request
                sendrequest(strangeruser,currentuser);
            });

            rightclick.setOnClickListener(rcv ->{


                // Start the Stranger Profile activity
                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.strangerid),notice.getReceiverid());

                Intent intent = new Intent(context,StrangerProfile.class);
                intent.putExtra(context.getString(R.string.strangerid),bundle);
                context.startActivity(intent);
            });


            cancel.setOnClickListener(cv -> sd.dismiss());

        }

        public void deleterequest(Request notice){


           /* DatabaseReference request = requestReference.child(notice.getRequestkey());

            // found the request value so we have to update it to seen by who is asking for it
            HashMap<String , Object> receiverseen = new HashMap<>();
            receiverseen.put(context.getString(R.string.requestsenderseen),context.getString(R.string.seen));
            request.updateChildren(receiverseen);*/

            requestReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Request requesting = snapshot.getValue(Request.class);

                        if(requesting != null){

                            if(requesting.getReceiverid().equalsIgnoreCase(notice.getReceiverid()) && requesting.getSenderid().equalsIgnoreCase(notice.getSenderid())){

                                // found the request value so we have to update it to seen by who is asking for it
                                HashMap<String , Object> receiverseen = new HashMap<>();
                                receiverseen.put(context.getString(R.string.requestsenderseen),context.getString(R.string.seen));
                                snapshot.getRef().updateChildren(receiverseen).addOnSuccessListener(sv ->{
                                    Toast.makeText(context, "You have deleted the Request", Toast.LENGTH_SHORT).show();
                                });



                            }


                        }



                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }

        public void sendrequest(User user, User userCurrent){


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



    class LetterHolder{

        private TextView noticedate;
        private ImageView more;
        private ImageView userphoto;
        private TextView username;

        public LetterHolder(View v , int p){


            Letter letter = notifications.get(p).getLetter();
            userphoto = (ImageView)v.findViewById(R.id.userphoto);
            username = (TextView)v.findViewById(R.id.username);
            more = (ImageView)v.findViewById(R.id.more);

            noticedate = (TextView)v.findViewById(R.id.noticetime);
            noticedate.setText(new SimpleDateFormat("hh:mm a  E dd MMM, yyyy",Locale.getDefault()).format(new Date(letter.getDate())));


            displaynotice(p);


            more.setOnClickListener(mc ->{

                PopupMenu popupMenu = new PopupMenu(context,mc);
                popupMenu.inflate(R.menu.notification_list);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.delete:
                                // delete the request from the Arraylist and Database by making it seen for the request receiver
                                deleteletter(p);
                                // notifications.remove(p);
                                break;
                        }


                        return false;
                    }
                });


            });
            v.setOnClickListener(vv ->{

                // start the Letter Viewer Activity
                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.letter),letter.getLetterid());
                Intent intent = new Intent(context,StrangerLetterViewer.class);
                intent.putExtra(context.getString(R.string.letter),bundle);
                context.startActivity(intent);

            });

        }

        public void displaynotice(int p){
            Letter letter = notifications.get(p).getLetter();

            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(letter.getSenderid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null){

                        try {
                            Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().placeholder(R.mipmap.strangeralone).into(userphoto);
                        } catch (Exception e) {
                            userphoto.setImageURI(Uri.parse(user.getPhotoUrl()));
                        }
                        username.setText(user.getFullname());


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

        public void deleteletter(int p){

            notifications.remove(p);

            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.deleted),p);
            Intent intent = new Intent(context.getString(R.string.deleted));
            intent.putExtra(context.getString(R.string.deleted),bundle );
            context.sendBroadcast(intent);

            Letter letter = notifications.get(p).getLetter();
            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.letter)).child(letter.getLetterid()).child(currentuser.getUid()).setValue(currentuser.getUid());

        }
    }

    class InterestUserHolder{

        private TextView noticedate;
        private ImageView more;
        private ImageView userphoto;
        private TextView username;
        private TextView interest;

        public InterestUserHolder(View v, int p){
            Notification notice = notifications.get(p);

            userphoto = (ImageView)v.findViewById(R.id.userphoto);
            username = (TextView)v.findViewById(R.id.username);
            interest = (TextView)v.findViewById(R.id.interest);

            noticedate = (TextView)v.findViewById(R.id.noticetime);
            noticedate.setText(new SimpleDateFormat("hh:mm a  E dd MMM, yyyy",Locale.getDefault()).format(new Date(notice.getDate())));



            more = (ImageView)v.findViewById(R.id.more);

            interest.setText(notice.getReceivername());
            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.location)).child(currentuser.getUid()).child(notice.getReceiverid()).setValue(new StrangerLocationModel(notice.getReceiverid(),"false",new GregorianCalendar().get(Calendar.DAY_OF_YEAR)));

            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(notice.getReceiverid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    if(user != null){

                        username.setText(user.getFullname());
                        try{
                            Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().placeholder(R.mipmap.strangeralone).into(userphoto);

                        }catch (Exception e){
                            userphoto.setImageURI(Uri.parse(user.getPhotoUrl()));
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            v.setOnClickListener(iuv ->{});
            more.setOnClickListener(miu ->{

                PopupMenu popupMenu = new PopupMenu(context,miu);
                popupMenu.inflate(R.menu.notification_list);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.delete:
                                // delete the request from the Arraylist and Database by making it seen for the request receiver
                                deletenotice(notice, p);
                                // notifications.remove(p);
                                break;
                        }


                        return false;
                    }
                });

            });

        }

        public void deletenotice(Notification notice , int p){

            notifications.remove(p);
            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.deleted),p);
            Intent intent = new Intent(context.getString(R.string.deleted));
            intent.putExtra(context.getString(R.string.deleted),bundle );
            context.sendBroadcast(intent);

            HashMap<String,Object> updatedelete = new HashMap<>();
            updatedelete.put(context.getString(R.string.deleted),"true");

            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.location)).child(currentuser.getUid()).child(notice.getReceiverid()).updateChildren(updatedelete);


        }
    }




    @Override
    public int getCount() {
        return notifications.size();
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

        String type = notifications.get(position).getNoticetype();


        if(type.equalsIgnoreCase(context.getString(R.string.requestreceived))){
            // The notice is for Request Receieved
            convertView = inflater.inflate(R.layout.notice_list_receive_request_lay,parent,false);
            new ReceivedHolder(convertView,position);


        }else if(type.equalsIgnoreCase(context.getString(R.string.requestaccepted))){
            // The notice is for Request Accepted
            convertView = inflater.inflate(R.layout.notice_list_accepted_request_lay,parent,false);
            new AcceptedHolder(convertView,position);

        }else if(type.equalsIgnoreCase(context.getString(R.string.requestrejected))){
            // The notice is for Request rejected
            convertView = inflater.inflate(R.layout.notice_list_rejected_request_lay,parent,false);
            new RejectedHolder(convertView,position);

        }else if(type.equalsIgnoreCase(context.getString(R.string.letter))){
            // Stranger letter lay
            convertView = inflater.inflate(R.layout.notice_list_stranger_letter,parent,false);
            new LetterHolder(convertView,position);

        }else if(type.equalsIgnoreCase(context.getString(R.string.interestuser))){
            // Interest User lay
            convertView  = inflater.inflate(R.layout.notice_list_interest_user,parent,false);
            new InterestUserHolder(convertView,position);

        }





        return convertView;
    }
}
