package com.mactrixapp.www.stranger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.graphics.TypefaceCompatApi26Impl;
import android.support.v4.graphics.TypefaceCompatUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
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
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.FontAdapter;
import com.mactrixapp.www.stranger.Adapters.LetterBackAdapter;
import com.mactrixapp.www.stranger.Model.FontModel;
import com.mactrixapp.www.stranger.Model.Letter;
import com.mactrixapp.www.stranger.Model.LetterBack;
import com.mactrixapp.www.stranger.Model.User;

import java.util.ArrayList;
import java.util.Date;

public class StrangerLetter extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<LetterBack> letterBacks;
    ArrayList<FontModel> fontModels;
    private EditText letterpaper;
    private ImageView letterbackimage;
    private Letter letter;
    User currentuser;
    FirebaseUser fireuser;
    private FontModel fontModel;
    private DatabaseReference letterReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_realm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fireuser = FirebaseAuth.getInstance().getCurrentUser();

        addLetterBack(); // add the Letter Background to the Arraylist, to be displayed in the Grid Select
        addfont(); // Add the fonts
        letter = new Letter();
        letterpaper = (EditText)findViewById(R.id.letterpaper);
        letterbackimage = (ImageView)findViewById(R.id.letterbackimage);


        FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(fireuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    
                if(dataSnapshot.getValue(User.class) != null ){
                    
                    currentuser = dataSnapshot.getValue(User.class);
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        letterReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.letter));


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


    public void addLetterBack(){
        letterBacks = new ArrayList<>();

        letterBacks.add(new LetterBack("NobLine",R.mipmap.nobline,getString(R.string.nobline)));
        letterBacks.add(new LetterBack("Flowerline",R.mipmap.flowerline,getString(R.string.flowerline)));
        letterBacks.add(new LetterBack("Abstract Star",R.mipmap.abstractstar,getString(R.string.abstractstar)));
        letterBacks.add(new LetterBack("Blueletter",R.mipmap.blueletter,getString(R.string.blueletter)));
        letterBacks.add(new LetterBack("Flowerpaper",R.mipmap.flowerpaper,getString(R.string.flowerpaper)));
        letterBacks.add(new LetterBack("Flowerup",R.mipmap.flowerup,getString(R.string.flowerup)));
        letterBacks.add(new LetterBack("Lambline",R.mipmap.lambline,getString(R.string.lambline)));
        letterBacks.add(new LetterBack("Maturepaper",R.mipmap.maturepaper,getString(R.string.maturepaper)));
        letterBacks.add(new LetterBack("Oldpaper",R.mipmap.oldpaper,getString(R.string.oldpaper)));
        letterBacks.add(new LetterBack("Ovalline",R.mipmap.ovalline,getString(R.string.ovalline)));
        letterBacks.add(new LetterBack("Pinkbold",R.mipmap.pinkbold,getString(R.string.pinkbold)));
        letterBacks.add(new LetterBack("Pinkwhite",R.mipmap.pinkwhite,getString(R.string.pinkwhite)));
        letterBacks.add(new LetterBack("Purplefly",R.mipmap.purplefly,getString(R.string.purplefly)));
        letterBacks.add(new LetterBack("Simpleline",R.mipmap.simpleline,getString(R.string.simpleline)));
        letterBacks.add(new LetterBack("Xmasred",R.mipmap.xmasred,getString(R.string.xmasred)));
        letterBacks.add(new LetterBack("Xmastree",R.mipmap.xmastree,getString(R.string.xmastree)));
        letterBacks.add(new LetterBack("Letterpaper",R.mipmap.letterpaper,getString(R.string.letterpaper)));
        letterBacks.add(new LetterBack("Lineborder",R.mipmap.lineborder,getString(R.string.lineborder)));
        letterBacks.add(new LetterBack("Paperpink",R.mipmap.paperpink,getString(R.string.paperpink)));
        letterBacks.add(new LetterBack("Pinklove",R.mipmap.pinklove,getString(R.string.pinklove)));
        letterBacks.add(new LetterBack("Purplelittle",R.mipmap.purplelittle,getString(R.string.puperlittle)));
        letterBacks.add(new LetterBack("Roseborder",R.mipmap.roseborder,getString(R.string.roseborder)));
        letterBacks.add(new LetterBack("Rosepaper",R.mipmap.rosepaper,getString(R.string.rosepaper)));
        letterBacks.add(new LetterBack("Whiterose",R.mipmap.whiterose,getString(R.string.whiterose)));
        letterBacks.add(new LetterBack("Wineline",R.mipmap.wineline,getString(R.string.wineline)));
        letterBacks.add(new LetterBack("Roseframe",R.mipmap.roseframe,getString(R.string.roseframe)));
        
        letterBack = new LetterBack("Simpleline",R.mipmap.simpleline,getString(R.string.simpleline));



    }

    Dialog sgd;
    LetterBack letterBack;
    public void showgridDialog(){
        sgd = new Dialog(this);
        sgd.setContentView(R.layout.dialog_select_letter);
        sgd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sgd.show();

        ImageView cancel = (ImageView)sgd.findViewById(R.id.lettercancel);
        GridView lettergrid = (GridView)sgd.findViewById(R.id.lettergrid);

        cancel.setOnClickListener(lc -> sgd.dismiss()); // Dismiss the dialog

        // Set the Adapter of Letter Back in the Gridview
        LetterBackAdapter letterBackAdapter = new LetterBackAdapter(letterBacks, this);
        lettergrid.setAdapter(letterBackAdapter);

        lettergrid.setOnItemClickListener((parent, view, position, id) -> {

            // The item is click, perform the necessary things
            letterBack = letterBacks.get(position);
            letterbackimage.setImageResource(letterBack.getBackres());
            sgd.dismiss();



        });






    }

    public void addfont(){
        fontModels = new ArrayList<>();

        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"alluraregular.otf"),0,getString(R.string.alluraregular),"Allura Regular"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"bernfash.ttf"),0,getString(R.string.bernfash),"Bern Fash"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"caviardreams.ttf"),0,getString(R.string.caviardreams),"Caviar Dreams"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"dancingscriptregular.otf"),0,getString(R.string.dancingscriptregular),"Dancing Script Regular"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"e111viva.ttf"),0,getString(R.string.e111viva),"E111Viva"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"humn.ttf"),0,getString(R.string.humn),"Humn"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"kabeln.ttf"),0,getString(R.string.kabeln),"Kabeln"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"kaushanscriptregular.otf"),0,getString(R.string.kaushanscriptregular),"Kaushan Script Regular"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"ralewayregular.ttf"),0,getString(R.string.ralewayregular),"Raleway Regular"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"stacc222.ttf"),0,getString(R.string.stacc222),"Stacc222"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"typoupri.ttf"),0,getString(R.string.typoupri),"Typo Upri"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"windsong.ttf"),0,getString(R.string.windsong),"Wind Song"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"lithogrl.ttf"),0,getString(R.string.lithogrl),"Lithogrl"));
        fontModels.add(new FontModel(Typeface.createFromAsset(getAssets(),"ozhandrm.ttf"),0,getString(R.string.ozhandrm),"Ozhandrm"));
        
        fontModel = new FontModel(Typeface.createFromAsset(getAssets(),"kaushanscriptregular.otf"),0,getString(R.string.kaushanscriptregular),"Kaushan Script Regular");
        
    }

    Dialog sfd;
    public void showfontDialog() {
        sfd = new Dialog(this);
        sfd.setContentView(R.layout.dialog_select_font);
        sfd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sfd.show();

        ImageView cancel = (ImageView) sfd.findViewById(R.id.lettercancel);
        ListView fontlist = (ListView) sfd.findViewById(R.id.fontlist);

        cancel.setOnClickListener(lc -> sfd.dismiss()); // Dismiss the dialog

        // Set the Adapter of Letter Back in the Gridview
        FontAdapter fontAdapter = new FontAdapter(fontModels,this);
        fontlist.setAdapter(fontAdapter);
        fontlist.setOnItemClickListener((parent, view, position, id) -> {

            // The item is click, perform the necessary things
            fontModel = fontModels.get(position);
            letterpaper.setTypeface(fontModel.getTypeface());
            sfd.dismiss();


        });
    }


    public void back(View view) {

        onBackPressed();


    }

    public void cancel(View view) {

        letterpaper.setText("");
    }

    public void font(View view) {

        showfontDialog();


    }

    public void send(View view) {

        showsenDialog();

    }

    public void changepaper(View view) {
        showgridDialog();

    }

    Dialog senDialog;
    public void showsenDialog(){

        senDialog = new Dialog(this);
        senDialog.setContentView(R.layout.send_letter_dialog);
        senDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        senDialog.show();

        //All Switch and entry

       Switch nameswitch = (Switch) senDialog.findViewById(R.id.nameswitch);
       EditText nameentry = (EditText) senDialog.findViewById(R.id.nameentry);
       Switch professionswitch = (Switch) senDialog.findViewById(R.id.professioswitch);
       EditText professionentry = (EditText) senDialog.findViewById(R.id.professionentry);
       Switch instituteswitch = (Switch) senDialog.findViewById(R.id.instituteswitch);
       EditText instituteentry = (EditText) senDialog.findViewById(R.id.instituteentry);
       Switch interestswitch = (Switch) senDialog.findViewById(R.id.interestswitch);
       EditText interesteentry = (EditText) senDialog.findViewById(R.id.interestentry);
       Switch workstateswitch = (Switch) senDialog.findViewById(R.id.workstateswitch);
       TextView workstateentry = (TextView) senDialog.findViewById(R.id.workstateentry);
       Switch genderswitch = (Switch) senDialog.findViewById(R.id.genderswitch);
       TextView genderentry = (TextView) senDialog.findViewById(R.id.genderentry);
       Switch locationswitch = (Switch) senDialog.findViewById(R.id.locationswitch);
       EditText locationentry = (EditText) senDialog.findViewById(R.id.locationentry);
       ImageView cancel = (ImageView)senDialog.findViewById(R.id.cancel);
       cancel.setOnClickListener(cv -> senDialog.dismiss());
       CardView sentletter = (CardView)senDialog.findViewById(R.id.sendletter);

       nameswitch.setOnCheckedChangeListener(checklisten(nameentry));
       professionswitch.setOnCheckedChangeListener(checklisten(professionentry));
       instituteswitch.setOnCheckedChangeListener(checklisten(instituteentry));
       interestswitch.setOnCheckedChangeListener(checklisten(interesteentry));
       workstateswitch.setOnCheckedChangeListener(checklisten(workstateentry));
       genderswitch.setOnCheckedChangeListener(checklisten(genderentry));
       locationswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
           if (isChecked) {

               AlertDialog.Builder builder = new AlertDialog.Builder(StrangerLetter.this);
               builder.setTitle("Location choice");
               builder.setIcon(R.drawable.map_marker);
               builder.setMultiChoiceItems(new String[]{"All Location", "Particular Location"}, new boolean[]{false, false}, (dialog, which, isCheckeda) -> {
                   switch (which) {
                       case 0:

                           locationentry.setText(getString(R.string.alllocation));
                           locationentry.setEnabled(false);
                           dialog.dismiss();
                           break;


                       case 1:

                           locationentry.setEnabled(true);
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


           } else {


               locationentry.setEnabled(false);
               
               // if the location is around me
               letter.setLocationstate(getString(R.string.aroundme));
           }
       });

       // implement the entry textchange listener
        nameentry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                letter.setName(s.toString());

            }
        });
        professionentry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                letter.setProfesssion(s.toString());
            }
        });
        interesteentry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                letter.setInterests(s.toString());
            }
        });
        instituteentry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                letter.setInstitute(s.toString());
            }
        });
        workstateentry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                letter.setWorkstate(s.toString());
            }
        });
        genderentry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                letter.setGender(s.toString());
            }
        });
        locationentry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                letter.setLocationstate(s.toString());
            }
        });
        letter.setLocationstate(getString(R.string.aroundme));
        sentletter.setOnClickListener(slv -> processSendfeatures());




    }

    public void processSendfeatures(){
        letter.setDate(new Date().getTime());
        letter.setLetterbackres(letterBack.getBackres());
        letter.setTypefaceid(fontModel.getFontid());
        letter.setMessage(letterpaper.getText().toString());
        letter.setSenderfullname(currentuser.getFullname());
        letter.setSenderlat(currentuser.getLatitude());
        letter.setSenderlng(currentuser.getLongitude());
        letter.setSenderid(currentuser.getUserid());
        letter.setSenderphotourl(currentuser.getPhotoUrl());
        letter.setSenderusername(currentuser.getUsername());
        
        DatabaseReference letterref = letterReference.push();
        letter.setLetterid(letterref.getKey());
        letterref.setValue(letter).addOnSuccessListener(vviod -> Toast.makeText(this, "Letter is Sent. Good luck with your Adventure", Toast.LENGTH_LONG).show());
        senDialog.dismiss();
        Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show();

    }

    public CompoundButton.OnCheckedChangeListener checklisten(TextView entry){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    entry.setEnabled(true);

                }else{

                    entry.setEnabled(false);
                }

            }
        };

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
        getMenuInflater().inflate(R.menu.interest_realm, menu);
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


    public void sendletter(View view) {

        showsenDialog();
    }
}
