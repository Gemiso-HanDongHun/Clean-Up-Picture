package com.example.testgallery.activities.mainActivities;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testgallery.R;
import com.example.testgallery.adapters.WC_recyclerAdapter;
import com.example.testgallery.adapters.WC_recyclerlistAdapter;
import com.example.testgallery.models.WC_itemlist;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class WorldCUPActivity<image> extends AppCompatActivity implements WC_AdapterEndingListener,WC_LongClickListener {
    private SliderView sliderView;
    private ImageView img_back_wolrd_cup, img_help;

    private Intent intent;
    private ArrayList<String> list ;
    private ArrayList<String> WCGRIDlist;
    private Long mLastClickTime = 0L;
    private static final String logTag = "ggoog";
    private WC_recyclerAdapter adapter,adapter1;
    private WC_recyclerlistAdapter listadapter;
    private RecyclerView recyclerView;
    private RecyclerView recyclerlistView;
    ArrayList<String> Savelist = new ArrayList<>();
    ArrayList<String> Deletelist = new ArrayList<>();
    private TextView WClistblider;
    ArrayList<WC_itemlist> itemlists  = new ArrayList<>();
    private int endnum = 0;
    TextView WC_title_textView;

    ImageView WC_image1;
    ImageView WC_image2;
    int i;
    int j = 2;
    int k;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worldcup);
        intent = getIntent();
        list = new ArrayList<>();
        WCGRIDlist = new ArrayList<>();
        list = intent.getStringArrayListExtra("data_worldlist");
        WCGRIDlist.addAll(list);


        i=3;
        mappingControls();
        event();
        init();
        getData();



    }

    public void result() {
        Intent intent = new Intent(WorldCUPActivity.this, WorldCUPActivity_result.class);
        intent.putStringArrayListExtra("WC_savelist", Savelist);
        intent.putStringArrayListExtra("WC_deletelist", Deletelist);
        startActivity(intent);

        finish();
    }


    private void event() {

        img_back_wolrd_cup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(WorldCUPActivity.this);

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
            }
        });

        img_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorldCUPActivity.this, WC_HelpExample.class);
                startActivity(intent);
            }
        });


    }

    private void mappingControls() {

        img_back_wolrd_cup = findViewById(R.id.img_back_world_cup);
        img_help = findViewById(R.id.img_help);
        WClistblider = findViewById(R.id.item_wc_list_blind);
        WC_title_textView = findViewById(R.id.WC_play_textname);
    }


    private void init() {


        recyclerView = findViewById(R.id.WC_recycler_play);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1 = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically(){
                return false;
            }
        };

        recyclerView.setLayoutManager(linearLayoutManager1);
        adapter = new WC_recyclerAdapter();
        recyclerView.setAdapter(adapter);


        itemlists = new ArrayList<WC_itemlist>();
        recyclerlistView = findViewById(R.id.WC_recycler_playlist);
        LinearLayoutManager listLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false){
            @Override
            public boolean canScrollHorizontally(){
                return false;
            }
        };
        recyclerlistView.setLayoutManager(listLayoutManager1);
        listadapter = new WC_recyclerlistAdapter(itemlists);
        recyclerlistView.setAdapter(listadapter);

        WC_listItemDecorater itemDecorater = new WC_listItemDecorater(5);
        recyclerlistView.addItemDecoration(itemDecorater);


      //  swipeHelper1 = new ItemTouchHelper(new WC_MySwipeHelper(adapter1));
     //   swipeHelper1.attachToRecyclerView(recyclerView);




        WC_MySwipeHelper swipeHelper= new WC_MySwipeHelper(WorldCUPActivity.this,recyclerView,300,adapter) {
            @Override
            public void instantiatrMyButton(RecyclerView.ViewHolder viewHolder, List<WC_MySwipeHelper.MyButton> buffer) {
                buffer.add(new MyButton(WorldCUPActivity.this,
                        "SAVE",
                        0,
                        R.drawable.wc_downloadimg,
                        Color.parseColor("#B2C7D9"),

                        new WC_MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                Savelist.add(list.get(viewHolder.getAdapterPosition()));
                               // list.remove(viewHolder.getAdapterPosition());
                                count(1);



                                list.remove(viewHolder.getAdapterPosition());


                                if (list.size()<2){
                                    adapter.getdeletelist(2);
                                }

                                // 해당 항목 삭제
                                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());    // Adapter에 알려주기.
                                listadapter.notifyDataSetChanged();

                            }




                        }));
            }

        };// swipeHelper



    }

    private void getData() {


        adapter.addItem(list,this,this );
        adapter.notifyDataSetChanged();

        for (int i =0;i< WCGRIDlist.size(); i++){
            WC_itemlist itemlist = new WC_itemlist();
            itemlist.setWClistimg(WCGRIDlist.get(i));
            itemlist.setblindnum(1);
            itemlists.add(itemlist);
        }

        listadapter.notifyDataSetChanged();

        WC_title_textView.setText("사진 월드컵 (2/"+WCGRIDlist.size()+")");

    }


    @Override
    public void niceEnding(int endnum,ArrayList<String> Deletelist) {


        if (endnum== 2)
        {
            Savelist.add(list.get(0));
            this.Deletelist = Deletelist;
            result();
        }else if (endnum ==1){
            this.Deletelist = Deletelist;
        }

    }

    @Override
    public void count(int count) {
        ArrayList<String> comparelist;
        comparelist = new ArrayList<>();

        comparelist.addAll(Deletelist);
        comparelist.addAll(Savelist);


        for (int i =0;i< WCGRIDlist.size(); i++) {

            if(comparelist != null) {
                for (String j : comparelist) {
                    if(j == WCGRIDlist.get(i)){
                        WC_itemlist itemlist = new WC_itemlist();
                        itemlist = itemlists.get(i);
                        itemlist.setblindnum(0);
                        itemlists.set(i,itemlist);
                        break;
                    }

                }
            }

        }
        i += count;
        recyclerlistView.scrollToPosition(i);
        listadapter.notifyDataSetChanged();


        if ( i-1<=WCGRIDlist.size())
        WC_title_textView.setText("사진 월드컵 (" + (i-1)  +"/"+WCGRIDlist.size()+")");
    }


    @Override
    public void onClick(int num) {
        
        ArrayList<String> image = new ArrayList<>();
        image.add(list.get(num));

        Intent intent = new Intent(this,WC_longClick.class);
        intent.putStringArrayListExtra("image",image);
        startActivityForResult(intent,1);
    }
}





