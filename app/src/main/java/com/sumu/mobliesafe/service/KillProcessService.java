package com.sumu.mobliesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 后台杀进程服务
 */
public class KillProcessService extends Service {

    private LockScreenReceiver lockScreenReceiver;

    public KillProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 锁屏执行的广播任务
     */
    private class LockScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            killBackgroundProcesses(context);
        }
    }

    /**
     * 杀后台进程
     * @param context
     */
    private void killBackgroundProcesses(Context context) {
        //获取到进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取到手机上面所以正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
        }
        System.out.println("--------后台杀进程---------");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lockScreenReceiver = new LockScreenReceiver();
        //锁屏过滤器
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
        //注册一个锁屏的广播
        registerReceiver(lockScreenReceiver,intentFilter);
        Timer timer = new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                killBackgroundProcesses(KillProcessService.this);
            }
        };
        //进行定时调度
        /**
         * 第一个参数  表示用哪个类进行调度
         *
         * 第二个参数表示延迟执行时间
         *
         * 第三个参数表示执行间隔时间
         */
        timer.schedule(timerTask,0,2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lockScreenReceiver!=null){
            unregisterReceiver(lockScreenReceiver);
            lockScreenReceiver=null;
        }
    }
}
