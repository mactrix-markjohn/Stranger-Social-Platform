package com.mactrixapp.www.stranger;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.GridProfileAdapter;
import com.mactrixapp.www.stranger.Adapters.ListProfileApdaper;
import com.mactrixapp.www.stranger.AsyncTaskPack.AsyncClass;
import com.mactrixapp.www.stranger.Model.Chat;
import com.mactrixapp.www.stranger.Model.MenuModel;
import com.mactrixapp.www.stranger.Model.OpenComment;
import com.mactrixapp.www.stranger.Model.Post;
import com.mactrixapp.www.stranger.Model.PostMore;
import com.mactrixapp.www.stranger.Model.Request;
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class StrangerProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CircleImageView strangerprofileimage;
    private TextView strangerusername;
    private TextView strangerfullname;
    private TextView profileprofession;
    private TextView profileinstitute;
    private TextView profilemarrital;
    private BottomNavigationView profilebottomview;
    private GridView profilegrid;
    private ListView profilelist;
    private String strangerid;
    private FirebaseUser fireuser;
    private DatabaseReference userReference;
    private String interest;
    private ListView listView;
    private String activityname;
    private ListView accountlist;
    private ArrayList<Post> posts;
    private DatabaseReference postReference;
    private ListProfileApdaper listProfileApdaper;
    private GridProfileAdapter gridProfileAdapter;
    private DataSnapshot lastSnapShot;
    private ArrayAdapter<String> accountadapter;
    private ArrayAdapter<String> arrayAdapter;
    private DatabaseReference friendsReferences;
    private DatabaseReference strangersReference;
    private User strangerUser;
    private User currentUser;
    private CardView strangerprofileaccountclick;
    private TextView strangerproflieaccounttext;
    private TextView postcountnum;
    private RelativeLayout strangerprofessional;
    private RelativeLayout strangerstudent;
    private TextView profileprofessions;
    private TextView profileinstitutes;
    private TextView profilemarritals;
    private Dialog showdialog;
    private String phonenumber;
    private DatabaseReference requestReference;
    private TextView orign;
    private TextView country;
    private TextView origns;
    private TextView countrys;

    private RelativeLayout viewsheet;
    private BottomSheetBehavior<View> bottomsheet;
    private ImageView postmore;
    private ImageView postimage;
    private ImageView postvideobutton;
    private ImageView postvideothumbnail;
    private CircleImageView poststrangerimage;
    private TextView postname;
    private TextView posttext;
    private TextView postcapture;
    private VideoView postvideoview;
    private RelativeLayout postvideolay;
    private RelativeLayout videocontrollay;
    private RelativeLayout postmedia;
    private RelativeLayout posttextlay;
    private RelativeLayout posttextback;
    private CardView playpasuecard;
    private ProgressBar videoprogressbar;
    private boolean prepared;
    private int count;
    private int resumePosition;
    //View v;
    private ImageView postdown;
    private MenuModel menuModel;
    private ImageView userheaderimage;
    private LinearLayout reply;
    private LinearLayout call;
    private LinearLayout strangerprofile;
    private LinearLayout like;
    private TextView likecount;
    private ImageView likeimage;
    private LinearLayout comment;
    private DatabaseReference likeref;
    private boolean hasChild =false;
    private SharedPref likewave;

    private OpenComment openComment;
    private View bottomview;
    private BottomSheetBehavior<View> sheetBehavior;
    private ImageView commentdown;
    private ListView commentlist;
    private CardView addmedia;
    private CardView addfile;
    private EditText commentype;
    private CardView sendcomment;
    private TextView commentcount;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stranger_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the views for this activity
        likewave = new SharedPref(this,getString(R.string.likewave));
        strangerprofileimage = (CircleImageView)findViewById(R.id.strangerprofileimage);
        userheaderimage = (ImageView)findViewById(R.id.userheaderimage);
        strangerusername = (TextView)findViewById(R.id.strangerprofileusername);
        strangerfullname = (TextView)findViewById(R.id.strangerprofilefullname);
        profileprofession = (TextView)findViewById(R.id.profieprofession);
        profileinstitute = (TextView)findViewById(R.id.profileinstitute);
        profilemarrital = (TextView)findViewById(R.id.profilemarrital);
        profilebottomview = (BottomNavigationView)findViewById(R.id.strangerprofilebottomview);
        profilegrid = (GridView)findViewById(R.id.strangerprofilegrid);
        profilelist = (ListView)findViewById(R.id.strangerprofilelist);
        strangerprofileaccountclick = (CardView)findViewById(R.id.strangerprofileaccountclick);
        strangerproflieaccounttext = (TextView)findViewById(R.id.strangerprofileaccounttext);
        postcountnum = (TextView)findViewById(R.id.postcountnum);
        menuModel = new MenuModel(this);

        // The Student and Professional Layout and Views initialization
        strangerprofessional = (RelativeLayout)findViewById(R.id.strangerprofessional);
        strangerstudent = (RelativeLayout)findViewById(R.id.strangerstudent);
        profileprofessions = (TextView)findViewById(R.id.profileprofessions);
        profileinstitutes = (TextView)findViewById(R.id.profileinstitudes);
        profilemarritals = (TextView)findViewById(R.id.profilemarritals);

        // new views
        orign = (TextView)findViewById(R.id.origin);
        country = (TextView)findViewById(R.id.country);
        origns = (TextView)findViewById(R.id.origins);
        countrys = (TextView)findViewById(R.id.countrys);


        // Bottom View
        viewsheet = findViewById(R.id.profilesheet);
        bottomsheet = BottomSheetBehavior.from(viewsheet);

        // Intialize all the views
        postmore = (ImageView) findViewById(R.id.postmore);
        postimage = (ImageView) findViewById(R.id.postimage);
        postvideobutton = (ImageView) findViewById(R.id.postvideobutton);
        postvideothumbnail = (ImageView) findViewById(R.id.postvideothumnail);
        poststrangerimage = (CircleImageView) findViewById(R.id.poststrangerimage);
        postname = (TextView) findViewById(R.id.postname);
        posttext = (TextView) findViewById(R.id.posttext);
        postcapture = (TextView) findViewById(R.id.postcapture);
        postvideoview = (VideoView) findViewById(R.id.postvideoview);
        postvideolay = (RelativeLayout) findViewById(R.id.postvideolay);
        videocontrollay = (RelativeLayout) findViewById(R.id.videocontrollay);
        postmedia = (RelativeLayout) findViewById(R.id.postmedia);
        posttextlay = (RelativeLayout) findViewById(R.id.posttextlay);
        posttextback = (RelativeLayout) findViewById(R.id.posttextback);
        playpasuecard = (CardView) findViewById(R.id.playpausecard);
        videoprogressbar = (ProgressBar)findViewById(R.id.postvideoprogress);
        postdown = (ImageView)findViewById(R.id.postdown);
        postdown.setOnClickListener(pdv -> bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED));

        // post extra
        reply = (LinearLayout)findViewById(R.id.reply);
        call = (LinearLayout)findViewById(R.id.callk);
        strangerprofile = (LinearLayout)findViewById(R.id.strangerprofile);
        like = (LinearLayout)findViewById(R.id.like);
        likecount = (TextView)findViewById(R.id.likecount);
        likeimage = (ImageView)findViewById(R.id.likeimage);
        comment = (LinearLayout)findViewById(R.id.comment);
        commentcount = (TextView)findViewById(R.id.commentcount);



        // initialize the comment bottomsheet

        bottomview = findViewById(R.id.commentsheet);
        sheetBehavior = BottomSheetBehavior.from(bottomview);
        commentdown = (ImageView)findViewById(R.id.commentdown);
        commentlist = (ListView)findViewById(R.id.commentlist);
        addmedia = (CardView)findViewById(R.id.chatmedia);
        addfile = (CardView)findViewById(R.id.chatfile);
        commentype = (EditText)findViewById(R.id.chattype);
        commentype.setEnabled(false);
        commentype.setVisibility(View.GONE);

        sendcomment = (CardView)findViewById(R.id.chatsend);


        commentlist.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return true;
            }
        });

        commentlist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }


                v.onTouchEvent(event);
                return true;
            }
        });


        openComment = new OpenComment(this,commentdown,commentlist,addmedia,addfile,commentype,sendcomment,sheetBehavior);
        openComment.setUpComment();
        registercomment();
        registerbroadcast();

        bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomsheet.setPeekHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        bottomsheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                    bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                    bottomsheet.setState(BottomSheetBehavior.STATE_EXPANDED);

                }else if (newState == BottomSheetBehavior.STATE_DRAGGING){
                    bottomsheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                }


            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });




        registerpostclick();

        // Recieve the User Id from the Bundle extra
        Bundle bundle = getIntent().getBundleExtra(getString(R.string.strangerid));
        if(bundle != null){

            strangerid = bundle.getString(getString(R.string.strangerid));

        }



        // Initialize the Firebase Database and CurrentUser
        fireuser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(strangerid);
        requestReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.request));

        //This is the node to indicate someone is following you and you are following someone

        new AsyncClass(this::checkfriend).execute(); // check if the current user is friends with the stranger
        //checkfriend();


        strangersReference = userReference.child(getString(R.string.strangers));


        userinfo(); // retrieve the stranger info

        profilebottomview.setOnNavigationItemSelectedListener((item) -> {

            switch (item.getItemId()){

                case R.id.gridview:
                    profilegrid.setVisibility(View.VISIBLE);
                    profilelist.setVisibility(View.GONE);

                    break;
                case R.id.listview:
                    profilelist.setVisibility(View.VISIBLE);
                    profilegrid.setVisibility(View.GONE);
                    break;



            }

            return true;}); // BottomNavigation implementation


        posts = new ArrayList<>();
        postReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.post));

        listProfileApdaper = new ListProfileApdaper(StrangerProfile.this,posts);
        profilelist.setAdapter(listProfileApdaper);

        gridProfileAdapter = new GridProfileAdapter(StrangerProfile.this,posts);
        profilegrid.setAdapter(gridProfileAdapter);

        new AsyncClass(this::postfeed).execute(); // retrieve all the post feed of the stranger
        //postfeed();


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

        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
    }

    private void postfeed() {
        if(posts.isEmpty()){


            postReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    // If the list empty, all the post in the Database is added to the ArrayList
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Post post = snapshot.getValue(Post.class);

                        if (post != null && post.getUserid().equalsIgnoreCase(strangerid)) {

                            if (!isPostContain(posts, post)) {
                                posts.add(snapshot.getValue(Post.class));
                            }
                        }
                    }

                    Collections.sort(posts, new Comparator<Post>() {
                        @Override
                        public int compare(Post o1, Post o2) {
                            return String.valueOf(o2.getDate()).compareTo(String.valueOf(o1.getDate()));
                        }
                    });


                    // Add the list to the Adapter and then to the list or grid view

                    runOnUiThread(() -> {


                        listProfileApdaper.notifyDataSetChanged();
                        gridProfileAdapter.notifyDataSetChanged();
                        postcountnum.setText(String.valueOf(posts.size()));


                    });






                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{



            postReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // This Implementation get the last Added post, once the post is got it will add it to the
                    // ArrayList
                    for(DataSnapshot snapShot : dataSnapshot.getChildren()){
                        lastSnapShot = snapShot;
                    }
                    Post post = lastSnapShot.getValue(Post.class);

                    // Check if the post was share by the user user with Id
                    if (post != null && post.getUserid().equalsIgnoreCase(strangerid)) {

                        if (!isPostContain(posts, post)) {
                            posts.add(lastSnapShot.getValue(Post.class));
                        }


                    }

                    Collections.sort(posts, new Comparator<Post>() {
                        @Override
                        public int compare(Post o1, Post o2) {
                            return String.valueOf(o2.getDate()).compareTo(String.valueOf(o1.getDate()));
                        }
                    });


                    // Add the list to the Adapter and them to the list or grid view
                    runOnUiThread(() -> {


                        listProfileApdaper.notifyDataSetChanged();
                        gridProfileAdapter.notifyDataSetChanged();
                        postcountnum.setText(String.valueOf(posts.size()));


                    });



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }

    private void userinfo() {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                strangerUser = user; // This iinstance store the stranger User.class to be used in the Unstranger click

                if (user != null) {
                    try {
                        Glide.with(StrangerProfile.this).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(strangerprofileimage);

                    } catch (Exception e) {
                        strangerprofileimage.setImageResource(R.mipmap.profileavatar);

                    }

                    try {

                        Glide.with(StrangerProfile.this).load(Uri.parse(user.getHeaderphotourl())).asBitmap().placeholder(R.mipmap.strangeralone).into(userheaderimage);
                    } catch (Exception e) {

                        userheaderimage.setImageResource(R.mipmap.strangeralone);
                    }
                    strangerusername.setText(user.getUsername());
                    strangerfullname.setText(user.getFullname());
                    profileprofession.setText(user.getProfession());
                    profileprofessions.setText(user.getProfession());
                    profileinstitute.setText(user.getInstitute());
                    profileinstitutes.setText(user.getInstitute());
                    profilemarrital.setText(user.getStatus());
                    profilemarritals.setText(user.getStatus());
                    orign.setText(user.getOrigin());
                    country.setText(user.getCountry());
                    origns.setText(user.getOrigin());
                    countrys.setText(user.getCountry());

                    phonenumber = user.getPhoneNumber();

                    interest = user.getInterest();

                    // Check if the user is a student or professional
                    if (user.getWorkstate() != null) {
                        String workstate = user.getWorkstate();
                        if(workstate.equalsIgnoreCase(getString(R.string.student))){


                            strangerstudent.setVisibility(View.VISIBLE);
                            strangerprofessional.setVisibility(View.GONE);


                        }else if (workstate.equalsIgnoreCase(getString(R.string.professional))){

                            strangerprofessional.setVisibility(View.VISIBLE);
                            strangerstudent.setVisibility(View.GONE);

                        }
                    }


                    // Extracting all the Interest seperated by comma and storing it in a list
                    ArrayList<String> interestarray = new ArrayList<>();
                    int currentposition = 0;
                    int storedposition = 0;
                    for (int i = 0; i < interest.length();i++){

                        char symbol = interest.charAt(i);

                        // if the symbol is "," then the position is store and it is used to split the string
                        // then that position is stored and increment by one to chater for the next interest
                        // so that we can extract the next interest
                        if(symbol == ','){

                            currentposition = i;
                            String singleinterest = interest.substring(storedposition,currentposition);
                            interestarray.add(singleinterest.trim());
                            storedposition = currentposition + 1;


                        }else if(i == (interest.length() - 1)){

                            currentposition = i;
                            String singleinterest = interest.substring(storedposition,currentposition);
                            interestarray.add(singleinterest.trim());

                        }

                    }

                    // Store the List in an Adapter and then in a Listview which will be shown in an AlertDialog
                    arrayAdapter = new ArrayAdapter<>(StrangerProfile.this,R.layout.interestlistlayoutprofile,R.id.interesttext,interestarray);





                    // Retrieve the Stranger Accounts and email and phone
                    ArrayList<String> accounts = new ArrayList<>();
                    accounts.add(user.getPhoneNumber());
                    accounts.add(user.getEmail());
                    accounts.add("Whatsapp: "+user.getWhatsapp());
                    accounts.add("Instagram: "+user.getInstagram());
                    accounts.add("Facebook: "+user.getFacebook());
                    accounts.add("Twitter: "+user.getTwitter());

                    accountadapter = new ArrayAdapter<>(StrangerProfile.this, R.layout.interestlistlayoutprofile,R.id.interesttext,accounts);




                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); // Retrieving all the Neccessary User info
    }

    private void checkfriend() {
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(fireuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentUser = dataSnapshot.getValue(User.class);
                friendsReferences = dataSnapshot.child(getString(R.string.friends)).getRef();


                friendsReferences.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot shot: dataSnapshot.getChildren()){
                            User frienduser = shot.getValue(User.class);

                            if(frienduser != null){


                                if(frienduser.getUserid().equals(strangerid)){

                                    // change the unstranger to white

                                    runOnUiThread(() -> {

                                        strangerprofileaccountclick.setCardBackgroundColor(0xffffffff);
                                        strangerprofileaccountclick.setBackgroundColor(0xffffffff);
                                        strangerproflieaccounttext.setTextColor(0xdd00aaff);
                                        strangerproflieaccounttext.setText(getString(R.string.unstrangered));
                                        unstranger = true;

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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void back(View view) {

        onBackPressed();


    }

    public void more(View view) {

        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.inflate(R.menu.stranger_profile);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.socialmediaaccount:

                        viewsocialmedia();
                        break;
                    case R.id.requestlocation:

                        if (strangerid.equals(fireuser.getUid())) {
                            Toast.makeText(StrangerProfile.this, "You can not send Permission Request to yourself", Toast.LENGTH_LONG).show();
                        } else{
                            showdialog(strangerUser, currentUser);
                        }
                        break;



                }


                return true;
            }
        });





    }

    public void showdialog(User user, User currentUser){

        Dialog sd = new Dialog(this);
        sd.setContentView(R.layout.dialog_sent_permission);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);

        leftclick.setOnClickListener(lcv ->{

            // Start the Stranger Profile activity
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.strangerid),user.getUserid());

            Intent intent = new Intent(this,StrangerProfile.class);
            intent.putExtra(getString(R.string.strangerid),bundle);
            startActivity(intent);
        });
        rightclick.setOnClickListener(rcv ->{

            sendrequest(user, currentUser);



        });
        cancel.setOnClickListener(cv -> sd.dismiss());

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

            Toast.makeText(StrangerProfile.this, "Permission Request has been Sent", Toast.LENGTH_LONG).show();

        }).addOnFailureListener((fv) ->{

            Toast.makeText(StrangerProfile.this, "Sorry, Something went wrong, Please resend Request", Toast.LENGTH_LONG).show();

        });
    }





    public void interestclick(View view) {

        // Display List in am AlertDialog

        showinterestdialog();

       /* listView = new ListView(StrangerProfile.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setDivider(getDrawable(R.drawable.linechangable));
        }
        listView.setAdapter(arrayAdapter);


        TextView textView = new TextView(StrangerProfile.this);
        textView.setTypeface(Typeface.DEFAULT);
        textView.setTextSize(14f);
        textView.setHeight(30);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextColor(getColor(R.color.dcblack));
        }
        textView.setGravity(Gravity.CENTER);
        textView.setText("Stranger Interest");


        AlertDialog.Builder builder = new AlertDialog.Builder(StrangerProfile.this);
        builder.setView(listView);
        builder.setCustomTitle(textView);
        builder.setNegativeButton("Dismiss",(dialog, which) -> {


            dialog.cancel();

        });
        builder.create().show();*/
    }


    // TODO: Here we will implement the follow and unfollow feature, we will call it Stranger and Strangered
    boolean unstranger = false;
    public void accountclick(View view) {


        if(strangerid.equals(fireuser.getUid())){
            Toast.makeText(this, "Sorry, You can not Unstranger yourself", Toast.LENGTH_SHORT).show();
        }else {

            unstrangerclick();
        }



    }

    public void viewsocialmedia(){


        // Display List in am AlertDialog

        showsocialdialog();


      /*  accountlist = new ListView(StrangerProfile.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            accountlist.setDivider(getDrawable(R.drawable.linechangable));
        }
        accountlist.setAdapter(accountadapter);

        TextView textView = new TextView(StrangerProfile.this);
        textView.setTypeface(Typeface.DEFAULT);
        textView.setHeight(30);
        textView.setTextSize(14f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            textView.setTextColor(getColor(R.color.dcblack));

        }
        textView.setGravity(Gravity.CENTER);
        textView.setText("Stranger Social Media Accounts");


        AlertDialog.Builder builder = new AlertDialog.Builder(StrangerProfile.this);
        builder.setView(accountlist);
        builder.setCustomTitle(textView);
        builder.setNegativeButton("Dismiss",(dialog, which) -> {
            dialog.cancel();

        });
        builder.create().show();
*/


    }

    public void call(View view) {

        // phone numbeer

        Toast.makeText(this, "Call", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+strangerUser.getPhoneNumber()));
        startActivity(intent);

    }

    public void message(View view) {

       if(strangerid.equals(fireuser.getUid())){
           Toast.makeText(this, "Sorry, You can not message yourself", Toast.LENGTH_SHORT).show();
       }else {

           messagestranger();
       }

    }

    public boolean isPostContain(ArrayList<Post> postsi, Post posti){

        boolean check = false;
        // Algorithm to check if a value is in a list, if not, return false


        for (int i = 0; i < postsi.size(); i++) {

            // if the user find any matching id, it will break.
            if ( postsi.get(i).getDate() == posti.getDate()) {
                check = true;
                break;
            }

            // once the loop has reached the end and it still did noy find a matching id, return false
            if (i == postsi.size() - 1 && postsi.get(i).getDate() == posti.getDate()) {

                check = false;

            }
        }
        return check;
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stranger_profile, menu);
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

    public void showinterestdialog(){

        showdialog = new Dialog(this);
        showdialog.setContentView(R.layout.dialog_interest_list);
        ListView  listView = (ListView)showdialog.findViewById(R.id.dialoginterestlist);
        CardView leftclick = (CardView)showdialog.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)showdialog.findViewById(R.id.rightclick);
        TextView lefttext = (TextView)showdialog.findViewById(R.id.lefttext);
        ImageView cancel = (ImageView)showdialog.findViewById(R.id.requestcancel);

        cancel.setOnClickListener((cc) ->{
            showdialog.dismiss();
        });


        listView.setAdapter(arrayAdapter);
        showdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showdialog.show();

        // check if the unstranger is true
        if(unstranger){
            leftclick.setCardBackgroundColor(0xffffffff);
            leftclick.setBackgroundColor(0xffffffff);
            lefttext.setTextColor(0xdd00aaff);
            lefttext.setText(getString(R.string.unstrangered));

        }

        leftclick.setOnClickListener((lcv) ->{

            unstrangerclick();

            if(unstranger){
                leftclick.setCardBackgroundColor(0xffffffff);
                leftclick.setBackgroundColor(0xffffffff);
                lefttext.setTextColor(0xdd00aaff);
                lefttext.setText(getString(R.string.unstrangered));

            }



        });

        rightclick.setOnClickListener((rcv) ->{
            messagestranger();
        });




    }
    public void showsocialdialog(){

        showdialog = new Dialog(this);
        showdialog.setContentView(R.layout.dialog_social_handle);
        ListView  listView = (ListView)showdialog.findViewById(R.id.dialogsocialhandle);
        CardView leftclick = (CardView)showdialog.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)showdialog.findViewById(R.id.rightclick);
        TextView lefttext = (TextView)showdialog.findViewById(R.id.lefttext);
        ImageView cancel = (ImageView)showdialog.findViewById(R.id.requestcancel);

        cancel.setOnClickListener((cc) ->{
            showdialog.dismiss();
        });

        listView.setAdapter(accountadapter);
        showdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showdialog.show();

        // check if the unstranger is true
        if(unstranger){
            leftclick.setCardBackgroundColor(0xffffffff);
            leftclick.setBackgroundColor(0xffffffff);
            lefttext.setTextColor(0xdd00aaff);
            lefttext.setText(getString(R.string.unstrangered));

        }

        leftclick.setOnClickListener((llcv) ->{
            unstrangerclick();


            if(unstranger){
                leftclick.setCardBackgroundColor(0xffffffff);
                leftclick.setBackgroundColor(0xffffffff);
                lefttext.setTextColor(0xdd00aaff);
                lefttext.setText(getString(R.string.unstrangered));

            }
        });

        rightclick.setOnClickListener((rccv) ->{
            messagestranger();
        });




    }

    public void unstrangerclick(){
        if(unstranger){
            // Here the current user has Strangered the Stranger
            strangerprofileaccountclick.setCardBackgroundColor(0xdd00aaff);
            strangerprofileaccountclick.setBackgroundColor(0xdd00aaff);
            strangerproflieaccounttext.setText(getString(R.string.unstranger));
            strangerproflieaccounttext.setTextColor(Color.WHITE);

            friendsReferences.child(strangerUser.getUserid()).removeValue().addOnSuccessListener(vv -> Toast.makeText(this, "You have Strangered the Stranger", Toast.LENGTH_SHORT).show());
            strangersReference.child(currentUser.getUserid()).removeValue().addOnSuccessListener(vvv -> Toast.makeText(this, "Strangered", Toast.LENGTH_SHORT).show());

            unstranger = false;
        }else{

            // if the user click on Unstranger, we will upload the stranger User.class to current user Friends node
            // and then we will upload the currentUser User.class to the Strangers node of the Stranger

            strangerprofileaccountclick.setCardBackgroundColor(0xffffffff);
            strangerprofileaccountclick.setBackgroundColor(0xffffffff);
            strangerproflieaccounttext.setTextColor(0xdd00aaff);
            strangerproflieaccounttext.setText(getString(R.string.unstrangered));

            friendsReferences.child(strangerUser.getUserid()).setValue(strangerUser).addOnSuccessListener(vv -> Toast.makeText(this, "You have UnStrangered the Stranger", Toast.LENGTH_SHORT).show());
            strangersReference.child(currentUser.getUserid()).setValue(currentUser).addOnSuccessListener(vvc -> Toast.makeText(this, "UnStrangered", Toast.LENGTH_SHORT).show());

            unstranger = true;
        }
    }

    public void messagestranger(){

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.strangerid),strangerid);
        Intent intent = new Intent(this, ChattingSpot.class);
        intent.putExtra(getString(R.string.strangerid),bundle);
        startActivity(intent);
    }






    // Profile Sheet post display

    public void displaypost(int position){

        int videocount = 0;
        Post post = posts.get(position);
        String filetype = post.getType();

        if(post.getPostid() == null)
            return;

        // Implement the constant views which are capture and the clickable[Reply, Call, Stranger Profile, more]


        likeref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.post)).child(post.getPostid()).child(getString(R.string.like));


        FirebaseDatabase.getInstance().getReference().child(getString(R.string.post)).child(post.getPostid()).child(getString(R.string.like)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                likecount.setText(String.valueOf(dataSnapshot.getChildrenCount()));

                if(dataSnapshot.hasChild(fireuser.getUid())){

                    likeimage.setImageResource(R.drawable.likefill);
                    hasChild = true;

                }else{
                    likeimage.setImageResource(R.drawable.likeempty);
                    hasChild = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        like.setOnClickListener(lcv ->{

            likewave.setBoolean(true);
            if(hasChild){

                likeimage.setImageResource(R.drawable.likeempty);
                hasChild = false;
                new android.os.Handler().postDelayed(() -> likeref.child(fireuser.getUid()).removeValue(),500);


            }else{


                likeimage.setImageResource(R.drawable.likefill);
                hasChild =true;
                new android.os.Handler().postDelayed(() -> likeref.child(fireuser.getUid()).setValue(fireuser.getUid()) ,500);


            }
        });


        // implement comment

        FirebaseDatabase.getInstance().getReference().child(getString(R.string.post)).child(post.getPostid()).child(getString(R.string.comment)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentcount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        comment.setOnClickListener(ccv ->{

            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.showcomment),post.getPostid());
            Intent intent = new Intent(getString(R.string.showcomment));
            intent.putExtra(getString(R.string.showcomment),bundle);
            sendBroadcast(intent);


        });

        reply.setOnClickListener((rv) ->{
            // TODO: Sent a Chat Messsage to the Stranger of this Post
            showreplydialog(post,filetype);
        });


        call.setOnClickListener((cv) ->{
            //TODO: Send the Stranger Phone Number to the call default application
            Toast.makeText(this, "Call", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+post.getPhonenum()));
            startActivity(intent);



        });
        strangerprofile.setOnClickListener((sv) ->{
            //TODO: Open the Stranger Profile Activity
            Toast.makeText(this, "Stranger Profile", Toast.LENGTH_SHORT).show();

            if(post.getUserid().equalsIgnoreCase(fireuser.getUid())){

                Bundle bundlee = new Bundle();
                bundlee.putInt("tab",4);
                Intent intente = new Intent(this,Stranger.class);
                intente.putExtra("tab",bundlee);
                startActivity(intente);
            }else {

                Intent intent = new Intent(this, StrangerProfile.class);
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.strangerid), post.getUserid());
                Bundle activityname = new Bundle();
                activityname.putString(getString(R.string.nameofactivity), getString(R.string.strangeract));
                intent.putExtra(getString(R.string.strangerid), bundle);
                intent.putExtra(getString(R.string.nameofactivity), activityname);
                startActivity(intent);


            }


        });




        postmore.setOnClickListener((pv) ->{

            //TODO: Implement a PopMenu here to allow the User to download the content of the Post. Whether Video, Image or
            // TODO: Audio(Not Conclusive)
            if(filetype.equals(getString(R.string.image))){

                menuModel.donwloadimage(postmore,post.getFileurl(),post.getCapture());

            }else if(filetype.equals(getString(R.string.video))){
                menuModel.donwloadvideo(postmore,post.getFileurl(),post.getCapture());

            }else{
                Toast.makeText(StrangerProfile.this, "You can not download this content", Toast.LENGTH_SHORT).show();
            }
        });

        postvideobutton.setOnClickListener((pvb) ->{

            // Play and Pause button control

            if(!prepared){


                if(filetype.equalsIgnoreCase(this.getString(R.string.video))){

                    playmedia(post.getFileurl());

                    postvideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {

                            prepared = true;

                        }
                    });


                    postvideothumbnail.setVisibility(View.GONE);
                    // playmedia(post.getFileurl());
                    postvideoview.start();

                    postvideobutton.setImageResource(R.drawable.pause_bottomsmall);



                }
                count = 1;
            }else {

                if (isPlaying()) {

                    postvideothumbnail.setVisibility(View.GONE);
                    pauseMedia();
                    postvideobutton.setImageResource(R.drawable.play_buttonsmall);
                    // prepared = false;
                } else {
                    postvideothumbnail.setVisibility(View.GONE);
                    resumeMedia();
                    postvideobutton.setImageResource(R.drawable.pause_bottomsmall);

                }
            }





        });
        postvideolay.setOnClickListener((pvl) ->{

            if(count == 0){
                videocontrollay.setVisibility(View.VISIBLE);
                count = 1;
            }else{
                videocontrollay.setVisibility(View.GONE);
                count=0;
            }
        });
        postcapture.setText(post.getCapture()); // The Capture text content



        //Get the File type to give direction on how to display the post


        if(filetype.equalsIgnoreCase(this.getString(R.string.image))){

            //TODO: Here we display the Image post

            // Let make visible those that need to be visible
            postmedia.setVisibility(View.VISIBLE);
            postimage.setVisibility(View.VISIBLE);

            //Let make Invisible those that need to be invisible
            postvideolay.setVisibility(View.GONE);
            posttextlay.setVisibility(View.GONE);

            //Let View the Image on the Imageview
            Glide.with(this).load(Uri.parse(post.getFileurl())).asBitmap().into(postimage);




        }else if(filetype.equalsIgnoreCase(this.getString(R.string.video))){
            //TODO: Here we display the Video post

            // Let Make visible those that need to be visible
            postmedia.setVisibility(View.VISIBLE);
            postvideolay.setVisibility(View.VISIBLE);
            postvideothumbnail.setVisibility(View.VISIBLE);
            videocontrollay.setVisibility(View.VISIBLE);

            //let make invisible those that need to be invisible
            postimage.setVisibility(View.GONE);
            posttextlay.setVisibility(View.GONE);

            // Let render the Video in the videoview

            try {
                Glide.with(this).load(Uri.parse(post.getVideothumbnailurl())).placeholder(R.mipmap.videoplaceh).into(postvideothumbnail);

            } catch (Exception e) {
                Glide.with(this).load(R.mipmap.videoplaceh).into(postvideothumbnail);
            }




        }else if(filetype.equalsIgnoreCase(this.getString(R.string.text))){
            //TODO: Here we display the text post

            //Let make visible those that need to be visible
            postmedia.setVisibility(View.VISIBLE);
            posttextlay.setVisibility(View.VISIBLE);

            //Let make invisible those that need to be invisible
            postimage.setVisibility(View.GONE);
            postvideolay.setVisibility(View.GONE);

            // Let implement the Text Post
            if(post.getPapertype().equalsIgnoreCase(this.getString(R.string.whiteback))){

                // White paper background
                posttextback.setBackgroundResource(R.mipmap.whitepaperback);
            }else{

                // Vintage paper background
                posttextback.setBackgroundResource(R.mipmap.vintagepaper);
            }

            posttext.setText(post.getTextmessage());


        }else{

            //TODO: Here we display only the capture because the file type is None
            //Let Invisible the PostMedia layout
            postmedia.setVisibility(View.GONE);

        }

    }

    public void setUpStrangerProfile(int position){

        try {
            // Here we collect the Stranger Profile Picture and Username
            Post post = posts.get(position);

            Glide.with(this).load(Uri.parse(post.getUserphotourl())).asBitmap().into(poststrangerimage);
            postname.setText(post.getUsername());
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong with the Post", Toast.LENGTH_SHORT).show();
        }


    }

    public boolean isPlaying(){
        boolean isPlaying=false;
        try {
            isPlaying = postvideoview.isPlaying();
        }catch (IllegalStateException e){
            postvideoview = (VideoView)findViewById(R.id.postvideoview);
            isPlaying = postvideoview.isPlaying();
        }catch (Exception e){
            postvideoview = (VideoView)findViewById(R.id.postvideoview);
        }


        return isPlaying;
    }


    public void playmedia(String data) {

        try {
            Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();

            //postvideoview.start();


        } catch (IllegalArgumentException e) {


            Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();

        } catch (Exception e) {
            Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.requestFocus();
            // postvideoview.start();


        }

        postvideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        if(percent < 100){

                            videoprogressbar.setVisibility(View.VISIBLE);

                        }else{

                            videoprogressbar.setVisibility(View.INVISIBLE);

                        }
                    }
                });
            }
        });



    }

    public void playmedia(Uri uri) {

        try {
            //Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.start();


        } catch (IllegalArgumentException e) {

        } catch (Exception e) {
            // Uri uri = Uri.parse(data);
            postvideoview.setVideoURI(uri);
            postvideoview.start();


        }
    }

    public void pauseMedia(){

        if (postvideoview.isPlaying()) {
            postvideoview.pause();
            resumePosition = postvideoview.getCurrentPosition();
        }

        //Intent intent = new Intent("pause");
        //sendBroadcast(intent);


    }

    public void resumeMedia(){

        if (!postvideoview.isPlaying()) {
            postvideoview.seekTo(resumePosition);
            postvideoview.start();
        }



    }




    BroadcastReceiver postreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getBundleExtra(context.getString(R.string.postclick));
            if(bundle != null){

                int postion = bundle.getInt(context.getString(R.string.postclick));
                displaypost(postion);
                setUpStrangerProfile(postion);
                bottomsheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

        }
    };

    public void registerpostclick(){

        IntentFilter intentFilter = new IntentFilter(getString(R.string.postclick));
       registerReceiver(postreceiver,intentFilter);

    }

    // show the dialog
    Dialog sd;
    public void showreplydialog(Post post, String filetype){
        sd = new Dialog(this);
        sd.setContentView(R.layout.dialog_reply_chat);
        sd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sd.show();

        CardView leftclick = (CardView)sd.findViewById(R.id.leftclick);
        CardView rightclick = (CardView)sd.findViewById(R.id.rightclick);
        ImageView cancel = (ImageView)sd.findViewById(R.id.requestcancel);
        EditText chatype = (EditText)sd.findViewById(R.id.chattype);

        cancel.setOnClickListener(c -> sd.dismiss());

        rightclick.setOnClickListener(rcv ->{
            sendreply(post,filetype,sd,chatype);


        });



    }


    public void sendreply(Post post, String filetype, Dialog sd, EditText chatype){

        Date date = new Date();
        Chat chat= new Chat();
        chat.setReceiverid(post.getUserid());
        chat.setSenderid(fireuser.getUid());
        chat.setDate(date.getTime());
        chat.setChatstatus(getString(R.string.delivered));


        if(filetype.equalsIgnoreCase(getString(R.string.text))){

            chat.setFiletype(getString(R.string.chatfiletype));
            chat.setMessage(post.getTextmessage());

            DatabaseReference chatting = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                    .child(fireuser.getUid()).child(getString(R.string.chat)).child(post.getUserid()).push();

            String key = chatting.getKey() != null ? chatting.getKey() : "hbfbbshsbasdg";


            DatabaseReference strangerchatReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                    .child(post.getUserid()).child(getString(R.string.chat)).child(fireuser.getUid())
                    .child(key);


            chat.setChatid(chatting.getKey());


            chatting.setValue(chat)
                    .addOnSuccessListener(vvvv ->{

                        chat.setMessage(chatype.getText().toString());


                        DatabaseReference chating =FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                                .child(fireuser.getUid()).child(getString(R.string.chat)).child(post.getUserid()).push();

                        String keys = chating.getKey() != null ? chating.getKey() : "hbfbbshsbasdg";


                        DatabaseReference strangerchatReferences = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                                .child(post.getUserid()).child(getString(R.string.chat)).child(fireuser.getUid())
                                .child(keys);


                        chat.setChatid(chating.getKey());
                        chating.setValue(chat)
                                .addOnSuccessListener(vvv -> {Toast.makeText(this, "Replied", Toast.LENGTH_SHORT).show(); sd.dismiss();});

                        strangerchatReferences.setValue(chat);

                    });

            strangerchatReference.setValue(chat);

        }else if(filetype.equalsIgnoreCase(getString(R.string.none))){

            chat.setFiletype(getString(R.string.chatfiletype));
            chat.setMessage(post.getCapture());

            DatabaseReference chatss = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                    .child(fireuser.getUid()).child(getString(R.string.chat)).child(post.getUserid()).push();

            String key = chatss.getKey() != null ? chatss.getKey() : "hbfbbshsbasdg";


            DatabaseReference strangerchatReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                    .child(post.getUserid()).child(getString(R.string.chat)).child(fireuser.getUid())
                    .child(key);


            chat.setChatid(chatss.getKey());
            chatss.setValue(chat)
                    .addOnSuccessListener(vvvv ->{

                        chat.setMessage(chatype.getText().toString());


                        DatabaseReference chattsin = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                                .child(fireuser.getUid()).child(getString(R.string.chat)).child(post.getUserid()).push();

                        String keys = chattsin.getKey() != null ? chattsin.getKey() : "hbfbbshsbasdg";


                        DatabaseReference strangerchatReferences = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                                .child(post.getUserid()).child(getString(R.string.chat)).child(fireuser.getUid())
                                .child(keys);


                        chat.setChatid(chattsin.getKey());
                        chattsin.setValue(chat)
                                .addOnSuccessListener(vvv -> {Toast.makeText(this, "Replied", Toast.LENGTH_SHORT).show();sd.dismiss();});

                        strangerchatReferences.setValue(chat);

                    });

            strangerchatReference.setValue(chat);



        }else if(filetype.equalsIgnoreCase(getString(R.string.image))){


            chat.setFiletype(getString(R.string.imagefiletype));
            chat.setMessage(chatype.getText().toString());
            chat.setFileuri(post.getFileurl());

            DatabaseReference chating = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                    .child(fireuser.getUid()).child(getString(R.string.chat)).child(post.getUserid()).push();

            String key = chating.getKey() != null ? chating.getKey() : "hbfbbshsbasdg";


            DatabaseReference strangerchatReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                    .child(post.getUserid()).child(getString(R.string.chat)).child(fireuser.getUid())
                    .child(key);



            chat.setChatid(chating.getKey());
            chating.setValue(chat)
                    .addOnSuccessListener(vvv -> {Toast.makeText(this, "Replied", Toast.LENGTH_SHORT).show();sd.dismiss();});

            strangerchatReference.setValue(chat);


        }else if(filetype.equalsIgnoreCase(getString(R.string.video))){

            chat.setFiletype(getString(R.string.videofiletype));
            chat.setMessage(chatype.getText().toString());
            chat.setFileuri(post.getFileurl());

            DatabaseReference chating = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                    .child(fireuser.getUid()).child(getString(R.string.chat)).child(post.getUserid()).push();


            String key = chating.getKey() != null ? chating.getKey() : "hbfbbshsbasdg";


            DatabaseReference strangerchatReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user))
                    .child(post.getUserid()).child(getString(R.string.chat)).child(fireuser.getUid())
                    .child(key);



            chat.setChatid(chating.getKey());
            chating.setValue(chat)
                    .addOnSuccessListener(vvv -> {Toast.makeText(this, "Replied", Toast.LENGTH_SHORT).show();
                        sd.dismiss();});

            strangerchatReference.setValue(chat);

        }


    }


    BroadcastReceiver showcomment = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getBundleExtra(context.getString(R.string.showcomment));
            if(bundle != null){

                String postid = bundle.getString(context.getString(R.string.showcomment));
                commentype.setFocusable(true);
                openComment.openComment(postid);

            }


        }
    };

    public void registercomment(){
        IntentFilter intentFilter = new IntentFilter(getString(R.string.showcomment));
        registerReceiver(showcomment,intentFilter);

    }


    BroadcastReceiver activityresult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getBundleExtra(context.getString(R.string.addfile));
            if(bundle != null){

                int addfile = bundle.getInt(context.getString(R.string.addfile));
                if(addfile == 10){
                    // take photo

                    Intent intenty = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        startActivityForResult(Intent.createChooser(intenty,"Select Camera Source"),addfile);
                    } catch (Exception e) {
                        Toast.makeText(context, "You do not have any Camera Source on this Device", Toast.LENGTH_SHORT).show();
                    }


                }else if(addfile == 15){

                    // Take video

                    Intent intentt = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    try {
                        startActivityForResult(Intent.createChooser(intentt,"Select Camera Source"),addfile);
                    } catch (Exception e) {
                        Toast.makeText(context, "You do not have any Camera Source on this Device", Toast.LENGTH_SHORT).show();
                    }

                }

                else if(addfile == 20){
                    // add file

                    Intent intente  = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intente.setType("*/*");
                    try {
                        startActivityForResult(Intent.createChooser(intente,"Select File Manager"),addfile);

                    } catch (Exception e) {
                        Toast.makeText(context, "No App can perform this task", Toast.LENGTH_SHORT).show();
                    }

                }

            }


        }
    };

    public void registerbroadcast(){

        IntentFilter intentFilter = new IntentFilter(getString(R.string.broadcastactivity));
        registerReceiver(activityresult,intentFilter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && resultCode == RESULT_OK){

            Uri uri = data.getData();

            if (uri != null) {
                if(requestCode == 10){
                    // take photo
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap image = (Bitmap) extras.get("data");
                        openComment.showdialogbitmap(image);
                    }



                }else if (requestCode == 15){
                    // take video
                    openComment.showdialogvideo(uri);

                }else if (requestCode == 20){
                    // add file
                    String path = uri.toString() != null ? uri.toString() : "unknown";

                    if(path.contains("image")){


                        openComment.showdialogimage(uri);


                    } else if(path.contains("video")){

                        openComment.showdialogvideo(uri);

                    } else if(path.contains("audio")){

                        openComment.showdialogaudio(uri);


                    } else {


                        openComment.showdialogfile(uri);

                    }



                }
            }else{

                if(requestCode == 10){
                    // take photo
                    // openComment.showdialogimage(uri);

                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap image = (Bitmap) extras.get("data");
                        openComment.showdialogbitmap(image);
                    }



                }
            }


        }

    }
}
