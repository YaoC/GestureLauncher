package cn.edu.pku.chengyao.gesturelauncher.tools;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author chengyao
 * date 2017/3/8
 * mail chengyao09@hotmail.com
 * 监听应用启动记录
 **/
public class AppMonitorService extends Service {

    private static final String TAG = "AppMonitorService";

    private Handler handler = new Handler();
    private Timer timer;

    private List<String> lastApps;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: start service!");
//        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        lastApps = ProcessManager.getRunningAppNames();

        if (timer == null) {
            timer = new Timer();
            Log.d(TAG, "onStartCommand: timer()");
            // 每5秒就执行一次刷新任务
            timer.scheduleAtFixedRate(new MonitorAppTask(), 0, 5000);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
    }

    private class MonitorAppTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
//                    List<String> currentApps = ProcessManager.getRunningAppNames();
//                    List<String> newApps = new ArrayList<String>(currentApps);
//                    newApps.removeAll(lastApps);
//                    if (!newApps.isEmpty()) {
//                        for (String app : newApps) {
//                            Log.d(TAG, "run: " + Utils.getTime() + " " + app);
//                        }
//                    }
//                    lastApps = currentApps;
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
                    for (int i = 0; i < runningAppProcessInfo.size(); i++) {
                        Log.v("Proc: ", runningAppProcessInfo.get(i).processName);
                    }

                }
            });
        }
    }

}
