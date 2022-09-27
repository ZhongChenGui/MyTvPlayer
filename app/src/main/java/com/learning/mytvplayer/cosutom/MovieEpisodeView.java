package com.learning.mytvplayer.cosutom;

import static androidx.leanback.widget.BaseGridView.FOCUS_SCROLL_PAGE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.BaseGridView;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.learning.mytvplayer.R;
import com.learning.mytvplayer.VideoController;
import com.learning.mytvplayer.adapter.MovieViewPagerAdapter;
import com.learning.mytvplayer.adapter.ViewPagerAdapter;
import com.learning.mytvplayer.bean.EpisodeBean;
import com.learning.mytvplayer.presenter.EpisodeGroupPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howe.zhong
 * on 2022/9/23  15:17
 */
public class MovieEpisodeView extends LinearLayout implements ViewTreeObserver.OnGlobalFocusChangeListener {
    private static final String TAG = "VideoEpisodeView";
    private final Context mContext;
    private final VideoController mController;
    private HorizontalGridView mGroup;
    private EpisodeViewPager mViewPager;
    private ArrayObjectAdapter mAdapter;
    private MovieViewPagerAdapter mViewPagerAdapter;
    private EpisodeTv mPlayView;
    private TextView oldTitleView;
    private int currentPage = 0;
    private boolean isGetFocus = false;
    private boolean isSkipGroup = false;
    private View mRightTip;
    private View mLeftTip;

    public MovieEpisodeView(Context context, VideoController controller) {
        super(context);
        mContext = context;
        this.mController = controller;
        this.setOrientation(VERTICAL);
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(mContext).inflate(R.layout.item_moive_episode, this, true);
        mGroup = findViewById(R.id.group);
        mGroup.setHorizontalSpacing(30);
        mAdapter = new ArrayObjectAdapter(new EpisodeGroupPresenter());
        ItemBridgeAdapter groupIba = new ItemBridgeAdapter(mAdapter);
        mGroup.setAdapter(groupIba);

        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setScroll(false);
        mViewPager.setClipToPadding(false);
//        mViewPager.setPadding(60, 0, 60, 0);
//        mViewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        mViewPagerAdapter = new MovieViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        mLeftTip = findViewById(R.id.left_tip);
        mRightTip = findViewById(R.id.right_tip);

        initListener();
    }

    private void initListener() {
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
        mGroup.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                super.onChildViewHolderSelected(parent, child, position, subposition);
                isSkipGroup = true;
                mViewPager.setCurrentItem(position);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: position " + position);
                if (position > 0) {
                    mLeftTip.setVisibility(VISIBLE);
                } else {
                    mLeftTip.setVisibility(INVISIBLE);
                }
                int count = mViewPagerAdapter.getCount();
                Log.d(TAG, "onPageSelected: position count " + count);
                if (position != count - 1) {
                    mRightTip.setVisibility(VISIBLE);
                } else {
                    mRightTip.setVisibility(INVISIBLE);
                }

                mGroup.setSelectedPosition(position);
                TextView tv = (TextView) mGroup.getLayoutManager().findViewByPosition(position);
                if (tv != null) {
                    oldTitleView.setTextColor(Color.WHITE);
                    tv.setTextColor(Color.GREEN);
                    oldTitleView = tv;
                }

                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        boolean isLoad = true;
                        HorizontalGridView primaryItem = mViewPagerAdapter.getPrimaryItem();
                        int childCount = primaryItem.getLayoutManager().getChildCount();
                        Log.d(TAG, "onPageSelected: childCount " + childCount);
                        if (childCount <= 0) {
                            isLoad = false;
                        }
                        for (int i = 0; i < childCount; i++) {
                            EpisodeTv childAt = (EpisodeTv) primaryItem.getLayoutManager().getChildAt(i);
                            if (childAt != null) {
                                Log.d(TAG, "onPageSelected: childAt " + childAt);
                                if (mPlayView != null && childAt.getText().equals(mPlayView.getText())) {
                                    childAt.setCheck(true);
                                    childAt.setTextColor(Color.GREEN);
                                    mPlayView = childAt;
                                    break;
                                }
                            } else {
                                isLoad = false;
                                break;
                            }
                        }
                        if (isGetFocus && mPlayView != null) {
                            isGetFocus = false;
                            if (!isSkipGroup) {
                                mPlayView.requestFocus();
                                isSkipGroup = false;
                            }
                        }
                        if (!isLoad) {
                            mViewPager.postDelayed(this, 100);
                        }
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPagerAdapter.setItemClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.onEpisodeItemClick();
                if (mPlayView != null) {
                    mPlayView.setCheck(false);
                }
                currentPage = mViewPager.getCurrentItem();
                mPlayView = ((EpisodeTv) v);
                mPlayView.setCheck(true);
                Log.d(TAG, "onClick: v " + ((EpisodeTv) v).getText());
            }
        });
    }


    public void setData(EpisodeBean episodeBean) {
        Log.d(TAG, "setData: episodeBean " + episodeBean);
        List<List<String>> list = episodeBean.getList();
        List<String> list1 = new ArrayList<>();
        for (List<String> strings : list) {
            list1.add(strings.get(0) + "-" + strings.get(strings.size() - 1));
        }
        mAdapter.clear();
        mAdapter.addAll(0, list1);
        mViewPagerAdapter.setData(list);
        if (list.size() < 2) {
            mGroup.setVisibility(GONE);
        } else {
            mRightTip.setVisibility(VISIBLE);
        }
        mGroup.postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layoutManager = mGroup.getLayoutManager();
                View view = layoutManager.findViewByPosition(0);
                if (view != null) {
                    oldTitleView = (TextView) view;
                    if (!view.hasFocus()) {
                        ((TextView) view).setTextColor(Color.GREEN);
                    }
                } else {
                    mGroup.postDelayed(this, 100);
                }
            }
        }, 30);


//        View childAt = mViewPager.getChildAt(0);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                HorizontalGridView primaryItem = mViewPagerAdapter.getPrimaryItem();
                if (primaryItem == null) {
                    mViewPager.postDelayed(this, 200);
                } else {
                    Log.d(TAG, "setData: primaryItem " + primaryItem);
                    EpisodeTv view = (EpisodeTv) primaryItem.getLayoutManager().findViewByPosition(0);
                    if (view != null) {
                        view.setTextColor(Color.GREEN);
                        view.setCheck(true);
                        mPlayView = view;
                    } else {
                        mViewPager.postDelayed(this, 200);
                    }
                }

            }
        });

    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (oldFocus == null || newFocus == null) {
            return;
        }

        if (oldFocus.getParent().equals(mGroup) && !(newFocus.getParent().equals(mGroup))) {
            ((TextView) oldFocus).setTextColor(Color.GREEN);
            oldTitleView = (TextView) oldFocus;
        }
    }

    public void episodeRequestFocus() {
        isGetFocus = true;
        mViewPager.setCurrentItem(currentPage);
        if (mPlayView != null) {
            mPlayView.requestFocus();
        }
    }

    public HorizontalGridView getGroup() {
        return mGroup;
    }

    public void setGroup(HorizontalGridView group) {
        mGroup = group;
    }

    public EpisodeViewPager getViewPager() {
        return mViewPager;
    }

    public void setViewPager(EpisodeViewPager viewPager) {
        mViewPager = viewPager;
    }
}
