package com.mactrixapp.www.stranger.Model;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;

public class DownloadModel {

    private File filingother;
    private File filingaudio;
    private File filingvideo;
    private File filingimage;
    Context context;
    File filing;

    public DownloadModel(Context context) {

        this.context = context;
        File[] duofile =context.getExternalFilesDirs(null);
        filing = new File(Environment.getExternalStorageDirectory(),"/Stranger");
        filing.mkdir();
        filingimage = new File(filing,"/image");
        filingimage.mkdir();
        filingvideo = new File(filing,"/video");
        filingvideo.mkdir();
        filingaudio = new File(filing,"/audio");
        filingaudio.mkdir();
        filingother = new File(filing,"/other");
        filingother.mkdir();



    }


    public void downloadimage(String fileuri){

        try {
            File file = File.createTempFile("strangeImage",".jpg",filingimage);
            FirebaseStorage.getInstance().getReferenceFromUrl(fileuri).getFile(file).addOnSuccessListener(ss ->
                    Toast.makeText(context, "Download Successfull\n"+file.getAbsolutePath(), Toast.LENGTH_LONG).show());
        } catch (IOException e) {
            Toast.makeText(context, "Somwthing went wrong, Try again", Toast.LENGTH_SHORT).show();
        }






    }
    public void downloadvideo(String fileuri){


        try {
            File file = File.createTempFile("strangeVideo",".mp4",filingvideo);
            FirebaseStorage.getInstance().getReferenceFromUrl(fileuri).getFile(file).addOnSuccessListener(ss ->
                    Toast.makeText(context, "Download Successfull\n"+file.getAbsolutePath(), Toast.LENGTH_LONG).show());
        } catch (IOException e) {
            Toast.makeText(context, "Somwthing went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }
    public void downloadfiles(String filename, String fileuri){

        try {
            String suffix = filename.substring(filename.lastIndexOf("."), filename.length());
            String preffix = filename.substring(0,filename.lastIndexOf("."));

            File file = File.createTempFile(preffix,suffix,filingother);
            FirebaseStorage.getInstance().getReferenceFromUrl(fileuri).getFile(file).addOnSuccessListener(ss ->
                    Toast.makeText(context, "Download Successfull\n"+file.getAbsolutePath(), Toast.LENGTH_LONG).show());
        } catch (IOException e) {
            Toast.makeText(context, "Somwthing went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    public void downloadaudio(String filename,String fileuri){

        try {
            String suffix = filename.substring(filename.lastIndexOf("."), filename.length());
            String preffix = filename.substring(0,filename.lastIndexOf("."));

            File file = File.createTempFile(preffix,suffix,filingaudio);
            FirebaseStorage.getInstance().getReferenceFromUrl(fileuri).getFile(file).addOnSuccessListener(ss ->
                    Toast.makeText(context, "Download Successfull\n"+file.getAbsolutePath(), Toast.LENGTH_LONG).show());
        } catch (IOException e) {
            Toast.makeText(context, "Somwthing went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }


}
