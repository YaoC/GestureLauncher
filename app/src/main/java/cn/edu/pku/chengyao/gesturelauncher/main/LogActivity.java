package cn.edu.pku.chengyao.gesturelauncher.main;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import cn.edu.pku.chengyao.gesturelauncher.R;
import cn.edu.pku.chengyao.gesturelauncher.permission.LeanCloudLog;
import cn.edu.pku.chengyao.gesturelauncher.permission.Utils;

public class LogActivity extends Activity {

    public static final String TAG = "LogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        String id = MyApplication.getID();
        if (!id.isEmpty()) {
            ((TextView) findViewById(R.id.device_id)).setText("本机唯一标识符：" + id);
        }

        final String key = "lastLogUploadDate";
        SharedPreferences sharedPreferences = getSharedPreferences("MainActivity", MODE_PRIVATE);
        long lastLogUploadDate = sharedPreferences.getLong(key, 0);

        TextView lastUploadTV = (TextView) findViewById(R.id.last_upload_time);

        if (lastLogUploadDate == 0) {
            lastUploadTV.setText("从未上传过日志");
        } else {
            lastUploadTV.setText("上次上传日志的时间：" + Utils.stampToTime(lastLogUploadDate));
        }

        TextView logView = (TextView) findViewById(R.id.log_viewer);
        List<String> files = LeanCloudLog.getLogFilesName(this);
        if (files.size() == 0) {
            logView.setText("当前本地无缓存日志");
        } else {
            for (String s : files) {
                logView.append(s + "\n");
            }
        }

    }

}
