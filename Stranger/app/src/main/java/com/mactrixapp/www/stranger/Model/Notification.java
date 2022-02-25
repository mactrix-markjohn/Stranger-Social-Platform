package com.mactrixapp.www.stranger.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {
    private String senderid;
    private String sendername;
    private String senderusername;
    private String senderphotourl;
    private String receiverid;
    private String receivername;
    private String receiverusername;
    private String receiverphotourl;
    private String noticetype;
    private long date;
    private Request request;
    private Letter letter;

    public Notification() {
    }

    protected Notification(Parcel in) {
        senderid = in.readString();
        sendername = in.readString();
        senderusername = in.readString();
        senderphotourl = in.readString();
        receiverid = in.readString();
        receivername = in.readString();
        receiverusername = in.readString();
        receiverphotourl = in.readString();
        noticetype = in.readString();
        date = in.readLong();
        request = in.readParcelable(Request.class.getClassLoader());
        letter = in.readParcelable(Letter.class.getClassLoader());
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
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

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }

    public String getReceivername() {
        return receivername;
    }

    public void setReceivername(String receivername) {
        this.receivername = receivername;
    }

    public String getReceiverusername() {
        return receiverusername;
    }

    public void setReceiverusername(String receiverusername) {
        this.receiverusername = receiverusername;
    }

    public String getReceiverphotourl() {
        return receiverphotourl;
    }

    public void setReceiverphotourl(String receiverphotourl) {
        this.receiverphotourl = receiverphotourl;
    }

    public String getNoticetype() {
        return noticetype;
    }

    public void setNoticetype(String noticetype) {
        this.noticetype = noticetype;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Letter getLetter() {
        return letter;
    }

    public void setLetter(Letter letter) {
        this.letter = letter;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(senderid);
        dest.writeString(sendername);
        dest.writeString(senderusername);
        dest.writeString(senderphotourl);
        dest.writeString(receiverid);
        dest.writeString(receivername);
        dest.writeString(receiverusername);
        dest.writeString(receiverphotourl);
        dest.writeString(noticetype);
        dest.writeLong(date);
        dest.writeParcelable(request, flags);
        dest.writeParcelable(letter, flags);
    }
}
