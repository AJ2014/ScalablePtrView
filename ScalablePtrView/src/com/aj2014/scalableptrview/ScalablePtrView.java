package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.PtrLoadingView.IRefreshCallback;
import com.aj2014.scalableptrview.ScalableImageView.IScaleCallback;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

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
	
	public static int mCurTopMargin = 0, mMinTopMargin, mMaxTopMargin = 0, mMarginDist;
	
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
		mScalableView.setId(R.id.personcenter_header_scalable_imageview);
		LayoutParams gParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		gParams.addRule(ALIGN_PARENT_TOP);
		addView(mScalableView, 0, gParams);
	}
	
	/**
	 * add custom header view
	 * @param inflatedView
	 */
	public void addCustomedHeader(View inflatedView) {
		LayoutParams gParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		gParams.addRule(RelativeLayout.ALIGN_BOTTOM, mScalableView.getId());
		addView(inflatedView, 1, gParams);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return false;
	}
	
	float mLastMotionY = 0f;
	float mStartMotionY = 0f;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = ev.getY();
			mStartMotionY = ev.getY();
			return false;

		default:
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		int distance = (int) (event.getY() - mLastMotionY);
		Log.i("junjiang2", "onTouchEvent " + action + " distance = " + distance);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = event.getY();
			mStartMotionY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			mScalableView.scaleTo(distance);
			mLastMotionY = event.getY();
			/**
			 * 因为是listview主动调的这个回调
			 * DOWN事件有可能传不进来
			 */
			if (mStartMotionY == 0) {
				mStartMotionY = event.getY();
			}
			break;
		case MotionEvent.ACTION_UP:
			distance = (int) (event.getY() - mStartMotionY);
			/**
			 * 判断lastMotionY是否为0来规避单击的情况
			 */
			if (mLastMotionY != 0 && distance != 0) { 
				mScalableView.recover(distance);
			}
			mStartMotionY = 0;
			mLastMotionY = 0;
			break;
		default:
			break;
		}
		return true;
	}
	
	/**
	 * 设置下拉刷新回调
	 * @param callback
	 */
	public void setRefreshCallback(IRefreshCallback callback) {
		if (null != mScalableView) {
			mScalableView.setRefreshCallback(callback);
		}
	}
	
	/**
	 * 设置头视图缩放回调
	 * @param callback
	 */
	public void setScaleCallback(IScaleCallback callback) {
		if (null != mScalableView) {
			mScalableView.setScaleCallback(callback);
		}
	}

	/**
	 * 数据刷新结束回调
	 */
	public void onRefreshComplete() {
		if (null != mScalableView && isRecoverableOnRefreshComplete()) {
			Log.i("ScalableImageView", "onRefreshComplete ----");
			mScalableView.onRefreshComplete();
		}
	}
	
	/**
	 * 设置头视图margin top
	 * @param margin
	 */
	public void setScalableViewMarginTop(int margin) {
		setMarginTop(margin);
	}
	
	public void setMarginTop(int margin) {
		MarginLayoutParams mParams = (MarginLayoutParams) getLayoutParams();
		mParams.topMargin = margin;
		setLayoutParams(mParams);
		Log.i("ScalableImageView", "setMarginTop " + margin);
		invalidate();
	}
	
	/**
	 * 头视图推出上部 TAB视图置顶
	 * @return
	 */
	public boolean topInvisible() {
		return mCurTopMargin == mMinTopMargin;
	}
	
	public boolean isOutofRange() {
		if (null == mScalableView) {
			return false;
		}
		return mScalableView.isOutofRange();
	}
	
	/**
	 * 设置头视图top margin
	 * @param margin
	 */
	public void setViewMarginTop(int margin) {

        margin = margin > mMaxTopMargin ? mMaxTopMargin : margin;
        margin = margin < mMinTopMargin ? mMinTopMargin : margin;

        setScalableViewMarginTop(margin);
        
        int distM = mCurTopMargin - margin;
        
        // used to sync the sibling fragments' list
        if (distM != 0) {
        	mMarginDist += distM;
        }
        
        mCurTopMargin = margin;
        
    }
	
	private boolean isRecoverableOnRefreshComplete() {
		return mCurTopMargin == mMaxTopMargin;
	}
	
	public boolean isRefreshing() {
		if (null != mScalableView) {
			return mScalableView.isRefreshing();
		}
		return false;
	}
	
}
