package com.example.testgallery.activities.subActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.testgallery.R;
import com.example.testgallery.activities.mainActivities.PictureActivity;
import com.example.testgallery.activities.mainActivities.SlideShowActivity;
import com.example.testgallery.activities.mainActivities.WorldCUPActivity;
import com.example.testgallery.adapters.AlbumSheetAdapter;
import com.example.testgallery.adapters.CategoryMultiAdapter;
import com.example.testgallery.models.Album;
import com.example.testgallery.models.Category;
import com.example.testgallery.models.Image;
import com.example.testgallery.utility.FileUtility;
import com.example.testgallery.utility.GetAllPhotoFromGallery_AllPhotosTab;
import com.example.testgallery.utility.ListTransInterface;
import com.example.testgallery.utility.SubInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiSelectImage extends AppCompatActivity implements ListTransInterface, SubInterface {
    private RecyclerView ryc_list_album;
    private Toolbar toolbar_item_album;
    private List<Category> listImg;
    private List<Image> imageList;
    private CategoryMultiAdapter categoryMultiAdapter;
    private ArrayList<Image> listImageSelected;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView ryc_album;
    EditText edittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_select_image);
        setUpData();        // setUpData 함수 호출
        mappingControls();      // mappingControls 함수 호출
        addEvents();        // addEvents 함수 호출
    }

    private void setUpData() {      // 선택한 사진들을 저장하는 객체 배열 선언
        listImageSelected = new ArrayList<>();
    }

    // 추가 이벤트
    private void addEvents() {
        setRyc();       // setRyc 함수 호출
        // 툴바 이벤트
        eventToolBar();     // eventToolBar 함수 호출
    }

    // 화면에 보여질 리싸이클러뷰 세팅
    private void setRyc() {
        categoryMultiAdapter = new CategoryMultiAdapter(MultiSelectImage.this);
        categoryMultiAdapter.setListTransInterface(MultiSelectImage.this);

        // 화면 배치
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MultiSelectImage.this, RecyclerView.VERTICAL, false);
        ryc_list_album.setLayoutManager(linearLayoutManager);

        // 어댑터 세팅
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    // 툴바 이벤트
    private void eventToolBar() {
        toolbar_item_album.inflateMenu(R.menu.menu_multi_select);
        toolbar_item_album.setTitle("다중 선택");

        toolbar_item_album.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.menuCreateAlbum:      // 앨범 생성 메뉴

                        // 앨범에 추가할 사진들 선택하고, 앨범 이름 입력하는 작업을 함
                        AlertDialog.Builder alert = new AlertDialog.Builder(MultiSelectImage.this);
                        edittext = new EditText(MultiSelectImage.this);
                        alert.setMessage("앨범 이름 입력");
                        alert.setView(edittext);
                        alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(!TextUtils.isEmpty(edittext.getText())) {
                                    MultiSelectImage.CreateAlbumAsyncTask createAlbumAsyncTask = new MultiSelectImage.CreateAlbumAsyncTask();
                                    createAlbumAsyncTask.execute();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Title null", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alert.show();

                        break;
                    case R.id.menuMultiDelete:      // 삭제 메뉴
                        deleteEvents();
                        break;
                    case R.id.menuSlideshow:        // 슬라이드쇼 메뉴
                        slideShowEvents();
                        break;
                    case R.id.menuAddAlbum:     // 앨범으로 추가 메뉴
                        openBottomDialog();
                        break;
                    case R.id.menuHide:     // 사진 숨기기/표시 메뉴
                        if(listImageSelected.size()!=0)
                        hideEvents();
                        else 
                            Toast.makeText(getApplicationContext(), "Danh sách trống", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menuGif:      // Gif 만들기 메뉴
                        gifEvents();
                        break;
                    case R.id.menu_worldcup:    // 사진 월드컵 메뉴
                        worldEvents();
                        break;
                }
                return true;
            }
        });
    }

    // gif 이벤트
    private void gifEvents() {
        Toast.makeText(getApplicationContext(),"앱에서 선택 목록에 있는 움짤을 없앱니다.", Toast.LENGTH_SHORT).show();
        ArrayList<String> list_send_gif = new ArrayList<>();
        for(int i =0;i<listImageSelected.size();i++) {
            if(!listImageSelected.get(i).getPath().contains(".gif"))
            list_send_gif.add(listImageSelected.get(i).getPath());
        }
        if(list_send_gif.size()!=0) {
            inputDialog(list_send_gif);
        }
        else
            Toast.makeText(getApplicationContext(),"빈 목록입니다.", Toast.LENGTH_SHORT).show();
    }

    // 사진 월드컵 이벤트
    private void worldEvents() {
        Intent intent = new Intent(MultiSelectImage.this, WorldCUPActivity.class);

        ArrayList<String> list2 = new ArrayList<>();
        for(int i=0;i<listImageSelected.size();i++) {

            list2.add(listImageSelected.get(i).getThumb());
        }

        if(list2.size() < 2 ){
            Toast.makeText(this,"사진을 두개 이상 선택하세요",Toast.LENGTH_SHORT).show();

        }
        else if(list2.size() >= 2 ){

            intent.putStringArrayListExtra("data_worldlist", list2);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 1);
            finish();
        }

    }

    // 입력 창 출력 => 사진 넘어가는 시간 입력해서 gif 파일로 만들어줌
    private void inputDialog(ArrayList<String> list_send_gif) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MultiSelectImage.this);
        alertDialog.setTitle("Enter delay");
        alertDialog.setMessage("Delay(millisecond): ");
        final EditText input = new EditText(MultiSelectImage.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("100");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!TextUtils.isEmpty(input.getText())) {
                    Intent intent_gif = new Intent(MultiSelectImage.this, GifShowActivity.class);
                    intent_gif.putExtra("delay", Integer.valueOf(input.getText().toString()));
                    intent_gif.putStringArrayListExtra("list", list_send_gif);
                    startActivity(intent_gif);
                    dialogInterface.cancel();
                }
                else
                    Toast.makeText(getApplicationContext(),"Please enter in full", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    // 사진 숨김 이벤트
    private void hideEvents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MultiSelectImage.this);

        builder.setTitle("확인");
        builder.setMessage("사진을 숨기거나 표시하시겠습니까?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                // 사진을 시크릿 폴더에 추가하는 작업
                AddSecretAsync addSecretAsync = new AddSecretAsync();
                addSecretAsync.execute();
                dialog.dismiss();
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

    // xml과 매핑
    private void mappingControls() {
        ryc_list_album = findViewById(R.id.ryc_list_album);
        toolbar_item_album = findViewById(R.id.toolbar_item_album);
    }

    // 카테고리 가져오는 함수 => 모든 사진 탭에서 날짜 나타냄
    private List<Category> getListCategory() {
        List<Category> categoryList = new ArrayList<>();
        int categoryCount = 0;
        imageList = GetAllPhotoFromGallery_AllPhotosTab.getAllImageFromGallery(MultiSelectImage.this);

        try {
            categoryList.add(new Category(imageList.get(0).getDateTaken(),new ArrayList<>()));
            categoryList.get(categoryCount).addListGirl(imageList.get(0));
            for(int i=1;i<imageList.size();i++){
                if(!imageList.get(i).getDateTaken().equals(imageList.get(i-1).getDateTaken())){
                    categoryList.add(new Category(imageList.get(i).getDateTaken(),new ArrayList<>()));
                    categoryCount++;
                }
                categoryList.get(categoryCount).addListGirl(imageList.get(i));
            }
            return categoryList;
        } catch (Exception e){
            return null;
        }
    }

    // 삭제 이벤트 => 사진 삭제할 때 호출됨 => 휴지통으로 이동시키려면 파일 이름을 같이 변경해줘야됨
    private void deleteEvents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MultiSelectImage.this);

        builder.setTitle("확인");
        builder.setMessage("사진을 삭제하시겠습니까?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String[] paths = new String[listImageSelected.size()];
                int i = 0;

                String folderName = "휴지통";      // 생성할 폴더 이름
                String afterFilePath = "/storage/emulated/0/Pictures";     // 옮겨질 경로
                String path = afterFilePath+"/"+folderName;     // 옮겨질 경로 + 생성할 폴더 이름 => 휴지통 경로
                File dir = new File(path);

                if (!dir.exists()) {        // 휴지통 없으면 폴더 생성
                    dir.mkdirs();
                    for (Image img :listImageSelected){
                        File imgFile = new File(img.getPath());

                        // 파일 최종수정일자를 현재 시간으로 변경 => 휴지통 내에 있는 사진들, 일정시간이 지나면 자동삭제되게 하기 위해서
                        imgFile.setLastModified(System.currentTimeMillis());

                        String imgFile_path = imgFile.getPath().replace("/" + imgFile.getName(), "");       // 파일 경로
                        String parentFolder = imgFile_path.substring(imgFile_path.lastIndexOf("/") + 1);        // 삭제할 파일이 위치한 폴더 이름

                        File desImgFile = new File(path,"휴지통" + "_" + parentFolder + "_" + imgFile.getName());  // 휴지통으로 이동한 후 파일 이름
                        imgFile.renameTo(desImgFile);       // 원본 파일 이름 변경
                        imgFile.deleteOnExit();     // 원본 파일 삭제
                        paths[i] = desImgFile.getPath();        // 이름 변경한 파일을 배열에 추가
                        i++;
                    }
                    // 미디어 스캐닝
                    MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
                } else {
                    for (Image img :listImageSelected){
                        File imgFile = new File(img.getPath());

                        // 파일 최종수정일자를 현재 시간으로 변경 => 휴지통 내에 있는 사진들, 일정시간이 지나면 자동삭제되게 하기 위해서
                        imgFile.setLastModified(System.currentTimeMillis());

                        String imgFile_path = imgFile.getPath().replace("/" + imgFile.getName(), "");       // 파일 경로
                        String parentFolder = imgFile_path.substring(imgFile_path.lastIndexOf("/") + 1);        // 삭제할 파일이 위치한 폴더 이름

                        File desImgFile = new File(path,"휴지통" + "_" + parentFolder + "_" + imgFile.getName());  // 휴지통으로 이동한 후 파일 이름
                        imgFile.renameTo(desImgFile);       // 원본 파일 이름 변경
                        imgFile.deleteOnExit();     // 원본 파일 삭제
                        paths[i] = desImgFile.getPath();        // 이름 변경한 파일을 배열에 추가
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
        Intent intent = new Intent(MultiSelectImage.this, SlideShowActivity.class);
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<listImageSelected.size();i++) {
            list.add(listImageSelected.get(i).getThumb());
        }
        intent.putStringArrayListExtra("data_slide", list);
        intent.putExtra("name", "Slide Show");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // 바텀 다이얼로그 호출
    private void openBottomDialog() {
        View viewDialog = LayoutInflater.from(MultiSelectImage.this).inflate(R.layout.layout_bottom_sheet_add_to_album, null);
        ryc_album = viewDialog.findViewById(R.id.ryc_album);
        ryc_album.setLayoutManager(new GridLayoutManager(this, 2));

        bottomSheetDialog = new BottomSheetDialog(MultiSelectImage.this);
        bottomSheetDialog.setContentView(viewDialog);
        AddSyncTask addSyncTask = new AddSyncTask();
        addSyncTask.execute();
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

    // 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)
            listImg = getListCategory();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {     // 스레드 작업이 모두 끝난 후에 수행할 작업(메인 스레드)
            super.onPostExecute(unused);
            // 카테고리 세팅 => 날짜 정보 세팅
            categoryMultiAdapter.setData(listImg);
            ryc_list_album.setAdapter(categoryMultiAdapter);
        }
    }

    // 앨범 생성 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class CreateAlbumAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)
            String albumName = edittext.getText().toString();
            String albumPath = Environment.getExternalStorageDirectory()+ File.separator+"Pictures" + File.separator +albumName;
            File directtory = new File(albumPath);
            if(!directtory.exists()){
                directtory.mkdirs();
                Log.e("File-no-exist",directtory.getPath());
            }
            String[] paths = new String[listImageSelected.size()];
            int i =0;

            // 선택된 사진들을 생성한 앨범에 추가하는 코드 => 이름 변경 필수
            for (Image img :listImageSelected){
                File imgFile = new File(img.getPath());
                File desImgFile = new File(albumPath,albumName+"_"+imgFile.getName());
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
            finish();
        }
    }

    // // 앨범에 사진 추가
    @Override
    public void add(Album album) {
        MultiSelectImage.AddAlbumAsync addAlbumAsync = new MultiSelectImage.AddAlbumAsync();
        addAlbumAsync.setAlbum(album);
        addAlbumAsync.execute();
    }

    // 추가하는 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class AddSyncTask extends AsyncTask<Void, Integer, Void> {
        private AlbumSheetAdapter albumSheetAdapter;
        private List<Album> listAlbum;
        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)
            List<Image> listImage = GetAllPhotoFromGallery_AllPhotosTab.getAllImageFromGallery(MultiSelectImage.this);
            listAlbum = getListAlbum(listImage);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {     // 스레드 작업이 모두 끝난 후에 수행할 작업(메인 스레드)
            super.onPostExecute(unused);

            // 어댑터 세팅
            albumSheetAdapter = new AlbumSheetAdapter(listAlbum, MultiSelectImage.this);
            albumSheetAdapter.setSubInterface(MultiSelectImage.this);
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
            finish();
        }
    }

    // 시크릿 앨범에 추가하는 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class AddSecretAsync extends AsyncTask<Void, Integer, Void> {
        private ArrayList<String> list;
        @Override
        protected void onPreExecute() {     // 작업이 실행되기 직전에 UI 스레드에 의해 호출. 초기화 작업 역할
            super.onPreExecute();
            list = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)
            String scrPath = Environment.getExternalStorageDirectory()+File.separator+".secret";
            File scrDir = new File(scrPath);
            if(!scrDir.exists()){
                Toast.makeText(MultiSelectImage.this, "비밀 앨범을 만들지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                for(int i=0;i<listImageSelected.size();i++) {
                    Image img = listImageSelected.get(i);
                    FileUtility fu = new FileUtility();
                    File imgFile = new File(img.getPath());
                    list.add(img.getPath());
                    fu.moveFile(img.getPath(), imgFile.getName(), scrPath);
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {     // 스레드 작업이 모두 끝난 후에 수행할 작업(메인 스레드)
            super.onPostExecute(unused);
            Intent intentResult = new Intent();
            intentResult.putStringArrayListExtra("list_hide",list);
            setResult(RESULT_OK, intentResult);
            finish();
        }
    }
}
