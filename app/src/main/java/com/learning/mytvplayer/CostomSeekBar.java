package com.learning.mytvplayer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by howe.zhong
 * on 2022/9/19  14:13
 */
public class CostomSeekBar extends LinearLayout implements SeekBar.OnSeekBarChangeListener {
    public Context mContext;
    public TextView mTv;
    public SeekBar mSeekBar;
    private OnSeekBarChannelListener mListener = null;

    public CostomSeekBar(Context context) {
        this(context, null);
    }

    public CostomSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CostomSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        this.setOrientation(VERTICAL);
        this.setClipToPadding(false);
        this.setClipChildren(false);
        initView();
    }

    private void initView() {
        mTv = new TextView(mContext);
        mTv.setTextColor(Color.BLUE);
        mTv.setGravity(Gravity.CENTER);
        mTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(mTv);

        mSeekBar = new SeekBar(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSeekBar.setMax(100);
        mSeekBar.setPadding(15, 0, 15, 0);
        mSeekBar.setFocusable(false);
        mSeekBar.setClickable(false);
        this.addView(mSeekBar, params);
        hideThumb();
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setSplitTrack(false);
    }

    public void setProcess(int process) {
        mSeekBar.setProgress(process);
    }

    public int getProcess() {
        return mSeekBar.getProgress();
    }

    public void hideThumb() {
        mSeekBar.getThumb().setAlpha(0);
        mTv.setAlpha(0);
    }

    public void showThumb() {
        mSeekBar.getThumb().setAlpha(255);
        mTv.setAlpha(1);
    }

    public void setMax(int max){
        mSeekBar.setMax(max);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int text = progress;
        if (mListener != null) {
            mListener.onSeekChannel(text);
        }

        //??????????????????
        float textWidth = mTv.getWidth();

        //??????seekbar????????????x??????
        float left = seekBar.getLeft();

        //?????????????????????
        float max = Math.abs(seekBar.getMax());

        //?????????thumb?????????,???seekbar???????????????,??????????????????seekbar ??????????????????????????????????????????????????????xml ?????????paddingStart ??? paddingEnd ??????????????????????????????,???????????????15dp;
        float thumb = dip2px(mContext, 15);


        //?????????1????????????text????????????????????? = (seekBar????????? - ??????????????????) / ??????progress??????
        float average = (((float) seekBar.getWidth()) - 2 * thumb) / max;

        //int to float
        float currentProgress = progress;

        //textview ????????????????????? = seekbar????????? + seekbar?????????????????? + ??????progress?????????????????? - textview???????????????(??????????????????)
        float pox = left - textWidth / 2 + thumb + average * currentProgress;
        mTv.setX(pox);
    }

    public void setText(String s) {
        mTv.setText(s);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mSeekBar.getThumb().setAlpha(0);
        mTv.setAlpha(0);
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface OnSeekBarChannelListener {
        void onSeekChannel(int progress);
    }

    public void setOnSeekBarChannelListener(OnSeekBarChannelListener listener) {
        this.mListener = listener;
    }

}
