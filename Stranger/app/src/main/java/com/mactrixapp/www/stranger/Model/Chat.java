package com.mactrixapp.www.stranger.Model;

import com.mactrixapp.www.stranger.R;

public class Chat {

    private String message;
    private String fileuri;
    private String filename;
    private String filetype;// wether the chat contains image, video or other fileor empty
    private String senderid;
    private String receiverid;
    private String chatid;
    private String chatstatus;// This will show wether the chat is {Sent, Deliver or Seen}
    private long date;


    public Chat() {
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileuri() {
        return fileuri;
    }

    public void setFileuri(String fileuri) {
        this.fileuri = fileuri;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }

    public String getChatstatus() {
        return chatstatus;
    }

    public void setChatstatus(String chatstatus) {
        this.chatstatus = chatstatus;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
