package com.learning.mytvplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.learning.mytvplayer.R;
import com.learning.mytvplayer.presenter.EpisodePresenter;
import com.learning.mytvplayer.presenter.MovieEpisodePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howe.zhong
 * on 2022/9/23  14:31
 */
public class MovieViewPagerAdapter extends PagerAdapter {

    private List<List<String>> mList = new ArrayList<>();
    private View.OnClickListener mClickListener;
    private HorizontalGridView mCurrentView;

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        HorizontalGridView horizontalGridView = (HorizontalGridView) LayoutInflater.from(container.getContext()).inflate(R.layout.item_horizontalgridview, container, false);
        horizontalGridView.setHorizontalSpacing(45);
        MovieEpisodePresenter episodePresenter = new MovieEpisodePresenter();
        episodePresenter.setItemClickListener(mClickListener);
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(episodePresenter);
        ItemBridgeAdapter iba = new ItemBridgeAdapter(adapter);
        horizontalGridView.setAdapter(iba);
        adapter.clear();
        adapter.addAll(0, mList.get(position));
        container.addView(horizontalGridView);
        return horizontalGridView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
//        super.destroyItem(container, position, object);
    }



    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentView = (HorizontalGridView) object;
    }

    public HorizontalGridView getPrimaryItem() {
        return mCurrentView;
    }

    public void setData(List<List<String>> list) {
        mList.clear();
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

    public void setItemClickListener(View.OnClickListener clickListener){
        this.mClickListener = clickListener;
    }
}
