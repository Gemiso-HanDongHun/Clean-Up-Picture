package com.example.testgallery.activities.mainActivities;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class WC_listItemDecorater extends RecyclerView.ItemDecoration {

    private final int mPadding;

    public WC_listItemDecorater(int a_padding) {
        mPadding = a_padding;
    }

    @Override
    public void getItemOffsets(Rect a_outRect, View a_view, RecyclerView a_parent, RecyclerView.State a_state) {
        super.getItemOffsets(a_outRect, a_view, a_parent, a_state);
        a_outRect.top = mPadding;
        a_outRect.bottom = mPadding;
        a_outRect.left = mPadding;
        a_outRect.right = mPadding;
    }
}

