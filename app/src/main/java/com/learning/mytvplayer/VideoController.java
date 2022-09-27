package com.learning.mytvplayer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.animation.LogDecelerateInterpolator;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.learning.mytvplayer.adapter.MyAdapter;
import com.learning.mytvplayer.adapter.PlayActionTipAdapter;
import com.learning.mytvplayer.bean.ClarityBean;
import com.learning.mytvplayer.bean.EpisodeBean;
import com.learning.mytvplayer.bean.SpeedBean;
import com.learning.mytvplayer.cosutom.ActionItemView;
import com.learning.mytvplayer.cosutom.IClickClarityOrSpeed;
import com.learning.mytvplayer.cosutom.IEpisodeItemClick;
import com.learning.mytvplayer.cosutom.VideoEpisodeView;
import com.learning.mytvplayer.presenter.ActionTitlePresenter;
import com.learning.mytvplayer.presenter.MyItemBridgeAdapter;
import com.learning.mytvplayer.util.FromTimerUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howe.zhong
 * on 2022/9/22  16:37
 */
public class VideoController extends FrameLayout implements IVideoController, CostomSeekBar.OnSeekBarChannelListener,
        IEpisodeItemClick, IClickClarityOrSpeed, ViewTreeObserver.OnGlobalFocusChangeListener {
    private static final String TAG = "VideoController";
    private Context mContext;
    private RecyclerView mActionContent;
    private MyAdapter mMyAdapter;
    private HorizontalGridView mActionTitle;
    private View mActionParent;


    //    private boolean isShow = false;
    private boolean isFirst = true;  // 是否第一次显示播放列表
    private boolean isFirstIn = true;  // 是否首次进入页面

    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    /**
     * 普通模式  快进快退
     **/
    public static final int TYPE_NORMAL = 1;
    /**
     * 操作模式 修改倍数 清晰度 切换视频
     */
    public static final int TYPE_ACTION = 2;

    private int currentType = TYPE_NORMAL;

    private View mActionView;
    private View mNormalView;
    private Handler mHandler;
    private VideoPlayer mPlayer;
    private View mBuffTipView;
    private TextView mTitle;
    private TextView mTime;
    private RecyclerView mActionTipsRv;
    private TextView mPosition;
    private CostomSeekBar mSeek;
    private TextView mDuration;
    private SeekBar mTipSeek;
    private View mTop;
    private View mSeekBarContainer;
    private View mDownTip;
    private TextView oldActionTitle;
    private TextView mZeroActionTitle;
    private TextView mBuffSpeed;
    private boolean mIsFirstAddTitle;

    public VideoController(@NonNull Context context) {
        this(context, null);
    }

    public VideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mHandler = new Handler();
        mNormalView = LayoutInflater.from(mContext).inflate(R.layout.controller_normal_view, this, false);
        mActionView = LayoutInflater.from(mContext).inflate(R.layout.controller_action_view, this, false);
        this.addView(mNormalView);
        this.addView(mActionView);
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
        initLayout();
    }

    private Runnable hideActionViewRunnable = new Runnable() {
        @Override
        public void run() {
//            hideActionView(false);
        }
    };

    private void initLayout() {
        mActionParent = findViewById(R.id.action_parent);
        mActionParent.setY(mActionParent.getY() + 500);
        mActionTitle = findViewById(R.id.action_title);
        mActionTitle.setHorizontalSpacing(30);
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(new ActionTitlePresenter());
        mIsFirstAddTitle = true;
        ItemBridgeAdapter iba = new MyItemBridgeAdapter(adapter) {
            @Override
            protected void onBind(ViewHolder viewHolder) {
                int position = viewHolder.getLayoutPosition();
                if (mIsFirstAddTitle && !viewHolder.itemView.hasFocus() && position == 0) {
                    ((TextView) viewHolder.itemView).setTextColor(Color.GREEN);
                    mIsFirstAddTitle = false;
                }
                super.onBind(viewHolder);
            }
        };
        mActionTitle.setAdapter(iba);
        List<String> listTitle = new ArrayList<>();
        listTitle.add("视频列表");
        listTitle.add("清晰度");
        listTitle.add("倍数");
        listTitle.add("看点");
        adapter.addAll(0, listTitle);

        mActionTitle.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                super.onChildViewHolderSelected(parent, child, position, subposition);
                mActionContent.scrollToPosition(position);
            }
        });


        mActionContent = this.findViewById(R.id.action_content);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        mActionContent.setLayoutManager(linearLayoutManager);
        mActionContent.setItemViewCacheSize(5);
        mMyAdapter = new MyAdapter(this);
        mActionContent.setAdapter(mMyAdapter);
        mMyAdapter.setDownView(mActionTitle);

        List<List<String>> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list1.add((i + 1) + "");
        }

        int ceil = (int) Math.ceil(1000 / 15f);
        for (int i = 0; i < ceil; i++) {
            if (i * 15 + 15 > 1000) {
                list.add(list1.subList(i * 15, 1000));
            } else {
                list.add(list1.subList(i * 15, i * 15 + 15));
            }
        }

        List<Object> listContent = new ArrayList<>();

        List<String> list2 = new ArrayList<>();
        list2.add("高清 1080P");
        list2.add("准高清 720P");
        list2.add("标清 480P");
        list2.add("流畅 270P");
