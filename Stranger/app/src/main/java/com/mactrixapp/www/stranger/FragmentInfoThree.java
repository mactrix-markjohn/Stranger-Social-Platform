package com.mactrixapp.www.stranger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.HashMap;

public class FragmentInfoThree extends Fragment {

    Context context;
    private TextView workstate;
    private EditText profession;
    private EditText institute;
    private EditText address;
    private EditText whatsapp;
    private EditText facebook;
    private EditText instagram;
    private EditText twitter;
    private FirebaseUser fireuser;
    private StorageReference storageReference;
    private DatabaseReference userReference;
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
        View v = inflater.inflate(R.layout.fragment_info_three,container,false);



        workstate = (TextView)v.findViewById(R.id.workstateentry);
        profession = (EditText) v.findViewById(R.id.professionentry);
        institute = (EditText) v.findViewById(R.id.instituteentry);
        address = (EditText)v.findViewById(R.id.addressentry);
        whatsapp = (EditText) v.findViewById(R.id.whatsappentry);
        facebook = (EditText) v.findViewById(R.id.facebookentry);
        instagram = (EditText) v.findViewById(R.id.instagramentry);
        twitter = (EditText) v.findViewById(R.id.twitterentry);


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


                if(user != null && user.getFacebook() != null){

                    workstate.setText(user.getWorkstate());
                    profession.setText(user.getProfession());
                    institute.setText(user.getInstitute());
                    whatsapp.setText(user.getWhatsapp());
                    facebook.setText(user.getFacebook());
                    instagram.setText(user.getInstagram());
                    twitter.setText(user.getTwitter());


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        workstate.setOnClickListener((wsv) ->{


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMultiChoiceItems(new String[]{getString(R.string.student), getString(R.string.professional)}, new boolean[]{false, false}, (dialog, which, isChecked) -> {
                switch (which) {
                    case 0:
                        workstate.setText(R.string.student);
                        dialog.dismiss();
                        break;
                    case 1:
                        workstate.setText(R.string.professional);
                        dialog.dismiss();
                        break;
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.create().show();


        });

        nextclick.setOnClickListener((aa) -> next());





    }


    public void next(){

        String[] textentry = new String[] {
                facebook.getText().toString(),
                instagram.getText().toString(),
                institute.getText().toString(),
                workstate.getText().toString(),
                profession.getText().toString(),
                twitter.getText().toString(),
                whatsapp.getText().toString()};

        boolean checker = false;

        for (String aTextentry : textentry) {
            if (aTextentry.isEmpty()) {
                checker = true;
            }
        }


        if(checker){

            Toast.makeText(context, "Please fill up the Empty field..", Toast.LENGTH_LONG).show();

        }else{





            //user.setAddress(address.getText().toString());

            HashMap<String,Object> updateedit = new HashMap<>();

            User user = new User();

            user.setFacebook(facebook.getText().toString());
            updateedit.put(context.getString(R.string.facebook),facebook.getText().toString());

            user.setInstagram(instagram.getText().toString());
            updateedit.put(context.getString(R.string.instagram),instagram.getText().toString());

            user.setInstitute(institute.getText().toString());
            updateedit.put(context.getString(R.string.institute),institute.getText().toString());

            user.setWorkstate(workstate.getText().toString());
            updateedit.put(context.getString(R.string.workstate),workstate.getText().toString());

            user.setProfession(profession.getText().toString());
            updateedit.put(context.getString(R.string.profession),profession.getText().toString());

            user.setTwitter(twitter.getText().toString());
            updateedit.put(context.getString(R.string.twitter),twitter.getText().toString());

            user.setWhatsapp(whatsapp.getText().toString());
            updateedit.put(context.getString(R.string.whatsapp),whatsapp.getText().toString());


            userReference.updateChildren(updateedit).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                    Toast.makeText(context, "Succussfully Uploaded Info", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, Stranger.class));


                    Intent intent = new Intent(context.getString(R.string.infopager));
                    intent.putExtra(context.getString(R.string.infoPosition),3);

                    context.sendBroadcast(intent);




                }
            });



        }










    }


}
