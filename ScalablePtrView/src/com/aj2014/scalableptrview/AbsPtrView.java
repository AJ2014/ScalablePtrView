package com.aj2014.scalableptrview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class AbsPtrView extends FrameLayout implements IPtrView {
	
	protected EPtrState mCurState;

	public AbsPtrView(Context context) {
		super(context);
	}
	
	public AbsPtrView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void switchTo(EPtrState state) {
		switch (state) {
		case PULL_TO_REFRESH:
			doPullToRefresh();
			break;

		case RELEASE_TO_REFRESH:
			doReleaseToRefresh();
			break;
		case REFRESHING: 
			doRefreshing();
			break;
		default:
			break;
		}
	}
	
	public abstract void doPullToRefresh();
	public abstract void doReleaseToRefresh();
	public abstract void doRefreshing();
	public abstract void doReset();
}
