package cn.edu.pku.chengyao.gesturelauncher;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chengyao on 2017/3/5.
 */

public class MyApplication extends Application{

    private static Application mApplication;

    public static final String TAG = "MyApp";

    private static List<AppInfo> appInfos;
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

//    private void initAppInfos() {
//        appInfos = new ArrayList<>();
//        PackageManager pm = mApplication.getPackageManager();
//        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
//        for (PackageInfo packageInfo : packageInfos) {
//            String packageName = packageInfo.packageName;
//            if (mApplication.getPackageManager()
//                    .getLaunchIntentForPackage(packageName) != null) {
//                String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
//                Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
//                AppInfo appInfo = new AppInfo(appName, packageName, icon);
//                appInfos.add(appInfo);
//            }
//        }
//    }

    //获取所有可以启动的activity
    private void initAppInfos2() {
        pm = mApplication.getPackageManager();
        Intent main=new Intent(Intent.ACTION_MAIN, null);
        launchables=pm.queryIntentActivities(main, 0);
        Collections.sort(launchables,
                new ResolveInfo.DisplayNameComparator(pm));
        Log.d(TAG, "initAppInfos2: "+launchables);

    }

    public static List<ResolveInfo> getLaunchables(){
        return launchables.subList(0, 4);
    }

//    public static List<AppInfo> getAppInfos(){
//        return appInfos;
//    }

//    public static Intent startApp(String packagename) {
//        Intent i = mApplication.getPackageManager()
//                .getLaunchIntentForPackage(packagename);
//        return i;
//    }

    public static PackageManager getMyPackageManager() {
        return pm;
    }

}
