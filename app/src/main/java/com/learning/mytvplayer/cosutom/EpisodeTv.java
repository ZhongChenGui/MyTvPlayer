package com.learning.mytvplayer.cosutom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by howe.zhong
 * on 2022/9/23  16:21
 */
public class EpisodeTv extends AppCompatTextView {
    private boolean mIsCheck = false;

    public EpisodeTv(Context context) {
        this(context, null);
    }

    public EpisodeTv(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public EpisodeTv(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isCheck() {
        return mIsCheck;
    }

    public void setCheck(boolean check) {
        mIsCheck = check;
        if (hasFocus()) {
            this.setTextColor(Color.WHITE);
        }
    }
}
