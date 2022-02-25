package com.mactrixapp.www.stranger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputType;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mactrixapp.www.stranger.Model.User;

import java.util.Set;

public class SignUp extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseAuth userAuth;
    DatabaseReference userReference;
    FirebaseUser user;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmpassword;
    private TextView errorinput;
    private User userspot;
    SharedPref onceclickPref;
    private RelativeLayout progresslay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //SharedPreference
        onceclickPref = new SharedPref(this,"onceclick");
        progresslay = (RelativeLayout)findViewById(R.id.progresslay);



        // The Firebase Auth and Database

        userAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user));





        // The Edittext
        username = (EditText)findViewById(R.id.usernameentry);
        email = (EditText)findViewById(R.id.emailentry);
        password = (EditText)findViewById(R.id.passwordentry);
        confirmpassword = (EditText)findViewById(R.id.confirmentry);
        errorinput = (TextView)findViewById(R.id.errorinput);


        confirmpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if(!password.getText().toString().contains(confirmpassword.getText().toString())){
                    errorinput.setVisibility(View.VISIBLE);
                }else{
                    errorinput.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





















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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
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

    public void signup(View view) {



        if(!email.getText().toString().isEmpty() && !confirmpassword.getText().toString().isEmpty()){


            Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show();
            onceclickPref.setInt(1);
            progresslay.setVisibility(View.VISIBLE);







            userAuth.createUserWithEmailAndPassword(email.getText().toString(),confirmpassword.getText().toString())
                    .addOnSuccessListener((authResult) -> {

                        // Get User Info and store it inf Firebase
                        user = authResult.getUser();
                        userspot = new User(user.getUid(),username.getText().toString(),email.getText().toString());
                        userReference.child(user.getUid()).setValue(userspot);
                        // Store user Info in Device Sharedpreference



                        if(onceclickPref.getInt() == 1){

                            startActivity(new Intent(SignUp.this, UserInfo.class));
                            finish();

                        }
                        onceclickPref.setInt(0);



                    }).addOnFailureListener((e) ->{
                        progresslay.setVisibility(View.GONE);
                        Toast.makeText(this, "Something Went Wrong, Please Try Again.", Toast.LENGTH_SHORT).show();});


        }else{
            progresslay.setVisibility(View.GONE);
            Toast.makeText(this, "Please Fill up the Required Fields..", Toast.LENGTH_SHORT).show();
        }





    }

    public void showpassword(View view) {

        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);


    }

    public void showconfrim(View view) {

        confirmpassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public void login(View view) {

        startActivity(new Intent(SignUp.this,Login.class));

    }
}
