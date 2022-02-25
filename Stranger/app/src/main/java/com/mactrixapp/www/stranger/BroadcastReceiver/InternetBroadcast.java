package com.mactrixapp.www.stranger.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mactrixapp.www.stranger.Service.InterestService;

public class InternetBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        // start the Interest Service here
        context.startService(new Intent(context,InterestService.class));



    }
}
