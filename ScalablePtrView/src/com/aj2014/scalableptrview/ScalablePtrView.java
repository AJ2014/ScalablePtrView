package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.PtrLoadingView.IRefreshCallback;
import com.aj2014.scalableptrview.ScalableImageView.IRecoverCallback;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * view container with scalableView header
 * @author Administrator
 *
 */
public class ScalablePtrView extends RelativeLayout {

	/**
	 * scalable head view
	 */
	private ScalableImageView mScalableView;
	
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
		mTouchSlope = ViewConfiguration.get(context).getScaledTouchSlop();
		
		mScalableView = new ScalableImageView(context, attrs);
		LayoutParams gParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		gParams.addRule(ALIGN_PARENT_TOP);
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
			return false;

		default:
			break;
		}
		boolean isOutofRange = false;//isOutofRange(distance);
		Log.i("junjiang2", "onInterceptTouchEvent " + distance);
		return isOutofRange;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		Log.i("junjiang2", "onTouchEvent " + action);
		int distance = (int) (event.getY() - mLastMotionY);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = event.getY();
			mStartMotionY = event.getY();
			Log.i("junjiang2", "mStartMotionY = " + mStartMotionY);
			break;
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
	
	public void setRecoverCallback(IRecoverCallback callback) {
		if (null != mScalableView) {
			mScalableView.setRecoverCallback(callback);
		}
	}

	public void onRefreshComplete() {
		if (null != mScalableView) {
			mScalableView.onRefreshComplete();
		}
	}
	
	public void setScalableViewMarginTop(int margin) {
		if (null != mScalableView) {
			mScalableView.setMarginTop(margin);
		}
	}
	
}
