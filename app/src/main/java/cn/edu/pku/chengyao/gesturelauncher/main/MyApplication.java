package cn.edu.pku.chengyao.gesturelauncher.main;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.pku.chengyao.gesturelauncher.tools.Utils;
import cn.edu.pku.yaochg.imagesimilarity.AssetCopyer;
import cn.edu.pku.yaochg.imagesimilarity.PingYinUtil;

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
    private static Map<String, Integer> launchableIdx = new HashMap<>();


    private static List<String> launchablePackageNames;// package name
    private static List<String> launchableAppNames;// App name


    private static double[] alphabetSim = new double[26 * 26];

    private static PackageManager pm;

    private  static WifiManager  wm;
    private static  String m_szWLANMAC;

    private static short startFromGestureLauncher = 0;

//    // TF
//    static {
//        System.loadLibrary("tensorflow_inference");
//    }

    private static TensorFlowInferenceInterface inferenceInterface;

    private static String[] labels = {"Adobe Air", "Chrome", "Facebook", "QQ", "WPS Office",
            "京东", "优酷", "健康", "去哪儿旅行", "天猫", "微信", "微博", "手机淘宝", "支付宝", "滴滴出行",
            "照片", "百度地图", "相机", "邮件", "金山词霸"};
    private static final String MODEL_FILE = "file:///android_asset/frozen_gesture_scores.pb";
    private static final String INPUT_NODE = "x_input";
    private static final String OUTPUT_NODE = "output/scores";
    private static final int[] INPUT_SIZE = {100,100};
    private static final int[] DROPOUT_SIZE = {1};

    private static Map<String, Map<String, Double>> similarity;

    private static Map<String, Map<String, Double>> similarityAlphabet;


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
        initID();
        initAppInfos();
        initTensorFlow();
        initIcons();
        initAppSimilarity();
        similarity = Utils.iconsSimilarity(mApplication);
//        showSimilarity();
        loadAlphabetSim();
        initsimilarityAlphabet();
    }

    public static Application getInstance(){
        return mApplication;
    }

    //获取所有可以启动的activity
    private void initAppInfos() {
        pm = mApplication.getPackageManager();
        Intent main=new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(main, 0);
        Set<String> systemApps = getSystemApps();
        Map<String, ResolveInfo> temp = new HashMap<>();
        launchableAppNames = new ArrayList<>();
        for (ResolveInfo resolveInfo : apps) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (!systemApps.contains(packageName)) {
                temp.put(packageName, resolveInfo);
                launchableAppNames.add((String) resolveInfo.loadLabel(pm));
            }
        }
        launchables = new ArrayList<>(temp.values());
        launchablePackageNames = new ArrayList<>(temp.keySet());
        for (int i = 0; i < launchables.size(); i++) {
            launchableIdx.put(launchables.get(i).loadLabel(pm).toString(), i);
        }


