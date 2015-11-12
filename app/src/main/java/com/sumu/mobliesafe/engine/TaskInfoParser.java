package com.sumu.mobliesafe.engine;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * ==============================
 * 作者：苏幕
 * <p/>
 * 时间：2015/11/11   15:50
 * <p/>
 * 描述：
 * <p/>
 * ==============================
 */
public class TaskInfoParser {
    /**
     * 获取所有运行的进程
     *
     * @param context
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<TaskInfo> taskInfos = new ArrayList<>();
        //获取到进程管理器
        ActivityManager activityManager = (ActivityManager) context.
                getSystemService(Context.ACTIVITY_SERVICE);
        //获得手机上面所有运行的进程
        List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            TaskInfo taskInfo = new TaskInfo();
            //获取进程名字
            String processName = runningAppProcessInfo.processName;
            taskInfo.setPackageName(processName);
            try {
                //获取到内存基本信息
                MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
                //获取到总共弄脏了多少内存（当前应用占用多少内存） Dirty 弄脏
                int totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty() * 1024;
                taskInfo.setMemorySize(totalPrivateDirty);
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                //获取图片
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                taskInfo.setIcon(icon);
                //获取到应用的名字
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                taskInfo.setAppName(appName);
                //获取到当前应用程序的标记
                int flags = packageInfo.applicationInfo.flags;
                //ApplicationInfo.FLAG_SYSTEM表示系统应用程序
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //系统应用
                    taskInfo.setUserApp(false);
                } else {
                    //用户应用
                    taskInfo.setUserApp(true);
                }

                System.out.println("------>" + taskInfo.toString());

            } catch (Exception e) {
                e.printStackTrace();
                // 系统核心库里面有些系统没有图标。必须给一个默认的图标
                taskInfo.setAppName(processName);
                taskInfo.setIcon(context.getResources().getDrawable(
                        R.mipmap.ic_launcher));
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
