package cn.edu.pku.chengyao.gesturelauncher.permission;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import cn.edu.pku.chengyao.gesturelauncher.main.MyApplication;

/**
 * @author chengyao
 *         date 2017/3/26
 *         mail chengyao09@hotmail.com
 **/
public class DetectionService extends AccessibilityService {
    private static volatile String lastForegroundPackageName = "";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String currentForegroundPackageName = event.getPackageName().toString();
            if (!currentForegroundPackageName.equals(lastForegroundPackageName)
                    && MyApplication.getlaunchableAppNames().contains(currentForegroundPackageName)) {
                Utils.appendLog(this, "usage", MyApplication.getMacAddress() + "," + Utils.getTime()
                        + "," + currentForegroundPackageName + "," + MyApplication.getStartFromGestureLauncherFlag());
                lastForegroundPackageName = currentForegroundPackageName;
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
