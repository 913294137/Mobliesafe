package com.sumu.mobliesafe.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * ==============================
 * 作者：苏幕
 * <p/>
 * 时间：2015/11/15   11:43
 * <p/>
 * 描述：
 *      流量统计对象
 * <p/>
 * ==============================
 */
public class TrafficInfo implements Serializable{
    private Drawable icon;//应用图标
    private String name;//应用名字
    private long rxBytes;//下载流量
    private long txBytes;//上传流量

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRxBytes() {
        return rxBytes;
    }

    public void setRxBytes(long rxBytes) {
        this.rxBytes = rxBytes;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public void setTxBytes(long txBytes) {
        this.txBytes = txBytes;
    }
}
