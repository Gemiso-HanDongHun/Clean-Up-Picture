package com.example.testgallery.activities.mainActivities;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.testgallery.R;
import com.example.testgallery.adapters.ViewPagerAdapter;
import com.example.testgallery.fragments.mainFragments.FavoriteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.karan.churi.PermissionManager.PermissionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager;
    PermissionManager permission;
    Button down;
    private Object FavoriteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 숨김
        /*getSupportActionBar();*/
        setContentView(R.layout.activity_main);

        // permission 부분(접근 권한)
        verifyStoragePermission(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.view_pager);

        permission = new PermissionManager() {
            @Override
            public void ifCancelledAndCannotRequest(Activity activity) {
            }
        };
        permission.checkAndRequestPermissions(this);
        setUpViewPager();

        
        // 휴지통 내에 있는 사진 자동삭제 함수 호출
        AutomaticDeletion();

        // 바텀 메뉴바
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.photo:        // photo 클릭했을때

                        viewPager.setCurrentItem(0);    // 모든 사진 탭으로 이동
                        break;

                    case R.id.album:        // album 클릭했을때

                        viewPager.setCurrentItem(1);    // 앨범 탭으로 이동
                        break;

                    case R.id.esamo:        // esamo 클릭했을때

                        viewPager.setCurrentItem(2);    // 이사모 탭으로 이동
                        break;

                    case R.id.screenshot:       // screenshot 클릭했을때

                        viewPager.setCurrentItem(3);    // 스크린샷 탭으로 이동
                        break;

                }
                return true;
            }
        });
    }

    // 휴지통 내에 있는 사진 자동삭제 함수
    public void AutomaticDeletion(){
        // 파일 수정시간 변경
        Calendar cal = Calendar.getInstance();    // Calendar 객체 생성
        long todayMil = cal.getTimeInMillis();    // 현재 시간을 밀리초 단위로 생성
        long oneDayMil = 24 * 60 * 60 * 1000;    // 일 단위 => 하루 시간을 밀리초 단위로

        Calendar fileCal = Calendar.getInstance();
        Date fileDate = null;

        File path2 = new File("/storage/emulated/0/Pictures/휴지통");      // 휴지통 경로
        if (!path2.exists()) {        // 폴더 없으면 폴더 생성
            path2.mkdirs();
        }

        File[] list2 = path2.listFiles();         // 휴지통에 있는 파일 리스트 가져오기
        for(int j = 0; j < list2.length; j++) {

            // 파일의 마지막 수정시간 가져오기
            fileDate = new Date(list2[j].lastModified());

            // 현재시간과 파일 수정시간 시간차 계산(단위 : 밀리 세컨드) => 1000이면 1초
            fileCal.setTime(fileDate);          // 파일 수정시간
            fileCal.getTimeInMillis();
            double diffMil = todayMil - fileCal.getTimeInMillis();;

            // 날짜로 계산
            double diffDay = (diffMil / oneDayMil);

            // 30일 지난 파일 삭제
            if (diffDay > 30 && list2[j].exists()) {
                list2[j].delete();
            }
        }
    }

    // 스토리지 사용 권한 확인
    private void verifyStoragePermission(MainActivity mainActivity) {
    }

    // 뷰페이저 세팅 함수 => 좌우로 스와이프할 때 필요
    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPagerAdapter.setContext(getApplicationContext());
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:     // 모든 사진 탭
                        bottomNavigationView.getMenu().findItem(R.id.photo).setChecked(true);
                        break;
                    case 1:     // 앨범 탭
                        bottomNavigationView.getMenu().findItem(R.id.album).setChecked(true);
                        break;
                    case 2:     // 이사모 탭
                        bottomNavigationView.getMenu().findItem(R.id.esamo).setChecked(true);
                        break;
                    case 3:     // 스크린샷 탭
                        bottomNavigationView.getMenu().findItem(R.id.screenshot).setChecked(true);
                        break;
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permission.checkResult(requestCode, permissions, grantResults);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSION_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
}


