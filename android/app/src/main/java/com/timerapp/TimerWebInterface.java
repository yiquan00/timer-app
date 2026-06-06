package com.timerapp;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;
import android.webkit.JavascriptInterface;

public class TimerWebInterface {
    private Context context;
    private DevicePolicyManager dpm;
    private ComponentName adminName;

    public TimerWebInterface(Context context) {
        this.context = context;
        this.dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.adminName = new ComponentName(context, AdminReceiver.class);
    }

    @JavascriptInterface
    public boolean isAdminActive() {
        return dpm.isAdminActive(adminName);
    }

    @JavascriptInterface
    public void requestAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "启用后，计时器可以在倒计时结束时自动锁屏");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @JavascriptInterface
    public void lockScreen() {
        if (dpm.isAdminActive(adminName)) {
            dpm.lockNow();
        }
    }

    @JavascriptInterface
    public void goToSleep() {
        // 如果管理员权限不可用，尝试让系统休眠
        // 某些设备可能需要特殊权限
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                // Android 9+ 不支持直接 goToSleep
            }
        } catch (SecurityException e) {
            // 没有权限，静默失败
        }
    }
}
