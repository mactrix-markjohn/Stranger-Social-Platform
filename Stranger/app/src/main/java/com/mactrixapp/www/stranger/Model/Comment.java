package com.mactrixapp.www.stranger.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {


    private String comment;
    private String userid;
    private String time;
    private long date;
    private String type;
    private String uri;
    private String filename;
    private String commentid;



    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTime() {
        return time;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String toDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  MMM dd, yyyy", Locale.getDefault());
        Date date = new Date(getDate());
        String todate = sdf.format(date);

        return todate;
    }


}
