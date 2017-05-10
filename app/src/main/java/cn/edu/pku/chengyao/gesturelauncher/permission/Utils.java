package cn.edu.pku.chengyao.gesturelauncher.permission;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.pku.chengyao.gesturelauncher.main.MyApplication;

/**
 * @author chengyao
 */
public class Utils {

	public static final String TAG = "Utils";

	/**
	 * 获取屏幕宽度
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenWidth(Context context) {
		return ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenHeight(Context context) {
		return ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getHeight();
	}

	//	获取当前时间
	public static String getTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		return formatter.format(curDate);
	}

	public static String getDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		return formatter.format(curDate);
	}


	public static void appendLog(Context context, String type, String text) {


		File logFileDir = context.getCacheDir();
		File logFile = new File(logFileDir.getPath() + "/log-" + type + "-" + MyApplication.getID() + "-" + getDate() + ".txt");
		if (!logFile.exists()) {
			try {

				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			//BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			buf.append(text);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void appendGestureLog(final Context context, final float[] img, final String packageName, final int position) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				MyGesture gesture = new MyGesture(img, position, packageName);
				File logFileDir = context.getCacheDir();
				String path = logFileDir.getPath() + "/log-myGestures-" + MyApplication.getID() + "-" + getDate() + ".txt";
				writeToBinary(path, gesture, true);
				Log.i(TAG, "run: 手势保存成功");
			}
		});
		thread.start();
	}

	public static float[] convertGestureToArray(Gesture gesture) {
		Bitmap b = gesture.toBitmap(100, 100, 25, Color.WHITE);
		int[] data = new int[100 * 100];
		b.getPixels(data, 0, 100, 0, 0, 100, 100);
		float[] img = new float[10000];
		for (int i = 0; i < 10000; i++) {
			if (data[i] != 0) {
				img[i] = 0;
			} else {
				img[i] = 1.0f;
			}
		}
		return img;
	}


	public static void writeToBinary(String filename, Object obj, boolean append) {
		File file = new File(filename);
		ObjectOutputStream out = null;

		try {
			if (!file.exists() || !append)
				out = new ObjectOutputStream(new FileOutputStream(filename));
			else out = new AppendableObjectOutputStream(new FileOutputStream(filename, append));
			out.writeObject(obj);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	private static class AppendableObjectOutputStream extends ObjectOutputStream {
		public AppendableObjectOutputStream(OutputStream out) throws IOException {
			super(out);
		}

		@Override
		protected void writeStreamHeader() throws IOException {
		}
	}

}