//        mClarityFragment.setData(list);
//        mSpeedFragment = new ClarityOrSpeedFragment();
        List<String> list3 = new ArrayList<>();
        list3.add("0.5x");
        list3.add("0.75x");
        list3.add("1x");
        list3.add("1.25x");
        list3.add("1.5x");
        list3.add("2.0x");


        List<String> lookList = new ArrayList<>();

        for (int i = 0; i < 28; i++) {
            lookList.add(i + "bb");
        }
        List<List<String>> lookLists = new ArrayList<>();
        int lookCeil = (int) Math.ceil(28 / 5f);
        for (int i = 0; i < lookCeil; i++) {
            if (i * 5 + 5 > 28) {
                lookLists.add(lookList.subList(i * 5, 28));
            } else {
                lookLists.add(lookList.subList(i * 5, i * 5 + 5));
            }
        }

        listContent.add(new EpisodeBean(list));
        listContent.add(new ClarityBean(list2));
        listContent.add(new SpeedBean(list3));
        listContent.add(new EpisodeBean(lookLists));
        mMyAdapter.setData(listContent);

        mTipSeek = findViewById(R.id.tip_seek);


        mBuffTipView = findViewById(R.id.buff_tips);
        mBuffSpeed = findViewById(R.id.buff_speed);

        mTop = findViewById(R.id.top);
        mSeekBarContainer = findViewById(R.id.seek_bar_container);
        mDownTip = findViewById(R.id.down_tip);

        mTitle = findViewById(R.id.title);
        mTime = findViewById(R.id.time);
        mActionTipsRv = findViewById(R.id.action_tips_rv);
        LinearLayoutManager linearManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mActionTipsRv.setLayoutManager(linearManager);
        PlayActionTipAdapter playActionTipAdapter = new PlayActionTipAdapter();
        mActionTipsRv.setAdapter(playActionTipAdapter);
        List<String> list11 = new ArrayList<>();
        list11.add("播放列表");
        list11.add("清晰度");
        list11.add("多倍数");
        list11.add("看点");
        playActionTipAdapter.setData(list11);

        mPosition = findViewById(R.id.position);
        mSeek = findViewById(R.id.seek);
        mSeek.setOnSeekBarChannelListener(this);
        mDuration = findViewById(R.id.total_time);

        hideTopBottom();
    }


    private Runnable hideTopBottomRunnable = new Runnable() {
        @Override
        public void run() {
            hideTopBottom();
        }
    };

    private void hideTopBottom() {
        mTop.setVisibility(GONE);
        mActionTipsRv.setVisibility(GONE);
        mSeekBarContainer.setVisibility(GONE);
        mDownTip.setVisibility(GONE);
    }

    private void showTopBottom() {
        mTop.setVisibility(VISIBLE);
        mActionTipsRv.setVisibility(VISIBLE);
        mSeekBarContainer.setVisibility(VISIBLE);
        mDownTip.setVisibility(VISIBLE);
    }

    @Override
    public void setTitle(@NonNull String title) {
        mTitle.setText(title);
    }

    @Override
    public void setVideoPlayer(VideoPlayer videoPlayer) {
        this.mPlayer = videoPlayer;
    }

    @Override
    public void reset() {
        cancelUpdateProgressTimer();
        mHandler.removeCallbacks(hideTopBottomRunnable);
        mHandler.removeCallbacks(hideActionViewRunnable);
    }

    @Override
    public void setDuration(long duration) {

    }

    private Runnable updateSpeedRunnable = new Runnable() {
        @Override
        public void run() {
            mBuffSpeed.setText((mPlayer.getTcpSpeed() / 1000) + "kb/s");
            mHandler.postDelayed(this, 1000);
        }
    };


    private void startUpdateSpeed() {
        mBuffTipView.setVisibility(VISIBLE);
        mHandler.post(updateSpeedRunnable);
    }

    private void stopUpdateSpeed() {
        mBuffTipView.setVisibility(GONE);
        mHandler.removeCallbacks(updateSpeedRunnable);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoPlayer.STATE_IDLE:
                break;
            case VideoPlayer.STATE_PREPARING:
                startUpdateSpeed();
//                mBuffTipView.setVisibility(VISIBLE);
//                mImage.setVisibility(View.GONE);
//                mLoading.setVisibility(View.VISIBLE);
//                mLoadText.setText("正在准备...");
//                mError.setVisibility(View.GONE);
//                mCompleted.setVisibility(View.GONE);
//                mTop.setVisibility(View.GONE);
//                mBottom.setVisibility(View.GONE);
//                mCenterStart.setVisibility(View.GONE);
//                mLength.setVisibility(View.GONE);
                break;
            case VideoPlayer.STATE_PREPARED:
//                mBuffTipView.setVisibility(GONE);
                stopUpdateSpeed();
                startUpdateProgressTimer();
                showTopBottom();
                break;
            case VideoPlayer.STATE_PLAYING:
                if (isFirstIn) {
                    mHandler.postDelayed(hideTopBottomRunnable, 3000);
                    isFirstIn = false;
                } else {
                    hideTopBottom();
                }
//                mLoading.setVisibility(View.GONE);
//                mRestartPause.setImageResource(R.drawable.ic_player_pause);
//                startDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_PAUSED:
                showTopBottom();
//                mLoading.setVisibility(View.GONE);
//                mRestartPause.setImageResource(R.drawable.ic_player_start);
//                cancelDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_BUFFERING_PLAYING:
                stopUpdateSpeed();
                mBuffTipView.setVisibility(GONE);
//                mLoading.setVisibility(View.VISIBLE);
//                mRestartPause.setImageResource(R.drawable.ic_player_pause);
//                mLoadText.setText("正在缓冲...");
//                startDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_BUFFERING_PAUSED:
                startUpdateSpeed();
//                mBuffTipView.setVisibility(VISIBLE);
//                mLoading.setVisibility(View.VISIBLE);
//                mRestartPause.setImageResource(R.drawable.ic_player_start);
//                mLoadText.setText("正在缓冲...");
//                cancelDismissTopBottomTimer();
                break;
            case VideoPlayer.STATE_ERROR:
                cancelUpdateProgressTimer();
//                setTopBottomVisible(false);
//                mTop.setVisibility(View.VISIBLE);
//                mError.setVisibility(View.VISIBLE);
                break;
            case VideoPlayer.STATE_COMPLETED:
                cancelUpdateProgressTimer();
                showTopBottom();
//                setTopBottomVisible(false);
//                mImage.setVisibility(View.VISIBLE);
//                mCompleted.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (currentType == TYPE_ACTION) {
            mHandler.removeCallbacks(hideActionViewRunnable);
            mHandler.postDelayed(hideActionViewRunnable, 5000);
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (currentType == TYPE_NORMAL) {
                        onKeyLeft(false);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (currentType == TYPE_NORMAL) {
                        onKeyRight(false);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_BACK:
                    if (hideActionView(true)) {
                        return true;
                    } else if (currentType == TYPE_NORMAL && (mPlayer.isPaused() || mPlayer.isBufferingPaused())) {
                        mPlayer.restart();
                        return true;
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (showActionView()) {
                        mHandler.postDelayed(hideActionViewRunnable, 5000);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (currentType == TYPE_NORMAL) {
                        mHandler.removeCallbacks(hideTopBottomRunnable);
                        if (mPlayer.isPlaying() || mPlayer.isBufferingPlaying()) {
                            cancelUpdateProgressTimer();
                            mPlayer.pause();
                        } else if (mPlayer.isPaused() || mPlayer.isBufferingPaused()) {
                            startUpdateProgressTimer();
                            mPlayer.restart();
                        }
                        return true;
                    }
                    break;
                default:
                    if (currentType == TYPE_NORMAL) {
                        return true;
                    }
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (currentType == TYPE_NORMAL) {
                        onKeyLeft(true);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (currentType == TYPE_NORMAL) {
                        onKeyRight(true);
                        return true;
                    }
                    break;
//                default:
//                    if (!isShow) {
//                        return true;
//                    }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void onKeyRight(boolean release) {
        if (!release) {
            mSeek.setProcess(mSeek.getProcess() + 1);
            cancelUpdateProgressTimer();
            mSeek.showThumb();
            hideTopBottom();
            mSeekBarContainer.setVisibility(VISIBLE);
            mDownTip.setVisibility(INVISIBLE);
        } else {
            mSeek.hideThumb();
            int position = (int) (mPlayer.getDuration() * mSeek.getProcess() / 100f);
//            mPlayer.seekTo(mSeek.getProcess() * 1000);
            if (position >= mPlayer.getDuration()) {
                position = position / 100 * 98;
            }
            mPlayer.seekTo(position);
            startUpdateProgressTimer();
            hideTopBottom();
            if (mPlayer.isPaused() || mPlayer.isBufferingPaused()) {
                mPlayer.restart();
            }
            if (mPlayer.isCompleted() || mPlayer.isError()) {
                mPlayer.releasePlayer();
                mPlayer.start(position);
            }
            mSeekBarContainer.setVisibility(GONE);
        }
    }

    /**
     * 拖动进度条时
     *
     * @param progress 进度
     */
    @Override
    public void onSeekChannel(int progress) {
        int totalPosition = (int) mPlayer.getDuration();
        int v = (int) (totalPosition * progress / 100f);
        String s = FromTimerUtil.formatTime(v);
        mSeek.setText(s);
    }

    private void onKeyLeft(boolean release) {
        if (!release) {
//            if (repeatCount > 5) {
//                mSeek.setProcess((int) (mSeek.getProcess() - (mPlayer.getDuration() / 1000 / 100)));
//            } else {
//                mSeek.setProcess(mSeek.getProcess() - 13);
//            }
            mSeek.setProcess(mSeek.getProcess() - 1);
            cancelUpdateProgressTimer();
            mSeek.showThumb();
            hideTopBottom();
            mSeekBarContainer.setVisibility(VISIBLE);
            mDownTip.setVisibility(INVISIBLE);
        } else {
            mSeek.hideThumb();
            int position = (int) (mPlayer.getDuration() * mSeek.getProcess() / 100f);
            mPlayer.seekTo(position);
            startUpdateProgressTimer();
            hideTopBottom();
            if (mPlayer.isPaused() || mPlayer.isBufferingPaused()) {
                mPlayer.restart();
            }
            if (mPlayer.isCompleted() || mPlayer.isError()) {
                mPlayer.releasePlayer();
                mPlayer.start(position);
            }
            mSeekBarContainer.setVisibility(GONE);
        }
    }

    private boolean showActionView() {
        if (currentType == TYPE_NORMAL) {
            if (isFirst) {
                mActionContent.scrollToPosition(0);
                mActionTitle.setSelectedPosition(0);
                VideoEpisodeView Episode = (VideoEpisodeView) mActionContent.getLayoutManager().findViewByPosition(0);
                if (Episode != null) {
                    Episode.episodeRequestFocus();
                    if (mZeroActionTitle != null) {
                        mZeroActionTitle.setTextColor(Color.GREEN);
                    }
                    if (oldActionTitle != null) {
                        oldActionTitle.setTextColor(Color.WHITE);
                    }
                }
                isFirst = false;
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mActionParent, "translationY", 500, 0);
            objectAnimator.start();
            hideTopBottom();
            currentType = TYPE_ACTION;
            return true;
        }
        return false;
    }

    private boolean hideActionView(boolean isShowTopBot) {
        if (currentType == TYPE_ACTION) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mActionParent, "translationY", 0, 500);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mActionContent.scrollToPosition(0);
                    mActionTitle.setSelectedPosition(0);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            VideoEpisodeView Episode = (VideoEpisodeView) mActionContent.getLayoutManager().findViewByPosition(0);
                            Log.d(TAG, "dispatchKeyEvent: Episode " + Episode);
                            if (Episode != null) {
                                Episode.episodeRequestFocus();
                                if (mZeroActionTitle != null) {
                                    mZeroActionTitle.setTextColor(Color.GREEN);
                                }
                                if (oldActionTitle != null) {
                                    oldActionTitle.setTextColor(Color.WHITE);
                                }
                            } else {
                                mHandler.postDelayed(this, 300);
                            }
                        }
                    });
                    mHandler.postDelayed(hideTopBottomRunnable, 3000);
                }
            });
            if (isShowTopBot) {
                showTopBottom();
            }
            objectAnimator.start();
            currentType = TYPE_NORMAL;
            return true;
        }
        return false;
    }


    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    VideoController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProcess();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }

    public void updateProcess() {
        //  更新时间进度条
        int currentPosition = (int) mPlayer.getCurrentPosition();
        mPosition.setText(FromTimerUtil.formatTime(currentPosition));
        int totalPosition = (int) mPlayer.getDuration();
        mDuration.setText(FromTimerUtil.formatTime(totalPosition));
        int progress = (int) (100f * currentPosition / totalPosition);
        mSeek.setProcess(progress);
        mTipSeek.setProgress(progress);
        mTime.setText(new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date()));
    }

    /**
     * 取消更新进度的计时器。
     */
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    /**
     * 点击切换第几集
     */
    @Override
    public void onEpisodeItemClick() {
        mPlayer.releasePlayer();
        mPlayer.setUp("http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4", null);
        mPlayer.start();
        Log.d(TAG, "onEpisodeItemClick: ...............");
        hideActionView(false);
    }

    /**
     * 切换倍数与清晰度
     */
    @Override
    public void onClickClarityOrSpeed(ActionItemView view, Object item, int type) {
        if (type == 1) {
            // 倍数
            String s = (String) item;
            String s1 = s.split("x")[0];
            Log.d(TAG, "onItemClick: s1 " + s1);
            mPlayer.setSpeed(Float.parseFloat(s1));
            hideActionView(false);
            Toast.makeText(mContext, "已切换到 " + s1 + "倍数播放", Toast.LENGTH_SHORT).show();
        } else {
            // 清晰度
            hideActionView(false);
            Toast.makeText(mContext, "已切换到 " + item, Toast.LENGTH_SHORT).show();
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ActionItemView childAt = (ActionItemView) parent.getChildAt(i);
            childAt.setCheck(false);
        }
        view.setCheck(true);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        Log.d(TAG, "onGlobalFocusChanged: oldFocus " + oldFocus);
        Log.d(TAG, "onGlobalFocusChanged: newFocus " + newFocus);
        if (oldFocus == null || newFocus == null) {
            return;
        }
        if (oldFocus.getParent() != null && newFocus.getParent() != null && mActionTitle != null) {
            if (oldFocus.getParent().equals(mActionTitle) && !(newFocus.getParent().equals(mActionTitle))) {
                ((TextView) oldFocus).setTextColor(Color.GREEN);
                oldActionTitle = ((TextView) oldFocus);
            }
        }
    }
}
