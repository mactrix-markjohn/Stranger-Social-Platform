package com.mactrixapp.www.stranger.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Letter implements Parcelable {

    private String message;
    private int letterbackres;
    private String typefaceid;
    private String senderid;
    private String senderfullname;
    private String senderusername;
    private String senderphotourl;
    private double senderlat;
    private double senderlng;
    private String name;
    private String professsion;
    private String interests;
    private String institute;
    private String workstate;
    private String gender;
    private String locationstate;
    private long date;
    private String letterid;

    public Letter() {
    }

    protected Letter(Parcel in) {
        message = in.readString();
        letterbackres = in.readInt();
        typefaceid = in.readString();
        senderid = in.readString();
        senderfullname = in.readString();
        senderusername = in.readString();
        senderphotourl = in.readString();
        senderlat = in.readDouble();
        senderlng = in.readDouble();
        name = in.readString();
        professsion = in.readString();
        interests = in.readString();
        institute = in.readString();
        workstate = in.readString();
        gender = in.readString();
        locationstate = in.readString();
        date = in.readLong();
        letterid = in.readString();
    }

    public static final Creator<Letter> CREATOR = new Creator<Letter>() {
        @Override
        public Letter createFromParcel(Parcel in) {
            return new Letter(in);
        }

        @Override
        public Letter[] newArray(int size) {
            return new Letter[size];
        }
    };

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLetterid() {
        return letterid;
    }

    public void setLetterid(String letterid) {
        this.letterid = letterid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLetterbackres() {
        return letterbackres;
    }

    public void setLetterbackres(int letterbackres) {
        this.letterbackres = letterbackres;
    }

    public String getTypefaceid() {
        return typefaceid;
    }

    public void setTypefaceid(String typefaceid) {
        this.typefaceid = typefaceid;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getSenderfullname() {
        return senderfullname;
    }

    public void setSenderfullname(String senderfullname) {
        this.senderfullname = senderfullname;
    }

    public String getSenderusername() {
        return senderusername;
    }

    public void setSenderusername(String senderusername) {
        this.senderusername = senderusername;
    }

    public String getSenderphotourl() {
        return senderphotourl;
    }

    public void setSenderphotourl(String senderphotourl) {
        this.senderphotourl = senderphotourl;
    }

    public double getSenderlat() {
        return senderlat;
    }

    public void setSenderlat(double senderlat) {
        this.senderlat = senderlat;
    }

    public double getSenderlng() {
        return senderlng;
    }

    public void setSenderlng(double senderlng) {
        this.senderlng = senderlng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfesssion() {
        return professsion;
    }

    public void setProfesssion(String professsion) {
        this.professsion = professsion;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getWorkstate() {
        return workstate;
    }

    public void setWorkstate(String workstate) {
        this.workstate = workstate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocationstate() {
        return locationstate;
    }

    public void setLocationstate(String locationstate) {
        this.locationstate = locationstate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeInt(letterbackres);
        dest.writeString(typefaceid);
        dest.writeString(senderid);
        dest.writeString(senderfullname);
        dest.writeString(senderusername);
        dest.writeString(senderphotourl);
        dest.writeDouble(senderlat);
        dest.writeDouble(senderlng);
        dest.writeString(name);
        dest.writeString(professsion);
        dest.writeString(interests);
        dest.writeString(institute);
        dest.writeString(workstate);
        dest.writeString(gender);
        dest.writeString(locationstate);
        dest.writeLong(date);
        dest.writeString(letterid);
    }
}
