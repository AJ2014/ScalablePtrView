package com.aj2014.scalableptrview;

import android.widget.AbsListView;

/**
 * Created by Administrator on 2014/6/26.
 */
public abstract class IOnScrollListener<T extends CustomedListView> implements AbsListView.OnScrollListener {

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        T customedListView = (T) absListView;
        CustomedOnScrollStateChanged(customedListView, i);
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        T customedListView = (T) absListView;
        CustomedOnScroll(customedListView, i, i2, i3);
    }

    public abstract void CustomedOnScrollStateChanged(T absListView, int state);

    public abstract void CustomedOnScroll(T absListView, int first, int visibleCount, int wholeCount);
}
