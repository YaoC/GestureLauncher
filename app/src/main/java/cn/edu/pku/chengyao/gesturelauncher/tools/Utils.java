package cn.edu.pku.chengyao.gesturelauncher.tools;

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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.edu.pku.chengyao.gesturelauncher.main.MyApplication;
import cn.edu.pku.yaochg.imagesimilarity.DssimInterface;

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
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		return formatter.format(curDate);
	}

	public static String getDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		return formatter.format(curDate);
	}

	/*
	 * 将时间戳转换为时间
     */
	public static String stampToTime(long s) {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(s);
		res = simpleDateFormat.format(date);
		return res;
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

	// 存储图片到指定位置
	public static boolean saveIconToFile(File dir, String fileName, Bitmap bm,
										 Bitmap.CompressFormat format, int quality) {

		File imageFile = new File(dir, fileName);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imageFile);

			bm.compress(format, quality, fos);

			fos.close();

			return true;
		} catch (IOException e) {
			Log.e("app", e.getMessage());
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return false;
	}

	public static Map<String, Map<String, Double>> iconsSimilarity(Context context) {


		File iconDir = new File(context.getCacheDir().getAbsolutePath() + "/icons");
		File[] icons = iconDir.listFiles(new pngFilter());

		File externalFileDir = context.getExternalFilesDir(null);
		File[] icons20 = externalFileDir.listFiles(new pngFilter());

		Map<String, Map<String, Double>> similarity = new HashMap<>();

		for (File icon : icons) {
			Map<String, Double> tempSim = new HashMap<>();
			for (File icon20 : icons20) {
				double sim = new DssimInterface().similarity(icon.getAbsolutePath(), icon20.getAbsolutePath());
				if (!(sim >= 0)) {
					sim = 100;
				}
				tempSim.put(icon20.getName().replace(".png", ""), sim);
			}

			similarity.put(icon.getName().replace(".png", ""), tempSim);
		}
		return similarity;
	}

	private static class pngFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith(".png");
		}
	}
}
