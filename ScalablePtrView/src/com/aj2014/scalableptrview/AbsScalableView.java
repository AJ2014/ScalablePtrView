package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.PtrLoadingView.IRefreshCallback;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

public abstract class AbsScalableView extends FrameLayout implements IScalableView, OnGlobalLayoutListener {
	
	/**
	 * pull to refresh header view
	 */
	private PtrLoadingView mPtrView;
	protected boolean mRefresh = false;
	
	public AbsScalableView(Context context) {
		super(context);
		init(context, null);
	}
	
	public AbsScalableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		mPtrView = new PtrLoadingView(context, attrs);
		// add the ptr child view
		LayoutParams gParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		gParams.gravity = Gravity.CENTER_HORIZONTAL;
		addView(mPtrView, 0, gParams);
		// reset ptr child view
		mPtrView.doReset();
	}
	
	@Override
	public void scaleTo(int distance) {
		if (null != mPtrView) {
			mPtrView.onPull(distance);
			mRefresh = mPtrView.releaseToRefresh();
		}
	}
	
	@Override
	public void recover(int distance) {
		if (null != mPtrView) {
			mPtrView.recover(distance);
		}
	}
	
	/**
	 * 是否已经滑出scale范围
	 * @return
	 */
	public boolean isOutofRange() {
		if (null != mPtrView) {
			return mPtrView.isOutofRange();
		}
		return true;
	}
	
	@Override
	public void onGlobalLayout() {
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
		if (null != mPtrView) {
			mPtrView.initMargins((int) (getHeight() * 0.5f));
			mPtrView.doReset();
		}
	}

	public void setRefreshCallback(IRefreshCallback callback) {
		if (null != mPtrView) {
			mPtrView.setRefreshCallback(callback);
		}
	}

	public void onRefreshComplete() {
		if (null != mPtrView) {
			mPtrView.onRefreshComplete();
			mRefresh = mPtrView.releaseToRefresh();
		}
	}
}
