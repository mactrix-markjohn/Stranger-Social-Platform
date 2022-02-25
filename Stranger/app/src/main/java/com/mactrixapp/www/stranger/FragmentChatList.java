package com.mactrixapp.www.stranger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.ChatlistAdapter;
import com.mactrixapp.www.stranger.AsyncTaskPack.AsyncClass;
import com.mactrixapp.www.stranger.Model.Chat;
import com.mactrixapp.www.stranger.Model.ChatUserModel;
import com.mactrixapp.www.stranger.Model.Group;
import com.mactrixapp.www.stranger.Model.IsListContain;
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class FragmentChatList extends Fragment {


    private ListView chatlistview;
    private FloatingActionButton fab;
    Context context;
    private FirebaseUser currentuser;
    private DatabaseReference chatReference;
    private ArrayList<ChatUserModel> chatusermodels;
    private DatabaseReference userReference;
    private ArrayList<String> chatusers;
    private IsListContain isListContain;
    private ChatlistAdapter chatlistAdapter;
   /* private String receiverid;
    private String senderid;*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatlist,container,false);

        chatlistview = (ListView)view.findViewById(R.id.chatlistview);
        fab = (FloatingActionButton)view.findViewById(R.id.fab);


        return view;
    }
    DataSnapshot lastshot ;

    @Override

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        chatReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                .child(currentuser.getUid()).child(getString(R.string.chat));

        userReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user));

        chatusermodels = new ArrayList<>();
        isListContain = new IsListContain();


        chatlistAdapter = new ChatlistAdapter(context,chatusermodels);
        chatlistview.setAdapter(chatlistAdapter);



        new AsyncClass(this::chatreference).execute();
        new AsyncClass(this::retrieveGroup).execute();
        new AsyncClass(this:: retrieveStrangersGroup).execute();

       /* new Thread(() -> {


            chatreference();

            retrieveGroup();
            retrieveStrangersGroup();


        }).start();*/

        fab.setOnClickListener(vv -> {

            context.startActivity(new Intent(context,StrangerList.class));


        });



    }

    private void chatreference() {
        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                chatusers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    //Chat chat = snapshot.getValue(Chat.class);

                    /*if(chat != null) {

                        if (chat.getSenderid().equals(currentuser.getUid())) {
                            // collect the chat receiver id
                            String receiverid = chat.getReceiverid();// important
                            String senderid = chat.getSenderid();

                            // if the list does not contain the value then add
                            if(!chatusers.contains(receiverid)){
                                chatusers.add(receiverid);
                            }


                            // Algorithm to check if a value is in a list, if not add the value to the list



                        }else if(chat.getReceiverid().equals(currentuser.getUid())){
                            // collect the chat sender id
                            String senderid = chat.getSenderid();// important
                            String receiverid = chat.getReceiverid();


                            if(!chatusers.contains(senderid)){
                                chatusers.add(senderid);
                            }


                        }

                    }*/


                    String strangers = snapshot.getKey();

                    if(!chatusers.contains(strangers)){
                        chatusers.add(strangers);
                    }
                }


                for (String stra: chatusers){

                    retriveinfo(stra,currentuser.getUid());
                }






            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void retriveinfo(String strangerid,String currentuserid){

        userReference.child(strangerid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    chatReference.child(strangerid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            int count = 0;
                            boolean chatpoint = false;
                            long time = new SharedPref(context,strangerid).getLong();


                            for (DataSnapshot snapshoting : dataSnapshot.getChildren()){

                                Chat chating = snapshoting.getValue(Chat.class);
                                if(chating != null){
                                    if((chating.getSenderid().equals(currentuser.getUid()) && chating.getReceiverid().equals(strangerid) ) ||(chating.getSenderid().equals(strangerid) && chating.getReceiverid().equals(currentuser.getUid()))){

                                        lastshot = snapshoting;

                                        // Implementing Unseen message count
                                        Chat chata = lastshot.getValue(Chat.class);
                                        if (chata != null) {

                                            if (chata.getDate() == time) {

                                                chatpoint = true;
                                            }

                                            if (chatpoint) {

                                                count ++;

                                            }

                                        }



                                    }
                                }
                            }

                            Chat lastchat = lastshot.getValue(Chat.class);
                            if (lastchat != null) {
                                String lastmessage = lastchat.getMessage();

                                // add the user and last message to the chatusermodels arraylist
                                ChatUserModel chatUserModel = new ChatUserModel(user,user.getUserid(),lastmessage,lastchat.getChatid(),lastchat.getDate(),context.getString(R.string.chat));
                                chatUserModel.setChatcount(count-1);
                                // Algorithm to check if a value is in a list, if not add the value to the list
                                if(chatusermodels.isEmpty()){

                                    chatusermodels.add(chatUserModel);
                                    Toast.makeText(context, "chat empty", Toast.LENGTH_SHORT).show();

                                }else {

                                    for (int i = 0; i < chatusermodels.size(); i++) {

                                        try {
                                            if (chatusermodels.get(i).getGroupid().equals(chatUserModel.getGroupid())) {
                                                chatusermodels.set(i,chatUserModel);

                                                break;
                                            }

                                            // once the loop has reached the end and it still did noy find a matching id, it add the id
                                            if (i == chatusermodels.size() - 1 && !chatusermodels.get(i).getGroupid().equals(chatUserModel.getGroupid())) {

                                                chatusermodels.add(chatUserModel);


                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(context, "Something is wrong", Toast.LENGTH_SHORT).show();
                                       }

                                    }
                                }





                            }

                            Collections.sort(chatusermodels, new Comparator<ChatUserModel>() {
                                @Override
                                public int compare(ChatUserModel o1, ChatUserModel o2) {
                                    return String.valueOf(o2.getDate()).compareTo(String.valueOf( o1.getDate()));
                                }
                            });


                            ((Activity) new Stranger()).runOnUiThread(() -> chatlistAdapter.notifyDataSetChanged());


                          /*  ChatlistAdapter chatlistAdapter = new ChatlistAdapter(context,chatusermodels);
                            chatlistview.setAdapter(chatlistAdapter);
*/
                           /* try {
                                Collections.sort(chatusermodels, new Comparator<ChatUserModel>() {
                                    @Override
                                    public int compare(ChatUserModel o1, ChatUserModel o2) {
                                        return o2.getChatid().compareTo(o1.getChatid());
                                    }
                                });

                                ChatlistAdapter chatlistAdapter = new ChatlistAdapter(context,chatusermodels);
                                chatlistview.setAdapter(chatlistAdapter);
                            } catch (Exception e) {*/





                           // }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    ArrayList<Group> grouparray ;
    public void retrieveGroup(){

        FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.group)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                grouparray = new ArrayList<>();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if (snapshot.child(context.getString(R.string.members)).hasChild(currentuser.getUid())) {
                        Group group = snapshot.getValue(Group.class);

                        if(group != null){

                            if(!isListContain.isGroupContain(grouparray,group)){

                                grouparray.add(group);

                            }

                        }


                    }
                }

                for(Group group : grouparray){

                    getGroupinfo(group,context.getString(R.string.group));

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    DataSnapshot dss;

    private void getGroupinfo(Group group, String grouptype) {



        FirebaseDatabase.getInstance().getReference().child(grouptype).child(group.getId()).child(context.getString(R.string.chat)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int count = 0;
                boolean chatpoint = false;
                String key = currentuser.getUid().concat(group.getId());
                long time = new SharedPref(context,key).getLong();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    dss = snapshot;

                    // Implementing Unseen message count
                    Chat chata = snapshot.getValue(Chat.class);
                    if (chata != null) {

                        if (chata.getDate() == time) {

                            chatpoint = true;
                        }

                        if (chatpoint) {

                            count ++;

                        }

                    }
                }

                if (dss != null) {
                    Chat chat = dss.getValue(Chat.class);
                    if (chat != null) {

                        String lastmessage = chat.getMessage();

                        ChatUserModel chatUserModel = new ChatUserModel(group,group.getId(),lastmessage,chat.getDate(),grouptype);
                        chatUserModel.setChatcount(count-1);
                        // Algorithm to check if a value is in a list, if not add the value to the list
                        if(chatusermodels.isEmpty()){

                            chatusermodels.add(chatUserModel);

                        }else {

                            for (int i = 0; i < chatusermodels.size(); i++) {

                                // if the user find any matching id, it will break.

                                String type = chatusermodels.get(i).getChattype() != null ? chatusermodels.get(i).getChattype() : "hello";


                                if (type.equalsIgnoreCase(context.getString(R.string.group)) || type.equalsIgnoreCase(context.getString(R.string.strangersgroup))) {
                                    if (chatusermodels.get(i).getGroupid().equals(chatUserModel.getGroupid())) {
                                        chatusermodels.set(i,chatUserModel);
                                        break;
                                    }

                                    // once the loop has reached the end and it still did noy find a matching id, it add the id
                                    if (i == chatusermodels.size() - 1 && !chatusermodels.get(i).getGroupid().equals(chatUserModel.getGroupid())) {

                                        chatusermodels.add(chatUserModel);

                                    }
                                }
                            }
                        }


                    }
                }else {

                    String lastmessage = " ";

                    ChatUserModel chatUserModel = new ChatUserModel(group,group.getId(),lastmessage,new Date().getTime(),grouptype);
                    chatUserModel.setChatcount(count-1);
                    // Algorithm to check if a value is in a list, if not add the value to the list
                    if(chatusermodels.isEmpty()){

                        chatusermodels.add(chatUserModel);

                    }else {

                        for (int i = 0; i < chatusermodels.size(); i++) {

                            // if the user find any matching id, it will break.

                            String type = chatusermodels.get(i).getChattype() != null ? chatusermodels.get(i).getChattype() : "hello";


                            if (type.equalsIgnoreCase(context.getString(R.string.group)) || type.equalsIgnoreCase(context.getString(R.string.strangersgroup))) {
                                if (chatusermodels.get(i).getGroupid().equals(chatUserModel.getGroupid())) {
                                    chatusermodels.set(i,chatUserModel);
                                    break;
                                }

                                // once the loop has reached the end and it still did noy find a matching id, it add the id
                                if (i == chatusermodels.size() - 1 && !chatusermodels.get(i).getGroupid().equals(chatUserModel.getGroupid())) {

                                    chatusermodels.add(chatUserModel);

                                }
                            }
                        }
                    }


                }

                Collections.sort(chatusermodels, new Comparator<ChatUserModel>() {
                    @Override
                    public int compare(ChatUserModel o1, ChatUserModel o2) {
                        return String.valueOf(o2.getDate()).compareTo(String.valueOf( o1.getDate()));
                    }
                });

                ((Activity) new Stranger()).runOnUiThread(() -> chatlistAdapter.notifyDataSetChanged());

               /* ChatlistAdapter chatlistAdapter = new ChatlistAdapter(context,chatusermodels);
                chatlistview.setAdapter(chatlistAdapter);*/



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void retrieveStrangersGroup(){

        FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.strangersgroup)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                grouparray = new ArrayList<>();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if (snapshot.child(context.getString(R.string.members)).hasChild(currentuser.getUid())) {
                        Group group = snapshot.getValue(Group.class);

                        if(group != null){

                            if(!isListContain.isGroupContain(grouparray,group)){

                                grouparray.add(group);

                            }

                        }


                    }
                }

                for(Group group : grouparray){

                    getGroupinfo(group,context.getString(R.string.strangersgroup));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
