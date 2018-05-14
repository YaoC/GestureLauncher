package cn.edu.pku.chengyao.gesturelauncher.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chengyao
 *         date 2017/3/25
 *         mail chengyao09@hotmail.com
 **/
public class LeanCloudLog {

    private static final String TAG = "LeanCloudLog";
    
    public static void uploadAppLaunchLog(AppLaunchLogObject appLaunchLogObject){

        AVObject log = new AVObject("AppLaunchLog");
        log.put("deviceId", appLaunchLogObject.getDeviceId());
        log.put("launchAt", appLaunchLogObject.getLaunchAt());
        log.put("launchByApp", appLaunchLogObject.getLaunchByApp());
        log.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.i(TAG, "done: upload success");
                } else {
                    Log.i(TAG, "done: upload error");
                }
            }
        });

    }

    public static void uploadAppLogFile(final Context context) {
        File logFileDir = context.getCacheDir();
        for (final File f : logFileDir.listFiles(new logFilter())) {
            try {
                AVFile file = AVFile.withAbsoluteLocalPath(f.getName(),
                        f.getPath());
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        Toast.makeText(context, "日志上传成功", Toast.LENGTH_LONG).show();
                        if (f.delete()) {
                            Log.i(TAG, "done: 日志删除成功！");
                        }else{
                            Log.i(TAG, "done: 日志删除失败！");
                        }
                        SharedPreferences sharedPreferences = context.getSharedPreferences("MainActivity", context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("lastLogUploadDate", System.currentTimeMillis());
                        editor.apply();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//            Log.d(TAG, "uploadAppLogFile: "+f.getPath());
        }
    }

    /*
     * 返回当前缓存的所有日志
     */
    public static List<String> getLogFilesName(Context context) {
        List<String> files = new ArrayList<>();
        File logFileDir = context.getCacheDir();
        for (File f : logFileDir.listFiles(new logFilter())) {
            files.add(f.getName());
        }
        return files;
    }

    private static class logFilter implements FilenameFilter {
        public boolean accept(File dir,String name){
            return name.startsWith("log") && name.endsWith(".txt");
        }
    }

    public static class AppLaunchLogObject{

        private String deviceId;
        private String launchAt;
        private boolean launchByApp;

        public AppLaunchLogObject(String deviceId, String launchAt, Boolean launchByApp) {
            this.deviceId = deviceId;
            this.launchAt = launchAt;
            this.launchByApp = launchByApp;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getLaunchAt() {
            return launchAt;
        }

        public boolean getLaunchByApp() {
            return launchByApp;
        }
    }
}
