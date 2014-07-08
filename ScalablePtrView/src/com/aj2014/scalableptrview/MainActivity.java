package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.PtrLoadingView.IRefreshCallback;
import com.aj2014.scalableptrview.ScalableImageView.IRecoverCallback;
import com.aj2014.scalableptrview.SmoothScroller.IScrollAction;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends FragmentActivity 
	implements OnClickListener, IRefreshCallback, IRecoverCallback {

	/**
	 * custom banner view
	 */
    ScalablePtrView mSPtrView;
    ViewPager mViewPager;
    /**
     * tab view container
     */
    LinearLayout mTabGroup;
    
    public static int curMargin = 0, minMargin, maxMargin = 0;
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
    /**
     * current fragment's listView's scroll distance
     */
 	int scrollDistance = 0;
 	/**
 	 * current margin changed range, used to sync the listView's scroll
 	 */
 	int marginDist = 0;
    
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
        mViewPager = (ViewPager) findViewById(R.id.main_page_container);
        mSPtrView = (ScalablePtrView) findViewById(R.id.scalable_ptr_view);
        mTabGroup = (LinearLayout) findViewById(R.id.tab_container);
        
        mSPtrView.setRefreshCallback(this);
        mSPtrView.setRecoverCallback(this);
        
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

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int curPage, float offsetRate, int offsetPix) {
            }

            @Override
            public void onPageSelected(final int i) {
                if (i != mCurPageIndex) {
                	// 若跳转到未初始化的fragment?
                	final int preIndex = mCurPageIndex;
                	mCurPageIndex = i;
                	if (curMargin == minMargin) {// 若Tab已经置顶 
                		/**
                		 * reset the current page 
                		 */
                		reAdjustSiblings(i);
                		reAdjustSiblings(preIndex < i ? i + 1 : i - 1);
                	} else {// 否则第i页执行autoScroll
                		mIsAutoScroll = true;
                		if (((SubFragment)mFragments[i]).requestAdjust(0, curMargin)) {
                			((SubFragment)mFragments[i]).scrollListBy(-(minMargin - curMargin));
                		} else {// 若已经scroll出范围
                			setSiblingSelections(preIndex);
                			setSiblingSelections(i);
                			((SubFragment)mFragments[i]).requestAdjust(0, curMargin);
                			mScroller.smoothScroll(mHander, curMargin, minMargin, new IScrollAction() {
								@Override
								public void actScroll(int nextVal) {
									setViewMarginTop(nextVal);
								}
							}, null);
                		}
                	}
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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
				mSPtrView.onRefreshComplete();
			}
		}, 1000l);
	}

	private void reAdjustSiblings(int position) {
		SubFragment cFragment = null; 
		if (position >= 0 && position < mFragments.length) {
			cFragment = (SubFragment) mFragments[position];
			cFragment.requestAdjust(0, minMargin);
		}
	}
	
	private void setSiblingSelections(int position) {
		SubFragment cFragment = null; 
		if (position >= 0 && position < mFragments.length) {
			cFragment = (SubFragment) mFragments[position];
			cFragment.setSelectionFromTop(0, minMargin);
		}
	}
	
    IOnScrollListener mOnScrollListener = new IOnScrollListener() {
        @Override
        public void CustomedOnScrollStateChanged(CustomedListView absListView, int state) {
        	
        	Log.i("junjiang2", "CustomedOnScrollStateChanged=" + state);
        	
        	final int fIndex = absListView.getFIndex();
        	if (SCROLL_STATE_IDLE == state && 0 != marginDist && mCurPageIndex == fIndex) {
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
        				cFragment.scrollListBy(marginDist, 1);
        			}
        			if (rightPagePos < mFragments.length) {
        				cFragment = (SubFragment) mFragments[rightPagePos];
        				cFragment.scrollListBy(marginDist, 1);
        			}
        		}
        		marginDist = 0;
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
            scrollDistance = distanceFromTop - top;
            Log.i("junjiang2", "CustomedOnScroll =" + scrollDistance + " distanceFromTop =" + distanceFromTop);
            //更新TabView margin
            setViewMarginTop(-scrollDistance);
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

    private void setViewMarginTop(int margin) {

        margin = margin > maxMargin ? maxMargin : margin;
        margin = margin < minMargin ? minMargin : margin;

        if (null != mSPtrView) {
        	mSPtrView.setScalableViewMarginTop(margin);
        }
        
        int distM = curMargin - margin;
        
        // used to sync the sibling fragments' list
        if (distM != 0) {
        	marginDist += distM;
        }
        
        curMargin = margin;
        
    }

	@Override
	public void scaleTo(int nextVal) {
		Log.i("junjiang2", "scaleTo " + nextVal);
		if (null != mFragments) {
			SubFragment tmp = null;
			for (Fragment fragment : mFragments) {
				tmp = (SubFragment) fragment;
				tmp.scaleListHeaderHeightTo(nextVal);
			}
		}
	}

}
