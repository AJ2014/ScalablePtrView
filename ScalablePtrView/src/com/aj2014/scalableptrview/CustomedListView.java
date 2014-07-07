package com.aj2014.scalableptrview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2014/6/26.
 */
public class CustomedListView extends ListView {

    private static final String TAG = CustomedListView.class.getSimpleName();
    /**
     * fragment index
     */
    private int mFIndex;
    /**
     * added header view
     */
    private View mHeader;
    
    private ScalablePtrView mSPtrView;
    /**
     * custom adapter
     */
    private CustomListAdapter mAdapter;

    public CustomedListView(Context context) {
        super(context);
        init(context);
    }

    public CustomedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    	/**
    	 * disable fast scroll, for the over scroll condition
    	 */
        setFastScrollEnabled(false);
    }
    
    public void setSPtrView(ScalablePtrView view) {
    	mSPtrView = view;
    }
    
    @Override
    public void setAdapter(ListAdapter adapter) {
    	mAdapter = (CustomListAdapter) adapter;
    	if (null != mHeader) {
    		initAdaptersDistanceEntries(mHeader);
    	}
    	super.setAdapter(adapter);
    }

    @Override
    public void addHeaderView(View v) {
        final int hCount = getHeaderViewsCount();
        if (0 != hCount) {
            removeViews(0, hCount);
        }
        mHeader = v;
        initAdaptersDistanceEntries(v);
        super.addHeaderView(v);
    }
    
    /**
     * initial the distance entries with first head row
     * @param header
     */
    private void initAdaptersDistanceEntries(View header) {
    	if (null != mAdapter) {
        	mAdapter.addViewObserverCallback(header, 
        			mAdapter.new CustomedOnGlobalLayoutListener(header, -1));
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        /**
         * empty implement, for over scroll condition solving 
         */
    }

	public int getFIndex() {
		return mFIndex;
	}

	public void setFIndex(int mFIndex) {
		this.mFIndex = mFIndex;
	}
    
	/**
	 * get the distance between listView's top and the item's top at the 'position'
	 * @param position
	 * @return
	 */
	public int getDistanceFromTop(int position) {
		if (null == mAdapter
				|| position < 0 
				|| position > mAdapter.getCount()) {
			return 0;
		}
		return mAdapter.getItemDistanceFromTopAtPosition(position);
	}
	
	public int getHeightAtPosition(int position) {
		if (null == mAdapter
				|| position < 0 
				|| position > mAdapter.getCount()) {
			return 0;
		}
		return mAdapter.getItemHeightAtPosition(position);
	}
	
	/**
	 * deal with the motion event dispatch
	 * when the viewPager contains listView in the sub fragment
	 */
	
	float lastMotionX = 0;
	float lastMotionY = 0;
	float distanceY = 0;
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
//		Log.i("junjiang2", "dispatchTouchEvent " + action);
    	float distanceX = ev.getX() - lastMotionX;
    	distanceY = ev.getY() - lastMotionY;
    	lastMotionX = ev.getX();
    	lastMotionY = ev.getY();
    	switch(action) {
	    	case MotionEvent.ACTION_MOVE: {
	    		if (Math.abs(distanceY) > Math.abs(distanceX)) {
	    			/**
	    			 * intercept the motion
	    			 */
	    			requestDisallowInterceptTouchEvent(true);
	    		} else {
	    			/**
	    			 * dispatch the motion to it's parent
	    			 */
	    			return false;
	    		}
	    		
	    	}
    	}
		return super.dispatchTouchEvent(ev);
	}
	
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		boolean superRet = super.onInterceptTouchEvent(ev);
//		Log.i("junjiang2", "list onInterceptTouchEvent ret = " + superRet);
//		return true;
//	}
//	
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		boolean superRet = super.onTouchEvent(ev);
//		
//		final int action = ev.getAction();
////		int distance = (int) (ev.getY() - lastMotionY);
//		Log.i("junjiang2", "list onTouchEvent action = " + action);
//		if (!isScalable((int) distanceY)) {
//			return superRet;
//		}
//		Log.i("junjiang2", "list onTouchEvent actio2 = " + action);
//		mSPtrView.onTouchEvent(ev);
//		scaleHeaderBy((int) distanceY);
////		switch (action) {
////		case MotionEvent.ACTION_DOWN:
////			lastMotionY = ev.getY();
////			break;
////			
////		case MotionEvent.ACTION_MOVE:
////			
////			break;
////
////		default:
////			break;
////		}
//		return true;
//	}
//	
//	private boolean isScalable(int distance) {
//		if (null == mHeader) {
//			return false;
//		}
//		final int normalHeight = getHeightAtPosition(0);
//		final int headerTop = mHeader.getTop();
//		final int headerHeight = mHeader.getHeight();
//		return headerTop == 0 
//				&& (headerHeight == normalHeight && distance >= 0 
//				|| headerHeight > normalHeight);
//	}
//	
//	private void scaleHeaderBy(int distance) {
//		if (!isScalable(distance)) {
//			return;
//		}
//		final int newHeight = (int) (mHeader.getHeight() + distance * 0.5f);
//		LayoutParams lParams = (LayoutParams) mHeader.getLayoutParams();
//		lParams.height = newHeight;
//		mHeader.setLayoutParams(lParams);
//		mHeader.invalidate();
//	}

}
