package com.hyj.music.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyj.music.Constants;
import com.hyj.music.R;

public class Utils {
	private static AlertDialog dialog;

	/**
	 * 通过layoutId获取一个View对象
	 * 
	 * @param context
	 * @param layoutId
	 * @return
	 */
	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(layoutId, null);
	}

	/**
	 * 界面跳转
	 * 
	 * @param context
	 * @param desti
	 */
	public static void startActivity(Context context, Class<?> desti) {
		Intent intent = new Intent(context, desti);
		context.startActivity(intent);
	}

	/**
	 * 显示自定义对话框
	 * 
	 * @param context
	 * @param msg
	 * @param okAction
	 */
	public static void showDialog(final Context context, String msg,
			final DialogAction okAction) {
		View dialogView = getView(context, R.layout.dialog_view);

		TextView tvMsg = (TextView) dialogView
				.findViewById(R.id.text_dialog_message);
		tvMsg.setText(msg);

		ImageButton okButton = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_ok);
		ImageButton cancelButton = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_cancel);

		if (null != okAction) {
			okButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					MyPlayer.playTone(context, MyPlayer.INDEX_ENTER);

					okAction.action();

					if (null != dialog) {
						dialog.dismiss();
					}
				}
			});
		}

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyPlayer.playTone(context, MyPlayer.INDEX_CACEL);

				if (null != dialog) {
					dialog.dismiss();
				}
			}
		});

		// 设置弹出窗口的样式
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				R.style.Theme_TransParent);
		builder.setView(dialogView);
		dialog = builder.create();
		dialog.show();
	}

	public interface DialogAction {
		public void action();
	}

	/**
	 * 数据保存
	 * 
	 * @param context
	 * @param stageIndex
	 * @param coins
	 */
	public static void saveData(Context context, int stageIndex, int coins) {
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(Constants.FILE_NAME_SAVE_DATA,
					Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeInt(stageIndex);
			dos.writeInt(coins);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.flush();
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static int[] loadData(Context context) {
		FileInputStream fis = null;
		int[] datas = { 0, Constants.TOTAL_COINS };

		try {
			fis = context.openFileInput(Constants.FILE_NAME_SAVE_DATA);
			DataInputStream dis = new DataInputStream(fis);
			datas[0] = dis.readInt();
			datas[1] = dis.readInt();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return datas;
	}
}
