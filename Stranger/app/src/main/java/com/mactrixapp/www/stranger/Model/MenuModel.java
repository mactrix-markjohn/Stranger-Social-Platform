package com.mactrixapp.www.stranger.Model;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import com.mactrixapp.www.stranger.R;

public class MenuModel {
    Context context;
    DownloadModel downloadModel;

    public MenuModel(Context context) {
        this.context = context;
        downloadModel = new DownloadModel(context);
    }

    public void donwloadimage(View v,String fileuri, String filename){

        PopupMenu popupMenu = new PopupMenu(context,v);
        popupMenu.inflate(R.menu.downloadmenu);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case R.id.download:
                        downloadModel.downloadimage(fileuri);

                        break;
                }

                return false;
            }
        });

    } public void donwloadvideo(View v,String fileuri, String filename){

        PopupMenu popupMenu = new PopupMenu(context,v);
        popupMenu.inflate(R.menu.downloadmenu);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case R.id.download:
                        downloadModel.downloadvideo(fileuri);

                        break;
                }

                return false;
            }
        });

    } public void donwloadaudio(View v,String fileuri, String filename){

        PopupMenu popupMenu = new PopupMenu(context,v);
        popupMenu.inflate(R.menu.downloadmenu);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case R.id.download:
                        downloadModel.downloadaudio(filename,fileuri);

                        break;
                }

                return false;
            }
        });

    } public void donwloadother(View v,String fileuri, String filename){

        PopupMenu popupMenu = new PopupMenu(context,v);
        popupMenu.inflate(R.menu.downloadmenu);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case R.id.download:
                        downloadModel.downloadfiles(filename,fileuri);

                        break;
                }

                return false;
            }
        });

    } public void donwloadmenu(View v,String fileuri, String filename){

        PopupMenu popupMenu = new PopupMenu(context,v);
        popupMenu.inflate(R.menu.downloadmenu);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case R.id.download:


                        break;
                }

                return false;
            }
        });

    }
}
