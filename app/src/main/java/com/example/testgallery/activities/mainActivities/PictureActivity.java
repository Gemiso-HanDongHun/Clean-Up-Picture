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
import android.os.ParcelFileDescriptor;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.ParcelFileDescriptor;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
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
import com.example.testgallery.utility.ImageUtil;
import com.example.testgallery.utility.PictureInterface;
import com.example.testgallery.utility.SubInterface;
import com.example.testgallery.utility.ToastUtil;
import com.example.testgallery.viewmodel.PhotoViewModel;
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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PictureActivity extends AppCompatActivity implements PictureInterface, SubInterface {
    private ViewPager viewPager_picture;
    private Toolbar toolbar_picture;
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView bottomNavigationView2;
    private BottomNavigationView bottomNavigationView3;
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
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView ryc_album;
    public static Set<String> imageListFavor = DataLocalManager.getListSet();

    TextView colorName;
    TextView colorHexa;
    TextView colorRGB;
    ImageView imageView;


    PhotoViewModel viewModel;

    @Override
    protected void onResume() {     // Activity가 재개될때 필요한 초기화 작업을 수행
        super.onResume();
        imageListFavor = DataLocalManager.getListSet();
    }

    private void updateDisplay() {      // 색 뽑는 코드구나 이거
        viewModel.getImage().observe(this, image -> {
            colorName.setText(image.getColor());
            Log.d("TAGG", "viewModel 00000000000000000000000000000000000000000000000000 - =" + image.getColor());
            colorHexa.setText(image.getHexadecimal());
            Log.d("TAGG", "viewModel 00000000000000000000000000000000000000000000000000 - =" + image.getHexadecimal());

            colorRGB.setText(image.getRGB());
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        //Fix Uri file SDK link: https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi?answertab=oldest#tab-top

        // 모니터링
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        imageView = (ImageView) findViewById(R.id.imgPhoto);
        colorName = (TextView) findViewById(R.id.color_name_gallery);
        colorHexa = (TextView) findViewById(R.id.color_hexa_gallery);
        colorRGB = (TextView) findViewById(R.id.color_rgb_gallery);
        intent = getIntent();

        mappingControls();      // mappingControls 함수 호출

        events();       // events 함수 호출
    }

    // 각종 이벤트 모아둔 함수
    private void events() {
        setDataIntent();
        setUpToolBar();
        setUpSilder();
        initializeUi();
        bottomNavigationViewEvents();
    }


    void initializeUi() {
        viewModel = ViewModelProviders.of(this).get(PhotoViewModel.class);

        try {
            Uri imageUri = Uri.parse("file://" + imageListPath.get(pos));

            Log.d("TAg","000000000000000000000000000ddddddddddddddddddddddd = " + imageUri);
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            float aspectRatio = selectedImage.getWidth() /
                (float) selectedImage.getHeight();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = Math.round(width / aspectRatio);
            Bitmap.createScaledBitmap(selectedImage, width, height, false);

            // imageView.setImageBitmap(resizeBitmap(selectedImage));
            Log.d("TAg","000000000000000000000000000ddddddddddddddddddddddd = " + selectedImage);
            Log.d("TAg","000000000000000000000000000ddddddddddddddddddddddd = " + viewModel);
            if (!viewModel.isImageTaken()) {
                viewModel.setImage(ImageUtil.mapBitmapToImage(selectedImage));
            } else {
                updateDisplay();
            }
            updateDisplay();
        } catch (FileNotFoundException e) {
            Log.d("TAg","33333333333333333333333333333333333333 = " + imageListPath);
            e.printStackTrace();
            ToastUtil.callShortToast(PictureActivity.this, R.string.unknown);
        }
    }

    // 하단 네비게이션 메뉴 이벤트 함수
    private void bottomNavigationViewEvents() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Uri targetUri = Uri.parse("file://" + thumb);       // 파일 경로를 Uri 객체로 생성

                switch (item.getItemId()) {

                    case R.id.sharePic:     // 공유 버튼 눌렀을 경우

                        if(thumb.contains("gif")){
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/*");
                            share.putExtra(Intent.EXTRA_STREAM, targetUri);
                            startActivity( Intent.createChooser(share, "사진을 지인들에게 공유해보세요") );
                        }
                        else {
                            Drawable mDrawable = Drawable.createFromPath(imgPath);
                            Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                            String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "사진 묘사", null);
                            thumb = thumb.replaceAll(" ", "");

                            Uri uri = Uri.parse(path);
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(shareIntent, "사진 공유"));
                        }

                        break;

                    case R.id.editPic:      // 편집 버튼 눌렀을 경우
                        Intent editIntent = new Intent(PictureActivity.this, DsPhotoEditorActivity.class);      // 편집 액티비티

                        if(imgPath.contains("gif")){        // 이미지 경로에 "gif"라는 문자열이 포함되어 있으면 => gif 파일이면

                            // 토스트 메시지 출력
                            Toast.makeText(PictureActivity.this,"GIF 이미지를 편집할 수 없습니다",Toast.LENGTH_SHORT).show();
                        }
                        else{       // gif 파일이 아니면
                            // 데이터 설정
                            editIntent.setData(Uri.fromFile(new File(imgPath)));
                            // 출력 폴더 설정
                            editIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Simple Gallery");
                            // 툴바 색상 설정
                            editIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#FF000000"));
                            // 배경 색상 설정
                            editIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#FF000000"));
                            // 액티비티 출력
                            startActivity(editIntent);
                        }

                        break;

                    case R.id.starPic:      // 즐겨찾기 버튼 눌렀을 경우

                        if(!imageListFavor.add(imgPath)){       // imageListFavor에 추가하지 않았을 경우
                            imageListFavor.remove(imgPath);     // 리스트에서 제거
                        }

                        DataLocalManager.setListImg(imageListFavor);
                        Toast.makeText(PictureActivity.this, imageListFavor.size()+"", Toast.LENGTH_SHORT).show();
                        if(!check(imgPath)){        // 이미지 경로가 imageListFavor 리스트에 들어있지 않으면 => 즐겨찾기가 되어있지 않으면

                            // 하단 메뉴에 있는 별을 빈 별로 표시
                            bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_star);
                        }
                        else{       // 리스트에 들어있으면 => 즐겨찾기가 되어있으면

                            // 별을 색칠한 별로 표시
                            bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_star_red);

                        }
                        break;

                    case R.id.deletePic:        // 삭제 버튼 눌렀을 경우 => 휴지통으로 이동시키려면 파일 이름을 같이 변경해줘야됨

                        AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);

                        builder.setTitle("확인");
                        builder.setMessage("사진을 삭제하시겠습니까?");

                        // YES 버튼 눌렀을 때
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(targetUri.getPath());      // 파일 경로

                                if (file.exists()) {        // 파일이 존재하면
                                    File img = new File(imgPath);

                                    String folderName = "휴지통";      // 생성할 폴더 이름
                                    String afterFilePath = "/storage/emulated/0/Pictures";      // 휴지통 상위 폴더
                                    String path = afterFilePath+"/"+folderName;     // 휴지통 경로
                                    File dir = new File(path);

                                    if(!dir.exists()) {     // 휴지통이 없으면
                                        dir.mkdir();        // 휴지통 생성
                                    }

                                    File imgFile = new File(img.getPath());

                                    // 파일 최종수정일자를 현재 시간으로 변경 => 휴지통 내에 있는 사진들, 일정시간이 지나면 자동삭제되게 하기 위해서
                                    imgFile.setLastModified(System.currentTimeMillis());

                                    // 파일 경로
                                    String imgFile_path = imgFile.getPath().replace("/" + imgFile.getName(), "");

                                    // 삭제할 파일이 있는 폴더 이름
                                    String parentFolder = imgFile_path.substring(imgFile_path.lastIndexOf("/") + 1);

                                    // 휴지통으로 이동시키고 난 후 파일 이름
                                    File desImgFile = new File(path,"휴지통" + "_" + parentFolder + "_" + imgFile.getName());

                                    imgFile.renameTo(desImgFile);   // 파일 이름 변경
                                    imgFile.deleteOnExit();     // 기존 파일 삭제
                                    desImgFile.getPath();       // 변경한 파일 경로

                                    // 미디어 스캐닝
                                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{path+File.separator+desImgFile.getName()}, null, null);
                                }
                                finish();       // 종료
                            }
                        });

                        // NO 버튼 눌렀을 때
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // 아무것도 하지 않음
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                        break;
                }
                return true;
            }

        });
    }

    // 하단 메뉴바 출력 함수
    private void showNavigation(boolean flag) {
        if (!flag) {
            bottomNavigationView.setVisibility(View.INVISIBLE);
            toolbar_picture.setVisibility(View.INVISIBLE);
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
            toolbar_picture.setVisibility(View.VISIBLE);
        }
    }

    // 툴바 이벤트 함수
    private void setUpToolBar() {
        // 툴바 이벤트
        toolbar_picture.inflateMenu(R.menu.menu_top_picture);
        setTitleToolbar("abc");

        // 뒤로가기 버튼
        toolbar_picture.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_picture.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 툴바 메뉴 클릭
        toolbar_picture.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.menuInfo:     // 사진 정보 버튼 눌렀을 경우
                        Uri targetUri = Uri.parse("file://" + thumb);       // 파일 경로
                        if (targetUri != null) {        // 경로가 널값이 아니면
                            showExif(targetUri);        // 해당 파일의 정보 출력
                        }
                        break;
                    case R.id.menuAddAlbum:     // 앨범 추가 버튼 눌렀을 경우
                        openBottomDialog();     // openBottomDialog 함수 호출 => 추가할 앨범 선택
                        break;
                    case R.id.menuAddSecret:        // 사진 숨기기/표시 버튼 눌렀을 경우
                        AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);

                        builder.setTitle("확인");
                        builder.setMessage("이미지를 숨기거나 표시하시겠습니까?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String scrPath = Environment.getExternalStorageDirectory()+File.separator+".secret";    // 숨김 사진 경로
                                File scrDir = new File(scrPath);
                                if(!scrDir.exists()){   // 숨김 폴더가 없을 경우
                                    Toast.makeText(PictureActivity.this, "비밀 앨범을 만들지 않았습니다", Toast.LENGTH_SHORT).show();
                                }
                                else{   // 숨김 폴더가 있으면
                                    FileUtility fu = new FileUtility();
                                    File img = new File(imgPath);
                                    if(!(scrPath+File.separator+img.getName()).equals(imgPath)){    // 이미지가 이미 숨겨져 있으면
                                        fu.moveFile(imgPath,img.getName(),scrPath);
                                        Toast.makeText(PictureActivity.this, "이미지가 숨겨진 상태입니다", Toast.LENGTH_SHORT).show();
                                    }
                                    else{       // 숨겨져 있지 않으면
                                        String outputPath = Environment.getExternalStorageDirectory()+File.separator+"DCIM" + File.separator + "Restore";
                                        File folder = new File(outputPath);
                                        File imgFile = new File(img.getPath());     // 변경 전 이미지
                                        File desImgFile = new File(outputPath,imgFile.getName());       // 변경 후 이미지
                                        if(!folder.exists()) {      // 폴더가 없을경우
                                            folder.mkdir();     // 폴더 생성
                                        }
                                        imgFile.renameTo(desImgFile);       // 파일 이름 변경
                                        imgFile.deleteOnExit();     // 기존 파일 삭제
                                        desImgFile.getPath();       // 변경한 파일 경로

                                        // 미디어 스캐닝
                                        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{outputPath+File.separator+desImgFile.getName()}, null, null);
                                    }
                                }
                                Intent intentResult = new Intent();
                                intentResult.putExtra("path_img", imgPath);     // 이미지 경로 인텐트로 전송
                                setResult(RESULT_OK, intentResult);
                                finish();
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

                        break;
                    case R.id.setWallpaper:     // 배경화면 설정 버튼 눌렀을 경우

                        // 배경화면 지정
                        Uri uri_wallpaper = Uri.parse("file://" + thumb);
                        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setDataAndType(uri_wallpaper, "image/*");
                        intent.putExtra("mimeType", "image/*");
                        startActivity(Intent.createChooser(intent, "Set as:"));
                }

                return true;
            }
        });
    }

    // 이미지 파일의 Exif 데이터 처리 함수
    private void showExif(Uri photoUri) {
        if (photoUri != null) {

            ParcelFileDescriptor parcelFileDescriptor = null;

            try {
                // 이미지를 가져오기 위해 ParcelFileDescriptor를 통해 접근
                parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                ExifInterface exifInterface = new ExifInterface(fileDescriptor);

                BottomSheetDialog infoDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                View infoDialogView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_info,
                                (LinearLayout) findViewById(R.id.infoContainer),
                                false
                        );
                TextView txtInfoProducer = (TextView) infoDialogView.findViewById(R.id.txtInfoProducer);
                TextView txtInfoSize = (TextView) infoDialogView.findViewById(R.id.txtInfoSize);
                TextView txtInfoModel = (TextView) infoDialogView.findViewById(R.id.txtInfoModel);
                TextView txtInfoFlash = (TextView) infoDialogView.findViewById(R.id.txtInfoFlash);
                TextView txtInfoFocalLength = (TextView) infoDialogView.findViewById(R.id.txtInfoFocalLength);
                TextView txtInfoAuthor = (TextView) infoDialogView.findViewById(R.id.txtInfoAuthor);
                TextView txtInfoTime = (TextView) infoDialogView.findViewById(R.id.txtInfoTime);
                TextView txtInfoName = (TextView) infoDialogView.findViewById(R.id.txtInfoName);
                TextView txtInfoGps1 = (TextView) infoDialogView.findViewById(R.id.txtInfoGps1);
                TextView txtInfoGps11 = (TextView) infoDialogView.findViewById(R.id.txtInfoGps11);
                TextView txtInfoGps22 = (TextView) infoDialogView.findViewById(R.id.txtInfoGps22);
                TextView txtInfoGps2 = (TextView) infoDialogView.findViewById(R.id.txtInfoGps2);

                txtInfoName.setText(imageName);
                txtInfoProducer.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE));
                txtInfoSize.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_IMAGE_LENGTH) + "x" + exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_IMAGE_WIDTH));
                txtInfoModel.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL));
                txtInfoFlash.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_FLASH));
                txtInfoFocalLength.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_FOCAL_LENGTH));
                txtInfoAuthor.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_ARTIST));
                txtInfoTime.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME));
                txtInfoGps1.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_GPS_LATITUDE));
                txtInfoGps11.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_GPS_LATITUDE_REF));
                txtInfoGps22.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_GPS_LONGITUDE));
                txtInfoGps2.setText(exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_GPS_LONGITUDE_REF));


                infoDialog.setContentView(infoDialogView);
                infoDialog.show();


                parcelFileDescriptor.close();


            } catch (FileNotFoundException e) {     // 오류 처리
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "오류 발생:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {       // 오류 처리
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "오류 발생:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            }


        } else {        // photoUri가 없으면
            Toast.makeText(getApplicationContext(),
                    "photoUri가 존재하지 않습니다",
                    Toast.LENGTH_LONG).show();
        }
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

            // 페이지 넘길 때마다 호출됨
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                thumb = imageListThumb.get(position);
                imgPath = imageListPath.get(position);
                setTitleToolbar(thumb.substring(thumb.lastIndexOf('/') + 1));
                if(!check(imgPath)){    // 이미지 경로가 imageListFavor 리스트에 들어있지 않으면 => 즐겨찾기가 되어있지 않으면
                    bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_star);
                }
                else{       // 리스트에 들어있으면 => 즐겨찾기가 되어있으면
                    bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_star_red);
                }
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
        toolbar_picture = findViewById(R.id.toolbar_picture);
        bottomNavigationView2 = findViewById(R.id.bottom_trash_picture);
        bottomNavigationView2.setVisibility(View.INVISIBLE);
        bottomNavigationView3 = findViewById(R.id.bottom_wc_picture);
        bottomNavigationView3.setVisibility(View.INVISIBLE);

    }

    // 즐겨찾기 유무 체크 함수
    public Boolean check(String  Path){
        for (String img: imageListFavor) {
            if(img.equals(Path)){
                return true;
            }
        }
        return false;
    }

    // 툴바에 사진 이름 세팅
    public void setTitleToolbar(String imageName) {
        this.imageName = imageName;
        toolbar_picture.setTitle(imageName);

    }

    // 바텀 다이어로그 출력 함수
    private void openBottomDialog() {
        View viewDialog = LayoutInflater.from(PictureActivity.this).inflate(R.layout.layout_bottom_sheet_add_to_album, null);
        ryc_album = viewDialog.findViewById(R.id.ryc_album);
        ryc_album.setLayoutManager(new GridLayoutManager(this, 2));

        bottomSheetDialog = new BottomSheetDialog(PictureActivity.this);
        bottomSheetDialog.setContentView(viewDialog);
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
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

    // 작업을 백그라운드 스레드에서 실행할 수 있게 해줌
    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        private AlbumSheetAdapter albumSheetAdapter;
        private List<Album> listAlbum;
        @Override
        protected Void doInBackground(Void... voids) {      // 스레드가 수행할 작업(생성된 스레드)

            // 모든 사진을 listImage 리스트에 저장
            List<Image> listImage = GetAllPhotoFromGallery.getAllImageFromGallery(PictureActivity.this);

            listAlbum = getListAlbum(listImage);
            String path_folder = imgPath.substring(0, imgPath.lastIndexOf("/"));    // 사진이 위치한 폴더 경로
            for(int i =0;i<listAlbum.size();i++) {      // 사진의 갯수만큼 반복하면서
                if(path_folder.equals(listAlbum.get(i).getPathFolder())) {  // 폴더 경로가 listAlbum에 들어있는 폴더 경로와 같으면
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
            albumSheetAdapter = new AlbumSheetAdapter(listAlbum, PictureActivity.this);
            albumSheetAdapter.setSubInterface(PictureActivity.this);
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

            // 사진 이동 코드 => 사진을 이동시키려면 사진 이름을 같이 변경해줘야됨
            File directtory = new File(album.getPathFolder());      // 폴더 경로
            if(!directtory.exists()){   // 폴더가 없으면
                directtory.mkdirs();    // 폴더 생성
            }
            String[] paths = new String[1];     // 배열 생성
            File imgFile = new File(imgPath);       // 이동 전 이미지 경로
            File desImgFile = new File(album.getPathFolder(),album.getName()+"_"+imgFile.getName());    // 이동 후 이미지 경로
            imgFile.renameTo(desImgFile);   // 이름 변경
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

            // 미디어 스캐닝
            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {     // 스레드 작업이 모두 끝난 후에 수행할 작업(메인 스레드)
            super.onPostExecute(unused);
            bottomSheetDialog.cancel();     // 바텀 다이얼로그 닫기
        }
    }
}
