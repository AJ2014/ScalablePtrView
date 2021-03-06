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
    public View mHeader;
    
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
	private float lastMotionX = 0;
	private float lastMotionY = 0;
	private float distanceX = 0;
	private float distanceY = 0;	
	private boolean sendActionDown = false;
	private boolean sendActionDown2 = false;
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (MotionEvent.ACTION_DOWN == ev.getAction()) {
			lastMotionX = ev.getX();
			lastMotionY = ev.getY();
			return super.onTouchEvent(ev);
		}
		distanceX = ev.getX() - lastMotionX;
		distanceY = ev.getY() - lastMotionY;
		lastMotionX = ev.getX();
		lastMotionY = ev.getY();
		if (Math.abs(distanceX) > Math.abs(distanceY)) {
			Log.i("junjiang2", String.format("distanceX:%f distanceY:%f", distanceX, distanceY));
			return false;
		}
		boolean scalable = isScalable((int) distanceY);
		if (!scalable) {
			/**
			 * 未进入缩放的范围，就由listView本身来处理
			 */
			if (sendActionDown && !sendActionDown2) {
				sendActionDown2 = true;
				ev.setAction(MotionEvent.ACTION_DOWN);
			}
			Log.i("ScalableImageView", "customlistview onTouchEvent " + ev.getAction());
			return super.onTouchEvent(ev);
		}
		if (!sendActionDown) {
			/**
			 * 开始缩放的临界点
			 * 需要传入一个DOWN事件，让scalablePtrView记录初始位置
			 */
			ev.setAction(MotionEvent.ACTION_DOWN);
			sendMotionEvent(ev);
			sendActionDown = true;
		} else {
			sendMotionEvent(ev);
		}
		if (MotionEvent.ACTION_UP == ev.getAction()) {
			sendActionDown = false;
			sendActionDown2 = false;
		}
		return true;
	}
	
	private void sendMotionEvent(MotionEvent ev) {
		if (null != mSPtrView) {
			mSPtrView.onTouchEvent(ev);
		}
	}
	
	private boolean isScalable(int distance) {
		if (null == mHeader) {
			return false;
		}
		final int normalHeight = getHeightAtPosition(0);
		final int headerTop = mHeader.getTop();
		final int headerHeight = mHeader.getHeight();
		Log.i("junjiang2", String.format("scalable top:%d height:%d normal:%d distance:%d", 
				headerTop, headerHeight, normalHeight, distance));
		return headerTop == 0 
				&& (headerHeight == normalHeight && distance >= 0 
				|| headerHeight > normalHeight);
	}
	
	public void scaleHeaderTo(int height) {
		LayoutParams lParams = (LayoutParams) mHeader.getLayoutParams();
		lParams.height = height;
		mHeader.setLayoutParams(lParams);
		Log.i("ScalableImageView", "scaleHeaderTo " + height + " top=" + mHeader.getTop());
		mHeader.invalidate();
	}

}
