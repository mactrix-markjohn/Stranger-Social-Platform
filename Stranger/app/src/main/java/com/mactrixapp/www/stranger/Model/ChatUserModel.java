package com.mactrixapp.www.stranger.Model;

public class ChatUserModel {

    private User user;
    private Group group;
    private String groupid;
    private String lastmessage;
    private String chatid;
    private long date;
    private String chattype;
    private int chatcount;

    /*public ChatUserModel(User user, String lastmessage, String chatid) {
        this.user = user;
        this.lastmessage = lastmessage;
        this.chatid = chatid;
    }*/


    public ChatUserModel( Group group, String groupid, String lastmessage, long date, String chattype) {

        this.group = group;
        this.groupid = groupid;
        this.lastmessage = lastmessage;

        this.date = date;
        this.chattype = chattype;
    }

    public Group getGroup() {
        return group;
    }

    public int getChatcount() {
        return chatcount;
    }

    public void setChatcount(int chatcount) {
        this.chatcount = chatcount;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public ChatUserModel(User user, String lastmessage, String chatid, long date) {
        this.user = user;
        this.lastmessage = lastmessage;
        this.chatid = chatid;
        this.date = date;
    }

    public ChatUserModel(User user, String lastmessage, String chatid, long date, String chattype) {
        this.user = user;
        this.lastmessage = lastmessage;
        this.chatid = chatid;
        this.date = date;
        this.chattype = chattype;
    }

    public ChatUserModel(User user,String groupid, String lastmessage, String chatid, long date, String chattype) {
        this.user = user;
        this.lastmessage = lastmessage;
        this.chatid = chatid;
        this.groupid = groupid;
        this.date = date;
        this.chattype = chattype;
    }

    public String getChattype() {
        return chattype;
    }

    public void setChattype(String chattype) {
        this.chattype = chattype;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public ChatUserModel() {

    }

    public ChatUserModel(User user, String lastmessage) {
        this.user = user;
        this.lastmessage = lastmessage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }
}
