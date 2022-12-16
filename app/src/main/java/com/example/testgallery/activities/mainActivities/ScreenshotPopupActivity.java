package com.example.testgallery.activities.mainActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.view.Window;

import com.example.testgallery.R;
import com.example.testgallery.activities.mainActivities.data_favor.DataLocalManager;
import com.google.android.material.button.MaterialButtonToggleGroup;


import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

public class ScreenshotPopupActivity extends Activity {
    private String thumb;
    Button button;

    private String imgPath;
    boolean checkedIn30 = false;
    boolean checkedfavorite = false;
    Calendar cal = Calendar.getInstance();    // Calendar 객체 생성
    long todayMil = cal.getTimeInMillis();    // 현재 시간을 밀리초 단위로 생성
    long oneDayMil = 24 * 60 * 60 * 1000;    // 일 단위 => 하루 시간을 밀리초 단위로
    String path1 = new String();
    private Set<String> imgListFavor;
    private MaterialButtonToggleGroup materialButtonToggleGroup;

    Calendar fileCal = Calendar.getInstance();
    Date fileDate = null;
    private static final String LOG_TAG = ScreenshotPopupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        imgListFavor= DataLocalManager.getListSet();
        init();
        init2();


        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File path2 = new File("/storage/emulated/0/Pictures/Screenshots");
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

                    if(checkedfavorite == true){

                        if(imgListFavor.contains(list2[j].getPath())){
                            continue;
                        }
                    }

                    if(checkedIn30 == false){
                        if (diffDay < 30 && list2[j].exists()) {
                            list2[j].delete();
                            Log.d("Favorite" , " aaaa" + list2[j]);
                        }
                    }else if(checkedIn30 == true){
                        if (diffDay >= 30 && list2[j].exists()) {
                            list2[j].delete();
                            Log.d("Favorite" , " bbbb" + list2[j]);
                        }
                    }
                }

                finish();

            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        SingleSelectToggleGroup single1 = findViewById(R.id.toggleButton1);
        single1.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                if (checkedId == R.id.btnCategoryA) {
                    checkedIn30 = true;
                }else if (checkedId == R.id.btnCategoryB){
                    checkedIn30 = false;
                }
            }
        });
    }

    private void init2() {
        SingleSelectToggleGroup single2 = findViewById(R.id.toggleButton2);
        single2.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                if (checkedId == R.id.btnCategoryC) {
                    checkedfavorite = true;
                }else if (checkedId == R.id.btnCategoryD){
                    checkedfavorite = false;
                }
            }
        });
    }
}












































//    TextView textView;
//
//    String[] items = {"7일 이상 경과 삭제", "15일 이상 경과 삭제", "30일 이상 경과 삭제"};
//
////    String[] items = {"최근 사진 10장 삭제", "최근 사진 50장 삭제", "최근 사진 100장 삭제"};
//
//    @Override
//    protected  void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_popup);
//
//        Spinner spinner = findViewById(R.id.spinner1);
////        textView = findViewById(R.id.textView1);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                this,android.R.layout.simple_spinner_item, items
//        );
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
//
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                textView.setText(items[position]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                textView.setText("선택: " );
//            }
//        });
//
//    }
//
//}





//    TextView txtText;
//    Button btncls;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //타이틀바 없애기
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_popup);
//
//        //UI 객체생성
//        txtText = (TextView)findViewById(R.id.txtText);
//
//        //데이터 가져오기
//        Intent intent = getIntent();
//        String data = intent.getStringExtra("data");
//        txtText.setText(data);
//    }
//
//    //적용 버튼 클릭
//    public void mOnClose(View v){
//        //데이터 전달하기
//        Intent intent = new Intent();
//        intent.putExtra("result", "Close Popup");
//        setResult(RESULT_OK, intent);
//
//        //액티비티(팝업) 닫기
//        finish();
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //바깥레이어 클릭시 안닫히게
//        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onBackPressed() {
//        //안드로이드 백버튼 막기
//        return;
//    }
