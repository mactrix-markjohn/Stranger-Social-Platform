package com.mactrixapp.www.stranger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.PostListAdapter;
import com.mactrixapp.www.stranger.AsyncTaskPack.AsyncClass;
import com.mactrixapp.www.stranger.Model.CheckInternetConnection;
import com.mactrixapp.www.stranger.Model.OpenComment;
import com.mactrixapp.www.stranger.Model.Post;
import com.mactrixapp.www.stranger.Model.PostMore;
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FragmentStrangersPost extends Fragment {

    //TODO :: We will implement here, so get ready


    Context context;
    private FloatingActionButton newpost;
    private ListView postlist;
    DatabaseReference postReference;
    PostListAdapter postListAdapter;
    ArrayList<PostMore> posts;
    private FirebaseUser currentuser;
    private DatabaseReference userReference;
    private DatabaseReference friendsReference;
    private OpenComment openComment;
    private View bottomview;
    private BottomSheetBehavior<View> sheetBehavior;
    private ImageView commentdown;
    private ListView commentlist;
    private CardView addmedia;
    private CardView addfile;
    private EditText commentype;
    private CardView sendcomment;
    private SharedPref likewave;
    private long lastdate;
    private long firstdate;
    private int lastcount = 0;
    private int co;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_strangers_post,container,false);

        newpost = (FloatingActionButton)view.findViewById(R.id.newpost);
        postlist = (ListView)view.findViewById(R.id.postlist);
        likewave = new SharedPref(context,context.getString(R.string.likewave)); // shared preference for like
        likewave.setBoolean(false);


        // initialize the bottomsheet

        bottomview = view.findViewById(R.id.commentsheet);
        sheetBehavior = BottomSheetBehavior.from(bottomview);
        commentdown = (ImageView)view.findViewById(R.id.commentdown);
        commentlist = (ListView)view.findViewById(R.id.commentlist);
        addmedia = (CardView)view.findViewById(R.id.chatmedia);
        addfile = (CardView)view.findViewById(R.id.chatfile);
        commentype = (EditText)view.findViewById(R.id.chattype);
        commentype.setEnabled(false);
        commentype.setVisibility(View.GONE);
        sendcomment = (CardView)view.findViewById(R.id.chatsend);


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


        // the below class displays the comment layout, by taking the necessary views
        openComment = new OpenComment(context,commentdown,commentlist,addmedia,addfile,commentype,sendcomment,sheetBehavior);



        return view;


    }

    DataSnapshot lastSnapShot;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Initialize the Current User Firebase Database Node
        openComment.setUpComment(); // this set's up the comment layout
        registercomment();
        registerbroadcast();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(currentuser.getUid());
        friendsReference = userReference.child(getString(R.string.friends));


        newpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,NewStrangerPost.class));
            }
        });


        posts = new ArrayList<>();
        postReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.post));

        postListAdapter = new PostListAdapter(context, posts);
        postlist.setAdapter(postListAdapter);




       // new Thread(this::fillPost).start();

        new AsyncClass(this::fillPost).execute();

       // fillPost();


        // implement onScroll listener for the post listview.
        // if the scroll reaches the end more older post will be added
        // if the scroll scroll past the begining



        SharedPref topscroll = new SharedPref(context,"top");
        co = 0;
        topscroll.setInt(co);

        postlist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {




            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {



                if(view.getFirstVisiblePosition() == 0 ){

                    // top scroll
                    new AsyncClass(() -> filluppost()).execute();

                   /* co = topscroll.getInt();
                    co++;
                    topscroll.setInt(co);

                    Toast.makeText(context, "Top Scroll: "+co, Toast.LENGTH_SHORT).show();*/
                }


                if(view.getLastVisiblePosition()==(totalItemCount-1)){
                    //dosomething for buttom scroll
                    new AsyncClass(() -> fillbuttompost()).execute();

                }



                int diff = (view.getBottom() - (view.getHeight() + view.getScrollY()));

                if(diff == 0){





                }






                /*if (!view.canScrollVertically(1)) {
                    // bottom of scroll view


                    Toast.makeText(context, "Button Scroll", Toast.LENGTH_SHORT).show();
                    new AsyncClass(() -> fillbuttompost()).execute();
                    //fillbuttompost();
                }
                if (!view.canScrollVertically(-1)) {
                    // top of scroll view
                    co = topscroll.getInt();
                    co++;
                    topscroll.setInt(co);

                    Toast.makeText(context, "Top Scroll: "+co, Toast.LENGTH_SHORT).show();
                    new AsyncClass(() -> filluppost()).execute();
                }*/


            }
        });

    }

    private void filluppost(){



        if(!posts.isEmpty()){

            postReference.orderByChild(context.getString(R.string.date)).startAt(firstdate).limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Here we will check every post that comes in and see if the post is from current user or from his/her friends
                    // else the post will not be added

                    if (true) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            Post post = snapshot.getValue(Post.class);

                            if(post != null){


                                friendsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        // We check every post with every friend to make sure that it is from a friend

                                        // if the friend database is empty
                                        if (dataSnapshot.getChildrenCount() == 0) {


                                            if (currentuser.getUid().equalsIgnoreCase(post.getUserid())) {

                                                // if the user id matches either the  current user id when there is not friend in the list then add  post
                                                if (!isPostContain(posts, post)) {
                                                    posts.add(new PostMore(post, snapshot.getKey()));


                                                    firstdate = post.getDate();
                                                }


                                            }

                                            // this allows administrative post to be viewed

                                        }else if(context.getString(R.string.adminemail).equalsIgnoreCase(post.getUserid())){


                                            // if the user id matches either the  current user id when there is not friend in the list then add  post
                                            if (!isPostContain(posts, post)) {
                                                posts.add(new PostMore(post, snapshot.getKey()));


                                                firstdate = post.getDate();
                                            }



                                        } else {


                                            // this check all the users in the current user friends database. Once the post contains the friends ID
                                            // the post will be added

                                            for (DataSnapshot snapshoting : dataSnapshot.getChildren()) {
                                                User user = snapshoting.getValue(User.class);

                                                if (user != null) {

                                                    if (user.getUserid().equalsIgnoreCase(post.getUserid()) || currentuser.getUid().equalsIgnoreCase(post.getUserid())) {

                                                        // if the user id matches either the Friends id  or current user id then add  post
                                                        if (!isPostContain(posts, post)) {
                                                            posts.add(new PostMore(post, snapshot.getKey()));


                                                            firstdate = post.getDate();
                                                        }
                                                    }

                                                } else {
                                                    if (currentuser.getUid().equalsIgnoreCase(post.getUserid())) {

                                                        // if the user id matches either the  current user id when there is not friend in the list then add  post
                                                        if (!isPostContain(posts, post)) {
                                                            posts.add(new PostMore(post, snapshot.getKey()));


                                                            firstdate = post.getDate();
                                                        }
                                                    }
                                                }
                                            }

                                        }

                                        Collections.sort(posts, new Comparator<PostMore>() {
                                            @Override
                                            public int compare(PostMore o1, PostMore o2) {
                                                return String.valueOf(o2.getPost().getDate()).compareTo(String.valueOf(o1.getPost().getDate()));
                                            }
                                        });


                                        // update the listview with the added Post

                                        ((Activity) new Stranger()).runOnUiThread(() -> postListAdapter.notifyDataSetChanged());

                                        //postListAdapter.notifyDataSetChanged();



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                            }

                        }
                    }

                    likewave.setBoolean(false);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



    }



    private void fillbuttompost(){

        lastcount = 0;

        if(!posts.isEmpty()){

            postReference.orderByChild(context.getString(R.string.date)).endAt(lastdate).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Here we will check every post that comes in and see if the post is from current user or from his/her friends
                    // else the post will not be added

                    if (true) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            Post post = snapshot.getValue(Post.class);

                            if(post != null){


                                friendsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        // We check every post with every friend to make sure that it is from a friend

                                        // if the friend database is empty
                                        if (dataSnapshot.getChildrenCount() == 0) {


                                            if (currentuser.getUid().equalsIgnoreCase(post.getUserid())) {

                                                // if the user id matches either the  current user id when there is not friend in the list then add  post
                                                if (!isPostContain(posts, post)) {
                                                    posts.add(new PostMore(post, snapshot.getKey()));

                                                    getLastDate(post);

                                                }


                                            }

                                            // this allows administrative post to be viewed

                                        }else if(getString(R.string.adminemail).equalsIgnoreCase(post.getUserid())){


                                            // if the user id matches either the  current user id when there is not friend in the list then add  post
                                            if (!isPostContain(posts, post)) {
                                                posts.add(new PostMore(post, snapshot.getKey()));

                                                getLastDate(post);

                                            }



                                        } else {


                                            // this check all the users in the current user friends database. Once the post contains the friends ID
                                            // the post will be added

                                            for (DataSnapshot snapshoting : dataSnapshot.getChildren()) {
                                                User user = snapshoting.getValue(User.class);

                                                if (user != null) {

                                                    if (user.getUserid().equalsIgnoreCase(post.getUserid()) || currentuser.getUid().equalsIgnoreCase(post.getUserid())) {

                                                        // if the user id matches either the Friends id  or current user id then add  post
                                                        if (!isPostContain(posts, post)) {
                                                            posts.add(new PostMore(post, snapshot.getKey()));

                                                            getLastDate(post);

                                                        }
                                                    }

                                                } else {
                                                    if (currentuser.getUid().equalsIgnoreCase(post.getUserid())) {

                                                        // if the user id matches either the  current user id when there is not friend in the list then add  post
                                                        if (!isPostContain(posts, post)) {
                                                            posts.add(new PostMore(post, snapshot.getKey()));

                                                            getLastDate(post);

                                                        }
                                                    }
                                                }
                                            }

                                        }

                                        Collections.sort(posts, new Comparator<PostMore>() {
                                            @Override
                                            public int compare(PostMore o1, PostMore o2) {
                                                return String.valueOf(o2.getPost().getDate()).compareTo(String.valueOf(o1.getPost().getDate()));
                                            }
                                        });


                                        // update the listview with the added Post

                                        ((Activity) new Stranger()).runOnUiThread(() -> postListAdapter.notifyDataSetChanged());

                                        //postListAdapter.notifyDataSetChanged();



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                            }

                        }
                    }

                    likewave.setBoolean(false);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void getLastDate(Post post) {
        lastcount ++;

        if(lastcount == 1){

            lastdate = post.getDate();


        }
    }


    private void fillPost() {

        lastcount = 0;


        if(posts.isEmpty()){



            postReference.orderByChild(context.getString(R.string.date)).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!likewave.getBoolean()){
                    // If the list empty, all the post in the Database is added to the ArrayList


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Post post = snapshot.getValue(Post.class);

                        if (post != null) {

                            // Here we will check every post that comes in and see if the post is from current user or from his/her friends
                            // else the post will not be added
                            friendsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    // We check every post with every friend to make sure that it is from a friend


                                    if (dataSnapshot.getChildrenCount() == 0) {


                                        if (currentuser.getUid().equalsIgnoreCase(post.getUserid())) {

                                            // if the user id matches either the  current user id when there is not friend in the list then add  post
                                            if (!isPostContain(posts, post)) {
                                                posts.add(new PostMore(post, snapshot.getKey()));


                                                getLastDate(post);
                                                firstdate = post.getDate();


                                            }


                                        }

                                        // this allows administrative post to be viewed

                                    }else if(getString(R.string.adminemail).equalsIgnoreCase(post.getUserid())){


                                        // if the user id matches either the  current user id when there is not friend in the list then add  post
                                        if (!isPostContain(posts, post)) {
                                            posts.add(new PostMore(post, snapshot.getKey()));


                                            getLastDate(post);
                                            firstdate = post.getDate();
                                        }



                                    } else {


                                        for (DataSnapshot snapshoting : dataSnapshot.getChildren()) {
                                            User user = snapshoting.getValue(User.class);

                                            if (user != null) {

                                                if (user.getUserid().equalsIgnoreCase(post.getUserid()) || currentuser.getUid().equalsIgnoreCase(post.getUserid())) {

                                                    // if the user id matches either the Friends id  or current user id then add  post
                                                    if (!isPostContain(posts, post)) {
                                                        posts.add(new PostMore(post, snapshot.getKey()));

                                                        getLastDate(post);
                                                        firstdate = post.getDate();
                                                    }
                                                }

                                            } else {
                                                if (currentuser.getUid().equalsIgnoreCase(post.getUserid())) {

                                                    // if the user id matches either the  current user id when there is not friend in the list then add  post
                                                    if (!isPostContain(posts, post)) {
                                                        posts.add(new PostMore(post, snapshot.getKey()));

                                                        getLastDate(post);
                                                        firstdate = post.getDate();
                                                    }
                                                }
                                            }
                                        }

                                    }

                                    Collections.sort(posts, new Comparator<PostMore>() {
                                        @Override
                                        public int compare(PostMore o1, PostMore o2) {
                                            return String.valueOf(o2.getPost().getDate()).compareTo(String.valueOf(o1.getPost().getDate()));
                                        }
                                    });


                                    // update the listview with the added Post

                                    ((Activity) new Stranger()).runOnUiThread(() -> postListAdapter.notifyDataSetChanged());

                                    //postListAdapter.notifyDataSetChanged();



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                           /* if(currentuser.getUid().equalsIgnoreCase(post.getUserid())){

                                // if the user id matches either the  current user id when there is not friend in the list then add  post
                                posts.add(post);
                            }*/

                        }
                    }
                }

                likewave.setBoolean(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{

            postReference.orderByChild(context.getString(R.string.date)).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!likewave.getBoolean()){

                        // This Implementation get the last Added post, once the post is got it will add it to the
                        // ArrayList
                        for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                            lastSnapShot = snapShot;
                        }

                    // Here we will check every post that comes in and see if the post is from current user or from his/her friends
                    // else the post will not be added

                    Post post = lastSnapShot.getValue(Post.class);

                    if (post != null) {

                        friendsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getChildrenCount() == 0) {

                                    if (currentuser.getUid().equalsIgnoreCase(post.getUserid())) {
                                        // if the user id matches either the Friends id  or current user id then add  post
                                        if (!isPostContain(posts, post)) {
                                            posts.add(new PostMore(post, lastSnapShot.getKey()));

                                            getLastDate(post);
                                            firstdate = post.getDate();
                                        }
                                    }

                                } else {

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                                        User user = snapshot.getValue(User.class);
                                        if (user != null) {


                                            if (user.getUserid().equalsIgnoreCase(post.getUserid()) || currentuser.getUid().equalsIgnoreCase(post.getUserid())) {
                                                // if the user id matches either the Friends id  or current user id then add  post
                                                if (!isPostContain(posts, post)) {
                                                    posts.add(new PostMore(post, lastSnapShot.getKey()));

                                                    getLastDate(post);
                                                    firstdate = post.getDate();
                                                }
                                            }
                                        } else {

                                            if (currentuser.getUid().equalsIgnoreCase(post.getUserid())) {
                                                // if the user id matches either the Friends id  or current user id then add  post
                                                if (!isPostContain(posts, post)) {
                                                    posts.add(new PostMore(post, lastSnapShot.getKey()));

                                                    getLastDate(post);
                                                    firstdate = post.getDate();
                                                }
                                            }
                                        }
                                    }


                                }

                                Collections.sort(posts, new Comparator<PostMore>() {
                                    @Override
                                    public int compare(PostMore o1, PostMore o2) {
                                        return String.valueOf(o2.getPost().getDate()).compareTo(String.valueOf(o2.getPost().getDate()));
                                    }
                                });


                                // update the listview with the added Post
                                ((Activity)new Stranger()).runOnUiThread(() -> postListAdapter.notifyDataSetChanged());
                                //postListAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                       /* if(currentuser.getUid().equalsIgnoreCase(post.getUserid())){
                            // if the user id matches either the Friends id  or current user id then add  post
                            posts.add(post);
                        }*/

                    }


                }

                likewave.setBoolean(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }

    public boolean isPostContain(ArrayList<PostMore> postsi, Post posti){

        boolean check = false;
        // Algorithm to check if a value is in a list, if not, return false


            for (int i = 0; i < postsi.size(); i++) {

                // if the user find any matching id, it will break.
                if ( postsi.get(i).getPost().getDate() == posti.getDate()) {
                    check = true;
                    break;
                }

                // once the loop has reached the end and it still did noy find a matching id, return false
                if (i == postsi.size() - 1 && postsi.get(i).getPost().getDate() == posti.getDate()) {

                    check = false;

                }
            }
            return check;
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && resultCode == RESULT_OK){

            Uri uri = data.getData();

            if (uri != null) {
                if(requestCode == 10){
                    // take photo
                   // openComment.showdialogimage(uri);

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
