package com.aj2014.scalableptrview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public abstract class AbsScalableView extends View implements IScalableView {
	
	private IPtrView mPtrView;
	
	public AbsScalableView(Context context) {
		super(context);
		init(context, null);
	}
	
	public AbsScalableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		
	}
}
