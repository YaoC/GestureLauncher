package cn.edu.pku.chengyao.gesturelauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;

import eu.chainfire.libsuperuser.Shell;
import eu.chainfire.libsuperuser.StreamGobbler;

/**
 * @author chengyao
 * date 2017/3/7
 * mail chengyao09@hotmail.com
 * 手势启动器主界面，提供添加／关闭悬浮窗的功能
 *
 **/
public class MainActivity extends Activity {

	private static final String TAG = "GestureLauncher";

	private FloatWindowManager floatWindowManager;

	private Context context;

	public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
//		Intent i = new Intent(context,AppMonitorService.class);
//		startService(i);
		floatWindowManager = FloatWindowManager.getInstance(context);
		uploadLogs();
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.addCategory(Intent.CATEGORY_DESK_DOCK);
//		ResolveInfo res = getPackageManager().resolveActivity(intent, 0);
//		Toast.makeText(this, res.activityInfo.packageName, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		uploadLogs();
	}

	/**
	 * 显示小窗口
	 * @param view
	 */
	public void show(View view) {
		//  悬浮窗权限
		if(Build.VERSION.SDK_INT>=23)
		{
			if(Settings.canDrawOverlays(this))
			{
				//有悬浮窗权限开启服务绑定 绑定权限
				//	启动悬浮窗服务
				Intent intent = new Intent(context, FloatWindowService.class);
				startService(intent);

			}else{
				//没有悬浮窗权限m,去开启悬浮窗权限
				try{
					Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
							Uri.parse("package:" + getPackageName()));
					startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
				}catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		} else{
			//默认有悬浮窗权限  但是 华为, 小米,oppo等手机会有自己的一套Android6.0以下  会有自己的一套悬浮窗权限管理 也需要做适配
			Intent intent = new Intent(context, FloatWindowService.class);
			startService(intent);
		}
	}

	/**
	 * 移除所有的悬浮窗
	 *
	 * @param view
	 */
	public void remove(View view) {
		floatWindowManager.removeAll();
	}

	/**
	 * 重载 MainActivity 下的返回键为关闭二级悬浮窗（手势板）
	 * @param keyCode
	 * @param event
     * @return
     */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 返回键移除二级悬浮窗
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			floatWindowManager.removeBigWindow();
			Intent i= new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	//	LeanCloud测试
	public void testLeanCloud(View view) {
		LeanCloudLog.uploadAppLogFile(context);
	}

	public void appendLogToFile(View view) {
		Utils.appendLog(context,"usage",Utils.getTime() + ",1");
	}


	private void uploadLogs(){
		final String key = "lastLogUploadDate";
		final long oneDay = 86400000;
//		测试用
//		final long oneDay = 60000;
		SharedPreferences sharedPreferences = getSharedPreferences(TAG,MODE_PRIVATE);
		long lastLogUploadDate = sharedPreferences.getLong(key, 0);
		if (lastLogUploadDate != 0) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastLogUploadDate >= oneDay) {
				LeanCloudLog.uploadAppLogFile(context);
			}
		}
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(key, System.currentTimeMillis());
		editor.apply();
	}
}
