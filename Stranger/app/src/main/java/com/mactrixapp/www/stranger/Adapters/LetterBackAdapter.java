package com.mactrixapp.www.stranger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mactrixapp.www.stranger.Model.LetterBack;
import com.mactrixapp.www.stranger.R;

import java.util.ArrayList;

public class LetterBackAdapter extends BaseAdapter {

    ArrayList<LetterBack> letterBacks;
    Context context;
    LayoutInflater inflater;

    public LetterBackAdapter(ArrayList<LetterBack> letterBacks, Context context) {
        this.letterBacks = letterBacks;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    class ViewBack{


        private ImageView letterimage;
        private TextView lettertitle;

        public ViewBack(View v, int p) {

            letterimage = (ImageView)v.findViewById(R.id.letterimage);
            lettertitle = (TextView)v.findViewById(R.id.lettertitle);

            letterimage.setImageResource(letterBacks.get(p).getBackres());
            lettertitle.setText(letterBacks.get(p).getBacktitle());

        }
    }


    @Override
    public int getCount() {
        return letterBacks.size();
    }

    @Override
    public LetterBack getItem(int position) {
        return letterBacks.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.gridletterselect,parent,false);
        }

        new ViewBack(convertView,position);



        return convertView;
    }
}
