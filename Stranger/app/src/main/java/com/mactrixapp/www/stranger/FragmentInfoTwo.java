package com.mactrixapp.www.stranger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentInfoTwo extends Fragment {


    Context context;
    private MultiAutoCompleteTextView interest;
    private FirebaseUser fireuser;
    private StorageReference storageReference;
    private DatabaseReference userReference;
    private DatabaseReference interestReference;
    private int commaIndex;
    private int precommaIndex;
    private ArrayList<String> interests;
    private ArrayAdapter adapter;
    private boolean checker;
    private User user;
    private Button nextclick;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_two,container,false);

        interest = (MultiAutoCompleteTextView) v.findViewById(R.id.interestentry);
        nextclick = (Button)v.findViewById(R.id.nextclick);


        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // initialize the firebase
        fireuser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child(getString(R.string.user)).child(fireuser.getUid());
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(fireuser.getUid());


        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                // Here we collect the already stored User.class from SignUp
                user = dataSnapshot.getValue(User.class);

                if(user != null && user.getInterest() != null){

                    interest.setText(user.getInterest());


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // create the Interest Database to store new Interest from users
        interestReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.interestreference));



        // implement the text watcher for Interest to direct the user in setting up the interest
        commaIndex = 0;
        precommaIndex = 0;

        interests  = new ArrayList<>();
        interestRetrieve();


        nextclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                next();



            }
        });



    }


    private void interestRetrieve(){


        interestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    //retreive all the interest in the database
                    String interest = snapshot.getValue(String.class);

                    // check if the Interest type by the user is contain in the Database Interest
                    if (interest != null) {


                        // check if the interest has been added before
                        if(!interests.contains(interest)){

                            interests.add(interest);
                            //Toast.makeText(context, "Interest is added", Toast.LENGTH_SHORT).show();

                        }

                    }


                }

                //arrayAdapter.notifyDataSetChanged();
                //adapter.notifyDataSetChanged();


                String[] interestarray = new String[interests.size()];

                // fill up the Interest Array
                for (int i = 0; i < interestarray.length;i++){


                    interestarray[i] = interests.get(i);

                }



                // arrayAdapter = new ArrayAdapter<>(EditProfile.this,R.layout.interestlistlayoutprofile,R.id.interesttext,interestarray);
                adapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line,interestarray);

                interest.setAdapter(adapter);
                interest.setThreshold(1);
                interest.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void interestAdd(String interest){

        interestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(interest)){


                    interestReference.child(interest).setValue(interest);

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private boolean interestContain(String entry){


        interestReference.addListenerForSingleValueEvent(new ValueEventListener() {

            boolean check = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    String interest = snapshot.getValue(String.class);

                    if(interest != null && interest.equalsIgnoreCase(entry)){

                        check = true;
                    }

                }

                checker = check;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });



        return checker;
    }


    public void next(){

        String[] textentry = new String[] {interest.getText().toString()};

        boolean checker = false;

        for (String aTextentry : textentry) {
            if (aTextentry.isEmpty()) {
                checker = true;
            }
        }


        String interestss = interest.getText().toString();

        String[] interestarray = interestss.split(",");

        for (String s : interestarray){

               /* if(!interestContain(s)) {

                    interestReference.child(s).setValue(s);

                }*/

            interestAdd(s);
        }

        if(checker){

            Toast.makeText(context, "Please fill up the Empty field..", Toast.LENGTH_LONG).show();

        }else{


            Toast.makeText(context, ""+interest.getText().toString(), Toast.LENGTH_SHORT).show();

            String intes = interest.getText().toString();
           // User user = new User();

            user.setInterest(intes);


            HashMap<String,Object> updateedit = new HashMap<>();
            updateedit.put(context.getString(R.string.interest),interest.getText().toString());

          /*  userReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                    Toast.makeText(context, "Succussfully Uploaded Info", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context.getString(R.string.infopager));
                    intent.putExtra(context.getString(R.string.infoPosition),2);

                    context.sendBroadcast(intent);



                }
            });*/

            userReference.updateChildren(updateedit).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(context, "Succussfully Uploaded Info", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context.getString(R.string.infopager));
                    intent.putExtra(context.getString(R.string.infoPosition),2);

                    context.sendBroadcast(intent);

                }
            });



        }

    }





}
