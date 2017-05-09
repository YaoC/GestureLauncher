package cn.edu.pku.chengyao.gesturelauncher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * @author chengyao
 *         date 2017/4/5
 *         mail chengyao09@hotmail.com
 **/
public class AllAppsActivity extends Activity {

    private ListView allApps;

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_apps);
        context = this;
        allApps = (ListView) findViewById(R.id.all_app_list);
        initAppList();
    }

    private void initAppList(){
        List<Map<String, Object>> appList = MyApplication.getAllLaunchables();
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this, appList, R.layout.all_app_item,
                new String[]{"appName", "activityName", "packageName", "icon"},
                new int[]{R.id.all_app_name, R.id.all_app_activity_name, R.id.all_app_package_name, R.id.all_app_icon});
        allApps.setAdapter(simpleAdapter);
        //加载Drawable
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String arg2) {
                if(view instanceof ImageView && data instanceof Drawable){
                    ImageView iv = (ImageView)view;
                    iv.setImageDrawable((Drawable)data);
                    iv.setFocusable(false);
                    return true;
                }else{
                    return false;
                }
            }
        });
        allApps.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //	标记该App是由GestureLauncher打开的
                MyApplication.setStartFromGestureLauncher();
                //	打开点击的APP
                String packageName = ((TextView) v.findViewById(R.id.all_app_package_name))
                        .getText().toString();
                String activityName = ((TextView) v.findViewById(R.id.all_app_activity_name))
                        .getText().toString();
                ComponentName name=new ComponentName(packageName,activityName);
                Intent i=new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                i.setComponent(name);
                context.startActivity(i);
            }
        });
    }
}
