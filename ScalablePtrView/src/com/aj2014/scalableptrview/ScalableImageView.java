package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.SmoothScroller.IScrollAction;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ScalableImageView extends AbsScalableView {

	private static final int BG_IMG_RID = R.drawable.bg_person_center_head;
	private static final float SCALE_RATE = 0.5f;
	private static final int DEF_SCALE_HEIGHT = 200;
	protected int mNormalSize;
	protected int mMaxSize;
	protected int mRefreshSize;
	protected int mCurSize;
	private Handler mHandler;
	private ImageView mScalableImage;
	private SmoothScroller mValueInsertor;
	
	public ScalableImageView(Context context) {
		super(context);
		init(context, null);
	}
	
	public ScalableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		
		int scaleHeight = DEF_SCALE_HEIGHT;
		int imgRid = BG_IMG_RID;
		if (null != attrs) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScaleToRefresh);
			if (a.hasValue(R.styleable.ScaleToRefresh_imgSrc)) {
				imgRid = a.getResourceId(R.styleable.ScaleToRefresh_imgSrc, 0);
			}
			if (null != a && a.hasValue(R.styleable.ScaleToRefresh_imgHeight)) {
				scaleHeight = (int) a.getDimension(R.styleable.ScaleToRefresh_imgHeight, DEF_SCALE_HEIGHT);
			}
			if (null != a) {
				a.recycle();
			}
		}
		
		mHandler = new Handler(context.getMainLooper());
		mValueInsertor = SmoothScroller.getInstance();
		mScalableImage = new ImageView(context);
		mScalableImage.setScaleType(ScaleType.CENTER_CROP);
		mScalableImage.setImageResource(imgRid);
		// add the scalable child view
		LayoutParams gParams = new LayoutParams(LayoutParams.MATCH_PARENT, scaleHeight);
		addView(mScalableImage, 0, gParams);
		
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	public void scaleTo(int distance) {
		mCurSize = mCurSize + (int) (distance * SCALE_RATE);
		mCurSize = mCurSize < mNormalSize ? mNormalSize : mCurSize;
		mCurSize = mCurSize > mMaxSize ? mMaxSize : mCurSize;
		// scale view's height
		setViewsHeight(mScalableImage, mCurSize);
		super.scaleTo(distance);
	}
	
	@Override
	public void recover(int distance) {
		int destSize = mRefresh ? mRefreshSize : mNormalSize;
		if (mCurSize != destSize && (!mRefresh || mCurSize > destSize)) {
			setSizeTo(destSize);
		}
		
		super.recover(distance);
	}
	
	private void setSizeTo(int to) {
		mValueInsertor.smoothScroll(mHandler, mCurSize, to, new IScrollAction() {
			@Override
			public void actScroll(int nextVal) {
				setViewsHeight(mScalableImage, nextVal);
			}
		}, null);
	}
	
	@Override
	public boolean isOutofRange() {
		boolean superOut = super.isOutofRange();
		return superOut && mCurSize == mNormalSize;
	}
	
	private void setViewsHeight(View view, int height) {
		LayoutParams gParams = (LayoutParams) mScalableImage.getLayoutParams();
		gParams.height = height;
		mScalableImage.setLayoutParams(gParams);
		mScalableImage.invalidate();
		mCurSize = height;
	}
	
	@Override
	public void onGlobalLayout() {
		super.onGlobalLayout();
		initScaleSizes();
	}
	
	private void initScaleSizes() {
		mCurSize = getHeight();
		mNormalSize = mCurSize;
		mMaxSize = (int) (mCurSize * 1.5f);
		mRefreshSize = (int) (mCurSize * 1.25f);
		// 设置最大可向上margin的距离
		MainActivity.minMargin = -mNormalSize;
	}
	
	@Override
	public void onRefreshComplete() {
		setSizeTo(mNormalSize);
		super.onRefreshComplete();
	}
	
	public void setMarginTop(int margin) {
		MarginLayoutParams mParams = (MarginLayoutParams) getLayoutParams();
		mParams.topMargin = margin;
		setLayoutParams(mParams);
		invalidate();
	}

}
