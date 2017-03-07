package cn.edu.pku.chengyao.gesturelauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


public class FloatWindowBigView extends RelativeLayout implements
		GestureOverlayView.OnGesturePerformedListener,
		GestureOverlayView.OnGesturingListener {

	private static final String TAG = "GestureLauncher";

	// 记录大悬浮窗的宽
	public int viewWidth;
	// 记录大悬浮窗的高
	public int viewHeight;

	public WindowManager.LayoutParams bigWindowParams;

	private Context context;

	//	手势面板
	private GestureOverlayView gesturePanel;

	//	APP面板
	private LinearLayout appPanel;

	private int[] ids = {
			R.id.app_0,
			R.id.app_1,
			R.id.app_2,
			R.id.app_3
	};

	public FloatWindowBigView(Context context) {
		super(context);
		this.context = context;

		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);

		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;


		//	隐藏 app_panel
		appPanel = (LinearLayout) findViewById(R.id.app_panel);
		appPanel.setVisibility(INVISIBLE);

		Log.d(TAG, "FloatWindowBigView: viewWidth=" + viewWidth + "  viewHeight=" + viewHeight);

		bigWindowParams = new WindowManager.LayoutParams();
		// 设置显示的位置，默认的是屏幕中心
		bigWindowParams.x = ScreenUtils.getScreenWidth(context) / 2 - viewWidth
				/ 2;
		bigWindowParams.y = ScreenUtils.getScreenHeight(context) / 2
				- viewHeight / 2;
		bigWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		bigWindowParams.format = PixelFormat.RGBA_8888;

		// 设置交互模式
		bigWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
		bigWindowParams.width = viewWidth;
		bigWindowParams.height = viewHeight;

		initView();

	}

	private void initView() {
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.big_window_layout);
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FloatWindowManager.getInstance(context).removeBigWindow();
			}
		});

		//	手势
		gesturePanel = (GestureOverlayView) findViewById(R.id.gesture_panel);
		//	设置手势可以多笔完成
		gesturePanel.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);

		//	绑定监听器
		gesturePanel.addOnGesturingListener(this);
		gesturePanel.addOnGesturePerformedListener(this);


	}

	//手势响应
	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		Log.d(TAG, "onGesturePerformed: ");
		// TODO: 2017/3/5  记录绘制完成时间,写入日志
		// TODO: 2017/3/5 识别手势，返回APP数组
		gesturePanel.setVisibility(INVISIBLE);
		appPanel.setVisibility(VISIBLE);
		List<ResolveInfo> appList = MyApplication.getLaunchables();
		Log.d(TAG, "onGesturePerformed: "+appList);
		PackageManager pm = MyApplication.getMyPackageManager();
		for (int i = 0; i < 4; i++) {
			LinearLayout app = (LinearLayout) findViewById(ids[i]);
			ImageView appIcon = (ImageView) app.findViewById(R.id.app_icon);
			TextView appName = (TextView) app.findViewById(R.id.app_name);
			TextView packageName = (TextView) app.findViewById(R.id.app_package_name);
			final TextView activityName = (TextView) app.findViewById(R.id.activity_name);
			appIcon.setImageDrawable(appList.get(i).loadIcon(pm));
			appName.setText(appList.get(i).loadLabel(pm));
			packageName.setText(appList.get(i).activityInfo.packageName);
			activityName.setText(appList.get(i).activityInfo.name);
			app.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FloatWindowManager.getInstance(context).removeBigWindow();
					String packageName = ((TextView) v.findViewById(R.id.app_package_name))
							.getText().toString();
					String activityName = ((TextView) v.findViewById(R.id.activity_name))
							.getText().toString();
					ComponentName name=new ComponentName(packageName,activityName);
					Intent i=new Intent(Intent.ACTION_MAIN);
					i.addCategory(Intent.CATEGORY_LAUNCHER);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
							Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					i.setComponent(name);
					context.startActivity(i);
				}
			});

		}
	}

	@Override
	public void onGesturingStarted(GestureOverlayView overlay) {
		Log.d(TAG, "onGesturingStarted: ");
		// TODO: 2017/3/5  记录开始时间
	}

	@Override
	public void onGesturingEnded(GestureOverlayView overlay) {
		Log.d(TAG, "onGesturingEnded: ");
	}


}
