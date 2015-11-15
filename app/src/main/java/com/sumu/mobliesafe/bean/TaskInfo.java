package com.sumu.mobliesafe.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * ==============================
 * 作者：苏幕
 * <p/>
 * 时间：2015/11/11   15:51
 * <p/>
 * 描述：
 *      进程信息对象
 * <p/>
 * ==============================
 */
public class TaskInfo implements Serializable{
    //进程图标
    private Drawable icon;
    //进程包名
    private String packageName;
    //进程名
    private String appName;
    //进程所占内存大小
    private long memorySize;
    //是否是用户进程
    private boolean userApp;
    //判断当前进程是否被选中
    private boolean check;


    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "icon=" + icon +
                ", packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", memorySize=" + memorySize +
                ", userApp=" + userApp +
                '}';
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }
}
