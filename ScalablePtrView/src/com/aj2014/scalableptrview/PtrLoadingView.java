package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.SmoothScroller.IScrollAction;
import com.aj2014.scalableptrview.SmoothScroller.OnSmoothScrollFinishedListener;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class PtrLoadingView extends AbsPtrView {

	private static final float SCALE_RATE = 0.25f;
	private TextView mLoadingLabel;
	private ImageView mLoadingIcon;
	private Handler mHandler;
	private SmoothScroller mScroller;
	
	private boolean mHasLabel = true;
	
	protected int mNormalMargin;
	protected int mMaxMargin;
	protected int mRefreshMargin;
	protected int mCurMargin;
	
	private String mStrPtrLabel, mStrRtrLabel, mStrRefLabel;
	private Animation mAnimPtr, mAnimRtr, mAnimRef;
	
	private IRefreshCallback mCallback;
	
	public void setRefreshCallback(IRefreshCallback callback) {
		mCallback = callback;
	}
	
	public static interface IRefreshCallback {
		public void onRefresh();
	}
	
	public PtrLoadingView(Context context) {
		super(context);
		init(context, null);
	}

	public PtrLoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	@Override
	public void doPullToRefresh() {
		setStateLabel(mStrPtrLabel);
		setStateAnim(mAnimPtr);
	}

	@Override
	public void doReleaseToRefresh() {
		setStateLabel(mStrRtrLabel);
		setStateAnim(mAnimRtr);
	}

	@Override
	public void doRefreshing() {
		setStateLabel(mStrRefLabel);
		setStateAnim(mAnimRef);
		if (null != mCallback) {
			mCallback.onRefresh();
		}
	}

	@Override
	public void doReset() {
		setStateLabel(null);
		setStateAnim(null);
		
		setMarginLayoutParams(mNormalMargin);
	}
	
	/**
	 * 设置是否显示状态字符
	 * @param has true：显示 false：不显示
	 */
	public void setHasLabel(boolean has) {
		mHasLabel = has;
		if (mHasLabel) {
			setLabelVisibility(View.VISIBLE);
		} else {
			setLabelVisibility(View.GONE);
		}
	}
	
	public void initMarginLayoutParams() {
		setMarginLayoutParams(mNormalMargin);
	}
	
	/**
	 * 更新topMargin值
	 * @param margin 目标topMargin值
	 */
	private void setMarginLayoutParams(int margin) {
		Log.i("junjiang2", "setMarginLayoutParams=" + margin);
		MarginLayoutParams mParams = (MarginLayoutParams) getLayoutParams();
		mParams.topMargin = margin;
		setLayoutParams(mParams);
		invalidate();
		mCurMargin = margin;
	}
	
	private void setLabelVisibility(int visibility) {
		if (null != mLoadingLabel) {
			mLoadingLabel.setVisibility(visibility);
		}
	}
	
	private void setStateLabel(CharSequence label) {
		if (mHasLabel && null != mLoadingLabel) {
			mLoadingLabel.setText(label);
			if (null == label) {
				setLabelVisibility(View.INVISIBLE);
			} else {
				setLabelVisibility(View.VISIBLE);
			}
		}
	}
	
	private void setStateAnim(Animation anim) {
		if (null != mLoadingIcon) {
			mLoadingIcon.clearAnimation();
			if (null == anim) {
				mLoadingIcon.setVisibility(View.INVISIBLE);
			} else {
				mLoadingIcon.setVisibility(View.VISIBLE);
				mLoadingIcon.startAnimation(anim);
			}
		}
	}
	
	private void init(Context context, AttributeSet attrs) {
		
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.adapter_listview_header_refresh, null);
		mLoadingLabel = (TextView) header.findViewById(R.id.pull_to_refresh_text);
		mLoadingIcon = (ImageView) header.findViewById(R.id.pull_to_refresh_image);
		LayoutParams fParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fParams.gravity = Gravity.CENTER_HORIZONTAL;
		addView(header, fParams);
		
		mStrPtrLabel = context.getResources().getString(R.string.label_ptr);
		mStrRtrLabel = context.getResources().getString(R.string.label_rtr);
		mStrRefLabel = context.getResources().getString(R.string.label_ref);
		
		mAnimPtr = AnimationUtils.loadAnimation(getContext(), R.anim.anim_ptr_rotate);
		mAnimRtr = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rtr_rotate);
		mAnimRef = AnimationUtils.loadAnimation(getContext(), R.anim.anim_ref_rotate);
		
		mLoadingIcon.setImageResource(R.drawable.icon_refresh);
		
		mHandler = new Handler(context.getMainLooper());
		mScroller = SmoothScroller.getInstance();
		
	}
	
	
	@Override
	public void onPull(int distance) {
		mCurMargin = (int) (mCurMargin + distance);
		mCurMargin = mCurMargin < mNormalMargin ? mNormalMargin : mCurMargin;
		mCurMargin = mCurMargin > mMaxMargin ? mMaxMargin : mCurMargin;
		// reset view's margin
		setMarginLayoutParams(mCurMargin);
		// 若移动范围超出刷新范围
		if (mCurMargin >= mRefreshMargin) {
			mRefreshable = true;
		} else {
			mRefreshable = false;
		}
		
		super.onPull(distance);
	}
	
	@Override
	public boolean recover(int distance) {
		boolean releaseToRefresh = releaseToRefresh();
		int destMargin = releaseToRefresh ? mRefreshMargin : mNormalMargin;
		if (destMargin != mCurMargin) {
			Log.i("junjiang2", "resize from " + mCurMargin + " to " + destMargin);
			smoothMarginTo(destMargin, releaseToRefresh);
		}
		return super.recover(distance);
	}
	
	public void onRefreshComplete() {
		final int destMargin = mNormalMargin;
		if (destMargin != mCurMargin) {
			smoothMarginTo(destMargin, false);
		}
	}
	
	private void smoothMarginTo(int to, final boolean refresh) {
		mScroller.smoothScroll(mHandler, mCurMargin, to, new IScrollAction() {
			@Override
			public void actScroll(int nextVal) {
				setMarginLayoutParams(nextVal);
			}
		}, new OnSmoothScrollFinishedListener() {
			@Override
			public void onSmoothScrollFinished() {
				if (refresh) {
					switchTo(EPtrState.REFRESHING);
				} else {
					switchTo(EPtrState.PULL_TO_REFRESH);
				}
			}
		});
	}

	@Override
	public boolean isOutofRange() {
		return mCurMargin > mNormalMargin;
	}
	
	public void initMargins(int scaleDistance) {
		final int margin = -getHeight();
		mNormalMargin = (int) (-scaleDistance / 4f);
		mNormalMargin = mNormalMargin > margin ? margin : mNormalMargin;
		mRefreshMargin = -mNormalMargin;
		mMaxMargin = (int) (-mNormalMargin * 2);
		mCurMargin = mNormalMargin;
		Log.i("junjiang2", String.format("normal:%d refresh:%d max:%d", mNormalMargin, mRefreshMargin, mMaxMargin));
	}

}
