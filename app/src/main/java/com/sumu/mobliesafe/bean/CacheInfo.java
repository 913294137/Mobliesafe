package com.sumu.mobliesafe.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * ==============================
 * 作者：苏幕
 * <p/>
 * 时间：2015/11/14   16:30
 * <p/>
 * 描述：
 * 清理缓存应用对象
 * <p/>
 * ==============================
 */
public class CacheInfo  implements Serializable{
    Drawable icon;//应用图标
    long cacheSize;//缓存大小
    String appName;//应用名字
    String packageName;//包名

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
