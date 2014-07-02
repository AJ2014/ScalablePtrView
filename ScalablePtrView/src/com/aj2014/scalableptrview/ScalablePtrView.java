package com.aj2014.scalableptrview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ScalablePtrView extends LinearLayout {

	/**
	 * scalable head view
	 */
	private IScalableView mScalableView;
	
	public ScalablePtrView(Context context) {
		super(context);
		
	}

	public ScalablePtrView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		mScalableView = new ScalableView(context);
	}

}
