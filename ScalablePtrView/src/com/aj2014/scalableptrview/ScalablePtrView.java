package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.PtrLoadingView.IRefreshCallback;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/**
 * view container with scalableView header
 * @author Administrator
 *
 */
public class ScalablePtrView extends LinearLayout {

	/**
	 * scalable head view
	 */
	private AbsScalableView mScalableView;
	
	private int mTouchSlope;
	/**
	 * y 方向上滑动距离
	 */
	private int mScrollY;
	
	public ScalablePtrView(Context context) {
		super(context);
		init(context, null);
	}

	public ScalablePtrView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		setOrientation(VERTICAL);
		mTouchSlope = ViewConfiguration.get(context).getScaledTouchSlop();
		
		mScalableView = new ScalableImageView(context, attrs);
		LayoutParams gParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(mScalableView, 0, gParams);
	}
	
	float mLastMotionY = 0f;
	float mStartMotionY = 0f;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		int distance = (int) (ev.getY() - mLastMotionY);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = ev.getY();
			mStartMotionY = ev.getY();
			break;

		default:
			break;
		}
		return isOutofRange(distance);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		int distance = (int) (event.getY() - mLastMotionY);
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			mScalableView.scaleTo(distance);
			mLastMotionY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			distance = (int) (event.getY() - mStartMotionY);
			mScalableView.recover(distance);
			mStartMotionY = 0;
			break;
		default:
			break;
		}
		return true;
	}
	
	private boolean isOutofRange(int distance) {
		/**
		 * 1、非缩放尺寸时上滑 
		 * 2、下滑未达到缩放开始临界值
		 */
		if (distance < 0 && mScalableView.isOutofRange()) {
			return true;
		}
		if (distance > 0 && mScrollY < 0) {
			return true;
		}
		return false;
	}
	
	public void setRefreshCallback(IRefreshCallback callback) {
		if (null != mScalableView) {
			mScalableView.setRefreshCallback(callback);
		}
	}

	public void onRefreshComplete() {
		if (null != mScalableView) {
			mScalableView.onRefreshComplete();
		}
	}
	
}
