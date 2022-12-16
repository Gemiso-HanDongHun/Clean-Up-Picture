package com.example.testgallery.activities.subActivities;


import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testgallery.R;
import com.example.testgallery.activities.mainActivities.ItemTrashCanActivity;
import com.example.testgallery.activities.mainActivities.PictureActivity;
import com.example.testgallery.activities.mainActivities.SlideShowActivity;
import com.example.testgallery.adapters.AlbumSheetAdapter;
import com.example.testgallery.adapters.ImageSelectAdapter;
import com.example.testgallery.models.Album;
import com.example.testgallery.models.Image;
import com.example.testgallery.utility.FileUtility;
import com.example.testgallery.utility.GetAllPhotoFromGallery;
import com.example.testgallery.utility.ListTransInterface;
import com.example.testgallery.utility.SubInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemTrashCanMultiSelectActivity extends AppCompatActivity implements ListTransInterface {
    private ArrayList<String> myAlbum;
    private RecyclerView ryc_album;
    private RecyclerView ryc_list_album;
    private Intent intent;
    private String album_name;
    Toolbar toolbar_item_album;
    private ArrayList<Image> listImageSelected;
    private static int REQUEST_CODE_SLIDESHOW = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_album);
        intent = getIntent();
        setUpData();            // setUpData 함수 호출
        mappingControls();      // mappingControls 함수 호출
        setData();      // setData 함수 호출
        setRyc();       // setRyc 함수 호출
        events();       // events 함수 호출
    }

    private void setUpData() {      // 선택한 사진들을 저장하는 객체 배열 선언
        listImageSelected = new ArrayList<>();
    }

    // 화면에 보여질 리싸이클러뷰 세팅
    private void setRyc() {
        album_name = intent.getStringExtra("name_1");       // 앨범 이름을 받음
        ryc_list_album.setLayoutManager(new GridLayoutManager(this, 3));        // 3열 그리드 형식으로 배치
        ImageSelectAdapter imageSelectAdapter = new ImageSelectAdapter(ItemTrashCanMultiSelectActivity.this);   // 어댑터 객체 생성
        List<Image> listImg = new ArrayList<>();

        // 앨범 크기만큼 반복하면서
        for(int i =0 ; i< myAlbum.size();i++) {
            Image img = new Image();        // img 객체 선언
            img.setThumb(myAlbum.get(i));
            img.setPath(myAlbum.get(i));
            listImg.add(img);       // 리스트에 이미지를 추가
        }

        // 어댑터에서 연결
        imageSelectAdapter.setData(listImg);
        imageSelectAdapter.setListTransInterface(this);
        ryc_list_album.setAdapter(imageSelectAdapter);
    }

    // 각종 이벤트 함수
    private void events() {
        // 툴바 이벤트
        toolbar_item_album.inflateMenu(R.menu.menu_top_multi_trash_can);
        toolbar_item_album.setTitle(album_name);

        // 뒤로가기 버튼
        toolbar_item_album.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_item_album.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 툴바 옵션
        toolbar_item_album.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.menuMultiDelete:      // 삭제 버튼 눌렀을 경우
                        deleteEvents();     // 삭제 이벤트 함수 호출
                        break;
                    case R.id.menuMultiRestore:     // 복구 버튼 눌렀을 경우
                        restoreEvent();     // 복구 이벤트 함수 호출
                        break;
                }

                return true;
            }
        });
    }

    // 복구 이벤트 함수 => 복구해서 이 전 폴더로 이동시키려면 파일 이름을 같이 변경해줘야됨
    private void restoreEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemTrashCanMultiSelectActivity.this);

        builder.setTitle("확인");
        builder.setMessage("사진을 복구하시겠습니까?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                // 복구 이벤트 처리
                RestoreAsync restoreAsync = new RestoreAsync();
                restoreAsync.execute();
                dialog.dismiss();
            }
        });

        // NO 버튼 눌렀을 때, 아무일도 일어나지 않음
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // 삭제 이벤트 함수 => 휴지통으로 이동시키려면 파일 이름을 같이 변경해줘야됨
    private void deleteEvents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemTrashCanMultiSelectActivity.this);

        builder.setTitle("확인");
        builder.setMessage("사진을 삭제하시겠습니까?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for(int i=0;i<listImageSelected.size();i++) {       // 선택된 사진들의 크기만큼 반복하면서
                    Uri targetUri = Uri.parse("file://" + listImageSelected.get(i).getPath());      // 선택된 사진들의 경로
                    File file = new File(targetUri.getPath());      // file 객체로 생성
                    if (file.exists()){     // 파일이 존재하면
                        file.delete();      // 파일 삭제
                    }
                }
                setResult(RESULT_OK);
                finish();
            }
        });

        // 아무일도 일어나지 않음
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // 데이터 세팅 함수
    private void setData() {
        myAlbum = intent.getStringArrayListExtra("data_1");     // 데이터를 인텐트로 받아옴
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // xml과 매핑
    private void mappingControls() {
        ryc_list_album = findViewById(R.id.ryc_list_album);
        toolbar_item_album = findViewById(R.id.toolbar_item_album);
    }

    // listImageSelected에 추가
    @Override
    public void addList(Image img) {
        listImageSelected.add(img);
    }

    // listImageSelected에서 삭제
    public void removeList(Image img) {
        listImageSelected.remove(img);
    }

    // 복구 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class RestoreAsync extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)
            String[] paths = new String[listImageSelected.size()];
            ArrayList<String> list = new ArrayList<>();
            int i = 0;

            for (Image img :listImageSelected){     // 선택된 사진들 반복하면서
                File imgFile = new File(img.getPath());


                int start = imgFile.getName().indexOf("_");     // 파일 이름에 있는 첫 '_' 인덱스 추출
                int end = imgFile.getName().indexOf("_", 4);    // 파일 이름에 있는 마지막 '_' 인덱스 추출

                // 파일 이름의 첫 '_' 인덱스와 마지막 '_' 인덱스 사이의 문자열 추출
                String fileRestore_folder = imgFile.getName().substring(start+1, end);

                File move_path = new File("/storage/emulated/0/Pictures/" + fileRestore_folder);      // 복원 후 폴더 경로

                // 이 전 폴더가 Pictures였을 경우, 복원 후 폴더 경로
                File move_path2 = new File("/storage/emulated/0/" + fileRestore_folder);

                switch (fileRestore_folder){
                    case "Pictures":        // 이전 경로가 Pictures였을 경우
                        File MoveFile1 = new File(move_path2,fileRestore_folder + "_" + imgFile.getName());     // 이동할 경로와 이동 후 파일명
                        list.add(MoveFile1.getPath());      // 리스트에 추가
                        imgFile.renameTo(MoveFile1);        // imgFile의 이름을 MoveFile1로 변경
                        imgFile.deleteOnExit();     // 기존 이미지 삭제
                        paths[i] = MoveFile1.getPath();     // 변경한 이미지 경로를 배열에 추가
                        i++;
                        break;

                    case "Screenshot":      // 이전 경로가 Screenshot였을 경우
                        File MoveFile2 = new File(move_path,fileRestore_folder + "s" + "_" + imgFile.getName());     // 이동할 경로와 이동 후 파일명
                        list.add(MoveFile2.getPath());      // 리스트에 추가
                        imgFile.renameTo(MoveFile2);        // imgFile의 이름을 MoveFile1로 변경
                        imgFile.deleteOnExit();     // 기존 이미지 삭제
                        paths[i] = MoveFile2.getPath();     // 변경한 이미지 경로를 배열에 추가
                        i++;
                        break;

                    default:        // 이전 경로가 다른 폴더였을 경우
                        File MoveFile = new File(move_path,fileRestore_folder + "_" + imgFile.getName());     // 이동할 경로와 이동 후 파일명
                        list.add(MoveFile.getPath());       // 리스트에 추가
                        imgFile.renameTo(MoveFile);     // imgFile의 이름을 MoveFile1로 변경
                        imgFile.deleteOnExit();     // 기존 이미지 삭제
                        paths[i] = MoveFile.getPath();      // 변경한 이미지 경로를 배열에 추가
                        i++;
                        break;
                }

            }
            // 미디어 스캐닝
            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {     // 스레드 작업이 모두 끝난 후에 수행할 작업(메인 스레드)
            super.onPostExecute(unused);
            setResult(RESULT_OK);
            finish();
        }
    }

}
