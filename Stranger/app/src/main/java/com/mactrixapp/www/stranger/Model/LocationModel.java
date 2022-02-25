package com.mactrixapp.www.stranger.Model;

public class LocationModel {

    private double latitude;
    private double longitude;
    private String userid;

    public LocationModel() {
    }

    public LocationModel(double latitude, double longitude, String userid) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userid = userid;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
