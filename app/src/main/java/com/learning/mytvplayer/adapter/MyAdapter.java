package com.learning.mytvplayer.adapter;

import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.Presenter;
import androidx.recyclerview.widget.RecyclerView;

import com.learning.mytvplayer.bean.ClarityBean;
import com.learning.mytvplayer.bean.EpisodeBean;
import com.learning.mytvplayer.R;
import com.learning.mytvplayer.VideoController;
import com.learning.mytvplayer.bean.SpeedBean;
import com.learning.mytvplayer.cosutom.ActionItemView;
import com.learning.mytvplayer.cosutom.ChangeVideoAttrGridView;
import com.learning.mytvplayer.cosutom.MovieEpisodeView;
import com.learning.mytvplayer.cosutom.VideoEpisodeView;
import com.learning.mytvplayer.presenter.ClarityOrSpeedPresenter;
import com.learning.mytvplayer.presenter.ClaritySpeedItemBridgeAdapter;
import com.learning.mytvplayer.presenter.MyItemBridgeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howe.zhong
 * on 2022/9/23  13:46
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_TV_EPISODE = 0;//集数
    private static final int ITEM_TYPE_CLARITY = 1;//清晰度
    private static final int ITEM_TYPE_SPEED = 2;//倍数
    private static final int ITEM_TYPE_MOVIE_EPISODE = 3;//集数
    private static final String TAG = "MyAdapter";
    private final VideoController mController;

    private List<Object> mList = new ArrayList<>();
    private View mDownView;

    public MyAdapter(VideoController controller) {
        this.mController = controller;
    }

    public void setDownView(View view) {
        this.mDownView = view;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_MOVIE_EPISODE:
                MovieEpisodeView movieEpisodeView = new MovieEpisodeView(parent.getContext(), mController);
                return new EpisodeHolder(movieEpisodeView);
            case ITEM_TYPE_TV_EPISODE:
                VideoEpisodeView videoEpisodeView = new VideoEpisodeView(parent.getContext(), mController);
                return new EpisodeHolder(videoEpisodeView);

            case ITEM_TYPE_CLARITY:
                ChangeVideoAttrGridView grid = (ChangeVideoAttrGridView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clarity_speed_horizontalgridview, parent, false);
                grid.setDownView(mDownView);
                grid.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
                return new ClarityHolder(grid);
            case ITEM_TYPE_SPEED:
                ChangeVideoAttrGridView gridSpeed = (ChangeVideoAttrGridView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clarity_speed_horizontalgridview, parent, false);
                gridSpeed.setDownView(mDownView);
                gridSpeed.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
                return new SpeedHolder(gridSpeed);
            default:
                TextView tv = new TextView(parent.getContext());
//        tv.setLayoutParams(new ViewGroup.LayoutParams(1920, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setPadding(10, 10, 10, 10);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40);
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundColor(Color.BLUE);
                tv.setFocusable(true);
                tv.setClickable(true);
                tv.setFocusableInTouchMode(true);
                tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            tv.setBackgroundColor(Color.RED);
                        } else {
                            tv.setBackgroundColor(Color.BLUE);
                        }
                    }
                });
                return new InnerHolder(tv);
        }
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: position " + position);
        switch (holder.getItemViewType()) {
            case ITEM_TYPE_MOVIE_EPISODE:
                if (mList.get(position) instanceof EpisodeBean) {
                    if (((EpisodeHolder) holder).itemView instanceof MovieEpisodeView) {
                        ((MovieEpisodeView) ((EpisodeHolder) holder).itemView).setData((EpisodeBean) mList.get(position));
                    }
                }
                break;
            case ITEM_TYPE_TV_EPISODE:
                if (mList.get(position) instanceof EpisodeBean) {
                    if (((EpisodeHolder) holder).itemView instanceof VideoEpisodeView) {
                        ((VideoEpisodeView) ((EpisodeHolder) holder).itemView).setData((EpisodeBean) mList.get(position));
                    }
                }
                break;
            case ITEM_TYPE_CLARITY:
                if (mList.get(position) instanceof ClarityBean) {
                    ((ClarityHolder) holder).bindData(((ClarityBean) mList.get(position)), 0);
                }
                break;
            case ITEM_TYPE_SPEED:
                if (mList.get(position) instanceof SpeedBean) {
                    ((SpeedHolder) holder).bindData(((SpeedBean) mList.get(position)), 2);
                }
                break;
            default:
                ((InnerHolder) holder).mTv.setText((String) mList.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        private TextView mTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mTv = (TextView) itemView;
        }
    }

    public class EpisodeHolder extends RecyclerView.ViewHolder {


        public EpisodeHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class SpeedHolder extends RecyclerView.ViewHolder {


        private HorizontalGridView mGridView;

        public SpeedHolder(@NonNull View itemView) {
            super(itemView);
            mGridView = (HorizontalGridView) itemView;
            mGridView.setPadding(60, 0, 0, 0);
            mGridView.setHorizontalSpacing(30);
        }

        public void bindData(SpeedBean speedBean, int i) {
            ArrayObjectAdapter aoa = new ArrayObjectAdapter(new ClarityOrSpeedPresenter(mController, 1));
//            ItemBridgeAdapter iba = new ItemBridgeAdapter(aoa);
            ClaritySpeedItemBridgeAdapter iba = new ClaritySpeedItemBridgeAdapter(aoa, i);
            aoa.addAll(0, speedBean.getList());
            mGridView.setAdapter(iba);
            mGridView.setSelectedPosition(i);
        }
    }

    public class ClarityHolder extends RecyclerView.ViewHolder {

        private HorizontalGridView mGridView;

        public ClarityHolder(@NonNull View itemView) {
            super(itemView);
            mGridView = (HorizontalGridView) itemView;
            mGridView.setHorizontalSpacing(30);
            mGridView.setPadding(60, 0, 0, 0);
        }

        public void bindData(ClarityBean clarityBean, int i) {
            ArrayObjectAdapter aoa = new ArrayObjectAdapter(new ClarityOrSpeedPresenter(mController, 2));
//            ItemBridgeAdapter iba = new ItemBridgeAdapter(aoa);
            ClaritySpeedItemBridgeAdapter iba = new ClaritySpeedItemBridgeAdapter(aoa, i);
            aoa.addAll(0, clarityBean.getList());
            mGridView.setAdapter(iba);
            mGridView.setSelectedPosition(i);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setData(List<? extends Object> list) {
        mList.clear();
        mList.addAll(0, list);
        notifyDataSetChanged();
    }
}
