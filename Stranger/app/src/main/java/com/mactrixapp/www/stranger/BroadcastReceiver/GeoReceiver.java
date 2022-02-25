package com.mactrixapp.www.stranger.BroadcastReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mactrixapp.www.stranger.EPADViewer;
import com.mactrixapp.www.stranger.R;
import com.mactrixapp.www.stranger.SQLLITE.GeoSQLLite;

import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class GeoReceiver extends BroadcastReceiver {

    private GeoSQLLite geoSQLLite;
    private Cursor cursor;
    private Location locationing;
    private String geoid;
    private String message;
    private String title;
    private String type;
    private String requestID;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        geoSQLLite = new GeoSQLLite(context,context.getString(R.string.epads),null,1);
        cursor = geoSQLLite.getAllGeofence();

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);


        if(event.hasError()){

            Toast.makeText(context, "Geofencing Event Error", Toast.LENGTH_SHORT).show();
            return;

        }

        int transition = event.getGeofenceTransition();
        List<Geofence> geofences = event.getTriggeringGeofences();
        locationing = event.getTriggeringLocation();

        for(int i= 0;i < geofences.size();i++){

           // String requestID;
            if(geofences.get(i).getRequestId() != null){
                requestID = geofences.get(i).getRequestId();
            }else{
                requestID = "EPADZone";
            }

            if(transition == Geofence.GEOFENCE_TRANSITION_DWELL){
                getDetails(cursor,requestID,context);

            } else if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {

                getDetails(cursor,requestID,context);
            }else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT){
                getDetails(cursor,requestID,context);

            }
        }

    }


    public void getDetails(Cursor cursor, String requestID, Context context){

        if(cursor != null){

            for(int c=0; c < cursor.getCount();c++){

                cursor.moveToPosition(c);
                geoid = cursor.getString(cursor.getColumnIndex(GeoSQLLite.GEOID));
                message = cursor.getString(cursor.getColumnIndex(GeoSQLLite.MESSAGE));
                title = cursor.getString(cursor.getColumnIndex(GeoSQLLite.TITLE));
                type = cursor.getString(cursor.getColumnIndex(GeoSQLLite.TYPE));

                if(requestID.equalsIgnoreCase(geoid)){

                    sendNotification(context,title,message,type);

                }

            }


        }
    }

    public void sendNotification(Context contexting,String title,String contene,String extra){


       /* if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){*/



            try {

                long[] vib = {1000,1100,1000,11000};

                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



                Notification.Builder builder = new Notification.Builder(contexting)
                        .setSmallIcon(R.drawable.about)
                        .setContentTitle(title)
                        .setContentText(contene)
                        .setVibrate(vib)
                        .setSound(sound)
                        .setOnlyAlertOnce(true);
                NotificationManager manager = (NotificationManager) contexting.getSystemService(Context.NOTIFICATION_SERVICE);

                Intent intent = new Intent(contexting, EPADViewer.class);
                Bundle bundle = new Bundle();
                bundle.putString(contexting.getString(R.string.epadsitem),requestID);
                intent.putExtra(contexting.getString(R.string.epadsitem),bundle);


                PendingIntent pendingIntent = PendingIntent.getActivity(contexting, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentIntent(pendingIntent);
                Notification notification = builder.build();

                if (manager != null) {
                    manager.notify(0, notification);
                }
            }catch (NullPointerException e){
                 Toast.makeText(contexting, "Cannot send Notification", Toast.LENGTH_SHORT).show();
            }


        /*}else{


            try {

                long[] vib = {1000,1100,1000,11000};

                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                RemoteViews expand = new RemoteViews(contexting.getPackageName(),R.layout.content_emergency_contacts);
                // expand.setImageViewResource(R.id.backexpand,R.mipmap.kidapp);
                expand.setTextViewText(R.id.fenceid,title);
                expand.setTextViewText(R.id.messagegeo,contene);
                expand.setTextViewText(R.id.advice,extra);
                // expand.setImageViewResource(R.id.iconkid,R.drawable.alarm);


                Icon icon = Icon.createWithResource(contexting,R.mipmap.strangeralone);
                Notification.Builder builder = new Notification.Builder(contexting)
                        .setSmallIcon(R.drawable.about)
                        .setContentTitle(title)
                        .setContentText(contene)
                        .setLargeIcon(icon)
                        .setVibrate(vib)
                        .setAutoCancel(true)
                        .setStyle(new Notification.DecoratedCustomViewStyle())
                        .setCustomBigContentView(expand)
                        .setSound(sound);
                NotificationManager manager = (NotificationManager) contexting.getSystemService(Context.NOTIFICATION_SERVICE);
                Intent intent = new Intent(contexting, EPADViewer.class);
                Bundle bundle = new Bundle();
                bundle.putString(contexting.getString(R.string.epadsitem),requestID);
                intent.putExtra(contexting.getString(R.string.epadsitem),bundle);

                PendingIntent pendingIntent = PendingIntent.getActivity(contexting, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentIntent(pendingIntent);
                Notification notification = builder.build();

                manager.notify(new Random().nextInt(), notification);
            }catch (NullPointerException e){
                // Toast.makeText(contexting, "Cannot send Notification", Toast.LENGTH_SHORT).show();
            }

        }*/


    }

}
