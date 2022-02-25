package com.mactrixapp.www.stranger.Model;

import com.mactrixapp.www.stranger.R;

public class Post {


    private String imageurl;
    private String videourl;
    private String audiourl;
    private String textmessage;
    private String capture;
    private String userid;
    private String userphotourl;
    private String username;
    private long date;
    private long time;
    private String fileurl;
    private String type;
    private String papertype;
    private String postid;
    private String phonenum;
    private String videothumbnailurl;
    private int likecount;
    private String interest;


    public String getInterest() {
        return interest;
    }
    public void setInterest(String interest) {
        this.interest = interest;
    }



    public String getVideothumbnailurl() {
        return videothumbnailurl;
    }

    public void setVideothumbnailurl(String videothumbnailurl) {
        this.videothumbnailurl = videothumbnailurl;
    }


    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }



    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }



    public Post(String textmessage, String capture) {
        this.textmessage = textmessage;
        this.capture = capture;
    }



    public Post() {


    }




    public String getPapertype() {
        return papertype;
    }

    public void setPapertype(String papertype) {
        this.papertype = papertype;
    }



    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }



    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }



    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }



    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }



    public String getAudiourl() {
        return audiourl;
    }

    public void setAudiourl(String audiourl) {
        this.audiourl = audiourl;
    }



    public String getTextmessage() {
        return textmessage;
    }

    public void setTextmessage(String textmessage) {
        this.textmessage = textmessage;
    }



    public String getCapture() {
        return capture;
    }

    public void setCapture(String capture) {
        this.capture = capture;
    }



    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }



    public String getUserphotourl() {
        return userphotourl;
    }

    public void setUserphotourl(String userphotourl) {
        this.userphotourl = userphotourl;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }
}
