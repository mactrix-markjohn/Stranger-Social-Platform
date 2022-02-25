package com.mactrixapp.www.stranger.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mactrixapp.www.stranger.R;

public class Request implements Parcelable {

    private String senderid;
    private String sendername;
    private String senderusername;
    private String senderphotourl;
    private String receiverid;
    private String receivername;
    private String receiverusername;
    private String receiverphotourl;
    private String requeststatus; // Seen or delivered
    private String requestsenderseen;
    private String requestreceiverseen;
    private long date;
    private String requestkey;
    private String permission;// Accepted or Rejected

    public Request() {

    }

    protected Request(Parcel in) {
        senderid = in.readString();
        sendername = in.readString();
        senderusername = in.readString();
        senderphotourl = in.readString();
        receiverid = in.readString();
        receivername = in.readString();
        receiverusername = in.readString();
        receiverphotourl = in.readString();
        requeststatus = in.readString();
        requestsenderseen = in.readString();
        requestreceiverseen = in.readString();
        date = in.readLong();
        requestkey = in.readString();
        permission = in.readString();
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getRequeststatus() {
        return requeststatus;
    }

    public void setRequeststatus(String requeststatus) {
        this.requeststatus = requeststatus;
    }

    public String getRequestreceiverseen() {
        return requestreceiverseen;
    }

    public void setRequestreceiverseen(String requestreceiverseen) {
        this.requestreceiverseen = requestreceiverseen;
    }

    public String getRequestsenderseen() {
        return requestsenderseen;
    }

    public void setRequestsenderseen(String requestsenderseen) {
        this.requestsenderseen = requestsenderseen;
    }

    public String getRequestkey() {
        return requestkey;
    }

    public void setRequestkey(String requestkey) {
        this.requestkey = requestkey;
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
        dest.writeString(requeststatus);
        dest.writeString(requestsenderseen);
        dest.writeString(requestreceiverseen);
        dest.writeLong(date);
        dest.writeString(requestkey);
        dest.writeString(permission);
    }
}
