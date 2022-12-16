package com.example.testgallery.activities.mainActivities;


import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.ParcelFileDescriptor;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.testgallery.R;
import com.example.testgallery.activities.mainActivities.data_favor.DataLocalManager;
import com.example.testgallery.activities.subActivities.ItemAlbumMultiSelectActivity;
import com.example.testgallery.adapters.AlbumSheetAdapter;
import com.example.testgallery.adapters.SearchRVAdapter;
import com.example.testgallery.adapters.SlideImageAdapter;
import com.example.testgallery.fragments.mainFragments.BottomSheetFragment;
import com.example.testgallery.fragments.mainFragments.PhotoFragment;
import com.example.testgallery.models.Album;
import com.example.testgallery.models.Image;
import com.example.testgallery.models.SearchRV;
import com.example.testgallery.utility.FileUtility;
import com.example.testgallery.utility.GetAllPhotoFromGallery;
import com.example.testgallery.utility.IClickListener;
import com.example.testgallery.utility.PictureInterface;
import com.example.testgallery.utility.SubInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TrashPictureActivity extends AppCompatActivity implements PictureInterface, SubInterface {
    private ViewPager viewPager_picture;
    private Toolbar toolbar_picture;
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView bottomNavigationView2;
    private BottomNavigationView bottomNavigationView3;
    private FrameLayout frame_viewPager;
    private ArrayList<String> imageListThumb;
    private ArrayList<String> imageListPath;
    private Intent intent;
    private int pos;
    private SlideImageAdapter slideImageAdapter;
    private PictureInterface activityPicture;
    private String imgPath;
    private String imageName;
    private String thumb;
    private Bitmap imageBitmap;
    private String title, link, displayedLink, snippet;
    private RecyclerView resultsRV;
    private SearchRVAdapter searchRVAdapter;
    //private ArrayList<SearchRV> searchRVArrayList;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView ryc_album;
    public static Set<String> imageListFavor = DataLocalManager.getListSet();


    @Override
    protected void onResume() {     // Activity가 재개될때 필요한 초기화 작업을 수행
        super.onResume();
        imageListFavor = DataLocalManager.getListSet();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        //Fix Uri file SDK link: https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi?answertab=oldest#tab-top

        // 모니터링
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mappingControls();      // mappingControls 함수 호출
        events();       // events 함수 호출
    }

    private void events() {
        setDataIntent();        // setDataIntent 함수 호출
        setUpToolBar();     // setUpToolBar 함수 호출
        setUpSilder();      // setUpSilder 함수 호출
        bottomNavigationViewEvents();       // bottomNavigationViewEvents 함수 호출
    }

    // 바텀네비게이션 이벤트 함수
    private void bottomNavigationViewEvents() {
        bottomNavigationView2.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Uri targetUri = Uri.parse("file://" + thumb);       // 파일 경로를 Uri 객체로 생성

                switch (item.getItemId()) {

                    case R.id.restoreTrashPic:      // 사진 복구 버튼
                        AlertDialog.Builder builder = new AlertDialog.Builder(TrashPictureActivity.this);

                        builder.setTitle("확인");
                        builder.setMessage("사진을 복구하시겠습니까?");

                        // YES 버튼 눌렀을 때
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(targetUri.getPath());      // 파일 경로

                                int start = file.getName().indexOf("_");    // 파일 이름에 있는 첫 '_' 인덱스 추출
                                int end = file.getName().indexOf("_", 4);   // 파일 이름에 있는 마지막 '_' 인덱스 추출

                                // 파일 이름의 첫 '_' 인덱스와 마지막 '_' 인덱스 사이의 문자열 추출
                                String fileRestore_folder = file.getName().substring(start+1, end);

                                File move_path = new File("/storage/emulated/0/Pictures/" + fileRestore_folder);      // 복원 후 폴더

                                // 이 전 폴더가 Pictures였을 경우, 복원 후 폴더 경로
                                File move_path2 = new File("/storage/emulated/0/" + fileRestore_folder);

                                switch (fileRestore_folder){
                                    case "Pictures":        // 이전 경로가 Pictures였을 경우
                                        File MoveFile1 = new File(move_path2,fileRestore_folder + "_" + file.getName());     // 이동할 경로와 이동 후 파일명

                                        file.renameTo(MoveFile1);     // filelist에 있는 파일들 이름 변경
                                        file.deleteOnExit();         // 원래 파일 삭제

                                        // 미디어 스캐닝
                                        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{move_path2+File.separator+MoveFile1.getName()}, null, null);

                                        finish();       // 종료
                                        break;

                                    case "Screenshot":      // 이전 경로가 Screenshot이었을 경우
                                        File MoveFile2 = new File(move_path,fileRestore_folder + "s" + "_" + file.getName());     // 이동할 경로와 이동 후 파일명

                                        file.renameTo(MoveFile2);     // filelist에 있는 파일들 이름 변경
                                        file.deleteOnExit();         // 원래 파일 삭제
                                        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{move_path+File.separator+MoveFile2.getName()}, null, null);

                                        finish();       // 종료
                                        break;

                                    default:        // 이전 경로가 다른 폴더였을 경우
                                        File MoveFile = new File(move_path,fileRestore_folder + "_" + file.getName());     // 이동할 경로와 이동 후 파일명

                                        file.renameTo(MoveFile);     // filelist에 있는 파일들 이름 변경
                                        file.deleteOnExit();         // 원래 파일 삭제
                                        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{move_path+File.separator+MoveFile.getName()}, null, null);

                                        finish();       // 종료
                                        break;
                                }
                            }
                        });

                        // NO 눌렀을 때
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // 아무것도 하지 않음
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();       // AlertDialog 객체를 생성
                        alert.show();       // 다이얼로그 창 출력

                        break;

                    case R.id.deleteTrashPic:       // 삭제 버튼 눌렀을 경우
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(TrashPictureActivity.this);

                        builder1.setTitle("확인");
                        builder1.setMessage("사진을 삭제하시겠습니까?");

                        // YES 버튼 눌렀을 때
                        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(targetUri.getPath());      // 파일 경로

                                if (file.exists()){     // 파일이 존재하면
                                    file.delete();      // 파일 삭제
                                }
                                finish();       // 종료
                            }
                        });

                        // NO 눌렀을 때
                        builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // 아무것도 하지 않음
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert1 = builder1.create();     // AlertDialog 객체를 생성
                        alert1.show();      // 다이얼로그 창 출력

                        break;
                }
                return true;
            }

        });
    }

    // 네비게이션바 출력 함수
    private void showNavigation(boolean flag) {
        if (!flag) {
            bottomNavigationView2.setVisibility(View.INVISIBLE);        // bottomNavigationView2 숨김
            toolbar_picture.setVisibility(View.INVISIBLE);      // toolbar_picture 숨김
        } else {
            bottomNavigationView2.setVisibility(View.VISIBLE);      // bottomNavigationView2 보이게
            toolbar_picture.setVisibility(View.VISIBLE);        // toolbar_picture 보이게
        }
    }

    // 툴바 함수
    private void setUpToolBar() {
        // 툴바 이벤트
        toolbar_picture.inflateMenu(R.menu.menu_top_trash_picture);
        setTitleToolbar("abc");

        // 뒤로가기 버튼
        toolbar_picture.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_picture.setNavigationOnClickListener(new View.OnClickListener() {       // 뒤로가기 버튼 누르면
            @Override
            public void onClick(View view) {
                finish();       // 종료
            }
        });
    }

    // 사진 좌우로 넘길때 실행하는 함수
    private void setUpSilder() {

        // 어댑터 세팅
        slideImageAdapter = new SlideImageAdapter();
        slideImageAdapter.setData(imageListThumb, imageListPath);
        slideImageAdapter.setContext(getApplicationContext());
        slideImageAdapter.setPictureInterface(activityPicture);
        viewPager_picture.setAdapter(slideImageAdapter);
        viewPager_picture.setCurrentItem(pos);

        // 좌우로 슬라이드
        viewPager_picture.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            // 페이지 넘길 때마다 호출됨
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                thumb = imageListThumb.get(position);
                imgPath = imageListPath.get(position);
                setTitleToolbar(thumb.substring(thumb.lastIndexOf('/') + 1));
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    // 인텐트로 데이터 받는 함수
    private void setDataIntent() {
        intent = getIntent();
        imageListPath = intent.getStringArrayListExtra("data_list_path");
        imageListThumb = intent.getStringArrayListExtra("data_list_thumb");
        pos = intent.getIntExtra("pos", 0);
        activityPicture = this;

    }

    // xml과 매핑하는 함수
    private void mappingControls() {
        viewPager_picture = findViewById(R.id.viewPager_picture);
        bottomNavigationView = findViewById(R.id.bottom_picture);
        bottomNavigationView2 = findViewById(R.id.bottom_trash_picture);
        bottomNavigationView3 = findViewById(R.id.bottom_wc_picture);
        toolbar_picture = findViewById(R.id.toolbar_picture);
        frame_viewPager = findViewById(R.id.frame_viewPager);

        bottomNavigationView.setVisibility(View.INVISIBLE);
        bottomNavigationView3.setVisibility(View.INVISIBLE);
    }

    // 툴바에 사진 이름 세팅
    public void setTitleToolbar(String imageName) {
        this.imageName = imageName;
        toolbar_picture.setTitle(imageName);
    }

    @Override
    public void actionShow(boolean flag) {
        showNavigation(flag);
    }

    // 추가하는 함수
    @Override
    public void add(Album album) {
        AddAlbumAsync addAlbumAsync = new AddAlbumAsync();
        addAlbumAsync.setAlbum(album);
        addAlbumAsync.execute();
    }

    // 앨범에 추가하는 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class AddAlbumAsync extends AsyncTask<Void, Integer, Void> {
        Album album;
        public void setAlbum(Album album) {
            this.album = album;
        }

        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)
            File directtory = new File(album.getPathFolder());      // 폴더 경로
            if(!directtory.exists()){       // 폴더가 없으면
                directtory.mkdirs();        // 폴더 생성
            }
            String[] paths = new String[1];     // 배열 생성
            File imgFile = new File(imgPath);       // 이동 전 이미지 경로
            File desImgFile = new File(album.getPathFolder(),album.getName()+"_"+imgFile.getName());        // 이동 후 이미지 경로
            imgFile.renameTo(desImgFile);       // 이름 변경
            imgFile.deleteOnExit();     // 원래 파일 삭제
            paths[0] = desImgFile.getPath();        // 이동 후 파일 경로를 배열에 저장

            // 즐겨찾기 여부 확인
            for (String imgFavor: imageListFavor){
                if(imgFavor.equals(imgFile.getPath())){     // 기존 이미지 파일이 즐겨찾기 돼있으면
                    imageListFavor.remove(imgFile.getPath());       // 기존 이미지를 리스트에서 삭제
                    imageListFavor.add(desImgFile.getPath());       // 이동된 이미지를 리스트에 추가
                    break;
                }
            }
            DataLocalManager.setListImg(imageListFavor);
            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);     // 미디어 스캐닝
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {     // 스레드 작업이 모두 끝난 후에 수행할 작업(메인 스레드)
            super.onPostExecute(unused);
            bottomSheetDialog.cancel();     // 바텀다이어로그 취소
        }
    }
}
