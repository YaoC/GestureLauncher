package cn.edu.pku.chengyao.gesturelauncher.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import cn.edu.pku.chengyao.gesturelauncher.R;
import cn.edu.pku.chengyao.gesturelauncher.tools.LeanCloudLog;


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
                LeanCloudLog.uploadAppLogFile(context);
            }
        });

        findViewById(R.id.btn_show_logs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(context, LogActivity.class);
                startActivity(i);
            }
        });

        // 自动检查时间，如果时间大于1天则自动上传日志
        final String key = "lastLogUploadDate";
        final long oneDay = 86400000;
        SharedPreferences sharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        long lastLogUploadDate = sharedPreferences.getLong(key, 0);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLogUploadDate >= oneDay) {
            LeanCloudLog.uploadAppLogFile(context);
        }
    }

}
