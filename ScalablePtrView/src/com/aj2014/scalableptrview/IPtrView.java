package com.aj2014.scalableptrview;

public interface IPtrView {

	public static enum EPtrState {
		PULL_TO_REFRESH,
		RELEASE_TO_REFRESH,
		REFRESHING
	}
	
	/**
	 * 
	 */
	public void switchTo(EPtrState state);
	
}
