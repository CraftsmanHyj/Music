package com.hyj.music;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.hyj.lib.tools.LogUtils;
import com.hyj.music.model.Song;
import com.hyj.music.model.WordButton;
import com.hyj.music.ui.MyGridView;
import com.hyj.music.ui.MyGridView.WordButtonClickListener;
import com.hyj.music.utils.MyPlayer;
import com.hyj.music.utils.Utils;
import com.hyj.music.utils.Utils.DialogAction;

public class MainActivity extends Activity implements OnClickListener {
	private final int COUNT_WORDS = 24;

	/**
	 * 答案正确
	 */
	public final static int STATUS_ANSWER_RIGHT = 1;
	/**
	 * 答案错误
	 */
	public final static int STATUS_ANSWER_WRONG = 2;
	/**
	 * 答案不完整
	 */
	public final static int STATUS_ANSWER_LACK = 3;

	private long exitTime;// 按返回键的时间

	// title相关
	private TextView tvTitleCoins;

	// 唱片相关动画
	private Animation mPanAnima;
	private LinearInterpolator mPanLin;// 线性动画，动画运动的速度

	// 拨杆动画
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;

	private ImageView mViewPan;
	private ImageView mViewPanBar;

	private ImageButton mBtnPlayStart;

	// 过关界面
	private LinearLayout mPassView;

	// 开始按钮动画
	private Animation mBtnSmallAnim;
	private LinearInterpolator mBtnSmallLin;

	private Animation mBtnBigAmin;
	private LinearInterpolator mBtnBigLin;

	// 文字选择
	private List<WordButton> lAllWords;// 存储所有的文字
	private MyGridView mMyGridView;

	private LinearLayout mViewWordsContainer;
	private List<WordButton> mBtnSelectWords;

	// 按钮
	private ImageButton ibDelete;
	private ImageButton ibTip;

	// 当前歌曲
	private Song mCurrentSong;
	// 当前关的索引
	private int mCurrentStageIndex;

	private TextView mCurrentStageView;

	// 当前关的索引
	private TextView mCurrentStagePassView;
	// 当前歌曲的名称
	private TextView mCurrentSongNamePassView;

	// 单签金币数量
	private int currentCoins = Constants.TOTAL_COINS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		int[] datas = Utils.loadData(this);

