package com.hyj.music;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * 通关界面
 * 
 * @Author hyj
 * @Date 2016-3-8 下午9:53:42
 */
public class AllPassView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_pass_view);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initListener();
	}

	private void initView() {
		findViewById(R.id.layout_bar_coin).setVisibility(View.GONE);
	}

	private void initData() {

	}

	private void initListener() {

	}
}
