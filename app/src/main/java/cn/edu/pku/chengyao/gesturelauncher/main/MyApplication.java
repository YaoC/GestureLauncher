package cn.edu.pku.chengyao.gesturelauncher.main;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    // TF
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private static TensorFlowInferenceInterface inferenceInterface;

    private static String[] labels = {"Adobe Air", "Chrome", "Facebook", "QQ", "WPS Office",
            "京东", "优酷", "健康", "去哪儿旅行", "天猫", "微信", "微博", "手机淘宝", "支付宝", "滴滴出行",
            "照片", "百度地图", "相机", "邮件", "金山词霸"};
    private static final String MODEL_FILE = "file:///android_asset/frozen_gesture_scores.pb";
    private static final String INPUT_NODE = "x_input";
    private static final String OUTPUT_NODE = "output/scores";
    private static final int[] INPUT_SIZE = {100,100};
    private static final int[] DROPOUT_SIZE = {1};

//    // TEST
//    private static final String IMG_FILE = "015-b_10.txt";

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
        initTensorFlow();
    }

    public static Application getInstance(){
        return mApplication;
    }

    //获取所有可以启动的activity
    private void initAppInfos2() {
        pm = mApplication.getPackageManager();
        Intent main=new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
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
        Bitmap b = gesture.toBitmap(100, 100, 25, Color.WHITE);
        int[] data = new int[100*100];
        b.getPixels(data,0, 100, 0, 0, 100, 100);
        float[] img = new float[100 * 100];
        for (int i=0;i<10000;i++) {
            if (data[i] != 0) {
                img[i] = 1.0f;
            }
        }
        Log.i(TAG, "getLaunchables: "+getResults(img));
//        MediaStore.Images.Media.insertImage(mApplication.getContentResolver(), b, "title", "description");
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

    public static List<Map<String,Object>> getAllLaunchables(){
        List<Map<String, Object>> apps = new ArrayList<>();
        for (ResolveInfo resolveInfo : launchables) {
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

    private static List<Map.Entry<String, Float>> getResults(float[] img){
        inferenceInterface.fillNodeFloat(INPUT_NODE, INPUT_SIZE, img);
        float[] dropout_keep_prob = {1.0f};

        inferenceInterface.fillNodeFloat("dropout_keep_prob",DROPOUT_SIZE,dropout_keep_prob);
        inferenceInterface.runInference(new String[] {OUTPUT_NODE});
        float[] prob = new float[20];
        inferenceInterface.readNodeFloat(OUTPUT_NODE, prob);

        Map<String, Float> result = new HashMap<>();
        for(int i = 0;i<labels.length;i++) {
            result.put(labels[i], prob[i]);
        }

        List<Map.Entry<String, Float>> output = new ArrayList<>(result.entrySet());

        Collections.sort(output, new Comparator<Map.Entry<String, Float>>() {
            public int compare(Map.Entry<String, Float> o1,
                               Map.Entry<String, Float> o2) {
                return (int)(o2.getValue() - o1.getValue());
            }
        });

        return output;
    }

    private void initTensorFlow() {
//        float img[] = readImg(IMG_FILE);
//        Log.d("readImg", String.valueOf(img[6][7]));
        inferenceInterface = new TensorFlowInferenceInterface();
        inferenceInterface.initializeTensorFlow(getAssets(), MODEL_FILE);

    }

    private float[] readImg(String path) {
        float[] img = new float[100*100];
//        File file = new File();
        try {
            InputStreamReader reader = new InputStreamReader(getResources().getAssets().open(path));
            BufferedReader bf = new BufferedReader(reader);
            String line = bf.readLine();
            int i = 0;
            while (line != null) {
                String[] l = line.split(" ");
                int t = 100*i;
                for(int j = 0;j<100;j++) {
                    img[t+j] = Float.parseFloat(l[j]);
                }
                line = bf.readLine();
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }


}
