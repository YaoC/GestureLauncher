package cn.edu.pku.chengyao.gesturelauncher;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.gesture.Gesture;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static List<String> launchableAppNames;

    private static PackageManager pm;

    private  static WifiManager  wm;
    private static  String m_szWLANMAC;

    private static short startFromGestureLauncher = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        //  LeanCloud 初始化
        AVOSCloud.initialize(this,"e0L04eCl2rQigGAuvFwH9uvP-gzGzoHsz","rjtSdvNwCmxky8tRQJecRFqN");
        //  放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);
        mApplication = this;
        initMacAddress();
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
        Set<String> systemApps = getSystemApps();
        Map<String, ResolveInfo> temp = new HashMap<>();
        for (ResolveInfo resolveInfo : apps) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (!systemApps.contains(packageName)) {
                temp.put(packageName, resolveInfo);
            }
        }
        launchables = new ArrayList<>(temp.values());
        launchableAppNames = new ArrayList<>(temp.keySet());
        Log.d(TAG, "initAppInfos2: "+launchableAppNames);
//        Log.d(TAG, "initAppInfos2: runing apps "+ProcessManager.getRunningApps());
    }

    //  根据输入的手势返回4个APP，现在只是随机返回四个
    public static List<Map<String,Object>> getLaunchables(Gesture gesture){
        // TODO: 2017/3/7 手势识别匹配
        int idx=(int)(Math.random()*(launchables.size()-9));
        List<ResolveInfo> appList = launchables.subList(idx, idx + 9);

        List<Map<String, Object>> apps = new ArrayList<>();
        for (ResolveInfo resolveInfo : appList) {
            Map<String, Object> app = new HashMap<>();
            app.put("appName", resolveInfo.loadLabel(pm));
            app.put("activityName", resolveInfo.activityInfo.name);
            app.put("packageName", resolveInfo.activityInfo.packageName);
            app.put("icon", resolveInfo.loadIcon(pm));
            apps.add(app);
        }
        return apps;

    }

    //  获取mac地址
    private void initMacAddress() {
        wm = (WifiManager) getSystemService(this.WIFI_SERVICE);
        if (wm.getConnectionInfo().getMacAddress() == null) {
            m_szWLANMAC = "000000000000";
        } else {
            m_szWLANMAC = wm.getConnectionInfo().getMacAddress().replace(":", "");
        }
    }

    public static String getMacAddress(){
        return m_szWLANMAC;
    }

    public static PackageManager getMyPackageManager() {
        return pm;
    }

    public static void setStartFromGestureLauncher(){
        startFromGestureLauncher = 1;
    }

    private static void setStartFromOthers(){
        startFromGestureLauncher = 0;
    }

    public static short getStartFromGestureLauncherFlag() {
        short returnVal = startFromGestureLauncher;
        setStartFromOthers();
        return returnVal;
    }

    public static List<String> getlaunchableAppNames() {
        return launchableAppNames;
    }

    private Set<String> getSystemApps(){
        Set<String> systemApps = new HashSet<>();
        systemApps.add(getSystemAppPackageName(Intent.CATEGORY_DESK_DOCK));
        systemApps.add(getSystemAppPackageName(Intent.CATEGORY_HOME));
        return systemApps;
    }

    private String getSystemAppPackageName(String category){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(category);
        ResolveInfo res = getPackageManager().resolveActivity(intent, 0);
        return res.activityInfo.packageName;
    }

}
