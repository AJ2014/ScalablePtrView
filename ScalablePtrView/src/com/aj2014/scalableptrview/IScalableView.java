package com.aj2014.scalableptrview;

public interface IScalableView {

	/**
	 * ���ŵ�ָ��ֵ
	 */
	public void scaleTo(float distance);
	/**
	 * ״̬�ָ�
	 * @param distance scale �ܾ���
	 */
	public void recover(int distance);
	
}
