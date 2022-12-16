package com.example.testgallery.activities.mainActivities;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.testgallery.R;
import com.example.testgallery.activities.mainActivities.data_favor.DataLocalManager;
import com.example.testgallery.adapters.ItemAlbumAdapter4;
import com.example.testgallery.adapters.ItemAlbumAdapter5;
import com.example.testgallery.adapters.SearchRVAdapter;
import com.example.testgallery.adapters.SlideImageAdapter;
import com.example.testgallery.models.Album;
import com.example.testgallery.utility.PictureInterface;
import com.example.testgallery.utility.SubInterface;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;



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

public class WC_result_PictureActivity extends AppCompatActivity implements PictureInterface, SubInterface {

        private ViewPager viewPager_picture;
        private Toolbar toolbar_picture;
        private BottomNavigationView bottomNavigationView;
        private BottomNavigationView bottomNavigationView2;
        private BottomNavigationView bottomNavigationView3;
        private FrameLayout frame_viewPager;
        private ArrayList<String> imageListThumb;
        private ArrayList<String> imageListPath;
        private ArrayList<String> imageListDelete;

        private Intent intent;
        private int pos,keynum;
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
        private ItemAlbumAdapter4 itemAlbumAdapter4;
        private ItemAlbumAdapter5 itemAlbumAdapter5;






        @Override
        protected void onResume() {
            super.onResume();
            imageListFavor = DataLocalManager.getListSet();
        }

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_picture);
            //Fix Uri file SDK link: https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi?answertab=oldest#tab-top
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            mappingControls();
            events();
            setResult(RESULT_CANCELED,intent);

        }

        private void events() {





            setDataIntent();
            setUpToolBar();
            setUpSilder();
            bottomNavigationViewEvents();
        }

        private void bottomNavigationViewEvents() {



            bottomNavigationView2.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Uri targetUri = Uri.parse("file://" + thumb);



                    if(keynum ==1){

                        switch (item.getItemId()) {

                            case R.id.gotoSAVE:


                                break;

                            case R.id.gotoDELETE:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(WC_result_PictureActivity.this);

                                builder1.setTitle("확인");
                                builder1.setMessage("사진을 삭제리스트로 이동시키겠습니까?");

                                builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {

                                    //    int i = imageListThumb.indexOf(thumb);
                                        Intent intent = new Intent();
                                        intent.putExtra("result", thumb);
                                        setResult(RESULT_OK,intent);
                                        finish();
                                        dialog.dismiss();
                                    }
                                });

                                builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setResult(RESULT_CANCELED,intent);
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alert1 = builder1.create();
                                alert1.show();

                                break;
                        }
                    }
                    else if (keynum ==2){
                        switch (item.getItemId()) {

                            case R.id.gotoSAVE:
                                AlertDialog.Builder builder = new AlertDialog.Builder(WC_result_PictureActivity.this);

                                builder.setTitle("확인");
                                builder.setMessage("사진을 저장리스트로 옮기시겠습니까?");

                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent();
                                        intent.putExtra("result", thumb);
                                        setResult(RESULT_OK,intent);
                                        finish();
                                        dialog.dismiss();

                                        dialog.dismiss();
                                    }
                                });

                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setResult(RESULT_CANCELED,intent);
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();

                                break;

                            case R.id.gotoDELETE:


                                break;
                        }
                    }




                    return true;
                }

            });
        }

        private void showNavigation(boolean flag) {
            if (!flag) {
                bottomNavigationView2.setVisibility(View.INVISIBLE);
                toolbar_picture.setVisibility(View.INVISIBLE);
            } else {
                bottomNavigationView2.setVisibility(View.VISIBLE);
                toolbar_picture.setVisibility(View.VISIBLE);
            }
        }


        private void setUpToolBar() {
            // Toolbar events
            toolbar_picture.inflateMenu(R.menu.menu_bottom_wcresult);
            setTitleToolbar("abc");

            // Show back button
            toolbar_picture.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
            toolbar_picture.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        private void setUpSilder() {

            slideImageAdapter = new SlideImageAdapter();
            slideImageAdapter.setData(imageListThumb, imageListPath);
            slideImageAdapter.setContext(getApplicationContext());
            slideImageAdapter.setPictureInterface(activityPicture);
            viewPager_picture.setAdapter(slideImageAdapter);
            viewPager_picture.setCurrentItem(pos);

            viewPager_picture.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
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

        private void setDataIntent() {
            intent = getIntent();
            imageListPath = intent.getStringArrayListExtra("data_list_Path");
            imageListThumb = intent.getStringArrayListExtra("data_list_Save");
            imageListDelete = intent.getStringArrayListExtra("data_list_Delete");
            keynum = intent.getIntExtra("key",0);
            pos = intent.getIntExtra("pos", 0);
            activityPicture = this;
            if(keynum ==1 ){
                BottomNavigationItemView item;
                item = findViewById(R.id.gotoSAVE);
               // item.setVisibility(View.INVISIBLE);
                item.setAlpha((float) 0.1);

            }else if(keynum ==2 ){
                BottomNavigationItemView item;
                item = findViewById(R.id.gotoDELETE);
                // item.setVisibility(View.INVISIBLE);
                item.setAlpha((float) 0.1);
            }

        }

        private void mappingControls() {
            viewPager_picture = findViewById(R.id.viewPager_picture);
            bottomNavigationView = findViewById(R.id.bottom_picture);
            bottomNavigationView2 = findViewById(R.id.bottom_wc_picture);
            bottomNavigationView3 = findViewById(R.id.bottom_trash_picture);

            toolbar_picture = findViewById(R.id.toolbar_picture);
            frame_viewPager = findViewById(R.id.frame_viewPager);

            bottomNavigationView.setVisibility(View.INVISIBLE);

            bottomNavigationView3.setVisibility(View.INVISIBLE);



        }

        public Boolean check(String  Path){
            for (String img: imageListFavor) {
                if(img.equals(Path)){
                    return true;
                }
            }
            return false;
        }

        public void setTitleToolbar(String imageName) {
            this.imageName = imageName;
            toolbar_picture.setTitle(imageName);
        }

        @Override
        public void actionShow(boolean flag) {
            showNavigation(flag);
        }

        @Override
        public void add(Album album) {
            WC_result_PictureActivity.AddAlbumAsync addAlbumAsync = new WC_result_PictureActivity.AddAlbumAsync();
            addAlbumAsync.setAlbum(album);
            addAlbumAsync.execute();
        }

        public class AddAlbumAsync extends AsyncTask<Void, Integer, Void> {
            Album album;
            public void setAlbum(Album album) {
                this.album = album;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                File directtory = new File(album.getPathFolder());
                if(!directtory.exists()){
                    directtory.mkdirs();
                    Log.e("File-no-exist",directtory.getPath());
                }
                String[] paths = new String[1];
                File imgFile = new File(imgPath);
                File desImgFile = new File(album.getPathFolder(),album.getName()+"_"+imgFile.getName());
                imgFile.renameTo(desImgFile);
                imgFile.deleteOnExit();
                paths[0] = desImgFile.getPath();
                for (String imgFavor: imageListFavor){
                    if(imgFavor.equals(imgFile.getPath())){
                        imageListFavor.remove(imgFile.getPath());
                        imageListFavor.add(desImgFile.getPath());
                        break;
                    }
                }
                DataLocalManager.setListImg(imageListFavor);
                MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                bottomSheetDialog.cancel();
            }
        }
    }


