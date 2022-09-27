package com.learning.mytvplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.learning.mytvplayer.adapter.MyAdapter;
import com.learning.mytvplayer.adapter.ViewPagerAdapter;
import com.learning.mytvplayer.cosutom.EpisodeTv;
import com.learning.mytvplayer.cosutom.VideoEpisodeView;
import com.learning.mytvplayer.presenter.ActionContentPresenter;
import com.learning.mytvplayer.presenter.ActionTitlePresenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalFocusChangeListener {

    private static final String TAG = "MainActivity";
    private VideoPlayer mVideoPlayer;

    private boolean isShow = false;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoPlayer = findViewById(R.id.video_player);
        mVideoPlayer.setController(new VideoController(this));
        mVideoPlayer.setUp("https://bvcdn.linkbroad.com/broadview/f526886f73b928c6772705021b18e01c/60ed363872d7f1f7a63ece99.mp4", null);
//        mVideoPlayer.setUp("http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4", null);
        mVideoPlayer.start();
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(this);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        Log.d(TAG, "onGlobalFocusChanged: oldFocus " + oldFocus);
        Log.d(TAG, "onGlobalFocusChanged: newFocus " + newFocus);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoPlayer.restart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoPlayer.release();
    }
}