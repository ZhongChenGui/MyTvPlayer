package com.learning.mytvplayer.cosutom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.learning.mytvplayer.R;

/**
 * Created by howe.zhong
 * on 2022/9/22  14:00
 */
public class ActionItemView extends LinearLayout implements View.OnFocusChangeListener {
    private ImageView mIcon;
    private TextView mContent;
    private boolean isCheck = false;

    public ActionItemView(Context context) {
        this(context, null);
    }

    public ActionItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_play_action, this, true);
        this.setBackgroundColor(Color.parseColor("#BDBDBD"));
        initView();
        this.setFocusable(true);
        this.setClickable(true);
        this.setFocusableInTouchMode(true);
        this.setOnFocusChangeListener(this);
    }

    private void initView() {
        mIcon = findViewById(R.id.icon);
        mContent = findViewById(R.id.content);
    }

    public ImageView getIcon() {
        return mIcon;
    }

    public TextView getContent() {
        return mContent;
    }

    public void setCheck(boolean check) {
        this.isCheck = check;
        mIcon.setVisibility(isCheck ? VISIBLE : GONE);
        mContent.setTextColor(isCheck ? Color.GREEN : Color.WHITE);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mContent.setTextColor(Color.WHITE);
        if (hasFocus) {
            this.setBackgroundColor(Color.parseColor("#bc5a2b"));
        } else {
            if (isCheck) {
                mContent.setTextColor(Color.GREEN);
            }
            this.setBackgroundColor(Color.parseColor("#BDBDBD"));
        }
    }

    public String getText() {
        return (String) mContent.getText();
    }
}
