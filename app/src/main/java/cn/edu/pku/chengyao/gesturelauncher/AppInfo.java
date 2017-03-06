package cn.edu.pku.chengyao.gesturelauncher;

import android.graphics.drawable.Drawable;

/**
 * Created by chengyao on 2017/3/5.
 * App 信息封装类
 *
 */

public class AppInfo {

    private String appName;
    private String packageName;
    private Drawable icon;

    public AppInfo() {}

    public AppInfo(String appName) {
        this.appName = appName;
    }

    public AppInfo(String appName, String packageName, Drawable icon) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }
}
