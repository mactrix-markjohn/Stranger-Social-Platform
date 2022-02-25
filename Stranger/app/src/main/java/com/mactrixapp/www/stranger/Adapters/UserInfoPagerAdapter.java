package com.mactrixapp.www.stranger.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mactrixapp.www.stranger.FragmentInfoOne;
import com.mactrixapp.www.stranger.FragmentInfoThree;
import com.mactrixapp.www.stranger.FragmentInfoTwo;

public class UserInfoPagerAdapter extends FragmentPagerAdapter {

    int count = 0;

    public UserInfoPagerAdapter(FragmentManager fm, int count) {
        super(fm);

        this.count  = count;

    }

    @Override
    public Fragment getItem(int position) {

        Fragment info1 = new FragmentInfoOne();
        Fragment info2 = new FragmentInfoTwo();
        Fragment info3 = new FragmentInfoThree();

        switch (position){
            case 0: return info1;
            case 1: return info2;
            case 2: return info3;
            default:return info1;


        }

    }

    @Override
    public int getCount() {
        return count;
    }
}
