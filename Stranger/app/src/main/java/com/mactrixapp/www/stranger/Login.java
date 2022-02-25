package com.mactrixapp.www.stranger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Model.User;

import java.util.Objects;

public class Login extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 21232;
    private static final int RequestSignInCode = 859;
    private EditText email;
    private EditText password;
    private CheckBox keeplogin;
    SharedPref keeploginPref;
    SharedPref loginonceclick;
    FirebaseAuth userAuth;
    DatabaseReference userReference;
    private FirebaseUser fireuser;

    User user;
    private SharedPref emailPref;
    private SharedPref passPref;
    private RelativeLayout progresslay;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        keeploginPref = new SharedPref(this,getString(R.string.keeplogin));
        loginonceclick = new SharedPref(this,getString(R.string.loginonceclick));
        emailPref = new SharedPref(this,getString(R.string.emailpref));
        passPref = new SharedPref(this,getString(R.string.passpref));

        email = (EditText)findViewById(R.id.emailentry);
        password = (EditText)findViewById(R.id.passwordentry);
        keeplogin = (CheckBox)findViewById(R.id.keeplogincheck);
        progresslay = (RelativeLayout)findViewById(R.id.progresslay);

        // google login configuration
        // Configure Google Sign In

        String webClientID = "819616124487-fa6k9npae4bnbt2ugqbtjnp5mnhcn38v.apps.googleusercontent.com";

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();




       mGoogleSignInClient = GoogleSignIn.getClient(this,gso);


        googleApiClient = new GoogleApiClient.Builder(Login.this)

                .enableAutoManage(Login.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();






        keeplogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {


                    // Check whether the email and password entry is filled before checking the keep login
                    if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                        keeploginPref.setBoolean(true);

                    }else{
                        Toast.makeText(Login.this, "Please fill up the required fields..", Toast.LENGTH_SHORT).show();
                        keeplogin.setChecked(false);

                    }

                } else {

                    keeploginPref.setBoolean(false);
                }
            }
        });




        userAuth = FirebaseAuth.getInstance();

        userReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user));





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



    public void login(View view) {

        if(!email.getText().toString().isEmpty()  && !password.getText().toString().isEmpty()){



            loginonceclick.setInt(1);
            progresslay.setVisibility(View.VISIBLE);


            // Sigin with email and password.
            userAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener((authResult )-> {


                // initializing the firebasse user with the signin creditials
                fireuser = authResult.getUser();

                // checking if the user has registered into the database.
                updateUI(fireuser);

            }).addOnFailureListener((authResult) -> {

                progresslay.setVisibility(View.GONE);
                Toast.makeText(this, "Wrong Email and Password, Please try again.", Toast.LENGTH_SHORT).show();});


        }else{

            progresslay.setVisibility(View.GONE);
            Toast.makeText(this, "Please fill the required fields.", Toast.LENGTH_SHORT).show();

        }

        Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();

    }

    private void updateUI(FirebaseUser fireuser) {

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if(dataSnapshot.hasChild(fireuser.getUid())){

                   if(loginonceclick.getInt() == 1) {


                       user = dataSnapshot.child(fireuser.getUid()).getValue(User.class);

                       if (user != null) {
                           if (user.getFullname() == null) {

                               // Start the Userinfo Activity so that user can fill the required info
                               Toast.makeText(Login.this, "You have not fill up the User Info form.", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(Login.this, UserInfo.class));
                               finish();
                           } else {

                               // Collect and store the user Email and Password in Phone SharedPreference
                               emailPref.setText(email.getText().toString());
                               passPref.setText(password.getText().toString());

                               // Start the Stranger Activity
                               Toast.makeText(Login.this, "You have successfully Logged in.", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(Login.this, Stranger.class));
                               //startActivity(new Intent(Login.this,Reputation.class));
                               finish();

                           }
                       }
                   }

                   loginonceclick.setInt(0);



               }else{

                   // User has not registered
                   Toast.makeText(Login.this, "You have not Signed Up, Please do so.\nClick on SignUp button above", Toast.LENGTH_SHORT).show();
                   //User  userspot = new User(fireuser.getUid(),fireuser.getDisplayName(),fireuser.getEmail());
                   //userReference.child(fireuser.getUid()).setValue(userspot);

               }


           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }


    public void googleLogin(){


        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);



    }

    // Sign In function Starts From Here.
    public void userSignInMethod(){

        // Passing Google Api Client into Intent.
        Intent authIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);

        startActivityForResult(authIntent, RequestSignInCode);
    }


    public void signup(View view) {

        startActivity(new Intent(Login.this,SignUp.class));

    }

    public void showpassword(View view) {

        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public void forgotpassowrd(View view) {

        if(!email.getText().toString().isEmpty()){

            userAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener((v) -> Toast.makeText(this, "Reset Email has been Sent..", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener((v) -> Toast.makeText(this, "Something when Wrong, Please try again..", Toast.LENGTH_SHORT).show());


        }else{

            Toast.makeText(this, "Please Enter your Email. Thank You", Toast.LENGTH_SHORT).show();

        }


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
        getMenuInflater().inflate(R.menu.login, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);





        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Toast.makeText(this, "RC_SIGN_IN", Toast.LENGTH_SHORT).show();


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                Toast.makeText(this, "task called", Toast.LENGTH_SHORT).show();

                task.addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {

                        GoogleSignInAccount account =  googleSignInAccount;

                        if (account != null) {
                            Toast.makeText(Login.this, "non null GOOGLE", Toast.LENGTH_SHORT).show();
                            firebaseAuthWithGoogle(account);
                        }

                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



               /* GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    Toast.makeText(this, "non null GOOGLE", Toast.LENGTH_SHORT).show();
                    firebaseAuthWithGoogle(account);
                }*/
            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately

                // ...
                Toast.makeText(this, "Google error occurred: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == RequestSignInCode){



            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (googleSignInResult.isSuccess()){

                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

                if (googleSignInAccount != null) {
                    firebaseAuthWithGoogle(googleSignInAccount);
                }
            }else{

                Toast.makeText(this, "Google result was not successfull", Toast.LENGTH_SHORT).show();
            }


        }



    }




    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Toast.makeText(this, "firebase Google called", Toast.LENGTH_SHORT).show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        userAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = userAuth.getCurrentUser();
                            Toast.makeText(Login.this, "Google User got", Toast.LENGTH_SHORT).show();
                            updateGUI(user);


                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });




    }


    private void updateGUI(FirebaseUser fireuser){


        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(fireuser.getUid())){



                        user = dataSnapshot.child(fireuser.getUid()).getValue(User.class);

                        if (user != null) {
                            if (user.getInterest() == null) {

                                // Start the Userinfo Activity so that user can fill the required info
                                Toast.makeText(Login.this, "You have not fill up the User Info form.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, UserInfo.class));
                                finish();
                            } else {

                                // Collect and store the user Email and Password in Phone SharedPreference
                                emailPref.setText(fireuser.getEmail());
                                passPref.setText(password.getText().toString());

                                // Start the Stranger Activity
                                Toast.makeText(Login.this, "You have successfully Logged in.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, Stranger.class));
                                //startActivity(new Intent(Login.this,Reputation.class));
                                finish();

                            }
                        }



                }else{

                    // User has not registered
                    Toast.makeText(Login.this, "You have Signed Up, Please fill up the User Info", Toast.LENGTH_SHORT).show();
                    User  userspot = new User(fireuser.getUid(),fireuser.getDisplayName(),fireuser.getEmail());
                    userspot.setFullname(fireuser.getDisplayName());
                    userspot.setPhoneNumber(fireuser.getPhoneNumber());
                    userspot.setPhotoUrl(Objects.requireNonNull(fireuser.getPhotoUrl()).toString());

                    userReference.child(fireuser.getUid()).setValue(userspot).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            startActivity(new Intent(Login.this, UserInfo.class));
                            finish();

                        }
                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    public void googlelogin(View view) {

        googleLogin();
        //userSignInMethod();


    }
}
