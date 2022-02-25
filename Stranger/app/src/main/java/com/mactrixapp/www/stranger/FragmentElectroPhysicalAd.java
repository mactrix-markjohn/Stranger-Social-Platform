package com.mactrixapp.www.stranger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mactrixapp.www.stranger.Adapters.EpAdsAdapter;
import com.mactrixapp.www.stranger.AsyncTaskPack.AsyncClass;
import com.mactrixapp.www.stranger.Model.EPAds;
import com.mactrixapp.www.stranger.Model.Notification;

import java.util.ArrayList;

public class FragmentElectroPhysicalAd extends Fragment {


    private TextView epacount;
    private ListView epadlist;
    private FloatingActionButton epadcreate;
    Context context;
    private DatabaseReference epadReference;
    private FirebaseUser currentuser;
    private ArrayList<EPAds> epadarray;
    private EpAdsAdapter epAdsAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_electrophy,container,false);

        epacount = (TextView)v.findViewById(R.id.epacount);
        epadlist = (ListView)v.findViewById(R.id.epadlist);
        epadcreate = (FloatingActionButton)v.findViewById(R.id.epadcreate);


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        epadReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.epads));
        epadarray = new ArrayList<>();


        epAdsAdapter = new EpAdsAdapter(context,epadarray);
        epadlist.setAdapter(epAdsAdapter);



        new AsyncClass(this::epadListener).execute();
        //new Thread(this::epadListener).start();
       // epadListener();

        epadcreate.setOnClickListener(epc -> context.startActivity(new Intent(context,NewEPAds.class)));




    }

    public void epadListener(){

        epadReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    EPAds epAds = snapshot.getValue(EPAds.class);

                    if(epAds != null){

                        if (epAds.getSenderid().equalsIgnoreCase(currentuser.getUid())) {

                            if (!isUserContain(epadarray,epAds)) {
                                epadarray.add(epAds);
                            }

                        }



                    }


                }



                (new Stranger()).runOnUiThread(() -> {

                    epAdsAdapter.notifyDataSetChanged();
                    epacount.setText(String.valueOf(epAdsAdapter.getCount()));
                });
                //epAdsAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public boolean isUserContain(ArrayList<EPAds> userlist, EPAds uservalue){
        boolean check = false;


        for (int i = 0; i< userlist.size(); i++){


            if(userlist.get(i).getDate() == uservalue.getDate()){
                check = true;
                break;
            }

            if (i == userlist.size()-1 && !(userlist.get(i).getDate() == uservalue.getDate())){
                check = false;
            }



        }



        return check ;
    }
}