//        Log.i(TAG, "initAppInfos: " + launchableIdx);
//        Log.d(TAG, "initAppInfos: "+ launchablePackageNames);
//        Log.d(TAG, "initAppInfos: runing apps "+ProcessManager.getRunningApps());
    }

    public static List<Map<String, Object>> getLaunchables(float[] img) {

        List<Map.Entry<String, Float>> resu = getResults(img);
        List<Map.Entry<String, Double>> finalResult = getFinalResult(resu);
        Log.i(TAG, "getLaunchables: " + finalResult);

//        MediaStore.Images.Media.insertImage(mApplication.getContentResolver(), b, "title", "description");
//        int idx=(int)(Math.random()*(launchables.size()-9));
//        List<ResolveInfo> appList = launchables.subList(idx, idx + 9);

        List<ResolveInfo> appList = new LinkedList<>();
        for (Map.Entry<String, Double> stringDoubleEntry : finalResult) {
            int idx = launchableIdx.get(stringDoubleEntry.getKey());
            appList.add(launchables.get(idx));
        }
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
    private void initID() {
        wm = (WifiManager) getSystemService(this.WIFI_SERVICE);
        if (wm.getConnectionInfo().getMacAddress() == null) {
            m_szWLANMAC = "000000000000";
        } else {
            m_szWLANMAC = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
    }

    public static String getID() {
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
        return launchablePackageNames;
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

    private static void initAppSimilarity() {
        File dir = mApplication.getCacheDir();
        File iconDir = new File(dir.getAbsolutePath() + "/icons");
        iconDir.delete();
        iconDir.mkdir();
        // 缓存所有App的图标，文件名： app名.png
        for (ResolveInfo resolveInfo : launchables) {
            String fileName = resolveInfo.loadLabel(pm) + ".png";
            Bitmap icon = ((BitmapDrawable) resolveInfo.loadIcon(pm)).getBitmap();
            icon = Bitmap.createScaledBitmap(icon, 72, 72, false);
//            Log.i(TAG, fileName + " size: " + icon.getWidth() + " * " + icon.getHeight());
            Utils.saveIconToFile(iconDir, fileName, icon, Bitmap.CompressFormat.PNG, 100);
        }
    }

    private static void initIcons() {
        try {
            new AssetCopyer(mApplication).copy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showSimilarity() {

        for (String icon : similarity.keySet()) {
            Log.i(TAG, "icon: " + icon);
            Log.i(TAG, "similaruty: " + similarity.get(icon));
        }

    }

    private static List<Map.Entry<String, Double>> getFinalResult(List<Map.Entry<String, Float>> resu) {
        // weight
        int w = 1024;
        Map<String, Double> weights = new HashMap<>();
        int len = resu.size();
        for (int i = 0; i < resu.size(); i++) {
            weights.put(resu.get(i).getKey(), (double) w);
            w /= 2;

        }
        List<Map.Entry<String, Double>> wei = new ArrayList<>(weights.entrySet());
        Collections.sort(wei, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return (int) (o2.getValue() - o1.getValue());
            }
        });
        Log.i(TAG, "getFinalResult: Tensorflow 输出的排行：" + wei);

        Map<String, Double> scores = new HashMap<>();
        //计算图标相似度
        for (String icon : launchableIdx.keySet()) {
            double score = 0.0;
            Map<String, Double> sim = similarity.get(icon);
            Map<String, Double> simAlphabet = similarityAlphabet.get(icon);
            for (String label : labels) {
                score += weights.get(label) * (60 / (1 + sim.get(label)) + 40 / (1 + simAlphabet.get(label)));
            }
            scores.put(icon, score);
        }
        List<Map.Entry<String, Double>> output = new ArrayList<>(scores.entrySet());
        Collections.sort(output, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (int) (o2.getValue() - o1.getValue());
            }
        });

        //test
        final Map<String, Double> testW = weights;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                testRatio(testW);
            }
        });
        thread.start();
        return output.subList(0, 9);
    }

    private static void loadAlphabetSim() {
        AssetManager mAssetManager = mApplication.getAssets();
        try {
            InputStream is = mAssetManager.open("alphabet.csv");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = null;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] sim = line.split(",");
                for (String s : sim) {
                    alphabetSim[i++] = Double.valueOf(s);
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static double similarityOf(String a, String b) {
//        Log.i(TAG, "similarityOf: " + a + " " + b);
        char first = PingYinUtil.getFirstSpell(a).toLowerCase().charAt(0);
        char second = PingYinUtil.getFirstSpell(b).toLowerCase().charAt(0);
//        Log.i(TAG, "Char: " + first + " " + second);
        int idx1 = first - 'a';
        int idx2 = second - 'a';
        return alphabetSim[idx1 * 26 + idx2];
    }

    private static void initsimilarityAlphabet() {
        similarityAlphabet = new HashMap<>();
        for (String icon : launchableIdx.keySet()) {
            Map<String, Double> temp = new HashMap<>();
            for (String label : labels) {
//                Log.i(TAG, icon + " " + label);
                double sim = similarityOf(icon, label);
//                Log.i(TAG, "similarity : " + sim);
                temp.put(label, sim);
            }
            similarityAlphabet.put(icon, temp);
        }
    }

    public static void testRatio(final Map<String, Double> weights) {
        Map<String, Double> scores = new HashMap<>();
        //计算图标相似度
        for(int i = 0;i<=100;i+=5) {
            for (String icon : launchableIdx.keySet()) {
                double score = 0.0;
                Map<String, Double> sim = similarity.get(icon);
                Map<String, Double> simAlphabet = similarityAlphabet.get(icon);
                for (String label : labels) {
                    score += weights.get(label) * (i / (1 + sim.get(label)) + (100-i) / (1 + simAlphabet.get(label)));
                }
                scores.put(icon, score);
            }
            List<Map.Entry<String, Double>> output = new ArrayList<>(scores.entrySet());
            Collections.sort(output, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> o1,
                                   Map.Entry<String, Double> o2) {
                    return (int) (o2.getValue() - o1.getValue());
                }
            });
            List<String> result = new ArrayList<>();
            for(int j=0;j<9;j++) {
                result.add(output.get(j).getKey());
            }
            Log.i("ratio="+i, result.toString());
        }

    }
}
