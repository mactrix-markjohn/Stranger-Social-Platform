package com.mactrixapp.www.stranger.Model;

import android.widget.TextView;

public class EditProfileModel {

    private TextView textView;
    private String dbKey;

    public EditProfileModel(TextView textView, String dbKey) {
        this.textView = textView;
        this.dbKey = dbKey;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }
}
