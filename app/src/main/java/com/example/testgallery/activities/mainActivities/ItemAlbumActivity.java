package com.example.testgallery.activities.mainActivities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Explode;
import androidx.transition.Transition;

import com.example.testgallery.R;
import com.example.testgallery.activities.mainActivities.data_favor.DataLocalManager;
import com.example.testgallery.activities.subActivities.ItemAlbumMultiSelectActivity;
import com.example.testgallery.activities.subActivities.ItemSecretMultiSelectActivity;
import com.example.testgallery.activities.subActivities.MultiSelectImage;
import com.example.testgallery.adapters.ItemAlbumAdapter;
import com.example.testgallery.adapters.ItemAlbumAdapter2;
import com.example.testgallery.adapters.ItemAlbumAdapter3;
import com.example.testgallery.models.Image;


import java.io.File;
import java.util.ArrayList;

public class ItemAlbumActivity extends AppCompatActivity {
    private ArrayList<String> myAlbum;
    private String path_folder ;
    private RecyclerView ryc_album;
    private RecyclerView ryc_list_album;
    private Intent intent;
    private String album_name;
    Toolbar toolbar_item_album;
    private ItemAlbumAdapter itemAlbumAdapter;
    private ItemAlbumAdapter2 itemAlbumAdapter2;
    private ItemAlbumAdapter3 itemAlbumAdapter3;
    private int spanCount;
    private int isSecret;
    private int duplicateImg;
    private int isAlbum;
    private static final int REQUEST_CODE_PIC = 10;
    private static final int REQUEST_CODE_CHOOSE = 55;
    private static final int REQUEST_CODE_ADD = 56;
    private static final int REQUEST_CODE_SECRET = 57;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_album);
        intent = getIntent();
        setUpSpanCount();       // setUpSpanCount 함수 호출
        mappingControls();      // mappingControls 함수 호출
        setData();      // setData 함수 호출
        setRyc();       // setRyc 함수 호출
        events();       // events 함수 호출
    }

    // 사진 정렬 기본값 설정 함수
    private void setUpSpanCount() {
        SharedPreferences sharedPref = getSharedPreferences("MyPreferences", MODE_PRIVATE);     // 앱의 데이터를 파일로 영속적으로 저장

        // spanCount => 열 수 => 기본 열 수 3
        spanCount = sharedPref.getInt("span_count", 3);     // span_count로 검색하여 spanCount에 저장 => 키값이 없으면 3으로 대체
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // resultCode와 RESULT_OK가 같고, requestCode와 사진 추가 요청 코드가 같으면
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADD) {

            // resultList에 전송받은 데이터를 저장
            ArrayList<String> resultList = data.getStringArrayListExtra("list_result");

            if(resultList !=null) {     // resultList가 비어있지 않으면
                myAlbum.addAll(resultList);     // myAlbum에 모두 추가
                spanAction();       // spanAction 함수 호출 => 열 관련 어댑터 세팅 함수
            }
        }

        // resultCode와 RESULT_OK가 같고, requestCode와 사진 선택 요청 코드가 같으면
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE) {
            if(data != null) {      // data가 널값이 아니면

                // 전달받은 데이터를 isMoved에 저장
                int isMoved = data.getIntExtra("move", 0);

                // isMoved가 1이면
                if (isMoved == 1) {

                    // resultList에 전송받은 데이터 저장
                    ArrayList<String> resultList = data.getStringArrayListExtra("list_result");

                    // resultList가 비어있지 않으면
                    if (resultList != null) {
                        myAlbum.remove(resultList);     // myAlbum에서 제거
                        spanAction();       // spanAction 함수 호출 => 열 관련 어댑터 세팅 함수
                    }
                }
            }
        }

        // resultCode와 RESULT_OK가 같고, requestCode와 REQUEST_CODE_SECRET가 같으면
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SECRET) {
            MyAsyncTask myAsyncTask = new MyAsyncTask();        // myAsyncTask 객체 생성
            myAsyncTask.execute();      // AsyncTask 실행
        }

        // resultCode와 RESULT_OK가 같고, requestCode와 REQUEST_CODE_PIC가 같으면
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PIC) {
            String path_img = data.getStringExtra("path_img");      // 이미지 경로를 path_img 변수에 저장

            // isSecret이 1이면
            if(isSecret == 1) {
                myAlbum.remove(path_img);       // myAlbum에서 path_img에 해당하는 이미지를 제거
                spanAction();       // spanAction 함수 호출 => 열 관련 어댑터 세팅 함수
            }else if (duplicateImg == 2){       //  duplicateImg가 2이면
                myAlbum.remove(path_img);       // myAlbum에서 path_img에 해당하는 이미지를 제거
                spanAction();       // spanAction 함수 호출 => 열 관련 어댑터 세팅 함수
            }
        }
    }

    // 열 관련 어댑터 세팅 함수
    private void spanAction() {
        if(spanCount == 1) {        // 1열이면
            ryc_list_album.setAdapter(new ItemAlbumAdapter3(myAlbum));      // ItemAlbumAdapter3에서 연결
        }
        else if(spanCount == 2) {       // 2열이면
            ryc_list_album.setAdapter(new ItemAlbumAdapter2(myAlbum));      // ItemAlbumAdapter2에서 연결
        }
        else{       // 3열, 4열이면
            ryc_list_album.setAdapter(new ItemAlbumAdapter(myAlbum, album_name, path_folder));      // ItemAlbumAdapter에서 연결
        }
    }

    // 리싸이클러뷰 세팅 함수
    private void setRyc() {
        album_name = intent.getStringExtra("name");     // 앨범 이름을 인텐트로 받음
        ryc_list_album.setLayoutManager(new GridLayoutManager(this, spanCount));        // 앨범을 그리드 형식으로 배치
        itemAlbumAdapter = new ItemAlbumAdapter(myAlbum, album_name, path_folder);      // itemAlbumAdapter 객체 생성
        if(spanCount == 1)      // 1열이면
            ryc_list_album.setAdapter(new ItemAlbumAdapter3(myAlbum));      // ItemAlbumAdapter3에서 연결
        else if(spanCount == 2)     // 2열이면
            ryc_list_album.setAdapter(new ItemAlbumAdapter2(myAlbum));      // ItemAlbumAdapter2에서 연결
        else        // 3열, 4열이면
            ryc_list_album.setAdapter(new ItemAlbumAdapter(myAlbum, album_name, path_folder));      // ItemAlbumAdapter에서 연결
    }

    // 정렬 비율 바꿀때마다 발생하는 애니메이션 함수
    private void animationRyc() {
        switch(spanCount) {
            case 1:     // 1열로 변경할 때 애니메이션
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_layout_ryc_1);
                ryc_list_album.setAnimation(animation1);
            case 2:     // 2열로 변경할 때 애니메이션
                Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_layout_ryc_1);
                ryc_list_album.setAnimation(animation2);
                break;
            case 3:     // 3열로 변경할 때 애니메이션
                Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_layout_ryc_2);
                ryc_list_album.setAnimation(animation3);
                break;
            case 4:     // 4열로 변경할 때 애니메이션
                Animation animation4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_layout_ryc_3);
                ryc_list_album.setAnimation(animation4);
                break;
        }
    }

    // 기본 이벤트 함수
    private void events() {
        // 툴바 이벤트
        toolbar_item_album.inflateMenu(R.menu.menu_top_item_album);     // 앨범마다 상단 메뉴 세팅
        toolbar_item_album.setTitle(album_name);        // 앨범 이름

        if(isAlbum == 0) {      // isAlbum이 0이면
            toolbar_item_album.getMenu().findItem(R.id.menu_add_image).setVisible(false);       // 이미지 추가 버튼 숨김
        } else      // 0이 아니면
            toolbar_item_album.getMenu().findItem(R.id.menu_add_image).setVisible(true);        // 이미지 추가 버튼 보이게

        // 뒤로가기 버튼
        toolbar_item_album.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_item_album.setNavigationOnClickListener(new View.OnClickListener() {        // 뒤로가기 버튼 누르면
            @Override
            public void onClick(View view) {
                finish();
            }   // 액티비티 종료
        });

        // 툴바 옵션
        toolbar_item_album.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.change_span_count:        // 사진 정렬 비율 변경 버튼 눌렀을 경우
                        spanCountEvent();       // spanCountEvent 함수 호출 => 정렬 이벤트 함수
                        break;
                    case R.id.menuChoose:       // 다중 선택 버튼 누를 경우
                        if(isSecret == 0) {     // isSecret이 0일 경우
                            // ItemAlbumMultiSelectActivity 호출
                            Intent intent_mul = new Intent(ItemAlbumActivity.this, ItemAlbumMultiSelectActivity.class);
                            intent_mul.putStringArrayListExtra("data_1", myAlbum);
                            intent_mul.putExtra("name_1", album_name);
                            intent_mul.putExtra("path_folder", path_folder);
                            startActivityForResult(intent_mul, REQUEST_CODE_CHOOSE);
                        }else {     // 0이 아닐 경우
                            // ItemSecretMultiSelectActivity 호출
                            Intent intent_mul = new Intent(ItemAlbumActivity.this, ItemSecretMultiSelectActivity.class);
                            intent_mul.putStringArrayListExtra("data_1", myAlbum);
                            intent_mul.putExtra("name_1", album_name);
                            startActivityForResult(intent_mul, REQUEST_CODE_SECRET);
                        }
                        break;
                    case R.id.album_item_slideshow:     // 슬라이드 쇼 버튼 누를 경우
                        slideShowEvents();      // slideShowEvents 함수 호출 => 슬라이드쇼 함수
                        break;
                    case R.id.menu_add_image:       // 이미지 추가 버튼 누를 경우
                        // AddImageToAlbumActivity 호출
                        Intent intent_add = new Intent(ItemAlbumActivity.this, AddImageToAlbumActivity.class);
                        intent_add.putStringArrayListExtra("list_image", myAlbum);
                        intent_add.putExtra("path_folder", path_folder);
                        intent_add.putExtra("name_folder", album_name);
                        startActivityForResult(intent_add, REQUEST_CODE_ADD);

                        break;
                }

                return true;
            }
        });
        if(isSecret == 1)       // isSecret이 1 경우
            hideMenu();     // hideMenu 함수 호출 => 메뉴 숨김 함수
    }

    // 메뉴 숨김 함수
    private void hideMenu() {
        toolbar_item_album.getMenu().findItem(R.id.menu_add_image).setVisible(false);   // 이미지 추가 버튼 숨김
    }

    // 정렬 이벤트 함수
    private void spanCountEvent() {
        if(spanCount == 1){     // 열이 1개면
            spanCount++;
            ryc_list_album.setLayoutManager(new GridLayoutManager(this, spanCount));
            ryc_list_album.setAdapter(itemAlbumAdapter2);
        }

        else if(spanCount < 4 && spanCount > 1) {       // 열이 2개, 3개면
            spanCount++;
            ryc_list_album.setLayoutManager(new GridLayoutManager(this, spanCount));
            ryc_list_album.setAdapter(itemAlbumAdapter);
        }
        else if(spanCount == 4) {       // 열이 4개면
            spanCount = 1;
            ryc_list_album.setLayoutManager(new LinearLayoutManager(this));
            ryc_list_album.setAdapter(itemAlbumAdapter3);

        }


        animationRyc();     // animationRyc 함수 호출 => 정렬 애니메이션 함수

        // 데이터를 파일로 저장
        SharedPreferences sharedPref = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("span_count", spanCount);
        editor.commit();
    }

    // 슬라이드 쇼 함수
    private void slideShowEvents() {
        Intent intent = new Intent(ItemAlbumActivity.this, SlideShowActivity.class);
        intent.putStringArrayListExtra("data_slide", myAlbum);
        intent.putExtra("name", album_name);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ItemAlbumActivity.this.startActivity(intent);
    }

    // 데이터 세팅 함수
    private void setData() {
        myAlbum = intent.getStringArrayListExtra("data");
        path_folder = intent.getStringExtra("path_folder");
        isSecret = intent.getIntExtra("isSecret", 0);
        duplicateImg = intent.getIntExtra("duplicateImg",0);
        itemAlbumAdapter2 = new ItemAlbumAdapter2(myAlbum);
        isAlbum = intent.getIntExtra("ok",0);
        itemAlbumAdapter3 = new ItemAlbumAdapter3(myAlbum);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {     // Activity가 재개될때 필요한 초기화 작업을 수행
        super.onResume();
        MyAsyncTask myAsyncTask = new MyAsyncTask();// myAsyncTask 객체 생성
        myAsyncTask.execute();      // AsyncTask 실행
    }

    // 매핑 함수
    private void mappingControls() {
        ryc_list_album = findViewById(R.id.ryc_list_album);
        toolbar_item_album = findViewById(R.id.toolbar_item_album);
    }

    // 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)
            for(int i=0;i<myAlbum.size();i++) {     // myAlbum 사이즈 만큼 반복
                File file = new File(myAlbum.get(i));
                if(!file.exists()) {        // file이 존재하지 않으면
                    myAlbum.remove(i);      // myAlbum에서 제거
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {     // 스레드 작업이 모두 끝난 후에 수행할 작업(메인 스레드)
            super.onPostExecute(unused);
            spanAction();       // spanAction 함수 호출 => 열 관련 어댑터 세팅 함수
        }
    }
}