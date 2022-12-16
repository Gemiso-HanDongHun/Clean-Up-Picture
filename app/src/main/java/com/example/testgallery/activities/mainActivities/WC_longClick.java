package com.example.testgallery.activities.mainActivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testgallery.R;

import java.util.ArrayList;


public class WC_longClick extends Activity {

    private ImageView img_back_wolrd_cup;
    ArrayList<String> imagelist ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wc_longclick);
        Intent intent = getIntent();

        imagelist = intent.getStringArrayListExtra("image");
        load();
    }
    private void event() {
        img_back_wolrd_cup = findViewById(R.id.img_back_world_cup);
        img_back_wolrd_cup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                imagelist.remove(0);
                finish();
            }
        });

    }
    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
    */

    public void load(){
        ImageView v = findViewById(R.id.WC_longimg);
        v.setImageDrawable(Drawable.createFromPath(imagelist.get(0)));
    }
}
