package com.sumu.mobliesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

/**
 * 桌面一键清理广播意图
 */
public class KillProcessAllReceiver extends BroadcastReceiver {
    public KillProcessAllReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //获取到进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取到手机上面所以正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
        }
        Toast.makeText(context, "清理完毕", Toast.LENGTH_SHORT).show();
        System.out.println("--------桌面一键清理---------");
    }
}
