package com.mactrixapp.www.stranger.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class InternetService extends Service {

    // TODO: There is mistake here, this should be a Broadcast receiver not a service

    public InternetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
