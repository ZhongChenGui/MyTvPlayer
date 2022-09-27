package com.learning.mytvplayer.presenter;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

/**
 * Created by howe.zhong
 * on 2022/9/23  14:15
 */
public class ActionTitlePresenter extends Presenter {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView tv = new TextView(parent.getContext());
//        tv.setLayoutParams(new ViewGroup.LayoutParams(1920, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(30, 10, 30, 10);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.BLUE);
        tv.setFocusable(true);
        tv.setClickable(true);
        tv.setFocusableInTouchMode(true);
        tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                tv.setTextColor(Color.WHITE);
                if (hasFocus) {
                    tv.setBackgroundColor(Color.RED);
                } else {
                    tv.setBackgroundColor(Color.BLUE);
                }
            }
        });
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
