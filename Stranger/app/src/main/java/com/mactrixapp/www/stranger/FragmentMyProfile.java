package com.mactrixapp.www.stranger;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.mactrixapp.www.stranger.Model.MenuModel;
import com.mactrixapp.www.stranger.Model.OpenComment;
import com.mactrixapp.www.stranger.Model.Post;
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentMyProfile extends Fragment {

    Context context;

    ListProfileApdaper listProfileApdaper;
    GridProfileAdapter gridProfileAdapter;

    CircleImageView profileimage;

    TextView profileusername;
    TextView profilefullname;
    TextView profieprofession;
    TextView profileinstitute;
    TextView profilemarrital;

    ListView profileinterestlist;
    ListView profilepostlist;

    GridView profilepostgrid;

    BottomNavigationView profilebottomview;

    CardView profileeditcard;
    FirebaseUser fireuser;
    DatabaseReference userReference;
    DatabaseReference postReference;
    private ArrayList<Post> posts;
    private DataSnapshot lastSnapShot;
    private TextView postcountnum;
    private RelativeLayout strangerprofessional;
    private RelativeLayout strangerstudent;
    private TextView profileprofessions;
    private TextView profileinstitutes;
    private TextView profilemarritals;
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
    View v;
    private ImageView postdown;
    private ImageView editphoto;
    private MenuModel menuModel;

    private ImageView likeimage;
    private TextView likecount;
    private LinearLayout comment;
    LinearLayout call;
    private LinearLayout like;
    private DatabaseReference likeref;
    private boolean hasChild = false;
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

    // The Firebase Database and Storage to get the user Info


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        registerpostclick();
        menuModel = new MenuModel(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_my_profile,container,false);
        // initialize the views
        profileimage = (CircleImageView)v.findViewById(R.id.profileimage);
        profileusername = (TextView)v.findViewById(R.id.profileusername);
        profilefullname = (TextView)v.findViewById(R.id.profilefullname);
        profieprofession = (TextView)v.findViewById(R.id.profieprofession);
        profileinstitute = (TextView)v.findViewById(R.id.profileinstitute);
        profilemarrital = (TextView)v.findViewById(R.id.profilemarrital);
        profileinterestlist = (ListView)v.findViewById(R.id.profileinterestlist);
        profilepostlist = (ListView)v.findViewById(R.id.profilepostlist);
        profilepostgrid = (GridView)v.findViewById(R.id.profilepostgrid);
        profilebottomview = (BottomNavigationView)v.findViewById(R.id.profilebottomview);
        profileeditcard = (CardView)v.findViewById(R.id.profileeditcard);
        postcountnum =  (TextView)v.findViewById(R.id.postcountnum);

        likewave = new SharedPref(context,context.getString(R.string.likewave));

        // The Student and Professional Layout and Views initialization
        strangerprofessional = (RelativeLayout)v.findViewById(R.id.strangerprofessional);
        strangerstudent = (RelativeLayout)v.findViewById(R.id.strangerstudent);
        profileprofessions = (TextView)v.findViewById(R.id.profileprofessions);
        profileinstitutes = (TextView)v.findViewById(R.id.profileinstitudes);
        profilemarritals = (TextView)v.findViewById(R.id.profilemarritals);

        // new views
        orign = (TextView)v.findViewById(R.id.origin);
        country = (TextView)v.findViewById(R.id.country);
        origns = (TextView)v.findViewById(R.id.origins);
        countrys = (TextView)v.findViewById(R.id.countrys);
        editphoto = (ImageView)v.findViewById(R.id.editphoto);

        // Bottom View
        viewsheet = v.findViewById(R.id.profilesheet);
        bottomsheet = BottomSheetBehavior.from(viewsheet);

        // Intialize all the views
        postmore = (ImageView) v.findViewById(R.id.postmore);
        postimage = (ImageView) v.findViewById(R.id.postimage);
        postvideobutton = (ImageView) v.findViewById(R.id.postvideobutton);
        postvideothumbnail = (ImageView) v.findViewById(R.id.postvideothumnail);
        poststrangerimage = (CircleImageView) v.findViewById(R.id.poststrangerimage);
        postname = (TextView) v.findViewById(R.id.postname);
        posttext = (TextView) v.findViewById(R.id.posttext);
        postcapture = (TextView) v.findViewById(R.id.postcapture);
        postvideoview = (VideoView) v.findViewById(R.id.postvideoview);
        postvideolay = (RelativeLayout) v.findViewById(R.id.postvideolay);
        videocontrollay = (RelativeLayout) v.findViewById(R.id.videocontrollay);
        postmedia = (RelativeLayout) v.findViewById(R.id.postmedia);
        posttextlay = (RelativeLayout) v.findViewById(R.id.posttextlay);
        posttextback = (RelativeLayout) v.findViewById(R.id.posttextback);
        playpasuecard = (CardView) v.findViewById(R.id.playpausecard);
        videoprogressbar = (ProgressBar)v.findViewById(R.id.postvideoprogress);
        postdown = (ImageView)v.findViewById(R.id.postdown);
        postdown.setOnClickListener(pdv -> bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED));
        profileeditcard.setOnClickListener(pec -> startActivity(new Intent(context,EditProfile.class)));
        editphoto.setOnClickListener(ep -> startActivity(new Intent(context,EditProfile.class)));




        // post extra views

        call = (LinearLayout)v.findViewById(R.id.call);
        like = (LinearLayout)v.findViewById(R.id.like);
        likecount = (TextView)v.findViewById(R.id.likecount);
        likeimage = (ImageView)v.findViewById(R.id.likeimage);
        comment = (LinearLayout)v.findViewById(R.id.comment);
        commentcount = (TextView)v.findViewById(R.id.commentcount);

        // comment bottomsheet
        // initialize the bottomsheet

        bottomview = v.findViewById(R.id.commentsheet);
        sheetBehavior = BottomSheetBehavior.from(bottomview);
        commentdown = (ImageView)v.findViewById(R.id.commentdown);
        commentlist = (ListView)v.findViewById(R.id.commentlist);
        addmedia = (CardView)v.findViewById(R.id.chatmedia);
        addfile = (CardView)v.findViewById(R.id.chatfile);
        commentype = (EditText)v.findViewById(R.id.chattype);
        commentype.setEnabled(false);
        commentype.setVisibility(View.GONE);

        sendcomment = (CardView)v.findViewById(R.id.chatsend);


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


        openComment = new OpenComment(context,commentdown,commentlist,addmedia,addfile,commentype,sendcomment,sheetBehavior);





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


        return v;
    }

    String interest;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize the firebase instances

        fireuser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.user)).child(fireuser.getUid());

        openComment.setUpComment();
        registercomment();
        registerbroadcast();

        //new Thread(this::getUserInfo).start();
        new AsyncClass(this::getUserInfo).execute();

        //getUserInfo(); // Retrieving all the Neccessary User info

        profilebottomview.setOnNavigationItemSelectedListener((item) -> {

            switch (item.getItemId()){

                case R.id.gridview:
                    profilepostgrid.setVisibility(View.VISIBLE);
                    profilepostlist.setVisibility(View.GONE);

                    break;
                case R.id.listview:
                    profilepostlist.setVisibility(View.VISIBLE);
                    profilepostgrid.setVisibility(View.GONE);
                    break;
                case R.id.addpost:

                    context.startActivity(new Intent(context,NewStrangerPost.class));
                    break;


            }

            return true;}); // BottomNavigation implementation

        posts = new ArrayList<>();
        postReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.post));

        new AsyncClass(this::postreference).execute();
       // new Thread(this::postreference).start();

        //postreference();
    }

    private void postreference() {
        if(posts.isEmpty()){


            postReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    // If the list empty, all the post in the Database is added to the ArrayList
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Post post = snapshot.getValue(Post.class);

                        if (post != null && post.getUserid().equalsIgnoreCase(fireuser.getUid())) {
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

                    (new Stranger()).runOnUiThread(() -> {

                        listProfileApdaper = new ListProfileApdaper(context,posts);
                        profilepostlist.setAdapter(listProfileApdaper);

                        gridProfileAdapter = new GridProfileAdapter(context,posts);
                        profilepostgrid.setAdapter(gridProfileAdapter);

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

                    if (post != null && post.getUserid().equalsIgnoreCase(fireuser.getUid())) {
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

                    (new Stranger()).runOnUiThread(() -> {


                        listProfileApdaper = new ListProfileApdaper(context,posts);
                        profilepostlist.setAdapter(listProfileApdaper);

                        gridProfileAdapter = new GridProfileAdapter(context,posts);
                        profilepostgrid.setAdapter(gridProfileAdapter);

                        postcountnum.setText(String.valueOf(posts.size()));


                    });



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }

    private void getUserInfo() {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    try {
                        Glide.with(context).load(Uri.parse(user.getPhotoUrl())).asBitmap().into(profileimage);
                    } catch (Exception e) {
                        profileimage.setImageResource(R.mipmap.profileavatar);
                    }
                    profileusername.setText(user.getUsername());
                    profilefullname.setText(user.getFullname());
                    profieprofession.setText(user.getProfession());
                    profileprofessions.setText(user.getProfession());
                    profileinstitute.setText(user.getInstitute());
                    profileinstitutes.setText(user.getInstitute());
                    profilemarrital.setText(user.getStatus());
                    profilemarritals.setText(user.getStatus());
                    orign.setText(user.getOrigin());
                    country.setText(user.getCountry());
                    origns.setText(user.getOrigin());
                    countrys.setText(user.getCountry());
                    interest = user.getInterest();

                    // Check if the user is a student or professional
                    if (user.getWorkstate() != null) {
                        String workstate = user.getWorkstate();
                        if(workstate.equalsIgnoreCase(context.getString(R.string.student))){
                            strangerstudent.setVisibility(View.VISIBLE);
                            strangerprofessional.setVisibility(View.GONE);
                        }else{

                            strangerprofessional.setVisibility(View.VISIBLE);
                            strangerstudent.setVisibility(View.GONE);

                        }
                    }


                    // Extracting all the Interest seperated by comma and storing it in a list
                    ArrayList<String> interestarray = new ArrayList<>();

                    if(interest != null) {
                        splitcustom(interestarray);
                    }

                    (new Stranger()).runOnUiThread(() -> {

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,R.layout.interestlistlayout,R.id.interesttext,interestarray);
                        profileinterestlist.setAdapter(arrayAdapter);


                    });






                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void splitcustom(ArrayList<String> interestarray) {
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
    }


    // Profile Sheet post display

    public void displaypost(View v,int position){

        try {
            int videocount = 0;
            Post post = posts.get(position);


            String filetype = post.getType();

            // Implement the constant views which are capture and the clickable[Reply, Call, Stranger Profile, more]

            likeref = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.post)).child(post.getPostid() != null ? post.getPostid() : "jh4f44tf44742dhjkg4832").child(context.getString(R.string.like));


            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.post)).child(post.getPostid() != null ? post.getPostid() : " hdhdshcbchhdsids87fs8vxycs67").child(context.getString(R.string.like)).addValueEventListener(new ValueEventListener() {
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
                    new Handler().postDelayed(() -> likeref.child(fireuser.getUid()).removeValue(),500);


                }else{


                    likeimage.setImageResource(R.drawable.likefill);
                    hasChild =true;
                    new Handler().postDelayed(() -> likeref.child(fireuser.getUid()).setValue(fireuser.getUid()) ,500);


                }
            });


            // implement comment

            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.post)).child(post.getPostid() != null ? post.getPostid() : "hfhshasjkadud87fy6dshd673").child(context.getString(R.string.comment)).addValueEventListener(new ValueEventListener() {
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
                bundle.putString(context.getString(R.string.showcomment),post.getPostid());
                Intent intent = new Intent(context.getString(R.string.showcomment));
                intent.putExtra(context.getString(R.string.showcomment),bundle);
                context.sendBroadcast(intent);


            });

            call.setOnClickListener((cv) ->{
                //TODO: Send the Stranger Phone Number to the call default application
                Toast.makeText(context, "Call", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+post.getPhonenum()));
                context.startActivity(intent);



            });


            postmore.setOnClickListener((pv) ->{

                //TODO: Implement a PopMenu here to allow the User to download the content of the Post. Whether Video, Image or
                // TODO: Audio(Not Conclusive)
                if(filetype.equals(context.getString(R.string.image))){

                    menuModel.donwloadimage(postmore,post.getFileurl(),post.getCapture());

                }else if(filetype.equals(context.getString(R.string.video))){
                    menuModel.donwloadvideo(postmore,post.getFileurl(),post.getCapture());

                }else{
                    Toast.makeText(context, "You can not download this content", Toast.LENGTH_SHORT).show();
                }
            });

            postvideobutton.setOnClickListener((pvb) ->{

                // Play and Pause button control

                if(!prepared){


                    if(filetype.equalsIgnoreCase(context.getString(R.string.video))){

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

                    if (isPlaying(v)) {

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


            if(filetype.equalsIgnoreCase(context.getString(R.string.image))){

                //TODO: Here we display the Image post

                // Let make visible those that need to be visible
                postmedia.setVisibility(View.VISIBLE);
                postimage.setVisibility(View.VISIBLE);

                //Let make Invisible those that need to be invisible
                postvideolay.setVisibility(View.GONE);
                posttextlay.setVisibility(View.GONE);

                //Let View the Image on the Imageview
                Glide.with(context).load(Uri.parse(post.getFileurl())).asBitmap().into(postimage);




            }else if(filetype.equalsIgnoreCase(context.getString(R.string.video))){
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
                    Glide.with(context).load(Uri.parse(post.getVideothumbnailurl())).placeholder(R.mipmap.videoplaceh).into(postvideothumbnail);

                } catch (Exception e) {
                    Glide.with(context).load(R.mipmap.videoplaceh).into(postvideothumbnail);
                }




            }else if(filetype.equalsIgnoreCase(context.getString(R.string.text))){
                //TODO: Here we display the text post

                //Let make visible those that need to be visible
                postmedia.setVisibility(View.VISIBLE);
                posttextlay.setVisibility(View.VISIBLE);

                //Let make invisible those that need to be invisible
                postimage.setVisibility(View.GONE);
                postvideolay.setVisibility(View.GONE);

                // Let implement the Text Post
                if(post.getPapertype().equalsIgnoreCase(context.getString(R.string.whiteback))){

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
        } catch (Exception e) {
            Toast.makeText(context, "Sorry, something went wrong with this post", Toast.LENGTH_LONG).show();
        }

    }

    public void setUpStrangerProfile(int position){

        try {
            // Here we collect the Stranger Profile Picture and Username
            Post post = posts.get(position);

            Glide.with(context).load(Uri.parse(post.getUserphotourl())).asBitmap().into(poststrangerimage);
            postname.setText(post.getUsername());
        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong with Post", Toast.LENGTH_SHORT).show();
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

    public boolean isPlaying(View v){
        boolean isPlaying=false;
        try {
            isPlaying = postvideoview.isPlaying();
        }catch (IllegalStateException e){
            postvideoview = (VideoView)v.findViewById(R.id.postvideoview);
            isPlaying = postvideoview.isPlaying();
        }catch (Exception e){
            postvideoview = (VideoView)v.findViewById(R.id.postvideoview);
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


                     displaypost(v,postion);

                     setUpStrangerProfile(postion);
                     bottomsheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                 }

             }
         };

        public void registerpostclick(){

            IntentFilter intentFilter = new IntentFilter(context.getString(R.string.postclick));
            context.registerReceiver(postreceiver,intentFilter);

        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        IntentFilter intentFilter = new IntentFilter(context.getString(R.string.showcomment));
        context.registerReceiver(showcomment,intentFilter);

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

        IntentFilter intentFilter = new IntentFilter(context.getString(R.string.broadcastactivity));
        context.registerReceiver(activityresult,intentFilter);


    }

}
