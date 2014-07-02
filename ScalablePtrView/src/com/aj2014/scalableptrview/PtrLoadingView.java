package com.aj2014.scalableptrview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class PtrLoadingView extends AbsPtrView {

	private TextView mLoadingLabel;
	private ImageView mLoadingIcon;
	
	private boolean mHasLabel = true;
	
	protected int mNormalMargin;
	protected int mMaxMargin;
	protected int mRefreshMargin;
	
	private String mStrPtrLabel, mStrRtrLabel, mStrRefLabel;
	private Animation mAnimPtr, mAnimRtr, mAnimRef;
	
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
	}

	@Override
	public void doReset() {
		setStateLabel(null);
		setStateAnim(null);
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
	
	/**
	 * 更新topMargin值
	 * @param destination 目标topMargin值
	 */
	private void setMarginLayoutParams(int destination) {
		MarginLayoutParams mParams = (MarginLayoutParams) getLayoutParams();
		mParams.topMargin = destination;
		setLayoutParams(mParams);
		invalidate();
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
		
		mStrPtrLabel = context.getResources().getString(R.string.label_ptr);
		mStrRtrLabel = context.getResources().getString(R.string.label_rtr);
		mStrRefLabel = context.getResources().getString(R.string.label_ref);
		
		mAnimPtr = (Animation) context.getResources().getAnimation(R.anim.anim_ptr_rotate);
		mAnimRtr = (Animation) context.getResources().getAnimation(R.anim.anim_rtr_rotate);
		mAnimRef = (Animation) context.getResources().getAnimation(R.anim.anim_ref_rotate);
		
	}

}
