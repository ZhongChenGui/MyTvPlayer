package com.learning.mytvplayer.presenter;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

/**
 * Created by howe.zhong
 * on 2022/9/23  13:34
 */
public class ActionContentPresenter extends Presenter {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView tv = new TextView(parent.getContext());
        tv.setLayoutParams(new ViewGroup.LayoutParams(1920, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(10, 10, 10, 10);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.BLACK);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((TextView) viewHolder.view).setText((String) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
