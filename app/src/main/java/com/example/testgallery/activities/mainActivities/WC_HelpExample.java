package com.example.testgallery.activities.mainActivities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.testgallery.R;
import com.example.testgallery.fragments.mainFragments.WC_help0_fragment;
import com.example.testgallery.fragments.mainFragments.WC_help1_fragment;
import com.example.testgallery.fragments.mainFragments.WC_help2_fragment;
import com.example.testgallery.fragments.mainFragments.WC_help3_fragment;
import com.example.testgallery.fragments.mainFragments.WC_help4_fragment;

import java.util.ArrayList;

public class WC_HelpExample extends  FragmentActivity {


    PageListener pageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wc_helpexample);
        Intent intent = getIntent();
        event();



    }
    private void event() {


        ViewPager pager = findViewById(R.id.pager);
        pageListener = new PageListener();
        pager.setOnPageChangeListener(pageListener);
        pager.setOffscreenPageLimit(5); //3개까지 caching

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), 1);
        WC_help0_fragment fragment0 = new WC_help0_fragment();
        adapter.addItem(fragment0);
        WC_help1_fragment fragment1 = new WC_help1_fragment();
        adapter.addItem(fragment1);
        WC_help2_fragment fragment2 = new WC_help2_fragment();
        adapter.addItem(fragment2);
        WC_help3_fragment fragment3 = new WC_help3_fragment();
        adapter.addItem(fragment3);
        WC_help4_fragment fragment4 = new WC_help4_fragment();
        adapter.addItem(fragment4);


        pager.setAdapter(adapter);

    }




    public class MainPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> items = new ArrayList<Fragment>();

        public MainPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addItem(Fragment item){
            items.add(item);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }


        @Override
        public CharSequence getPageTitle(int position){
            return "도움말" +(position+1) +"/5";
        }
    }


    private static class PageListener extends ViewPager.SimpleOnPageChangeListener {
        private int currentPage;

        public void onPageSelected(int position) {
            Log.i("TAG", "page selected ===================" + position);
            currentPage = position;
        }
    }
}
