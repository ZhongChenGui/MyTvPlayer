package com.learning.mytvplayer.cosutom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.leanback.widget.HorizontalGridView;

/**
 * Created by howe.zhong
 * on 2022/9/26  11:48
 */
public class ChangeVideoAttrGridView extends HorizontalGridView {
    private static final String TAG = "ChangeVideoAttrGridView";
    private View mDownView;

    public ChangeVideoAttrGridView(Context context) {
        this(context, null);
    }

    public ChangeVideoAttrGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeVideoAttrGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDownView(View view) {
        this.mDownView = view;
    }



    @Override
    public View focusSearch(View focused, int direction) {
        View view = super.focusSearch(focused, direction);
        if (direction == FOCUS_LEFT || direction == FOCUS_RIGHT) {
            if (view.getParent() != this) {
                return focused;
            }
        }
        if (direction == FOCUS_DOWN) {
            if (mDownView != null) {
                return mDownView;
            } else {
                Log.e(TAG, "focusSearch: 请 setDownView() 。。。。");
                return focused;
            }
        }
        return view;
    }
}
