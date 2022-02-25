package com.mactrixapp.www.stranger.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class EPAds implements Parcelable  {

    private String id;
    private String senderid;
    private long date;

    private String title;
    private String message;

    private double latitude;
    private double longitude;
    private String address;
    private String venue;

    private String filetype;
    private String fileurl;
    private String audioname;
    private String filename;

    public EPAds() {
    }

    public EPAds(String id, String title, String message, double latitude, double longitude, String address, String filetype, String fileurl) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.filetype = filetype;
        this.fileurl = fileurl;
    }

    public EPAds(String id, String senderid, String title, String message, double latitude, double longitude, String address, String filetype, String fileurl) {
        this.id = id;
        this.senderid = senderid;
        this.title = title;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.filetype = filetype;
        this.fileurl = fileurl;
    }


    protected EPAds(Parcel in) {
        id = in.readString();
        senderid = in.readString();
        date = in.readLong();
        title = in.readString();
        message = in.readString();
        audioname = in.readString();
        filename = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
        venue = in.readString();
        filetype = in.readString();
        fileurl = in.readString();
    }

    public static final Creator<EPAds> CREATOR = new Creator<EPAds>() {
        @Override
        public EPAds createFromParcel(Parcel in) {
            return new EPAds(in);
        }

        @Override
        public EPAds[] newArray(int size) {
            return new EPAds[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getAudioname() {
        return audioname;
    }

    public void setAudioname(String audioname) {
        this.audioname = audioname;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(senderid);
        dest.writeLong(date);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(audioname);
        dest.writeString(filename);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address);
        dest.writeString(venue);
        dest.writeString(filetype);
        dest.writeString(fileurl);
    }
}
