package com.mactrixapp.www.stranger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mactrixapp.www.stranger.Adapters.StrangerListPagerAdapter;

public class StrangerListy extends AppCompatActivity {

    private TabLayout tablay;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_stranger_listy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tablay = (TabLayout)findViewById(R.id.strangerlisttablay);
        viewpager = (ViewPager)findViewById(R.id.strangerlistviewpager);



        tablay.addTab(tablay.newTab().setText("Friends List"));
        tablay.addTab(tablay.newTab().setText("Strangers List"));

        StrangerListPagerAdapter listPagerAdapter = new StrangerListPagerAdapter(getSupportFragmentManager(),tablay.getTabCount());
        viewpager.setAdapter(listPagerAdapter);

        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablay));

        tablay.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewpager.setCurrentItem(tab.getPosition(),true);


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });






        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: Here, open the FindStranger or FindStranger Activity

                startActivity(new Intent(StrangerListy.this,FindStranger.class));


                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    public void back(View view) {
    }

    public void more(View view) {
    }
}
