package com.example.measureit.Part_RECORD;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.measureit.R;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

public class MyRecyclerAdapter extends BaseQuickAdapter<RecyclerModel, BaseViewHolder> {

    public MyRecyclerAdapter(@LayoutRes int layoutResId, @Nullable List<RecyclerModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helpler, RecyclerModel item) {
        helpler.setText(R.id.recordNumber, item.getRecordNumber())
                .setText(R.id.recordMode, item.getRecordMode())
                .setText(R.id.createdTime, item.getCreatedTime())
                .addOnClickListener(R.id.recordMenu);
    }

}
