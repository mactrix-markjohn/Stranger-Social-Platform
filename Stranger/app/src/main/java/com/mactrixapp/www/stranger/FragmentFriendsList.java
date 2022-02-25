package com.mactrixapp.www.stranger;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.FriendsListAdapter;
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;

public class FragmentFriendsList extends Fragment {

    Context  context;
    private TextView friendcount;
    private ListView friendslistview;
    private FirebaseUser currentuser;
    private DatabaseReference userReference;
    private DatabaseReference friendsReference;
    ArrayList<User> users;
    DataSnapshot snapshoting;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_friends_list,container,false);
        friendcount = (TextView)v.findViewById(R.id.friendcount);
        friendslistview = (ListView)v.findViewById(R.id.friendslistview);

        return v;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(currentuser.getUid());
        friendsReference = userReference.child(context.getString(R.string.friends));


        users = new ArrayList<>();

        if(users.isEmpty()){

            friendsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        User user = snapshot.getValue(User.class);
                        if(user != null){

                            if(!isFriendContain(users,user)){

                                users.add(user);
                            }


                        }



                    }

                    FriendsListAdapter adapter = new FriendsListAdapter(context, users);
                    friendslistview.setAdapter(adapter);
                    friendcount.setText(String.valueOf(users.size()));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{



            friendsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){


                        snapshoting = snapshot;

                    }

                    User user = snapshoting.getValue(User.class);
                    if(user != null){
                        if(!isFriendContain(users,user)){

                            users.add(user);
                        }

                    }

                    FriendsListAdapter adapter = new FriendsListAdapter(context, users);
                    friendslistview.setAdapter(adapter);
                    friendcount.setText(String.valueOf(users.size()));





                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }






    }

    public boolean isFriendContain(ArrayList<User> userlist, User userinst){

        boolean check = false;
        // Algorithm to check if a value is in a list, if not, return false


        for (int i = 0; i < userlist.size(); i++) {

            // if the user find any matching id, it will break.
            if ( userlist.get(i).getUserid().equals(userinst.getUserid())) {
                check = true;
                break;
            }

            // once the loop has reached the end and it still did noy find a matching id, return false
            if (i == userlist.size() - 1 && userlist.get(i).getUserid().equals(userinst.getUserid())) {

                check = false;

            }
        }
        return check;

    }


}
