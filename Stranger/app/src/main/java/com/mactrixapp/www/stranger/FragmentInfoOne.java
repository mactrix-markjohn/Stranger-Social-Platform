package com.mactrixapp.www.stranger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentInfoOne extends Fragment {

    Context context;
    private EditText fullname;
    private EditText phonenumber;
    private TextView datebirth;
    private TextView gender;
    private EditText origin;
    private EditText country;
    private TextView status;
  /*  private CircleImageView userimage;
    private ImageView userheaderimage;
    private EditText usernameedit;*/
    private FirebaseUser fireuser;
    private StorageReference storageReference;
    private DatabaseReference userReference;
    private User user;
    private CardView next;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_info_one,container,false);

        fullname = (EditText) v.findViewById(R.id.fullnameentry);
        phonenumber = (EditText)v.findViewById(R.id.phoneentry);
        datebirth = (TextView) v.findViewById(R.id.birthentry);
        gender = (TextView) v.findViewById(R.id.genderentry);
        origin = (EditText)v.findViewById(R.id.originentry);
        country = (EditText)v.findViewById(R.id.countryentry);
        status = (TextView) v.findViewById(R.id.marritalentry);

       /* userimage = (CircleImageView) v.findViewById(R.id.userimage);
        userheaderimage = (ImageView) v.findViewById(R.id.userheaderimage);
        usernameedit = (EditText) v.findViewById(R.id.usernameedit);*/

        next = (CardView)v.findViewById(R.id.next);




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

                if(user != null && user.getFullname() != null){

                    fullname.setText(user.getFullname());
                    phonenumber.setText(user.getPhoneNumber());
                    datebirth.setText(user.getDatebirth());
                    gender.setText(user.getGender());
                    origin.setText(user.getOrigin());
                    country.setText(user.getCountry());


                }


               /* usernameedit.setText(user != null ? user.getUsername() : "");*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        datebirth.setOnClickListener((View vssd) -> {

            DatePicker datePicker = new DatePicker(context);
            datePicker.init(1999, 4-1, 26, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String date = dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                    datebirth.setText(date);
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(datePicker);
            builder.setTitle("Set Date of Birth");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    datebirth.setText("");
                }
            });
            builder.setPositiveButton("Set",(dialog, which) ->{

                //String date = datePicker.getDayOfMonth() + "/" + datePicker.getMonth() + "/" + datePicker.getYear();
                //datebirth.setText(date);
                dialog.dismiss();

            });
            builder.create().show();

        });

        gender.setOnClickListener((vv) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMultiChoiceItems(new String[]{getString(R.string.male), getString(R.string.female), getString(R.string.other)}, new boolean[]{false, false, false}, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    switch (which) {
                        case 0:
                            gender.setText(R.string.male);
                            dialog.dismiss();
                            break;
                        case 1:
                            gender.setText(R.string.female);
                            dialog.dismiss();
                            break;
                        case 2:
                            gender.setText(R.string.other);
                            dialog.dismiss();
                            break;
                    }
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




        next.setOnClickListener((vv) -> {


            next();




        });







    }

    public void next(){

        String[] textentry = new String[] {fullname.getText().toString(),
                phonenumber.getText().toString(),
                datebirth.getText().toString(),
                gender.getText().toString(),
                origin.getText().toString(),
                country.getText().toString()};

        boolean checker = false;

        for (String aTextentry : textentry) {
            if (aTextentry.isEmpty()) {
                checker = true;
            }
        }


        if(checker){

            Toast.makeText(context, "Please fill up the Empty field..", Toast.LENGTH_LONG).show();

        }else{




            user.setFullname(fullname.getText().toString());
            user.setPhoneNumber(phonenumber.getText().toString());

            user.setDatebirth(datebirth.getText().toString());

            user.setGender(gender.getText().toString());
            user.setOrigin(origin.getText().toString());
            user.setCountry(country.getText().toString());

            user.setStatus(status.getText().toString());

            /*user.setUsername(usernameedit.getText().toString());*/

            userReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                    Toast.makeText(context, "Succussfully Uploaded Info", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context.getString(R.string.infopager));
                    intent.putExtra(context.getString(R.string.infoPosition),1);

                    context.sendBroadcast(intent);



                }
            });



        }

    }




}
