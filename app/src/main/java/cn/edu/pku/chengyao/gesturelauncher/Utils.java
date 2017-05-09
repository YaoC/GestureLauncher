package cn.edu.pku.chengyao.gesturelauncher;

import android.content.Context;
import android.view.WindowManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author chengyao
 */
public class Utils {

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
		File logFile = new File(logFileDir.getPath() + "/log-" + type + "-" + MyApplication.getMacAddress() + "-" + getDate() + ".txt");
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

}
