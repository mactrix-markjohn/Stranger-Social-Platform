package com.mactrixapp.www.stranger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mactrixapp.www.stranger.Model.PairValueHolder;
import com.mactrixapp.www.stranger.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AnalysAdapter extends BaseAdapter {
    Context context;
    ArrayList<PairValueHolder> valueHolders = new ArrayList<>();
    LayoutInflater inflater;

    public AnalysAdapter(Context context, ArrayList<PairValueHolder> valueHolders) {
        this.context = context;
        this.valueHolders = valueHolders;
         inflater = LayoutInflater.from(context);
    }


    class ValueHolder{

        TextView name;
        TextView count;


        public ValueHolder(View v, int p){
            name = (TextView)v.findViewById(R.id.name);
            count = (TextView)v.findViewById(R.id.count);

            PairValueHolder pairValueHolder = valueHolders.get(p);

            name.setText(pairValueHolder.getString());
            count.setText(String.valueOf(pairValueHolder.getCount()));

        }

    }

    @Override
    public int getCount() {
        return valueHolders.size();
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

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.analysiscell,parent,false);
            new ValueHolder(convertView,position);
        }

        return convertView;
    }
}
