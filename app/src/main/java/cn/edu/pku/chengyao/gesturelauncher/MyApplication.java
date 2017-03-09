package cn.edu.pku.chengyao.gesturelauncher;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.gesture.Gesture;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chengyao
 * date 2017/3/7
 * mail chengyao09@hotmail.com
 *
 **/
public class MyApplication extends Application{

    private static Application mApplication;

    public static final String TAG = "MyApp";

    private static List<ResolveInfo> launchables;

    private static PackageManager pm;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initAppInfos2();
    }

    public static Application getInstance(){
        return mApplication;
    }

    //获取所有可以启动的activity
    private void initAppInfos2() {
        pm = mApplication.getPackageManager();
        Intent main=new Intent(Intent.ACTION_MAIN, null);
        List<ResolveInfo> apps = pm.queryIntentActivities(main, 0);
        Map<String, ResolveInfo> temp = new HashMap<>();
        for (ResolveInfo resolveInfo : apps) {
//            Log.d(TAG, "initAppInfos2: "+resolveInfo.activityInfo.packageName);
            temp.put(resolveInfo.activityInfo.packageName, resolveInfo);
        }
        launchables = new ArrayList<>(temp.values());
        Log.d(TAG, "initAppInfos2: runing apps "+ProcessManager.getRunningApps());
    }

    //  根据输入的手势返回4个APP，现在只是随机返回四个
    public static List<ResolveInfo> getLaunchables(Gesture gesture){
        // TODO: 2017/3/7 手势识别匹配
        int idx=(int)(Math.random()*launchables.size());
        return launchables.subList(idx, idx + 4);
    }


    public static PackageManager getMyPackageManager() {
        return pm;
    }

}
