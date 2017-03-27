package cn.edu.pku.chengyao.gesturelauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;

/**
 * @author chengyao
 * date 2017/3/7
 * mail chengyao09@hotmail.com
 * 手势启动器主界面，提供添加／关闭悬浮窗的功能
 *
 **/
public class MainActivity extends Activity {

	private FloatWindowManager floatWindowManager;

	private Context context;

	public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		Intent i = new Intent(context,AppMonitorService.class);
		startService(i);
		floatWindowManager = FloatWindowManager.getInstance(context);
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
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	//	LeanCloud测试
	public void testLeanCloud(View view) {
//		LeanCloudLog.AppLaunchLogObject test1 = new LeanCloudLog.AppLaunchLogObject("02:00:00:00:00:00", Utils.getTime(),true);
//		LeanCloudLog.uploadAppLaunchLog(test1);
		LeanCloudLog.uploadAppLogFile(context);
	}

	public void appendLogToFile(View view) {
		Utils.appendLog(context,"usage",Utils.getTime() + ",1");
	}

	public  void activityTopTest(View view){
//		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//		List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
//		for (int i = 0; i < runningAppProcessInfo.size(); i++) {
//			Log.v("Proc: ", runningAppProcessInfo.get(i).processName);
//		}
	}
}
