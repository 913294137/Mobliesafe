package com.sumu.mobliesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Sumu on 2015/11/9.
 */
public class SystemInfoUtils {

    /**
     * 检测服务是否正在运行
     *
     * @param context
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取系统所有正在运行的服务，最多100个
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = manager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfos) {
            String className = runningServiceInfo.service.getClassName();//获取服务名称
            if (className.equals(serviceName)) {//服务存在
                return true;
            }
        }
        return false;
    }

    /**
     * 返回进程的总个数
     *
     * @param context
     * @return
     */
    public static int getProcessCount(Context context) {
        // 得到进程管理者
        ActivityManager service = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取到当前手机上面所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = service.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    /**
     * 获取剩余的内存
     *
     * @param context
     * @return
     */
    public static long getAvailMem(Context context) {
        // 得到进程管理者
        ActivityManager service = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取到内存的基本信息
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        service.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    /**
     * 获取总内存
     *
     * @param context
     * @return
     */
    public static long getTotalMem(Context context) {
        /*
		 * 这个地方不能直接跑到低版本的手机上面 long totalMem = outInfo.totalMem
		 * MemTotal: 344740 kB "/proc/meminfo"
		 */
        try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String readLine = reader.readLine();
            StringBuffer buffer=new StringBuffer();
            for (char c : readLine.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    buffer.append(c);
                }
            }
            return Long.parseLong(buffer.toString())*1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
