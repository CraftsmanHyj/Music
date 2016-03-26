package com.hyj.music.utils;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * 音乐播放类
 * 
 * @Author hyj
 * @Date 2016-3-9 下午10:28:15
 */
public class MyPlayer {

	public static final int INDEX_ENTER = 0;
	public static final int INDEX_CACEL = 1;
	public static final int INDEX_COIN = 2;

	// 歌曲播放
	private static MediaPlayer player;

	private final static String[] SONG_NAMES = { "enter.mp3", "cancel.mp3",
			"coin.mp3" };
	// 音效数组
	private static MediaPlayer[] mToneMediaPlayer = new MediaPlayer[SONG_NAMES.length];

	/**
	 * 播放音效
	 * 
	 * @param context
	 * @param index
	 */
	public static void playTone(Context context, int index) {
		if (null == mToneMediaPlayer[index]) {
			mToneMediaPlayer[index] = new MediaPlayer();
		}
		mToneMediaPlayer[index].reset();

		// 加载声音
		AssetManager assetManager = context.getAssets();

		try {
			AssetFileDescriptor fileDescriptor = assetManager
					.openFd(SONG_NAMES[index]);
			// 设置数据源
			mToneMediaPlayer[index]
					.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());
			mToneMediaPlayer[index].prepare();
			mToneMediaPlayer[index].start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void playSong(Context context, String fileName) {
		if (null == player) {
			player = new MediaPlayer();
		}

		// 强制重置
		player.reset();

		// 加载声音
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			// 设置数据源
			player.setDataSource(fileDescriptor.getFileDescriptor(),
					fileDescriptor.getStartOffset(), fileDescriptor.getLength());

			player.prepare();
			player.start();// 播放声音
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stopTheSone(Context context) {
		if (null != player) {
			player.stop();
		}
	}
}
