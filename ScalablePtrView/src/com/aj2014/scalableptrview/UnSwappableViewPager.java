package com.aj2014.scalableptrview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class UnSwappableViewPager extends ViewPager {

	private static boolean mEnableSwap = false;
	
	public void enableSwap(boolean enable) {
		mEnableSwap = enable;
	}
	
	public UnSwappableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return mEnableSwap ? super.onInterceptTouchEvent(arg0) : false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return mEnableSwap ? super.onTouchEvent(arg0) : false;
	}

}
