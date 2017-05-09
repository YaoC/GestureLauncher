package cn.edu.pku.chengyao.gesturelauncher.permission;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

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
                    Log.d(TAG, "done: upload success");
                } else {
                    Log.d(TAG, "done: upload error");
                }
            }
        });

    }

    public static void uploadAppLogFile(Context context) {
        File logFileDir = context.getCacheDir();
        for (final File f : logFileDir.listFiles(new logFilter())) {
            try {
                AVFile file = AVFile.withAbsoluteLocalPath(f.getName(),
                        f.getPath());
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        Log.d(TAG, "上传成功！");
                        if (f.delete()) {
                            Log.d(TAG, "done: 日志删除成功！");
                        }else{
                            Log.d(TAG, "done: 日志删除失败！");
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//            Log.d(TAG, "uploadAppLogFile: "+f.getPath());
        }
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
