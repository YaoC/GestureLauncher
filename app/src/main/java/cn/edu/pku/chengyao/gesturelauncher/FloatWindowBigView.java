package cn.edu.pku.chengyao.gesturelauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * project: GestureLauncher
 * package: cn.edu.pku.chengyao.gesturelauncher
 * author: chengyao
 * date: 2017/3/7
 * mail: chengyao09@hotmail.com
 *
 **/
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
	private GridView appPanel;

	private String startTime;

	public FloatWindowBigView(Context context) {
		super(context);
		this.context = context;

		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);

		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;


		//	隐藏 app_panel
		appPanel = (GridView) findViewById(R.id.app_panel);
		appPanel.setVisibility(INVISIBLE);

		Log.d(TAG, "FloatWindowBigView: viewWidth=" + viewWidth + "  viewHeight=" + viewHeight);

		bigWindowParams = new WindowManager.LayoutParams();
		// 设置显示的位置，默认的是屏幕中心
		bigWindowParams.x = Utils.getScreenWidth(context) / 2 - viewWidth / 2;
		bigWindowParams.y = Utils.getScreenHeight(context) / 2 - viewHeight / 2;
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
		gesturePanel.setVisibility(INVISIBLE);
		appPanel.setVisibility(VISIBLE);
		// TODO: 2017/3/5 识别手势，返回APP数组
		List<Map<String, Object>> appList = MyApplication.getLaunchables(gesture);
		Log.d(TAG, "onGesturePerformed: 手势结束时间 " + Utils.getTime());
		// TODO: 2017/3/5  记录绘制完成时间,写入日志
		String endTime = Utils.getTime();
		Utils.appendLog(context, "gesture", MyApplication.getMacAddress() + "," + startTime + "," + endTime);
		SimpleAdapter simpleAdapter = new SimpleAdapter(
				context, appList, R.layout.app_item,
				new String[]{"appName", "activityName", "packageName", "icon"},
				new int[]{R.id.app_name, R.id.activity_name, R.id.app_package_name, R.id.app_icon});
		appPanel.setAdapter(simpleAdapter);
		//加载Drawable
		simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String arg2) {
				if(view instanceof ImageView && data instanceof Drawable){
					ImageView iv = (ImageView)view;
					iv.setImageDrawable((Drawable)data);
					return true;
				}else{
					return false;
				}
			}
		});
		appPanel.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				//	标记该App是由GestureLauncher打开的
				MyApplication.setStartFromGestureLauncher();
				//	关闭appPanel
				FloatWindowManager.getInstance(context).removeBigWindow();
				//	打开点击的APP
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

	@Override
	public void onGesturingStarted(GestureOverlayView overlay) {
		Log.d(TAG, "onGesturingStarted: 手势开始时间 "+Utils.getTime());
		// TODO: 2017/3/5  在日志中记录手势开始时间
		startTime = Utils.getTime();

	}

	@Override
	public void onGesturingEnded(GestureOverlayView overlay) {
		Log.d(TAG, "onGesturingEnded: ");
	}



}
