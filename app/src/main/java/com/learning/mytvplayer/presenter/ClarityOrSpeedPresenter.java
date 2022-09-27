package com.learning.mytvplayer.presenter;

import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.Presenter;

import com.learning.mytvplayer.VideoController;
import com.learning.mytvplayer.cosutom.ActionItemView;

/**
 * Created by howe.zhong
 * on 2022/9/22  14:11
 */
public class ClarityOrSpeedPresenter extends Presenter {
    private final int TYPE_SPEED = 1;
    private final int TYPE_CLARITY = 2;
    private int currentType = TYPE_SPEED;
    private final VideoController mController;

    public ClarityOrSpeedPresenter(VideoController controller, int type) {
        this.mController = controller;
        this.currentType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        ActionItemView actionItemView = new ActionItemView(parent.getContext());
        return new ViewHolder(actionItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        if (viewHolder.view instanceof ActionItemView) {
            ActionItemView actionItemView = (ActionItemView) viewHolder.view;
            actionItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mController.onClickClarityOrSpeed(actionItemView, item, currentType);
                }
            });
            actionItemView.getContent().setText((String) item);
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
