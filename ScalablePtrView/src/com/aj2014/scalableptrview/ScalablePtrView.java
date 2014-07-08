package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.PtrLoadingView.IRefreshCallback;
import com.aj2014.scalableptrview.ScalableImageView.IScaleCallback;

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
			 * ��Ϊ��listview������������ص�
			 * DOWN�¼��п��ܴ�������
			 */
			if (mStartMotionY == 0) {
				mStartMotionY = event.getY();
			}
			break;
		case MotionEvent.ACTION_UP:
			distance = (int) (event.getY() - mStartMotionY);
			/**
			 * �ж�lastMotionY�Ƿ�Ϊ0����ܵ��������
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
	 * ��������ˢ�»ص�
	 * @param callback
	 */
	public void setRefreshCallback(IRefreshCallback callback) {
		if (null != mScalableView) {
			mScalableView.setRefreshCallback(callback);
		}
	}
	
	/**
	 * ����ͷ��ͼ���Żص�
	 * @param callback
	 */
	public void setScaleCallback(IScaleCallback callback) {
		if (null != mScalableView) {
			mScalableView.setScaleCallback(callback);
		}
	}

	/**
	 * ����ˢ�½����ص�
	 */
	public void onRefreshComplete() {
		if (null != mScalableView) {
			mScalableView.onRefreshComplete();
		}
	}
	
	/**
	 * ����ͷ��ͼmargin top
	 * @param margin
	 */
	public void setScalableViewMarginTop(int margin) {
		if (null != mScalableView) {
			mScalableView.setMarginTop(margin);
		}
	}
	
}
