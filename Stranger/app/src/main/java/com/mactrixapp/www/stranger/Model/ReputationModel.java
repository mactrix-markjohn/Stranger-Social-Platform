package com.mactrixapp.www.stranger.Model;

public class ReputationModel {

    private int rank;
    private long followers;
    private User user;

    public ReputationModel(int rank, long followers, User user) {
        this.rank = rank;
        this.followers = followers;
        this.user = user;
    }

    public ReputationModel() {
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
