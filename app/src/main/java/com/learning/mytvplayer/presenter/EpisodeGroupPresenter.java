package com.learning.mytvplayer.presenter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

/**
 * Created by howe.zhong
 * on 2022/9/20  17:20
 */
public class EpisodeGroupPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(20, 5, 20, 5);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.parseColor("#55000000"));
        tv.setFocusable(true);
        tv.setClickable(true);
        tv.setGravity(Gravity.CENTER);
        tv.setFocusableInTouchMode(true);
        tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                tv.setTextColor(Color.WHITE);
                if (hasFocus) {
                    tv.setBackgroundColor(Color.parseColor("#FFB400"));
                    return;
                }
                tv.setBackgroundColor(Color.parseColor("#55000000"));
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
