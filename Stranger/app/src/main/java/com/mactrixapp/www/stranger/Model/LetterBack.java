package com.mactrixapp.www.stranger.Model;

public class LetterBack {

    private String backtitle;
    private int backres;
    private String backid;

    public LetterBack(String backtitle, int backres, String backid) {
        this.backtitle = backtitle;
        this.backres = backres;
        this.backid = backid;
    }

    public String getBacktitle() {
        return backtitle;
    }

    public void setBacktitle(String backtitle) {
        this.backtitle = backtitle;
    }

    public int getBackres() {
        return backres;
    }

    public void setBackres(int backres) {
        this.backres = backres;
    }

    public String getBackid() {
        return backid;
    }

    public void setBackid(String backid) {
        this.backid = backid;
    }
}
