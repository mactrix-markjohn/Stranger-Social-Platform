package com.mactrixapp.www.stranger;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class StrangerViewPagerAdapter extends FragmentPagerAdapter {

    private int  tabnum=0;

    public StrangerViewPagerAdapter(FragmentManager fm, int tabNumber) {
        super(fm);
        tabnum = tabNumber;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment post = new FragmentStrangersPost();
        Fragment interestSearch = new FragmentInterestSearch();
        Fragment chatlist = new FragmentChatList();
        Fragment electrophy = new FragmentElectroPhysicalAd();
        Fragment strangerProfile = new FragmentMyProfile();

        switch (position){
            case 0: return post;
            case 1: return interestSearch;
            case 2: return chatlist;
            case 3: return electrophy;
            case 4: return strangerProfile;
            default:return post;
        }
    }

    @Override
    public int getCount() {
        return tabnum;
    }
}
