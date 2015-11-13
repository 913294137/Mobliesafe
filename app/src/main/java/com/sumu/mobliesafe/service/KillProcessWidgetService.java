package com.sumu.mobliesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.receiver.MyAppWidgetProvider;
import com.sumu.mobliesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 清理桌面小空间的服务
 */
public class KillProcessWidgetService extends Service {

    private Timer timer;
    private TimerTask timerTask;

    public KillProcessWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //桌面小控件的管理者
        final AppWidgetManager widgetManager = AppWidgetManager.getInstance(getApplicationContext());

        //每隔5秒钟更新一次桌面
        //初始化定时器
        timer = new Timer();
        //初始化一个定时任务
        timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("KillProcessWidgetService");
                //第一个参数表示上下文
                //第二个参数表示当前有哪一个广播进行去处理当前的桌面小控件
                ComponentName componentName = new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
                /**
                 * 这个是把当前的布局文件添加进行
                 * 初始化一个远程的view
                 * Remote 远程
                 */
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
                int processCount = SystemInfoUtils.getProcessCount(getApplicationContext());//获取当前手机总共有多少个运行进程
                /**
                 * 需要注意。这个里面没有findingviewyid这个方法
                 * 设置当前文本里面一共有多少个进程
                 * 设置文本
                 */
                remoteViews.setTextViewText(R.id.process_count, "正在运行的软件:" + String.valueOf(processCount));
                long availMem = SystemInfoUtils.getAvailMem(getApplicationContext());//获取当前手机所剩余的内存
                remoteViews.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(getApplicationContext(), availMem));

                Intent intent=new Intent();
                //发送一个隐式意图
                intent.setAction("com.sumu.mobliesafe.receiver.KillProcessWidgetService");
                PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
                //设置点击事件
                remoteViews.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);

                //更新桌面
                widgetManager.updateAppWidget(componentName, remoteViews);
            }
        };
        //从0开始。每隔5秒钟更新一次
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
