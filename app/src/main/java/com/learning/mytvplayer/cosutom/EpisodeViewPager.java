package com.learning.mytvplayer.cosutom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by howe.zhong
 * on 2022/9/23  15:21
 */
public class EpisodeViewPager extends ViewPager {
    private boolean scroll = true;

    public EpisodeViewPager(@NonNull Context context) {
        this(context, null);
    }

    public EpisodeViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public View focusSearch(View focused, int direction) {
        if (direction == FOCUS_UP || direction == FOCUS_DOWN) {
            return super.focusSearch(focused, direction);
        }
        return focused;
    }

    @Override

    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, scroll);
    }

    @Override

    public void setCurrentItem(int item) {
        super.setCurrentItem(item, scroll);
    }

//设置方法

    public void setScroll(boolean scroll) {

        this.scroll = scroll;

    }

}
