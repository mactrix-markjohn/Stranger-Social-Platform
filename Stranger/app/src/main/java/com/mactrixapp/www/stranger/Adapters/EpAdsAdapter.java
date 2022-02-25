package com.mactrixapp.www.stranger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.style.IconMarginSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.mactrixapp.www.stranger.EPADViewer;
import com.mactrixapp.www.stranger.Model.EPAds;
import com.mactrixapp.www.stranger.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EpAdsAdapter extends BaseAdapter {

    Context context;
    ArrayList<EPAds> epAdsArrayList;
    LayoutInflater inflater;

    public EpAdsAdapter(Context context, ArrayList<EPAds> epAdsArrayList) {
        this.context = context;
        this.epAdsArrayList = epAdsArrayList;
        inflater = LayoutInflater.from(context);
    }

    class EpAdHolder{


        private TextView epadate;
        private TextView epadtitle;
        private TextView epadmessage;
        private TextView epadvenue;
        private ImageView epadsign;

        public EpAdHolder(View v , int p){

            EPAds epAds = epAdsArrayList.get(p);

            epadtitle = (TextView)v.findViewById(R.id.epadtitle);
            epadmessage = (TextView)v.findViewById(R.id.epadmessage);
            epadvenue = (TextView)v.findViewById(R.id.epadaddress);
            epadsign = (ImageView)v.findViewById(R.id.epadsign);
            epadate = (TextView)v.findViewById(R.id.epadate);

            epadtitle.setText(epAds.getTitle());
            epadmessage.setText(epAds.getMessage());
            epadvenue.setText(epAds.getVenue());
            epadate.setText(new SimpleDateFormat("hh:mm a  E dd MMM, yyyy",Locale.getDefault()).format(new Date(epAds.getDate())));

            String filetype = epAds.getFiletype();

            if (filetype.equals(context.getString(R.string.none))) {

                epadsign.setImageResource(R.drawable.adselectro_blue);

            } else if (filetype.equals(context.getString(R.string.image))) {
                epadsign.setImageResource(R.drawable.photo_camera_blue);
            } else if (filetype.equals(context.getString(R.string.video))) {
                epadsign.setImageResource(R.drawable.video_camera_blue);
            } else if (filetype.equals(context.getString(R.string.audio))) {
                epadsign.setImageResource(R.drawable.music_headset_blue);
            } else if (filetype.equals(context.getString(R.string.other))) {

                epadsign.setImageResource(R.drawable.folder_blue);
            }


            v.setOnClickListener(epvc ->{

                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.epadsitem),epAds.getId());

                Intent intent = new Intent(context,EPADViewer.class);
                intent.putExtra(context.getString(R.string.epadsitem),bundle);
                context.startActivity(intent);



            });
            v.setOnLongClickListener(eplc ->{

                PopupMenu popupMenu = new PopupMenu(context,eplc);
                popupMenu.inflate(R.menu.notification_list);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(item -> {

                    switch (item.getItemId()){
                        case R.id.delete:
                            // delete the request from the Arraylist and Database by making it seen for the request receiver
                            epAdsArrayList.remove(p);
                            FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.epads)).child(epAds.getId()).removeValue().addOnSuccessListener(ssc ->
                                    Toast.makeText(context, "EP Ads has been deleted", Toast.LENGTH_SHORT).show());


                            break;
                    }


                    return false;
                });



                return true;});

        }

    }

    @Override
    public int getCount() {
        return epAdsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.epadlay,parent,false);
        }

        new EpAdHolder(convertView,position);




        return convertView;
    }
}
