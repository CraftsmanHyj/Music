package com.hyj.music.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.hyj.music.R;
import com.hyj.music.model.WordButton;
import com.hyj.music.utils.Utils;

public class MyGridView extends GridView {
	public final static int COUNTS_WORDS = 24;

	private List<WordButton> mArrayList = new ArrayList<WordButton>();

	private MyGridAdapter mAdapter;

	private Context mContext;

	private Animation mScaleAnimation;

	private WordButtonClickListener wordButtonListener;

	/**
	 * 设置点击响应事件
	 * 
	 * @param wordButtonListener
	 */
	public void setWordButtonListener(WordButtonClickListener wordButtonListener) {
		this.wordButtonListener = wordButtonListener;
	}

	public MyGridView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		mContext = context;

		mAdapter = new MyGridAdapter();
		this.setAdapter(mAdapter);
	}

	public void updateData(List<WordButton> list) {
		mArrayList = list;

		// 重新设置数据源
		setAdapter(mAdapter);
	}

	private class MyGridAdapter extends BaseAdapter {
		public int getCount() {
			return mArrayList.size();
		}

		public Object getItem(int position) {
			return mArrayList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View v, ViewGroup p) {
			final WordButton holder;

			if (v == null) {
				v = Utils.getView(mContext, R.layout.self_ui_gridview_item);

				holder = mArrayList.get(position);

				// 加载动画
				mScaleAnimation = AnimationUtils.loadAnimation(mContext,
						R.anim.scale_word);
				// 设置动画延迟时间
				mScaleAnimation.setStartOffset(position * 100);

				holder.mIndex = position;
				if (null == holder.mViewButton) {
					holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
					holder.mViewButton
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (null != wordButtonListener) {
										wordButtonListener
												.onWordButtonClick(holder);
									}
								}
							});
				}

				v.setTag(holder);
			} else {
				holder = (WordButton) v.getTag();
			}

			holder.mViewButton.setText(holder.mWordString);
			// 播放动画
			v.startAnimation(mScaleAnimation);

			return v;
		}
	}

	public interface WordButtonClickListener {
		public void onWordButtonClick(WordButton wordButton);
	}
}
