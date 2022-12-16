package com.example.testgallery.activities.mainActivities;



import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Explode;
import androidx.transition.Transition;

import com.example.testgallery.R;
import com.example.testgallery.activities.subActivities.ItemAlbumMultiSelectActivity;
import com.example.testgallery.adapters.ItemAlbumAdapter4;
import com.example.testgallery.adapters.ItemAlbumAdapter5;
import com.example.testgallery.models.Image;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.util.ArrayList;


public class WorldCUPActivity_result extends AppCompatActivity implements WC_DragDropListener   {
    private ItemAlbumAdapter4 adapter1;
    private ItemAlbumAdapter5 adapter2;
    private Intent intent;
    private ArrayList<String> Savelist;
    private ArrayList<String> Deletelist;
    private int position;
    int i = 1;
    private WC_result_PictureActivity wc_result_pictureActivity;
    private int REQUEST_TEST = 1;
    TextView savetext,deletetext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worldcup_result);

        intent = getIntent();
        Savelist = intent.getStringArrayListExtra("WC_savelist");
        Deletelist= intent.getStringArrayListExtra("WC_deletelist");



        init();
        getData();
        init2();
        getData2();
        textset();
    }


    private void textset(){

        savetext.setText("저장 (" + Savelist.size() + "/" + (Savelist.size()+Deletelist.size() )+ ")");
        deletetext.setText("삭제 (" + Deletelist.size() + "/" + (Savelist.size()+Deletelist.size()) + ")");
    }



    private void init() {
        RecyclerView recyclerView = findViewById(R.id.WC_recycle);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter1 = new ItemAlbumAdapter4();
        recyclerView.setAdapter(adapter1);


        LinearLayout linearLayout = findViewById(R.id.WC_SaveLayout);
        linearLayout.setOnDragListener(new DragListener());

        savetext = findViewById(R.id.WC_result_savetext);
        deletetext= findViewById(R.id.WC_result_deletetext);
    }

    private void init2() {
        RecyclerView recyclerView = findViewById(R.id.WC_recycle2);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter2 = new ItemAlbumAdapter5();
        recyclerView.setAdapter(adapter2);

     //   recyclerView.setOnDragListener(new DragListener());

        LinearLayout linearLayout = findViewById(R.id.WC_DeleteLayout);
        linearLayout.setOnDragListener(new DragListener());

    }

    private void getData() {
        adapter1.addItem(Savelist);
        adapter1.ItemAlbumAdapter4(this);
        adapter1.notifyDataSetChanged();
    }
    private void getData2() {
        adapter2.addItem(Deletelist,this);
        adapter2.notifyDataSetChanged();
    }


    public void OnClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_wc_save:
                AlertDialog.Builder builder = new AlertDialog.Builder(WorldCUPActivity_result.this);

                builder.setTitle("월드컵 결과 저장");
                builder.setMessage("삭제리스트에 위치한 사진은 삭제됩니다");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String[] paths = new String[Deletelist.size()];
                        ArrayList<String> list = new ArrayList<>();
                        int i = 0;

                        String folderName = "휴지통";      // 생성할 폴더 이름
                        String afterFilePath = "/storage/emulated/0/Pictures";     // 옮겨질 경로
                        String path = afterFilePath+"/"+folderName;     // 옮겨질 경로 + 생성할 폴더 이름 => 휴지통 경로
                        File dir = new File(path);

                        if (!dir.exists()) {        // 폴더 없으면 폴더 생성
                            dir.mkdirs();
                            for (String img :Deletelist){
                                File imgFile = new File(img);
                                File desImgFile = new File(path,"휴지통" + "_" + imgFile.getName());
                                list.add(desImgFile.getPath());
                                imgFile.renameTo(desImgFile);
                                imgFile.deleteOnExit();
                                paths[i] = desImgFile.getPath();
                                i++;
                            }
                            // 밑에 코드가 있어야 휴지통으로 이동한 복사본이 보임
                            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
                        } else {
                            for (String img :Deletelist){
                                File imgFile = new File(img);
                                File desImgFile = new File(path,"휴지통" + "_" + imgFile.getName());
                                list.add(desImgFile.getPath());
                                imgFile.renameTo(desImgFile);
                                imgFile.deleteOnExit();
                                paths[i] = desImgFile.getPath();
                                i++;
                            }
                            // 밑에 코드가 있어야 휴지통으로 이동한 복사본이 보임
                            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
                        }
                        finish();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                break;

            case R.id.btn_wc_delete:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(WorldCUPActivity_result.this);

                builder1.setTitle("확인");
                builder1.setMessage("월드컵을 취소하시겠습니까?");

                builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                finish();

                            }
                        });

                    builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert1 = builder1.create();
                    alert1.show();

                    break;
        }

    }



    @Override
    public void onLongClick(int position , View view) {

        this.position = position;
        view.setTag("imView");

        ClipData.Item item = new ClipData.Item(
                (CharSequence) view.getTag());

        String[] mimeTypes = { ClipDescription.MIMETYPE_TEXT_PLAIN };
        ClipData data = new ClipData(view.getTag().toString(),
                mimeTypes, item);
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                view);

        view.startDrag(data, // data to be dragged
                shadowBuilder, // drag shadow
                view, // 드래그 드랍할  Vew
                0 // 필요없은 플래그
        );

        view.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(int position, View view) {


        Intent intent = new Intent(this, WC_result_PictureActivity.class);
        intent.putStringArrayListExtra("data_list_Delete", Deletelist);
        intent.putStringArrayListExtra("data_list_Save", Savelist);
        intent.putStringArrayListExtra("data_list_Path", Savelist);
        intent.putExtra("pos",position);
        intent.putExtra("key",1);

        startActivityForResult(intent, REQUEST_TEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String img = data.getStringExtra("result");
        if (requestCode == REQUEST_TEST) {
            if (resultCode == RESULT_OK) {

                Savelist.remove(img);
                Deletelist.add(img);

                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();


            } else {   // RESULT_CANCEL

            }
        }
        else if (requestCode == 10) {

            if (resultCode == RESULT_OK) {

                Deletelist.remove(img);
                Savelist.add(img);

                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();


            } else {   // RESULT_CANCEL

            }

            }
        textset();
        }



    class DragListener implements View.OnDragListener {



        View view ;


        public boolean onDrag(View v, DragEvent event) {

            adapter1 = new ItemAlbumAdapter4();
            adapter2 = new ItemAlbumAdapter5();



            // 이벤트 시작
            switch (event.getAction()) {

                // 이미지를 드래그 시작될때
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("DragClickListener", "ACTION_DRAG_STARTED");


                    break;

                // 드래그한 이미지를 옮길려는 지역으로 들어왔을때
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d("DragClickListener", "ACTION_DRAG_ENTERED");
                    // 이미지가 들어왔다는 것을 알려주기 위해 배경이미지 변경

                    if (i == 1) {
                        i++;
                        view = v;
                        break;
                    } else if (view == v) {
                        i = 2;
                    }else if (view != v) {
                        i = 3;
                    }

                    v.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    break;

                // 드래그한 이미지가 영역을 빠져 나갈때
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d("DragClickListener", "ACTION_DRAG_EXITED");

                    v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    break;

                // 이미지를 드래그해서 드랍시켰을때
                case DragEvent.ACTION_DROP:
                    Log.d("DragClickListener", "ACTION_DROP");

                    if (v == findViewById(R.id.WC_SaveLayout)) {

                        if (i%2 == 0){

                        }else if (i%2 ==1){

                            Savelist.add(Deletelist.get(position));
                            Deletelist.remove(position);

                            adapter2.notifyItemRemoved(position);
                            adapter1.notifyDataSetChanged();

                        }

                    }else if (v == findViewById(R.id.WC_DeleteLayout)) {

                        if (i%2 == 0){

                        }else if (i%2 == 1) {

                            Deletelist.add(Savelist.get(position));
                            Savelist.remove(position);

                            adapter1.notifyItemRemoved(position);
                            adapter2.notifyDataSetChanged();
                        }

                    }else {

                        break;
                    }
                    break;

                case DragEvent.ACTION_DRAG_ENDED:


                    if(v== findViewById(R.id.WC_DeleteLayout)) {
                        Log.d("DragClickListener", "ACTION_DRAG_ENDED");
                        v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        textset();
                        finish();
                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                        Intent intent = getIntent(); //인텐트
                        startActivity(intent); //액티비티 열기
                        overridePendingTransition(0, 0);
                    }
                default:

                    break;
            }


            return true;
        }
    }

}









