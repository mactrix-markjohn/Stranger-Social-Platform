package com.mactrixapp.www.stranger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mactrixapp.www.stranger.Adapters.UserInfoPagerAdapter;
import com.mactrixapp.www.stranger.Model.User;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfo extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int HEADERPHOTOREQUESTCODE = 20;
    private EditText fullname;
    private EditText phonenumber;
    private TextView datebirth;
    private TextView gender;
    private EditText origin;
    private EditText country;
    private TextView status;
    private MultiAutoCompleteTextView interest;
    private EditText profession;
    private EditText institute;
    private EditText whatsapp;
    private EditText facebook;
    private EditText instagram;
    private EditText twitter;

    DatabaseReference userReference;
    FirebaseUser fireuser;
    StorageReference storageReference;
    private CircleImageView userimage;
    private EditText usernameedit;
    private User user;
    private EditText address;
    private TextView workstate;
    private RelativeLayout progresslay;
    private ImageView userheaderimage;
    private DatabaseReference interestReference;
    private int commaIndex;
    private int precommaIndex;
    private String entry;
    private String lastchar;

    ArrayList<String> interests;
    private ArrayAdapter adapter;
    ArrayAdapter<String> arrayAdapter;
    private View firstcircle;
    private View secondcircle;
    private View thirdcircle;
    private ViewPager infopager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userimage = (CircleImageView) findViewById(R.id.userimage);
        userheaderimage = (ImageView) findViewById(R.id.userheaderimage);
        usernameedit = (EditText) findViewById(R.id.usernameedit);
        progresslay = (RelativeLayout)findViewById(R.id.progresslay);


        firstcircle = (View)findViewById(R.id.firstcirlce);
        secondcircle = (View)findViewById(R.id.secondcircle);
        thirdcircle = (View)findViewById(R.id.thirdcircle);

        infopager = (ViewPager)findViewById(R.id.infopager);



        // initialize the firebase
        fireuser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child(getString(R.string.user)).child(fireuser.getUid());
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(fireuser.getUid());


        // create the Interest Database to store new Interest from users
        interestReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.interestreference));





        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                // Here we collect the already stored User.class from SignUp
                user = dataSnapshot.getValue(User.class);
                usernameedit.setText(user != null ? user.getUsername() : "");
                Glide.with(UserInfo.this).load(Uri.parse(user != null ? user.getPhotoUrl() : "fjn")).asBitmap().placeholder(R.mipmap.profileavatar).into(userimage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // set up the view pager fragment
        UserInfoPagerAdapter userInfoPagerAdapter = new UserInfoPagerAdapter(getSupportFragmentManager(),3);
        infopager.setAdapter(userInfoPagerAdapter);

        infopager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position){

                    case 0:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            firstcircle.setBackground(getDrawable(R.drawable.orangercircle));
                            secondcircle.setBackground(getDrawable(R.drawable.greycircle));
                            thirdcircle.setBackground(getDrawable(R.drawable.greycircle));
                        }

                        break;
                    case 1:

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            firstcircle.setBackground(getDrawable(R.drawable.greycircle));
                            secondcircle.setBackground(getDrawable(R.drawable.orangercircle));
                            thirdcircle.setBackground(getDrawable(R.drawable.greycircle));
                        }

                        break;
                    case 2:

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            firstcircle.setBackground(getDrawable(R.drawable.greycircle));
                            secondcircle.setBackground(getDrawable(R.drawable.greycircle));
                            thirdcircle.setBackground(getDrawable(R.drawable.orangercircle));
                        }

                        break;

                }




            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        registerInfoReceiver();



        //infosaving();






        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

       /* NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
    }


    // set up the broadcast receive to receive message from the fragment to change the Viewpager
    BroadcastReceiver infoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int position = intent.getIntExtra(getString(R.string.infoPosition),0);

            if(position == 3){

                finish();

            }else {

                infopager.setCurrentItem(position, true);
            }



        }
    };

    public void registerInfoReceiver(){

        registerReceiver(infoReceiver,new IntentFilter(getString(R.string.infopager)));


    }






    public void next(View view){

        String[] textentry = new String[] {
                fullname.getText().toString(),
            phonenumber.getText().toString(),
            address.getText().toString(),
            datebirth.getText().toString(),
            facebook.getText().toString(),
           gender.getText().toString(),
            instagram.getText().toString(),
            institute.getText().toString(),
            interest.getText().toString(),
                workstate.getText().toString(),
            profession.getText().toString(),
            status.getText().toString(),
                origin.getText().toString(),
                country.getText().toString(),
            twitter.getText().toString(),
           whatsapp.getText().toString(),
           usernameedit.getText().toString()
        };

        boolean checker = false;

        for (String aTextentry : textentry) {
            if (aTextentry.isEmpty()) {
                checker = true;
            }
        }


        if(checker){

            Toast.makeText(this, "Please fill up the Empty field..", Toast.LENGTH_LONG).show();

        }else{

            progresslay.setVisibility(View.VISIBLE);


            user.setFullname(fullname.getText().toString());
            user.setPhoneNumber(phonenumber.getText().toString());
            user.setAddress(address.getText().toString());
            user.setDatebirth(datebirth.getText().toString());
            user.setFacebook(facebook.getText().toString());
            user.setGender(gender.getText().toString());
            user.setOrigin(origin.getText().toString());
            user.setCountry(country.getText().toString());
            user.setInstagram(instagram.getText().toString());
            user.setInstitute(institute.getText().toString());
            user.setInterest(interest.getText().toString());
            user.setWorkstate(workstate.getText().toString());
            user.setProfession(profession.getText().toString());
            user.setStatus(status.getText().toString());
            user.setTwitter(twitter.getText().toString());
            user.setWhatsapp(whatsapp.getText().toString());
            user.setUsername(usernameedit.getText().toString());

            userReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    progresslay.setVisibility(View.GONE);
                    Toast.makeText(UserInfo.this, "Succussfully Uploaded Info", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserInfo.this, Login.class));


                }
            }).addOnFailureListener(fv -> progresslay.setVisibility(View.GONE));



        }


        String interestss = interest.getText().toString();

        String[] interestarray = interestss.split(",");

        for (String s : interestarray){

               /* if(!interestContain(s)) {

                    interestReference.child(s).setValue(s);

                }*/

            interestAdd(s);
        }









    }

    public void back(View view) {

        onBackPressed();
    }


    // Open Gallery to select picture
    final int PHOTOREQUESTCODE = 10;
    Bitmap bitmap;
    Uri uri;


    public void changephoto(View view) {

        Intent photointent = new Intent(Intent.ACTION_GET_CONTENT);
        photointent.setType("image/*");
        startActivityForResult(photointent,PHOTOREQUESTCODE);

    }

    // StartActivityForResult callback method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTOREQUESTCODE){

            if(resultCode == RESULT_OK){

                uri = data.getData();

                // Set the image in the circle Image
                Glide.with(this).load(uri).asBitmap().placeholder(R.mipmap.profileavatar).into(userimage);


                // Upload it to Firebase Storage
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    if (inputStream != null) {
                        UploadTask uploadTask =storageReference.putStream(inputStream);

                        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                return storageReference.getDownloadUrl();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Toast.makeText(UserInfo.this, "Upload is Successfull", Toast.LENGTH_SHORT).show();
                                user.setPhotoUrl(uri.toString());

                            }
                        });





                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }


        }else if(requestCode == HEADERPHOTOREQUESTCODE){

            if(resultCode == RESULT_OK){

                uri = data.getData();

                // Set the image in the circle Image
                Glide.with(this).load(uri).asBitmap().placeholder(R.mipmap.profileavatar).into(userheaderimage);

                // Upload it to Firebase Storage
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    if (inputStream != null) {
                        UploadTask uploadTask =storageReference.putStream(inputStream);

                        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                return storageReference.getDownloadUrl();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Toast.makeText(UserInfo.this, "Upload is Successfull", Toast.LENGTH_SHORT).show();
                                user.setHeaderphotourl(uri.toString());

                            }
                        });





                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }


        }


    }

    public void enablename(View view) {

        usernameedit.setEnabled(true);
    }






    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
       onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void editheaderimage(View view) {

        Intent photointent = new Intent(Intent.ACTION_GET_CONTENT);
        photointent.setType("image/*");
        startActivityForResult(photointent,HEADERPHOTOREQUESTCODE);



    }



    private void infosaving() {

        // Initialize the views
        fullname = (EditText) findViewById(R.id.fullnameentry);
        phonenumber = (EditText) findViewById(R.id.phoneentry);
        datebirth = (TextView) findViewById(R.id.birthentry);
        gender = (TextView) findViewById(R.id.genderentry);
        origin = (EditText)findViewById(R.id.originentry);
        country = (EditText)findViewById(R.id.countryentry);
        status = (TextView) findViewById(R.id.marritalentry);
        interest = (MultiAutoCompleteTextView) findViewById(R.id.interestentry);
        workstate = (TextView)findViewById(R.id.workstateentry);
        profession = (EditText) findViewById(R.id.professionentry);
        institute = (EditText) findViewById(R.id.instituteentry);
        address = (EditText)findViewById(R.id.addressentry);
        whatsapp = (EditText) findViewById(R.id.whatsappentry);
        facebook = (EditText) findViewById(R.id.facebookentry);
        instagram = (EditText) findViewById(R.id.instagramentry);
        twitter = (EditText) findViewById(R.id.twitterentry);













        datebirth.setOnClickListener((View v) -> {

            DatePicker datePicker = new DatePicker(this);
            datePicker.init(1999, 4-1, 26, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String date = dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                    datebirth.setText(date);
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        gender.setOnClickListener((v) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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


        workstate.setOnClickListener((wsv) ->{


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        status.setOnClickListener((v) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMultiChoiceItems(new String[]{getString(R.string.single), getString(R.string.married), getString(R.string.engaged),
                    getString(R.string.notinterest)}, new boolean[]{false, false, false, false}, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    switch (which) {
                        case 0:
                            status.setText(R.string.single);
                            dialog.dismiss();
                            break;
                        case 1:
                            status.setText(R.string.married);
                            dialog.dismiss();
                            break;
                        case 2:
                            status.setText(R.string.engaged);
                            dialog.dismiss();
                            break;
                        case 3:
                            status.setText(R.string.notinterest);
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

        // implement the text watcher for Interest to direct the user in setting up the interest
        commaIndex = 0;
        precommaIndex = 0;

        interests  = new ArrayList<>();
        interestRetrieve();
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
                            Toast.makeText(UserInfo.this, "Interest is added", Toast.LENGTH_SHORT).show();

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
                adapter = new ArrayAdapter(UserInfo.this, android.R.layout.simple_dropdown_item_1line,interestarray);

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

    private void interestextract(Editable s) {
        /**
         * track the last character the user types
         * if the character is not comma, the entered character will be compared and computation will be carried out
         * if a comma is entered , the new starting index will move +1 from the comma index
         * */

        // get the last character typed
        entry = s.toString();
        lastchar = entry.substring(entry.length()-1,entry.length());

        if(lastchar.equals(",")){
            // the last character is a comma
            precommaIndex = commaIndex;
            commaIndex = entry.indexOf(",",commaIndex + 1);

            // TODO: At this point we should add the new interest to the database
            // First extract the interest to add and evalute

            String extract  = entry.substring(precommaIndex,commaIndex);

            if(!interestContain(extract)){

                // if the interest is not in the database, then add it into the database
                interestReference.child(extract).setValue(extract);

            }

        }else{

            // the last character is not a comma, so we break the string using the comma Index
            String interest = entry.substring(commaIndex, entry.length());

            interestSuggest(interest);

        }
    }



    boolean checker = false;
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

    private void interestSuggest(CharSequence entry) {


        ArrayList<String> interests  = new ArrayList<>();


        interestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    //retreive all the interest in the database
                    String interest = snapshot.getValue(String.class);

                    // check if the Interest type by the user is contain in the Database Interest
                    if (interest != null && interest.contains(entry)) {


                        // check if the interest has been added before
                        if(!interests.contains(interest)){

                            interests.add(interest);

                        }

                    }


                }

                // TODO: At this point we will display and implement the Popup of the suggestion
                PopupWindow popupWindow = new PopupWindow(UserInfo.this);
                ListView listView = new ListView(UserInfo.this);


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UserInfo.this,R.layout.interestlistlayoutprofile,R.id.interesttext,interests);
                listView.setAdapter(arrayAdapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String suggest = arrayAdapter.getItem(position);

                        String intText = interest.getText().toString();

                        String nonsuggest = intText.substring(0, commaIndex +1);

                        String fullInterest = nonsuggest+suggest;

                        interest.setText(fullInterest);

                        popupWindow.dismiss();




                    }
                });

                popupWindow.setFocusable(true);
                popupWindow.setWidth(250);
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);


                popupWindow.setContentView(listView);
                popupWindow.showAsDropDown(interest);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
