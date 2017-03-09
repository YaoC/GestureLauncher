package cn.edu.pku.chengyao.gesturelauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
		//	启动悬浮窗服务
		Intent intent = new Intent(context, FloatWindowService.class);
		startService(intent);
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

}
