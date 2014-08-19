package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.PtrLoadingView.IRefreshCallback;
import com.aj2014.scalableptrview.ScalableImageView.IScaleCallback;
import com.aj2014.scalableptrview.SmoothScroller.IScrollAction;
import com.aj2014.scalableptrview.SmoothScroller.OnSmoothScrollFinishedListener;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * TODO 遗留的3个问题：
 * 1、fling比较卡；
 * 2、考虑ListView内容为空的情况；
 * 3、考虑Header有自定义视图的情况
 * @author Administrator
 *
 */
public class MainActivity extends FragmentActivity 
	implements OnClickListener, IRefreshCallback, IScaleCallback {

	/**
	 * custom banner view
	 */
    ScalablePtrView mSPtrView;
    /**
     * content container
     */
    UnSwappableViewPager mViewPager;
    /**
     * tab view container
     */
    LinearLayout mTabGroup;
    
    /**
     * current fragment index
     */
    int mCurPageIndex = 0;
    Fragment[] mFragments;

    Handler mHander; 
    /**
     * value inserter, used to set header's margin
     */
    SmoothScroller mScroller;
    /**
     * auto scroll invoked by page select callback 
     */
    boolean mIsAutoScroll = false;
    
    @Override
    public void onClick(View v) {
    	final int tag = (Integer) v.getTag();
    	switch (tag) {
    	case 0:
    		mViewPager.setCurrentItem(0);
    		break;
    	case 1:
    		mViewPager.setCurrentItem(1);
    		break;
    	case 2:
    		mViewPager.setCurrentItem(2);
    		break;
    	case 3:
    		mViewPager.setCurrentItem(3);
    		break;
    	case 4:
    		mViewPager.setCurrentItem(4);
    		break;
    		
    	}
    	
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mViewPager = (UnSwappableViewPager) findViewById(R.id.main_page_container);
        mSPtrView = (ScalablePtrView) findViewById(R.id.scalable_ptr_view);
        mTabGroup = (LinearLayout) findViewById(R.id.tab_container);
        
        mSPtrView.setRefreshCallback(this);
        mSPtrView.setScaleCallback(this);
        
        mHander = new Handler(getMainLooper());
        mScroller = SmoothScroller.getInstance();
        final FragmentManager fManager = getSupportFragmentManager();
        mFragments = new Fragment[] {
                new SubFragment(0, null, mOnScrollListener, mSPtrView),
                new SubFragment(1, null, mOnScrollListener, mSPtrView)
        };
      
        LayoutParams lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.weight = 1;
        TextView tab0 = new TextView(getApplicationContext());
        tab0.setText("T0");
        tab0.setTag(0);
        tab0.setOnClickListener(this);
        mTabGroup.addView(tab0, lParams);
        TextView tab1 = new TextView(getApplicationContext());
        tab1.setText("T1");
        tab1.setTag(1);
        tab1.setOnClickListener(this);
        mTabGroup.addView(tab1, lParams);
        
        mViewPager.setAdapter(new CustomFragmentPagerAdapter(fManager, mFragments));

        mViewPager.setOnPageChangeListener(mOnPageCnangeListener);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setCurrentItem(0);
        
        ViewServer.get(getApplicationContext()).addWindow(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ViewServer.get(getApplicationContext()).removeWindow(this);
	}

	@Override
	public void onRefresh() {
		mSPtrView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mViewPager.enableSwap(false);
				mSPtrView.onRefreshComplete();
				mViewPager.enableSwap(true);
			}
		}, 2000l);
	}

	private void reAdjustSiblings(int position) {
		SubFragment cFragment = null; 
		if (position >= 0 && position < mFragments.length) {
			cFragment = (SubFragment) mFragments[position];
			cFragment.requestAdjust(0, mSPtrView.mMinTopMargin);
		}
	}
	
	private void setSiblingSelections(int position) {
		SubFragment cFragment = null; 
		if (position >= 0 && position < mFragments.length) {
			cFragment = (SubFragment) mFragments[position];
			cFragment.setSelectionFromTop(0, mSPtrView.mMinTopMargin);
		}
	}
	
	private OnPageChangeListener mOnPageCnangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int curPage, float offsetRate, int offsetPix) {
        }

        @Override
        public void onPageSelected(final int i) {
            if (i != mCurPageIndex && !mSPtrView.isOutofRange()) {
            	// 若跳转到未初始化的fragment?
            	final int preIndex = mCurPageIndex;
            	mCurPageIndex = i;
            	if (mSPtrView.topInvisible()) {// 若Tab已经置顶 
            		Log.i("junjiang2", "onPageSelected reAdjustSiblings ");
            		/**
            		 * reset the current page 
            		 */
            		reAdjustSiblings(i);
            		reAdjustSiblings(preIndex < i ? i + 1 : i - 1);
            	} else {// 否则第i页执行autoScroll
            		Log.i("junjiang2", "onPageSelected setViewMarginTop ");
            		mIsAutoScroll = true;
            		SubFragment fragment = (SubFragment)mFragments[i];
            		if (fragment.requestAdjust(0, mSPtrView.mCurTopMargin)) {
            			Log.i("junjiang2", "onPageSelected setViewMarginTop 1");
            			setSiblingSelections(preIndex);
            			fragment.scrollListBy(-(mSPtrView.mMinTopMargin - mSPtrView.mCurTopMargin));
            		} else {// 若已经scroll出范围
            			setSiblingSelections(preIndex);
            			setSiblingSelections(i);
            			fragment.requestAdjust(0, mSPtrView.mCurTopMargin);
            			mScroller.smoothScroll(mHander, mSPtrView.mCurTopMargin, mSPtrView.mMinTopMargin, 
            					new IScrollAction() {
									@Override
									public void actScroll(int nextVal) {
										mSPtrView.setViewMarginTop(nextVal);
									}
								}, new OnSmoothScrollFinishedListener() {
									
									@Override
									public void onSmoothScrollFinished() {
										setSiblingSelections(preIndex);
									}
								});
            		}
            	}
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
	
    IOnScrollListener mOnScrollListener = new IOnScrollListener() {
        @Override
        public void CustomedOnScrollStateChanged(CustomedListView absListView, int state) {
        	
        	final int fIndex = absListView.getFIndex();
        	if (SCROLL_STATE_IDLE == state && 0 != mSPtrView.mMarginDist && mCurPageIndex == fIndex) {
        		SubFragment cFragment = null; 
        		if (mIsAutoScroll) {
        			// 若是pageSelect触发的scroll
        			mIsAutoScroll = false;
        			int leftPagePos = mCurPageIndex - 1;
        			int rightPagePos = mCurPageIndex + 1;
        			reAdjustSiblings(leftPagePos);
        			reAdjustSiblings(rightPagePos);
        		} else {// 否则两边都要同步
        			int leftPagePos = mCurPageIndex - 1;
        			int rightPagePos = mCurPageIndex + 1;
        			
        			if (leftPagePos >= 0) {
        				cFragment = (SubFragment) mFragments[leftPagePos];
        				cFragment.scrollListBy(mSPtrView.mMarginDist, 1);
        			}
        			if (rightPagePos < mFragments.length) {
        				cFragment = (SubFragment) mFragments[rightPagePos];
        				cFragment.scrollListBy(mSPtrView.mMarginDist, 1);
        			}
        		}
        		mSPtrView.mMarginDist = 0;
        	}
        }

        @Override
        public void CustomedOnScroll(CustomedListView absListView, int first, int visibleCount, int wholeCount) {
        	final View child = absListView.getChildAt(0);
        	final int fIndex = absListView.getFIndex();
        	if (null == child || mCurPageIndex != fIndex) {
        		return;
        	}

            //计算List scroll的距离
            final int distanceFromTop = absListView.getDistanceFromTop(first);
            final int top = child.getTop();
            final int scrollDist = top - distanceFromTop;
            mSPtrView.setViewMarginTop(scrollDist);
//            scrollListBy(scrollDist);
        }
    };

    class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments;

        public CustomFragmentPagerAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return null == fragments ? null : fragments[i];
        }

        @Override
        public int getCount() {
            return null == fragments ? 0 : fragments.length;
        }
        
    }

	@Override
	public void scaleTo(int nextVal) {
		if (null != mFragments) {
			SubFragment tmp = null;
			for (Fragment fragment : mFragments) {
				tmp = (SubFragment) fragment;
				tmp.scaleListHeaderHeightTo(nextVal);
			}
		}
	}
	
	public void scrollListBy(int distance) {
		if (null != mFragments) {
			SubFragment tmp = null;
			for (int i = 0; i < mFragments.length; i++) {
				tmp = (SubFragment) mFragments[i];
				if (i != mCurPageIndex)
				tmp.scrollListBy(distance, 1);
			}
		}
	}

}
