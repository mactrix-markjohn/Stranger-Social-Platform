package com.mactrixapp.www.stranger.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mactrixapp.www.stranger.FragmentFriendsList;
import com.mactrixapp.www.stranger.FragmentInterestSearch;
import com.mactrixapp.www.stranger.FragmentStrangerList;
import com.mactrixapp.www.stranger.FragmentStrangersPost;

public class StrangerListPagerAdapter extends FragmentPagerAdapter {

    private int  tabnum=0;

    public StrangerListPagerAdapter(FragmentManager fm,  int tabNumber) {
        super(fm);
        tabnum = tabNumber;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment friendslist = new FragmentFriendsList();
        Fragment strangerList = new FragmentStrangerList();
        switch (position){
            case 0: return friendslist;
            case 1: return strangerList;
            default:return friendslist;
        }
    }

    @Override
    public int getCount() {
        return tabnum;
    }
}
