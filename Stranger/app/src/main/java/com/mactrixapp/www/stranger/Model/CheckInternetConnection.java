package com.mactrixapp.www.stranger.Model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class CheckInternetConnection {

    Context context;

    public CheckInternetConnection(Context context) {
        this.context = context;
    }

    public boolean checkInternetConnectivity(){

        boolean connected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        try {

            connected= connectivityManager.getActiveNetworkInfo().isConnected();

        }catch (NullPointerException e){

            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                connected = true;
            }


        }



        return connected;

    }
}
