package com.hyj.music.model;

import android.widget.Button;

/**
 * 文字按钮
 * 
 * @Author hyj
 * @Date 2016-2-23 下午10:30:31
 */
public class WordButton {

	public int mIndex;// 索引
	public boolean mIsVisible;// 是否显示
	public String mWordString;// 显示文字

	public Button mViewButton;

	public WordButton() {
		mIsVisible = true;
		mWordString = "";
	}
}
