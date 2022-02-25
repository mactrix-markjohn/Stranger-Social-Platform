package com.mactrixapp.www.stranger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mactrixapp.www.stranger.Model.FontModel;
import com.mactrixapp.www.stranger.Model.LetterBack;
import com.mactrixapp.www.stranger.R;

import java.util.ArrayList;

public class FontAdapter extends BaseAdapter {

    ArrayList<FontModel> fontModels;
    Context context;
    LayoutInflater inflater;

    public FontAdapter(ArrayList<FontModel> fontmodels, Context context) {
        this.fontModels = fontmodels;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    class ViewBack{


        private TextView fontsample;
        private TextView fonttitle;

        public ViewBack(View v, int p) {

          fontsample = (TextView) v.findViewById(R.id.fontsample);
          fonttitle = (TextView)v.findViewById(R.id.fonttitle);

          fontsample.setTypeface(fontModels.get(p).getTypeface());
          fonttitle.setText(fontModels.get(p).getFonttitle());


        }
    }


    @Override
    public int getCount() {
        return fontModels.size();
    }

    @Override
    public FontModel getItem(int position) {
        return fontModels.get(position);
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
            convertView = inflater.inflate(R.layout.fontlistlay,parent,false);
        }

        new ViewBack(convertView,position);



        return convertView;
    }
}
