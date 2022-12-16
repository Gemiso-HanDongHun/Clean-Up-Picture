package com.example.testgallery.activities.subActivities;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testgallery.R;
import com.example.testgallery.activities.mainActivities.PictureActivity;
import com.example.testgallery.activities.mainActivities.SlideShowActivity;
import com.example.testgallery.activities.mainActivities.WorldCUPActivity;
import com.example.testgallery.activities.mainActivities.WorldCUPActivity_result;
import com.example.testgallery.adapters.AlbumSheetAdapter;
import com.example.testgallery.adapters.ImageSelectAdapter;
import com.example.testgallery.adapters.ItemAlbumAdapter;
import com.example.testgallery.models.Album;
import com.example.testgallery.models.Category;
import com.example.testgallery.models.Image;
import com.example.testgallery.utility.GetAllPhotoFromGallery;
import com.example.testgallery.utility.ListTransInterface;
import com.example.testgallery.utility.SubInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemAlbumMultiSelectActivity extends AppCompatActivity implements ListTransInterface, SubInterface {
    private ArrayList<String> myAlbum;
    private RecyclerView ryc_album;
    private RecyclerView ryc_list_album;
    private Intent intent;
    private String album_name;
    private String path_folder;
    Toolbar toolbar_item_album;
    private BottomSheetDialog bottomSheetDialog;
    private ArrayList<Image> listImageSelected;
    private static int REQUEST_CODE_SLIDESHOW = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_album);
        intent = getIntent();
        setUpData();        // setUpData 함수 호출
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
        album_name = intent.getStringExtra("name_1");       // 앨범 이름
        path_folder = intent.getStringExtra("path_folder");     // 폴더 경로
        ryc_list_album.setLayoutManager(new GridLayoutManager(this, 3));    // 3열 그리드 형식
        ImageSelectAdapter imageSelectAdapter = new ImageSelectAdapter(ItemAlbumMultiSelectActivity.this);
        List<Image> listImg = new ArrayList<>();

        // 앨범 크기만큼 반복하면서
        for(int i =0 ; i< myAlbum.size();i++) {
            Image img = new Image();
            img.setThumb(myAlbum.get(i));
            img.setPath(myAlbum.get(i));
            listImg.add(img);       // 리스트에 이미지 추가
        }

        // 어댑터 세팅
        imageSelectAdapter.setData(listImg);
        imageSelectAdapter.setListTransInterface(this);
        ryc_list_album.setAdapter(imageSelectAdapter);
    }

    // 각종 이벤트 함수
    private void events() {
        // 툴바 이벤트
        toolbar_item_album.inflateMenu(R.menu.menu_top_multi_album);
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
                        deleteEvents();     // deleteEvents 함수 호출
                        break;
                    case R.id.menuSlideshow:        // 슬라이드쇼 버튼 눌렀을 경우
                        slideShowEvents();  // slideShowEvents 함수 홏ㄹ
                        break;
                    case R.id.menu_move_image:      // 사진 이동 버튼 눌렀을 경우
                        moveEvent();        // moveEvent 함수 호출
                        break;
                    case R.id.menuGif:      // Gif 만들기 버튼 눌렀을 경우
                        gifEvents();        // gifEvents 함수 호출
                        break;
                    case R.id.menu_worldcup:    // 월드컵 버튼 눌렀을 경우
                        worldEvents();      // worldEvents 함수 호출
                        break;

                }

                return true;
            }
        });
    }

    // gif 이벤트
    private void gifEvents() {
        Toast.makeText(getApplicationContext(),"선택목록에 있는 GIF 사진을 제거합니다", Toast.LENGTH_SHORT).show();
        ArrayList<String> list_send_gif = new ArrayList<>();
        for(int i =0;i<listImageSelected.size();i++) {      // 선택된 사진들의 갯수만큼 반복
            if(!listImageSelected.get(i).getPath().contains(".gif"))    // 파일이 gif 파일이 아니면
                list_send_gif.add(listImageSelected.get(i).getPath());      // list_send_gif에 선택된 사진 추가
        }
        if(list_send_gif.size()!=0) {   // list_send_gif의 크기가 0이 아니면
            inputDialog(list_send_gif);     // list_send_gif를 매개변수로 하는 inputDialog 함수 호출

        }
        else    // 크기가 0이면
            // 토스트 메시지 출력
            Toast.makeText(getApplicationContext(),"빈 목록", Toast.LENGTH_SHORT).show();
    }

    // 입력 창 출력 => 사진 넘어가는 시간 입력해서 gif 파일로 만들어줌
    private void inputDialog(ArrayList<String> list_send_gif) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItemAlbumMultiSelectActivity.this);
        alertDialog.setTitle("간격 입력 지연");
        alertDialog.setMessage("지연: ");
        final EditText input = new EditText(ItemAlbumMultiSelectActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!TextUtils.isEmpty(input.getText())) {
                    Intent intent_gif = new Intent(ItemAlbumMultiSelectActivity.this, GifShowActivity.class);
                    intent_gif.putExtra("delay", Integer.valueOf(input.getText().toString()));
                    intent_gif.putStringArrayListExtra("list", list_send_gif);
                    startActivity(intent_gif);
                    dialogInterface.cancel();
                }
                else
                    Toast.makeText(getApplicationContext(),"전체 입력", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    // 사진 이동 이벤트
    private void moveEvent() {
        openBottomDialog();     // openBottomDialog 함수 호출
    }

    // 하단 다이얼로그 출력 => 이동할 앨범 선택할 수 있게 해줌
    private void openBottomDialog() {
        View viewDialog = LayoutInflater.from(ItemAlbumMultiSelectActivity.this).inflate(R.layout.layout_bottom_sheet_add_to_album, null);
        ryc_album = viewDialog.findViewById(R.id.ryc_album);
        ryc_album.setLayoutManager(new GridLayoutManager(this, 2));

        bottomSheetDialog = new BottomSheetDialog(ItemAlbumMultiSelectActivity.this);
        bottomSheetDialog.setContentView(viewDialog);
        ItemAlbumMultiSelectActivity.MyAsyncTask myAsyncTask = new ItemAlbumMultiSelectActivity.MyAsyncTask();
        myAsyncTask.execute();

    }

    // 삭제 이벤트 함수 => 사진 삭제할 때 호출됨 => 휴지통으로 이동시키려면 파일 이름을 같이 변경해줘야됨
    private void deleteEvents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemAlbumMultiSelectActivity.this);

        builder.setTitle("확인");
        builder.setMessage("사진을 삭제하시겠습니까?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String[] paths = new String[listImageSelected.size()];
                ArrayList<String> list = new ArrayList<>();
                int i = 0;

                String folderName = "휴지통";      // 생성할 폴더 이름
                String afterFilePath = "/storage/emulated/0/Pictures";     // 휴지통이 존재할 상위 경로
                String path = afterFilePath+"/"+folderName;     // 휴지통이 존재할 상위 경로 + 생성할 폴더 이름 => 휴지통 경로
                File dir = new File(path);

                if (!dir.exists()) {        // 휴지통 없으면 휴지통 생성
                    dir.mkdirs();
                    for (Image img :listImageSelected){     // 선택된 사진들만큼 반복
                        File imgFile = new File(img.getPath());     // 원본 파일 경로

                        // 파일 최종수정일자를 현재 시간으로 변경 => 휴지통 내에 있는 사진들, 일정시간이 지나면 자동삭제되게 하기 위해서
                        imgFile.setLastModified(System.currentTimeMillis());

                        String imgFile_path = imgFile.getPath().replace("/" + imgFile.getName(), "");       // 파일 경로
                        String parentFolder = imgFile_path.substring(imgFile_path.lastIndexOf("/") + 1);        // 삭제할 파일이 위치한 폴더 이름

                        File desImgFile = new File(path,"휴지통" + "_" + parentFolder + "_" + imgFile.getName());  // 휴지통으로 이동한 후 파일 이름
                        imgFile.renameTo(desImgFile);   // 원본 파일 이름 변경
                        imgFile.deleteOnExit();     // 원본 파일 삭제
                        paths[i] = desImgFile.getPath();    // 이름 변경한 파일을 배열에 추가
                        i++;
                    }
                    // 미디어 스캐닝
                    MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);

                } else {    // 휴지통이 존재하면
                    for (Image img :listImageSelected){     // 선택된 사진들만큼 반복
                        File imgFile = new File(img.getPath());

                        // 파일 최종수정일자를 현재 시간으로 변경 => 휴지통 내에 있는 사진들, 일정시간이 지나면 자동삭제되게 하기 위해서
                        imgFile.setLastModified(System.currentTimeMillis());

                        String imgFile_path = imgFile.getPath().replace("/" + imgFile.getName(), "");       // 파일 경로
                        String parentFolder = imgFile_path.substring(imgFile_path.lastIndexOf("/") + 1);        // 삭제할 파일이 위치한 폴더 이름

                        File desImgFile = new File(path,"휴지통" + "_" + parentFolder + "_" + imgFile.getName());  // 휴지통으로 이동한 후 파일 이름
                        imgFile.renameTo(desImgFile);       // 원본 파일 이름 변경
                        imgFile.deleteOnExit();      // 원본 파일 삭제
                        paths[i] = desImgFile.getPath();    // 이름 변경한 파일을 배열에 추가
                        i++;
                    }
                    // 미디어 스캐닝
                    MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
                }
                finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 아무것도 하지 않음
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // 슬라이드쇼 이벤트
    private void slideShowEvents() {
        Intent intent = new Intent(ItemAlbumMultiSelectActivity.this, SlideShowActivity.class);
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<listImageSelected.size();i++) {
            list.add(listImageSelected.get(i).getThumb());
        }
        intent.putStringArrayListExtra("data_slide", list);
        intent.putExtra("name", "Slide Show");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUEST_CODE_SLIDESHOW);
    }

    // 사진 월드컵 이벤트
    private void worldEvents() {
        Intent intent = new Intent(ItemAlbumMultiSelectActivity.this, WorldCUPActivity.class);
        ArrayList<String> list3 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        for(int i=0;i<listImageSelected.size();i++) {
            list3.add(listImageSelected.get(i).getPath());
            list2.add(listImageSelected.get(i).getThumb());

        }

        if(list2.size() < 2 ){
            Toast.makeText(this,"사진을 두개 이상 선택하세요",Toast.LENGTH_SHORT).show();

        }
        else if(list2.size() >= 2 ){

            intent.putStringArrayListExtra("data_worldlist", list2);
            intent.putStringArrayListExtra("data_worldcuplist", list3);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 1);
            finish();
        }

    }

    // 데이터 세팅
    private void setData() {myAlbum = intent.getStringArrayListExtra("data_1"); }

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


    @Override
    // listImageSelected에 추가
    public void addList(Image img) {
        listImageSelected.add(img);
    }

    // listImageSelected에서 제거
    public void removeList(Image img) {
        listImageSelected.remove(img);
    }


    @Override
    // 앨범에 사진 추가
    public void add(Album album) {
        ItemAlbumMultiSelectActivity.AddAlbumAsync addAlbumAsync = new ItemAlbumMultiSelectActivity.AddAlbumAsync();
        addAlbumAsync.setAlbum(album);
        addAlbumAsync.execute();
    }

    // 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        private AlbumSheetAdapter albumSheetAdapter;
        private List<Album> listAlbum;
        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)

            // 모든 사진을 listImage 리스트에 저장
            List<Image> listImage = GetAllPhotoFromGallery.getAllImageFromGallery(ItemAlbumMultiSelectActivity.this);

            listAlbum = getListAlbum(listImage);
            if(path_folder!=null)
            for(int i =0;i<listAlbum.size();i++) {      // 사진의 갯수만큼 반복하면서
                if(path_folder.equals(listAlbum.get(i).getPathFolder())) {      // 폴더 경로가 listAlbum에 들어있는 폴더 경로와 같으면
                    listAlbum.remove(i);    // 리스트에서 제거
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {     // 스레드 작업이 모두 끝난 후에 수행할 작업(메인 스레드)
            super.onPostExecute(unused);

            // 어댑터 세팅
            albumSheetAdapter = new AlbumSheetAdapter(listAlbum, ItemAlbumMultiSelectActivity.this);
            albumSheetAdapter.setSubInterface(ItemAlbumMultiSelectActivity.this);
            ryc_album.setAdapter(albumSheetAdapter);
            bottomSheetDialog.show();
        }
        @NonNull
        private List<Album> getListAlbum(List<Image> listImage) {
            List<String> ref = new ArrayList<>();
            List<Album> listAlbum = new ArrayList<>();

            for (int i = 0; i < listImage.size(); i++) {
                String[] _array = listImage.get(i).getThumb().split("/");
                String _pathFolder = listImage.get(i).getThumb().substring(0, listImage.get(i).getThumb().lastIndexOf("/"));
                String _name = _array[_array.length - 2];
                if (!ref.contains(_pathFolder)) {
                    ref.add(_pathFolder);
                    Album token = new Album(listImage.get(i), _name);
                    token.setPathFolder(_pathFolder);
                    token.addItem(listImage.get(i));
                    listAlbum.add(token);
                } else {
                    listAlbum.get(ref.indexOf(_pathFolder)).addItem(listImage.get(i));
                }
            }

            return listAlbum;
        }
    }

    // 앨범에 추가하는 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class AddAlbumAsync extends AsyncTask<Void, Integer, Void> {
        Album album;
        ArrayList<String> list;

        @Override
        protected void onPreExecute() {     // 작업이 실행되기 직전에 UI 스레드에 의해 호출. 초기화 작업 역할
            super.onPreExecute();
            list = new ArrayList<>();
        }
        public void setAlbum(Album album) {
            this.album = album;
        }

        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)
            String[] paths = new String[listImageSelected.size()];
            int i =0;

            // 사진 이동 코드 => 사진을 이동시키려면 사진 이름을 같이 변경해줘야됨
            for (Image img :listImageSelected){
                File imgFile = new File(img.getPath());
                File desImgFile = new File(album.getPathFolder(),album.getName()+"_"+imgFile.getName());
                list.add(desImgFile.getPath());
                imgFile.renameTo(desImgFile);
                imgFile.deleteOnExit();
                paths[i] = desImgFile.getPath();
                i++;
            }

            // 미디어 스캐닝
            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {     // 스레드 작업이 모두 끝난 후에 수행할 작업(메인 스레드)
            super.onPostExecute(unused);
            bottomSheetDialog.cancel();
            Intent resultIntent = new Intent();

            resultIntent.putStringArrayListExtra("list_result", list);
            resultIntent.putExtra("move", 1);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
