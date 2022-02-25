package com.mactrixapp.www.stranger.AsyncTaskPack;

import android.os.AsyncTask;

import com.mactrixapp.www.stranger.Interface.AsyncFace;

public class AsyncClass extends AsyncTask<Void,Object,Integer> {


    AsyncFace asyncFace;
    public AsyncClass(AsyncFace asyncFace) {

        this.asyncFace = asyncFace;
    }

    @Override
    protected Integer doInBackground(Void... voids) {


        asyncFace.async();

        return 0;
    }
}
