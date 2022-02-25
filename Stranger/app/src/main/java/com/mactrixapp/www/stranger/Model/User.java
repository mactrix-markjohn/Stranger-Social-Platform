package com.mactrixapp.www.stranger.Model;

import com.mactrixapp.www.stranger.R;

public class User {

    private String userid;
    private String username;
    private String email;
    private String photoUrl;
    private String fullname,phoneNumber,datebirth,gender;
    private String origin;
    private String status,interest,profession,institute,address;
    private String whatsapp,instagram,facebook,twitter;
    private String connectionstatus; //Online or Offiline
    private String country;
    private long lastseen;
    private long onlinetime;
    private int logincount;
    private double latitude;
    private double longitude;
    private String currentaddress;
    private String headerphotourl;

    public String getCurrentaddress() {
        return currentaddress;
    }

    public void setCurrentaddress(String currentaddress) {
        this.currentaddress = currentaddress;
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

    public String getWorkstate() {
        return workstate;
    }

    public void setWorkstate(String workstate) {
        this.workstate = workstate;
    }

    private String workstate;// this has two values which are Student and Professional


    // Empty Constructor
    public User(){


    }

    public User(String userid, String username, String email) {
        this.userid = userid;
        this.username = username;
        this.email = email;
    }





    public String getConnectionstatus() {
        return connectionstatus;
    }

    public void setConnectionstatus(String connectionstatus) {
        this.connectionstatus = connectionstatus;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDatebirth() {
        return datebirth;
    }

    public void setDatebirth(String datebirth) {
        this.datebirth = datebirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }


    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public long getLastseen() {
        return lastseen;
    }

    public void setLastseen(long lastseen) {
        this.lastseen = lastseen;
    }

    public long getOnlinetime() {
        return onlinetime;
    }

    public void setOnlinetime(long onlinetime) {
        this.onlinetime = onlinetime;
    }

    public int getLogincount() {
        return logincount;
    }

    public void setLogincount(int logincount) {
        this.logincount = logincount;
    }

    public String getHeaderphotourl() {
        return headerphotourl;
    }

    public void setHeaderphotourl(String headerphotourl) {
        this.headerphotourl = headerphotourl;
    }
}
