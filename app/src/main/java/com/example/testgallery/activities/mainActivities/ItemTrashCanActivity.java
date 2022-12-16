package com.example.testgallery.activities.mainActivities;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testgallery.R;
import com.example.testgallery.activities.subActivities.ItemAlbumMultiSelectActivity;
import com.example.testgallery.activities.subActivities.ItemSecretMultiSelectActivity;
import com.example.testgallery.activities.subActivities.ItemTrashCanMultiSelectActivity;
import com.example.testgallery.adapters.ItemAlbumAdapter;
import com.example.testgallery.adapters.ItemTrashCanAdapter;
import com.example.testgallery.adapters.ItemTrashCanAdapter2;
import com.example.testgallery.adapters.ItemTrashCanAdapter3;
import com.example.testgallery.models.Image;


import java.io.File;
import java.util.ArrayList;

public class ItemTrashCanActivity extends AppCompatActivity {
    private ArrayList<String> myAlbum;
    private String path_folder ;
    private RecyclerView ryc_album;
    private RecyclerView ryc_list_album;
    private Intent intent;
    private String album_name;
    Toolbar toolbar_item_album;
    private ItemTrashCanAdapter itemTrashCanAdapter;
    private ItemTrashCanAdapter2 itemTrashCanAdapter2;
    private ItemTrashCanAdapter3 itemTrashCanAdapter3;

    private int spanCount;
    private int isSecret;
    private int duplicateImg;
    private int isAlbum;
    private static final int REQUEST_CODE_PIC = 10;
    private static final int REQUEST_CODE_CHOOSE = 55;      // 사진 선택 요청 코드..?
    private static final int REQUEST_CODE_ADD = 56;     // 사진 추가 요청 코드..?
    private static final int REQUEST_CODE_SECRET = 57;
    private ArrayList<Image> listImageSelected;


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
           if(data != null) {       // data가 널값이 아니면

               // 전달받은 데이터를 isMoved에 저장
               int isMoved = data.getIntExtra("move", 0);

               // isMoved가 1이면
               if (isMoved == 1) {

                   // resultList에 전송받은 데이터 저장
                   ArrayList<String> resultList = data.getStringArrayListExtra("list_result");

                   // resultList가 비어있지 않으면
                   if (resultList != null) {
                       myAlbum.remove(resultList);      // myAlbum에서 제거
                       spanAction();        // spanAction 함수 호출 => 열 관련 어댑터 세팅 함수
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
                spanAction();       // spanAction 함수 호출 => 열 관련 어댑터 세팅 함수/
            }else if (duplicateImg == 2){       //  duplicateImg가 2이면
                myAlbum.remove(path_img);       // myAlbum에서 path_img에 해당하는 이미지를 제거
                spanAction();       // spanAction 함수 호출 => 열 관련 어댑터 세팅 함수
            }
        }
    }

    // 열 관련 어댑터 세팅 함수
    private void spanAction() {
        if(spanCount == 1) {        // 1열이면
            ryc_list_album.setAdapter(new ItemTrashCanAdapter3(myAlbum));       // ItemTrashCanAdapter3에서 연결
        }
        else if(spanCount == 2) {       // 2열이면
            ryc_list_album.setAdapter(new ItemTrashCanAdapter2(myAlbum));       // ItemTrashCanAdapter2에서 연결해서 화면에 보여줌
        }
        else{       // 3열, 4열이면
            ryc_list_album.setAdapter(new ItemTrashCanAdapter(myAlbum, album_name, path_folder));       // ItemTrashCanAdapter에서 연결해서 화면에 보여줌
        }
    }

    // 리싸이클러뷰 세팅 함수
    private void setRyc() {
        album_name = intent.getStringExtra("name");     // 앨범 이름을 인텐트로 받음
        ryc_list_album.setLayoutManager(new GridLayoutManager(this, spanCount));        // 앨범을 그리드 형식으로 배치

        itemTrashCanAdapter = new ItemTrashCanAdapter(myAlbum, album_name, path_folder);

        if(spanCount == 1)      // 1열이면
            ryc_list_album.setAdapter(new ItemTrashCanAdapter3(myAlbum));       // ItemTrashCanAdapter3에서 연결
        else if(spanCount == 2)     // 2열이면
            ryc_list_album.setAdapter(new ItemTrashCanAdapter2(myAlbum));       // ItemTrashCanAdapter2에서 연결

        else        // 3열, 4열이면
            ryc_list_album.setAdapter(new ItemTrashCanAdapter(myAlbum, album_name, path_folder));       // ItemTrashCanAdapter에서 연결
    }

