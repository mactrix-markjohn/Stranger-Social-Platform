package com.mactrixapp.www.stranger.Model;

public class PairValueHolder {

    private String string;
    private long count;

    public PairValueHolder(String string, long count) {
        this.string = string;
        this.count = count;
    }

    public PairValueHolder() {
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
