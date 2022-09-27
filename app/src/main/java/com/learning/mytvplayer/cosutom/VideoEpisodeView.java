package com.learning.mytvplayer.cosutom;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.learning.mytvplayer.bean.EpisodeBean;
import com.learning.mytvplayer.VideoController;
import com.learning.mytvplayer.adapter.ViewPagerAdapter;
import com.learning.mytvplayer.presenter.EpisodeGroupPresenter;
import com.learning.mytvplayer.presenter.MyItemBridgeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howe.zhong
 * on 2022/9/23  15:17
 */
public class VideoEpisodeView extends LinearLayout implements ViewTreeObserver.OnGlobalFocusChangeListener {
    private static final String TAG = "VideoEpisodeView";
    private final Context mContext;
    private final VideoController mController;
    private HorizontalGridView mGroup;
    private EpisodeViewPager mViewPager;
    private ArrayObjectAdapter mAdapter;
    private ViewPagerAdapter mViewPagerAdapter;
    private EpisodeTv mPlayView;
    private TextView oldTitleView;
    private int currentPage = 0;
    private boolean isGetFocus = false;
    private boolean isSkipGroup = false;
    private boolean isSkipGroup2 = false;
    private boolean isFirst = true;
    private MyItemBridgeAdapter mGroupIba;

    public VideoEpisodeView(Context context, VideoController controller) {
        super(context);
        this.mController = controller;
        this.setOrientation(VERTICAL);
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mContext = context;
        mGroup = new HorizontalGridView(mContext);
        LayoutParams groupParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);

        groupParams.bottomMargin = 20;
        mGroup.setLayoutParams(groupParams);
        mGroup.setHorizontalSpacing(30);
        mGroup.setPadding(60, 0, 0, 0);

        mAdapter = new ArrayObjectAdapter(new EpisodeGroupPresenter());

        mGroupIba = new MyItemBridgeAdapter(mAdapter) {
            @Override
            protected void onBind(ViewHolder viewHolder) {
                int checkSelect = mGroupIba.getCheckSelect();
                if (checkSelect != -1) {
                    if (viewHolder.getAdapterPosition() == checkSelect) {
                        ((TextView) viewHolder.itemView).setTextColor(Color.GREEN);
                        mGroupIba.setCheckSelect(-1);
                    }
                }
                super.onBind(viewHolder);
            }
        };
        mGroup.setAdapter(mGroupIba);
        this.addView(mGroup);

        mViewPager = new EpisodeViewPager(mContext);
        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(60, 0, 60, 0);
        mViewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        this.addView(mViewPager);

        initListener();
    }

    private void initListener() {
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
        mGroup.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                super.onChildViewHolderSelected(parent, child, position, subposition);
                if (isFirst) {
                    isFirst = false;
                } else {
                    isSkipGroup = true;
                    isSkipGroup2 = true;
                }
                mViewPager.setCurrentItem(position);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TextView tv = (TextView) mGroup.getLayoutManager().findViewByPosition(position);
                Log.d(TAG, "onPageSelected: 11 isSkipGroup2 " + isSkipGroup2 + " tv " + tv);
                if (tv != null && !isSkipGroup2) {
                    oldTitleView.setTextColor(Color.WHITE);
                    tv.setTextColor(Color.GREEN);
                    oldTitleView = tv;
                }
                mGroup.setSelectedPosition(position);
                isSkipGroup2 = false;


                HorizontalGridView primaryItem = mViewPagerAdapter.getPrimaryItem();
                int childCount = primaryItem.getLayoutManager().getChildCount();
                Log.d(TAG, "onPageSelected: childCount " + childCount);
                if (childCount <= 0) {
                    return;
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
                        break;
                    }
                }
                if (isGetFocus && mPlayView != null) {
                    isGetFocus = false;
                    if (!isSkipGroup) {
                        mPlayView.requestFocus();
                    }
                }
                isSkipGroup = false;
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
        }
        mGroupIba.setCheckSelect(0);

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
        if (oldFocus.getParent() != null && newFocus.getParent() != null && mGroup != null) {
            if (oldFocus.getParent().equals(mGroup) && !(newFocus.getParent().equals(mGroup))) {
                ((TextView) oldFocus).setTextColor(Color.GREEN);
                oldTitleView = (TextView) oldFocus;
            }
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
