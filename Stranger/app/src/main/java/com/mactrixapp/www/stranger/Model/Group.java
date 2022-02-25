package com.mactrixapp.www.stranger.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mactrixapp.www.stranger.R;

public class Group implements Parcelable {

    private String name;
    private String description;
    private String id;
    private String imageurl;
    private long date;
    private String creatorid;
    private String invitelink;
    private String access;
    private String grouptype;




    public Group(String name, String description, String id, String imageurl, long date, String creatorid, String invitelink) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.imageurl = imageurl;
        this.date = date;
        this.creatorid = creatorid;
        this.invitelink = invitelink;
    }
 public Group(String name, String description, String id, long date, String creatorid, String invitelink) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.date = date;
        this.creatorid = creatorid;
        this.invitelink = invitelink;
    }

    public Group(String name, String description, String id, String imageurl, long date, String creatorid, String invitelink, String access) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.imageurl = imageurl;
        this.date = date;
        this.creatorid = creatorid;
        this.invitelink = invitelink;
        this.access = access;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getInvitelink() {
        return invitelink;
    }

    public void setInvitelink(String invitelink) {
        this.invitelink = invitelink;
    }

    public Group(String name, String description, String id, String imageurl) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.imageurl = imageurl;
    }

    public Group(String name, String description, String id, String imageurl, long date, String creatorid) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.imageurl = imageurl;
        this.date = date;
        this.creatorid = creatorid;
    }

    public Group(String name, String description, String id, long date, String creatorid) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.date = date;
        this.creatorid = creatorid;
    }

    public Group() {
    }

    protected Group(Parcel in) {
        name = in.readString();
        description = in.readString();
        id = in.readString();
        imageurl = in.readString();
        date = in.readLong();
        creatorid = in.readString();
        access = in.readString();
        grouptype = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(id);
        dest.writeString(imageurl);
        dest.writeLong(date);
        dest.writeString(creatorid);
        dest.writeString(access);
        dest.writeString(grouptype);
    }

    public String getGrouptype() {
        return grouptype;
    }

    public void setGrouptype(String grouptype) {
        this.grouptype = grouptype;
    }
}
