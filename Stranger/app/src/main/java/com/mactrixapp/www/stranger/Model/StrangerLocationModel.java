package com.mactrixapp.www.stranger.Model;

import com.mactrixapp.www.stranger.R;

public class StrangerLocationModel  {

    private String userid;
    private String deleted;
    private long date;

    public StrangerLocationModel(String userid, String deleted) {
        this.userid = userid;
        this.deleted = deleted;
    }

    public StrangerLocationModel(String userid, String deleted, long date) {
        this.userid = userid;
        this.deleted = deleted;
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public StrangerLocationModel() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