    // 정렬 비율 변경할 때마다 발생하는 애니메이션 함수
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
        toolbar_item_album.inflateMenu(R.menu.menu_top_item_trash_can);     // 앨범마다 상단 메뉴 세팅
        toolbar_item_album.setTitle(album_name);        // 앨범 이름

        // 뒤로가기 버튼
        toolbar_item_album.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_item_album.setNavigationOnClickListener(new View.OnClickListener() {        // 뒤로가기 버튼 누르면
            @Override
            public void onClick(View view) {
                finish();       // 액티비티 종료
            }
        });

        // 툴바 옵션
        toolbar_item_album.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {       // 툴바에 있는 메뉴 누르면
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.change_span_count:        // 사진 정렬 비율 변경할 때
                        spanCountEvent();       // spanCountEvent 함수 호출 => 정렬 이벤트 함수
                        break;
                    case R.id.menuChoose:       // 다중 선택 버튼 누를 경우
                        // 휴지통 사진 선택 액티비티인 ItemTrashCanMultiSelectActivity 호출
                        Intent intent_mul = new Intent(ItemTrashCanActivity.this, ItemTrashCanMultiSelectActivity.class);
                        intent_mul.putStringArrayListExtra("data_1", myAlbum);      // myAlbum 데이터 전송
                        intent_mul.putExtra("name_1", album_name);      // 앨범 이름 전송
                        intent_mul.putExtra("path_folder", path_folder);        // 폴더 경로 전송
                        startActivityForResult(intent_mul, REQUEST_CODE_CHOOSE);        // 사진 선택 액티비티 화면에 출력
                        break;
                    case R.id.album_delete_all:     // 모두 삭제 버튼 누를 경우
                        deleteAllEvents();      // 휴지통 내에 있는 모든 사진 영구삭제
                        break;
                    case R.id.menu_restore_all:     // 모두 복구 버튼 누를 경우
                        restoreAllEvents();     // 휴지통 내에 있는 모든 사진 복구
                        break;
                }
                return true;
            }
        });
    }

    // 정렬 이벤트 함수
    private void spanCountEvent() {
        if(spanCount == 1){     // 열이 1개면
            spanCount++;        // 1씩 증가
            ryc_list_album.setLayoutManager(new GridLayoutManager(this, spanCount));
            ryc_list_album.setAdapter(itemTrashCanAdapter2);
        }

        else if(spanCount < 4 && spanCount > 1) {       // 열이 2개, 3개면
            spanCount++;        // 1씩 증가
            ryc_list_album.setLayoutManager(new GridLayoutManager(this, spanCount));
            ryc_list_album.setAdapter(itemTrashCanAdapter);
        }
        else if(spanCount == 4) {       // 열이 4개면

            spanCount = 1;
            ryc_list_album.setLayoutManager(new LinearLayoutManager(this));
            ryc_list_album.setAdapter(itemTrashCanAdapter3);

        }


        animationRyc();     // animationRyc 함수 호출 => 정렬 애니메이션 함수

        // 데이터를 파일로 저장
        SharedPreferences sharedPref = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("span_count", spanCount);
        editor.commit();
    }

    // 모두 삭제 함수
    private void deleteAllEvents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemTrashCanActivity.this);

        builder.setTitle("확인");
        builder.setMessage("모든 사진을 삭제하시겠습니까?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {       // YES 버튼 눌렀을 때
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                File path = new File("/storage/emulated/0/Pictures/휴지통");       // 휴지통 경로
                File[] fileList = path.listFiles();     // 휴지통 경로에 있는 파일들을 배열로 리턴해서 fileList에 저장

                for(int i=0; i<fileList .length; i++){      // fileList의 크기만큼 반복하면서
                    fileList[i].delete();       // 파일 삭제
                }
                finish();   // 종료
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {     // NO 버튼 눌렀을 때
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 아무것도 하지 않음
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();       // AlertDialog 객체를 생성
        alert.show();       // 다이얼로그 창 출력
    }

    // 모두 복구 함수
    private void restoreAllEvents(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemTrashCanActivity.this);

        builder.setTitle("확인");
        builder.setMessage("모든 사진을 복구하시겠습니까?");

        // YES 버튼 눌렀을 때
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                File path = new File("/storage/emulated/0/Pictures/휴지통");       // 휴지통 폴더 경로

                // 복원 전 폴더에 있는 파일들을 filelist에 배열로 저장
                File[] filelist = path.listFiles();     // listFiles() : 디렉토리 경로에 있는 파일들을 배열로 리턴

                // 복원 전 폴더에 있는 파일들의 이름을 filelist에 배열로 저장
                String[] paths = path.list();           // list() : 디렉토리 경로에 있는 파일들의 이름들을 배열로 리턴

                for(int i = 0; i<filelist.length; i++){     // filelist의 크기만큼 반복
                    File file = new File(filelist[i].getPath());        // file 객체 생성

                    int start = file.getName().indexOf("_");    // 파일 이름에 있는 첫 '_' 인덱스 추출
                    int end = file.getName().indexOf("_", 4);   // 파일 이름에 있는 마지막 '_' 인덱스 추출

                    // 파일 이름의 첫 '_' 인덱스와 마지막 '_' 인덱스 사이의 문자열 추출
                    String fileRestore_folder = file.getName().substring(start+1, end);

                    File move_path = new File("/storage/emulated/0/Pictures/" + fileRestore_folder);      // 복원 후 폴더 경로

                    // 이 전 폴더가 Pictures였을 경우, 복원 후 폴더 경로
                    File move_path2 = new File("/storage/emulated/0/" + fileRestore_folder);

                    switch(fileRestore_folder){
                        case "Pictures":        // 이전 경로가 Pictures였을 경우
                            File MoveFile1 = new File(move_path2,fileRestore_folder + "_" + file.getName());     // 이동할 경로와 이동 후 파일명
                            filelist[i].renameTo(MoveFile1);     // filelist에 있는 파일들 이름 변경
                            filelist[i].deleteOnExit();         // 원래 파일 삭제
                            paths[i] = MoveFile1.getPath();      // 복원한 파일의 경로를 문자열로 저장
                            break;

                        case "Screenshot":      // 이전 경로가 Screenshot이었을 경우
                            File MoveFile2 = new File(move_path,fileRestore_folder + "_" + file.getName());     // 이동할 경로와 이동 후 파일명
                            filelist[i].renameTo(MoveFile2);     // filelist에 있는 파일들 이름 변경
                            filelist[i].deleteOnExit();         // 원래 파일 삭제
                            paths[i] = MoveFile2.getPath();      // 복원한 파일의 경로를 문자열로 저장
                            break;

                        default:        // 이전 경로가 다른 폴더였을 경우
                            File MoveFile = new File(move_path,fileRestore_folder + "_" + file.getName());     // 이동할 경로와 이동 후 파일명
                            filelist[i].renameTo(MoveFile);     // filelist에 있는 파일들 이름 변경
                            filelist[i].deleteOnExit();         // 원래 파일 삭제
                            paths[i] = MoveFile.getPath();      // 복원한 파일의 경로를 문자열로 저장
                            break;
                    }

                }
                MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);     // 미디어 스캐닝

                finish();       // 종료
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {     // NO 버튼 눌렀을 때
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 아무것도 하지 않음
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();       // AlertDialog 객체를 생성
        alert.show();       // 다이얼로그 창 출력
    }

    // 데이터 세팅 함수
    private void setData() {
        myAlbum = intent.getStringArrayListExtra("data");       // 데이터
        path_folder = intent.getStringExtra("path_folder");     // 폴더 경로
        isSecret = intent.getIntExtra("isSecret", 0);       // 숨김 사진이 있는지..
        duplicateImg = intent.getIntExtra("duplicateImg",0);        // 중복 사진이 있는지..
        itemTrashCanAdapter2 = new ItemTrashCanAdapter2(myAlbum);       // ItemTrashCanAdapter2 객체 생성
        isAlbum = intent.getIntExtra("ok",0);       // 앨범인지 확인하는 인텐트..?
        itemTrashCanAdapter3 = new ItemTrashCanAdapter3(myAlbum);       // ItemTrashCanAdapter3 객체 생성

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {     // Activity가 재개될때 필요한 초기화 작업을 수행
        super.onResume();
        MyAsyncTask myAsyncTask = new MyAsyncTask();        // myAsyncTask 객체 생성
        myAsyncTask.execute();      // AsyncTask 실행
    }

    // 매핑 함수
    private void mappingControls() {
        ryc_list_album = findViewById(R.id.ryc_list_album);     // 사진 목록
        toolbar_item_album = findViewById(R.id.toolbar_item_album);     // 툴바 메뉴
    }

    // 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)

            for(int i=0;i<myAlbum.size();i++) {     // myAlbum 사이즈 만큼 반복
                File file = new File(myAlbum.get(i));       // file 객체 생성
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
