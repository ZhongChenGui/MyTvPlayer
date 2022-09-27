package com.learning.mytvplayer.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howe.zhong
 * on 2022/9/19  16:32
 */
public class PlayActionTipAdapter extends RecyclerView.Adapter<PlayActionTipAdapter.InnerHolder> {

    private List<String> mList = new ArrayList<>();

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setTextColor(Color.parseColor("#e1e1e1"));
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.rightMargin = 20;
        tv.setLayoutParams(layoutParams);
        return new InnerHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        if (holder.itemView instanceof TextView) {
            ((TextView) holder.itemView).setText(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setData(List<String> list) {
        mList.clear();
        mList.addAll(0, list);
        notifyItemRangeChanged(0, list.size());
    }


}
