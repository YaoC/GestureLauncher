package cn.edu.pku.chengyao.gesturelauncher.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import cn.edu.pku.chengyao.gesturelauncher.R;
import cn.edu.pku.chengyao.gesturelauncher.permission.LeanCloudLog;


public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_show_or_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindowManager.getInstance().applyOrShowFloatWindow(MainActivity.this);
            }
        });

        findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindowManager.getInstance().dismissWindow();
            }
        });

        findViewById(R.id.btn_log_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String key = "lastLogUploadDate";
//                final long oneDay = 86400000;
//		测试用
                final long oneDay = 6;
                SharedPreferences sharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
                long lastLogUploadDate = sharedPreferences.getLong(key, 0);
                if (lastLogUploadDate != 0) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastLogUploadDate >= oneDay) {
                        LeanCloudLog.uploadAppLogFile(context);
                    }
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(key, System.currentTimeMillis());
                editor.apply();
            }
        });
    }

}
