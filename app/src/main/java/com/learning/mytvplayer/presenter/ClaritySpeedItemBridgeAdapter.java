package com.learning.mytvplayer.presenter;

import android.view.View;

import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.Presenter;

import com.learning.mytvplayer.cosutom.ActionItemView;

public class ClaritySpeedItemBridgeAdapter extends ItemBridgeAdapter {

    private static final String TAG = "MyItemBridgeAdapter";
    private int mCheck = -1;
    private ActionItemView oldView;

    public ClaritySpeedItemBridgeAdapter(ObjectAdapter adapter) {
        super(adapter, null);
    }

    public ClaritySpeedItemBridgeAdapter(ObjectAdapter adapter, int check) {
        super(adapter, null);
        this.mCheck = check;
    }

    public void setCheck(int check) {
        mCheck = check;
    }

    @Override
    protected void onBind(final ViewHolder viewHolder) {
        if (mCheck != -1) {
            if (!viewHolder.itemView.hasFocus() && viewHolder.getAdapterPosition() == mCheck) {
                ActionItemView actionItemView = (ActionItemView) viewHolder.itemView;
                actionItemView.setCheck(true);
                if (oldView != null) {
                    oldView.setCheck(false);
                }
                oldView = actionItemView;
            }
        }

        if (getOnItemViewClickedListener() != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnItemViewClickedListener().onItemClicked(v, viewHolder.getViewHolder(),
                            viewHolder.getItem(), viewHolder.getAdapterPosition());

                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (getOnItemViewLongClickedListener() != null) {
                        return getOnItemViewLongClickedListener().onItemLongClicked(v, viewHolder.getViewHolder(),
                                viewHolder.getItem());
                    }
                    return true;
                }
            });
        }
        if (getOnItemFocusChangedListener() != null) {
            viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    getOnItemFocusChangedListener().onItemFocusChanged(v, viewHolder.getViewHolder(),
                            viewHolder.getItem(), hasFocus, viewHolder.getAdapterPosition());
                }
            });
        }
        super.onBind(viewHolder);
    }

    @Override
    protected void onUnbind(ViewHolder viewHolder) {
        super.onUnbind(viewHolder);
        viewHolder.itemView.setOnClickListener(null);
        if (getOnItemFocusChangedListener() != null) {
            viewHolder.itemView.setOnFocusChangeListener(null);
        }
    }

    public OnItemViewClickedListener getOnItemViewClickedListener() {
        return null;
    }

//    public abstract OnItemViewClickedListener getOnItemViewClickedListener();

    public OnItemViewLongClickedListener getOnItemViewLongClickedListener() {
        return null;
    }

    public OnItemFocusChangedListener getOnItemFocusChangedListener() {
        return null;
    }

    public interface OnItemViewClickedListener {
        void onItemClicked(View focusView, Presenter.ViewHolder itemViewHolder, Object item, int pos);
    }

    public interface OnItemViewLongClickedListener {
        boolean onItemLongClicked(View focusView, Presenter.ViewHolder itemViewHolder, Object item);
    }

    public interface OnItemFocusChangedListener {
        void onItemFocusChanged(View focusView, Presenter.ViewHolder itemViewHolder, Object item, boolean hasFocus, int pos);
    }
}
