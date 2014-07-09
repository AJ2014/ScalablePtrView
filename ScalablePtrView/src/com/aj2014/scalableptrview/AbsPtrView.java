package com.aj2014.scalableptrview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public abstract class AbsPtrView extends FrameLayout implements IPtrView {
	
	/**
	 * 当前状态
	 */
	protected EPtrState mCurState;
	/**
	 * 状态变化
	 */
	protected boolean mStateChanged = false;
	/**
	 * 是否可执行刷新
	 */
	protected boolean mRefreshable = false;

	public AbsPtrView(Context context) {
		super(context);
	}
	
	public AbsPtrView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void switchTo(EPtrState state) {
//			Log.i("junjiang2", "onPageSelected switchTo " + state);
		switch (state) {
		case PULL_TO_REFRESH:
			doPullToRefresh();
			break;
		case RELEASE_TO_REFRESH:
			doReleaseToRefresh();
			break;
		case REFRESHING: 
			doRefreshing();
			break;
		default:
			break;
		}
	}
	
	public abstract void doPullToRefresh();
	public abstract void doReleaseToRefresh();
	public abstract void doRefreshing();
	public abstract void doReset();
	
	public abstract boolean isOutofRange();
	
	@Override
	public void onPull(int distance) {
		if (mRefreshable) {
			mStateChanged = !pullToRefresh();
			mCurState = EPtrState.RELEASE_TO_REFRESH;
		} else {
			mStateChanged = pullToRefresh();
			mCurState = EPtrState.PULL_TO_REFRESH;
		}
		// 若状态发生变化 执行状态切换
		if (mStateChanged) {
			switchTo(mCurState);
		}
	}
	
	@Override
	public boolean recover(int distance) {
		return releaseToRefresh();
	}
	
	protected boolean refreshing() {
		return mCurState == EPtrState.REFRESHING;
	}
	
	protected boolean releaseToRefresh() {
		return mCurState == EPtrState.RELEASE_TO_REFRESH;
	}
	
	protected boolean pullToRefresh() {
		return mCurState == EPtrState.PULL_TO_REFRESH;
	}
}