		mCurrentStageIndex = datas[0];
		currentCoins = datas[1];

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initListener();
		initCurrentStageData();
	}

	private void initView() {
		tvTitleCoins = (TextView) findViewById(R.id.txt_bar_coins);

		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mViewPan = (ImageView) findViewById(R.id.imageViewPan);
		mViewPanBar = (ImageView) findViewById(R.id.imageViewBar);

		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

		mPassView = (LinearLayout) findViewById(R.id.pass_view);

		ibDelete = (ImageButton) findViewById(R.id.btn_delete_word);
		ibTip = (ImageButton) findViewById(R.id.btn_tip_answer);
	}

	private void initData() {
		tvTitleCoins.setText(currentCoins + "");

		// 加载设置好的动画
		mPanAnima = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnima.setInterpolator(mPanLin);// 设置动画时线性变化的

		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45_in);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setInterpolator(mBarInLin);

		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45_out);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setInterpolator(mBarOutLin);

		mBtnSmallAnim = AnimationUtils.loadAnimation(this, R.anim.scale_small);
		mBtnSmallLin = new LinearInterpolator();
		mBtnSmallAnim.setInterpolator(mBtnSmallLin);

		mBtnBigAmin = AnimationUtils.loadAnimation(this, R.anim.scale_big);
		mBtnBigLin = new LinearInterpolator();
		mBtnBigAmin.setInterpolator(mBtnBigLin);
	}

	private void initListener() {
		mBtnPlayStart.setOnClickListener(this);

		ibDelete.setOnClickListener(this);
		ibTip.setOnClickListener(this);

		mMyGridView.setWordButtonListener(new WordButtonClickListener() {

			@Override
			public void onWordButtonClick(WordButton wordButton) {
				wordButtonClick(wordButton);
			}
		});

		mBarInAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mBtnPlayStart.setEnabled(false);
				mBtnPlayStart.startAnimation(mBtnSmallAnim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewPan.startAnimation(mPanAnima);
			}
		});

		mPanAnima.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				MyPlayer.playSong(MainActivity.this, mCurrentSong.getFileName());
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewPanBar.startAnimation(mBarOutAnim);
				mBtnPlayStart.startAnimation(mBtnBigAmin);
			}
		});

		mBarOutAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mBtnPlayStart.setEnabled(true);
			}
		});
	}

	/**
	 * 读取当前关的歌曲信息
	 * 
	 * @param stageIndex
	 * @return
	 */
	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();
		String[] stage = Constants.SONG_INFO[stageIndex];
		song.setFileName(stage[0]);
		song.setName(stage[1]);
		return song;
	}

	/**
	 * 初始化当前关卡的数据
	 */
	private void initCurrentStageData() {
		// 读取当前关卡的消息
		mCurrentSong = loadStageSongInfo(mCurrentStageIndex++);

		// 初始化已选择框
		mBtnSelectWords = initWordSelect();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		// 清空原来的答案
		mViewWordsContainer.removeAllViews();
		for (WordButton button : mBtnSelectWords) {
			mViewWordsContainer.addView(button.mViewButton, params);
		}

		// 当前关的索引
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		mCurrentStageView.setText(mCurrentStageIndex + "");

		// 获得数据
		lAllWords = initAllWord();
		// 更新数据 myGridView
		mMyGridView.updateData(lAllWords);
	}

	/**
	 * 初始化待选文字框
	 * 
	 * @return
	 */
	private List<WordButton> initAllWord() {
		List<WordButton> lDatas = new ArrayList<WordButton>();

		String[] words = generateWords();

		for (int i = 0; i < COUNT_WORDS; i++) {
			WordButton button = new WordButton();
			button.mWordString = words[i];

			lDatas.add(button);
		}

		return lDatas;
	}

	/**
	 * 初始化已选择文字框
	 * 
	 * @return
	 */
	private List<WordButton> initWordSelect() {
		List<WordButton> lData = new ArrayList<WordButton>();

		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			View view = Utils.getView(MainActivity.this,
					R.layout.self_ui_gridview_item);

			final WordButton holder = new WordButton();
			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisible = false;

			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			holder.mViewButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clearTheAnswer(holder);
				}
			});

			lData.add(holder);
		}

		return lData;
	}

	/**
	 * 处理所有的过关界面及事件
	 */
	private void handlePassEvent() {
		mPassView.setVisibility(View.VISIBLE);

		// 停止动画
		mViewPan.clearAnimation();

		MyPlayer.stopTheSone(this);
		MyPlayer.playTone(this, MyPlayer.INDEX_COIN);

		// 当前关的索引
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		if (null != mCurrentStagePassView) {
			mCurrentStagePassView.setText((mCurrentStageIndex) + "");
		}

		// 显示歌曲的名称
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
		if (null != mCurrentSongNamePassView) {
			mCurrentSongNamePassView.setText(mCurrentSong.getName());
		}

		// 下一关按键处理
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (judegAppPassed()) {
					// 进入到通关界面
					Utils.startActivity(MainActivity.this, AllPassView.class);
				} else {
					// 开始下一关
					mPassView.setVisibility(View.GONE);

					// 加载关卡数据
					initCurrentStageData();
				}
			}
		});
	}

	/**
	 * 判断是否通关
	 * 
	 * @return
	 */
	private boolean judegAppPassed() {
		return mCurrentStageIndex == (Constants.SONG_INFO.length - 1);
	}

	/**
	 * 清除已经选择的答案
	 * 
	 * @param wordButton
	 */
	private void clearTheAnswer(WordButton wordButton) {
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisible = false;

		// 设置待选框可见性
		setButtonVisible(lAllWords.get(wordButton.mIndex), View.VISIBLE);
	}

	/**
	 * 设置文字选中逻辑
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// 设置答案文字框内容及可见性
				mBtnSelectWords.get(i).mViewButton
						.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mIsVisible = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
				// 记录索引
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;

				LogUtils.i("已选文字索引值：" + wordButton.mIndex);
				// 设置待选框的可见性
				setButtonVisible(wordButton, View.INVISIBLE);

				break;
			}
		}
	}

	/**
	 * 设置按钮的可见性
	 * 
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisible(WordButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisible = (visibility == View.VISIBLE) ? true : false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_play_start:
			playSong();
			break;

		case R.id.btn_delete_word:
			Utils.showDialog(MainActivity.this, "确认花掉30个金币去掉一个错误答案？",
					new DialogAction() {

						@Override
						public void action() {
							handleDeleteWord();
						}
					});
			break;

		case R.id.btn_tip_answer:
			Utils.showDialog(MainActivity.this, "确认花掉90个金币获得一个文字提示？",
					new DialogAction() {

						@Override
						public void action() {
							handleTipAnswer();
						}
					});
			break;
		}
	}

	public void wordButtonClick(WordButton wordButton) {
		setSelectWord(wordButton);

		mPassView.setVisibility(View.GONE);

		// 获得答案状态
		int checkResult = checkTheAnswer();
		switch (checkResult) {
		case STATUS_ANSWER_LACK:
			for (WordButton word : mBtnSelectWords) {
				word.mViewButton.setTextColor(Color.WHITE);
			}
			break;

		case STATUS_ANSWER_WRONG:
			// 错误提示
			sparkTheWords();
			break;

		case STATUS_ANSWER_RIGHT:
			// 过关
			handlePassEvent();
			break;
		}
	}

	/**
	 * 播放歌曲
	 */
	private void playSong() {
		mViewPanBar.startAnimation(mBarInAnim);
	}

	/**
	 * 生成所有的待选文字
	 * 
	 * @return
	 */
	private String[] generateWords() {
		String[] words = new String[MyGridView.COUNTS_WORDS];
		// 将歌名存入
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}

		// 获取随机文字并存入数组
		for (int i = mCurrentSong.getNameLength(); i < words.length; i++) {
			words[i] = getRandomChar() + "";
		}

		// 打乱文字数据
		List<String> lData = Arrays.asList(words);
		Collections.sort(lData, new Comparator<String>() {

			@Override
			public int compare(String lhs, String rhs) {
				return Math.random() > 0.5 ? 1 : -1;
			}
		});

		words = lData.toArray(new String[words.length]);
		return words;
	}

	/**
	 * <pre>
	 * 随机生成汉字：
	 * 	 原理：http://www.cnblogs.com/skyivben/archive/2012/10/20/2732484.html
	 * </pre>
	 * 
	 * @return
	 */
	private char getRandomChar() {
		String str = "";
		int heightPos;// 高位
		int lowPos;// 地位

		Random random = new Random();
		heightPos = 176 + Math.abs(random.nextInt(39));// 最大可取87
		lowPos = 161 + Math.abs(random.nextInt(93));// 最大可取94

		byte[] b = new byte[2];
		b[0] = Integer.valueOf(heightPos).byteValue();
		b[1] = Integer.valueOf(lowPos).byteValue();

		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return str.charAt(0);
	}

	/**
	 * 检查答案
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// 判断答案长度
		String answer = "";
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// 如果有空，则答案没有选完
			String str = mBtnSelectWords.get(i).mWordString;
			if (TextUtils.isEmpty(str)) {
				return STATUS_ANSWER_LACK;
			}
			answer += str;
		}

		// 检查答案正确性
		if (answer.equals(mCurrentSong.getName())) {
			return STATUS_ANSWER_RIGHT;
		}

		return STATUS_ANSWER_WRONG;
	}

	Timer timer = new Timer();

	/**
	 * 文字闪烁
	 */
	private void sparkTheWords() {
		// 定时器相关
		TimerTask task = new TimerTask() {
			boolean change = false;
			// 闪烁的次数
			int mSpartTimes = 0;

			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						if (++mSpartTimes > 6) {
							// timer.cancel();
							return;
						}
						// 执行闪烁：交替显示红色和白色
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							Button btn = mBtnSelectWords.get(i).mViewButton;
							int colors = change ? Color.RED : Color.WHITE;
							btn.setTextColor(colors);
						}
						change = !change;
					}
				});
			}
		};

		timer.schedule(task, 1, 150);
	}

	/**
	 * 增加/减少金币
	 * 
	 * @param data
	 * @return
	 */
	private boolean handleCoins(int data) {
		// 判断当前总的金币数量
		if (currentCoins + data >= 0) {
			currentCoins += data;

			tvTitleCoins.setText(currentCoins + "");
			return true;
		}
		return false;
	}

	/**
	 * 读取删除操作所需的金币数
	 * 
	 * @return
	 */
	private int getDeleteWordCoins() {
		return getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * 读取提示所需金币数
	 * 
	 * @return
	 */
	private int getTipCoins() {
		return getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * 处理删除待选文字事件
	 */
	private void handleDeleteWord() {
		// 减少金币
		if (!handleCoins(-getDeleteWordCoins())) {
			Utils.showDialog(MainActivity.this, "您金币不够，请购买",
					new DialogAction() {

						@Override
						public void action() {
						}
					});
			return;
		}

		// 将索引对应的WordButton设置为不可见

		Random random = new Random();
		setButtonVisible(
				findNotAnswerWord(random.nextInt(MyGridView.COUNTS_WORDS)),
				View.INVISIBLE);
	}

	private WordButton findNotAnswerWord(int index) {

		WordButton buf = null;
		buf = lAllWords.get(index);

		if (!buf.mIsVisible || isTheAnserWord(buf)) {
			Random random = new Random();
			findViewById(random.nextInt(MyGridView.COUNTS_WORDS));
		}

		LogUtils.i("提示删除的文字为：" + buf.mWordString);
		return buf;
	}

	/**
	 * 找到一个答案文字
	 * 
	 * @param index
	 *            当前需要填入答案框的索引
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;

		for (WordButton button : lAllWords) {
			if (button.mWordString
					.equals(mCurrentSong.getNameCharacters()[index] + "")) {
				buf = button;
				break;
			}
		}
		return buf;
	}

	/**
	 * 判断文字是否是答案
	 * 
	 * @param word
	 * @return
	 */
	private boolean isTheAnserWord(WordButton word) {
		for (char songChar : mCurrentSong.getNameCharacters()) {
			if (word.mWordString.equals(songChar + "")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 处理提示按钮事件
	 */
	private void handleTipAnswer() {
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// 减少金币
				if (!handleCoins(-getTipCoins())) {
					Utils.showDialog(MainActivity.this, "您金币不够，请购买",
							new DialogAction() {

								@Override
								public void action() {
								}
							});
					return;
				}

				wordButtonClick(findIsAnswerWord(i));
				tipWord = true;
				break;
			}
		}

		// 没有找到可以填充的答案
		if (!tipWord) {
			// 闪烁文字提示用户
			sparkTheWords();
		}
	}

	@Override
	protected void onPause() {
		Utils.saveData(this, mCurrentStageIndex - 1, currentCoins);

		mViewPan.clearAnimation();
		MyPlayer.stopTheSone(this);

		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();

		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			LogUtils.e(bundle.getString("value"));
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - exitTime > 2000) {
				com.hyj.lib.tools.DialogUtils.showToastShort(this, "再按一次退出程序");
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
