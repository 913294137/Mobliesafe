package com.sumu.mobliesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.sumu.mobliesafe.activity.EnterPswActivity;
import com.sumu.mobliesafe.db.dao.AppLockDao;

import java.util.List;


/**
 * 程序锁服务
 */
public class LockService extends Service {

    private ActivityManager activityManager;
    private AppLockDao dao;
    //临时停止保护的包名
    private String tempStopProtectPackageName;
    private LockObserver lockObserver;

    public LockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class LockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.sumu.mobliesafe.stopprotect")) {
                //获取到停止保护的对象
                tempStopProtectPackageName = intent.getStringExtra("packageName");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                tempStopProtectPackageName = null;
                // 让服务休息
                flag = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //让服务继续干活
                if (flag == false) {
                    startLock();
                }
            }
        }
    }

    private class LockObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public LockObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //当观察到数据发生改变时重新读取加锁程序包名
            appLockInfos = dao.findAllLockApp();
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        lockObserver = new LockObserver(new Handler());
        //注册内容观察者
        getContentResolver().registerContentObserver(Uri.parse("content://com.sumu.mobliesafe.db.change"), true, lockObserver);

        dao = new AppLockDao(this);
        //取出所有上锁的包名
        appLockInfos = dao.findAllLockApp();
        //注册广播接受者
        receiver = new LockReceiver();
        IntentFilter filter = new IntentFilter();
        //停止保护
        filter.addAction("com.sumu.mobliesafe.stopprotect");
        //注册一个锁屏的广播
        /**
         * 当屏幕锁住的时候。服务就休息
         * 屏幕解锁的时候。服务继续运行
         */
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
        //获取到进程管理器
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        startLock();
    }

    //标记当前的服务是否停下来
    private boolean flag = false;
    private List<String> appLockInfos;
    private LockReceiver receiver;

    private void startLock() {

        new Thread() {
            public void run() {
                flag = true;
                while (flag) {
                    //由于这个一直在后台运行。为了避免程序阻塞。
                    //获取到当前正在运行的任务栈
                    List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
                    //获取到最上面的进程
                    ActivityManager.RunningTaskInfo taskInfo = tasks.get(0);
                    //获取到最顶端应用程序的包名
                    String packageName = taskInfo.topActivity.getPackageName();
                    System.out.println(packageName);
                    //让服务休息一会
                    SystemClock.sleep(30);
                    //直接从数据库里面查找当前的数据
                    //这个可以优化。改成从内存当中寻找
                     if (appLockInfos.contains(packageName)) {
           //         if (dao.find(packageName)) {
//                  System.out.println("在程序锁数据库里面");
                        //说明需要临时取消保护
                        //是因为用户输入了正确的密码
                        if (!packageName.equals(tempStopProtectPackageName)) {
                            Intent intent = new Intent(LockService.this, EnterPswActivity.class);
                            /**
                             * 需要注意：如果是在服务里面往activity界面跳的话。需要设置flag
                             */
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //停止保护的对象
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
        receiver = null;
        getContentResolver().unregisterContentObserver(lockObserver);
    }
}
