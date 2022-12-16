package com.example.testgallery.adapters;

import android.content.Context;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testgallery.R;
import com.example.testgallery.activities.mainActivities.WC_AdapterEndingListener;
import com.example.testgallery.activities.mainActivities.WC_LongClickListener;
import com.example.testgallery.activities.mainActivities.WC_MySwipeListner;
import com.example.testgallery.activities.mainActivities.WorldCUPActivity;


import java.util.ArrayList;

public class WC_recyclerAdapter extends RecyclerView.Adapter<WC_recyclerAdapter.ItemViewHolder> implements WC_MySwipeListner {

    public ArrayList<String> listData;
    ArrayList<String> DeleteData2 = new ArrayList<>();
    private Intent intent;
    public String pp;
    private WC_AdapterEndingListener listener;
    private WC_LongClickListener listener1;


    public void addItem(ArrayList<String> imageList, WC_AdapterEndingListener listener, WC_LongClickListener listener1) {

        listData = new ArrayList<>();
        this.listData = imageList;
        this.listener = listener;
        this.listener1 = listener1;
    }




    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wc_gogo, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
        this.pp = listData.get(position);
    }

    @Override
    public int getItemCount() {
        return listData.size();

    }

    @Override
    public void onItemSwipe(int position) {
        DeleteData2.add(listData.remove(position));

        getdeletelist(1);
        if (listData.size()<2) {
            getdeletelist(2);
        }

        listener.count(1);
        notifyItemRemoved(position);
    }

    public void getdeletelist(int endnum){
        listener.niceEnding(endnum, DeleteData2);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {


        private ImageView imageView;
        private Context context;

        ItemViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_wc_gogo);
            context = itemView.getContext();

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener1.onClick(getAdapterPosition());

                }
            });

        }

        void onBind(String img) {
            Glide.with(context).load(img).into(imageView);
        }
    }
}
