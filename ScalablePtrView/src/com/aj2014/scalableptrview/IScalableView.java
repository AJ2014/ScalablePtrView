package com.aj2014.scalableptrview;

public interface IScalableView {

	/**
	 * 缩放到指定值
	 */
	public void scaleTo(float distance);
	/**
	 * 状态恢复
	 * @param distance scale 总距离
	 */
	public void recover(int distance);
	
}
