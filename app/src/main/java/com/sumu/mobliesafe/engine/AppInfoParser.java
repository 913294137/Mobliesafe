package com.sumu.mobliesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.sumu.mobliesafe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumu on 2015/11/10.
 */
public class AppInfoParser {
    /**
     * 获取手机里面的所有的应用程序
     * @param context 上下文
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context){
        List<AppInfo> appInfos = new ArrayList<>();
        //获取包的管理者
        PackageManager packageManager = context.getPackageManager();
        //获取到安装包
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo installedPackage : installedPackages) {
            AppInfo appInfo = new AppInfo();
            //获取到应用的图标
            Drawable icon = installedPackage.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(icon);
            //获取到应用的名字
            String apkName = installedPackage.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);
            //获取到应用的包名
            String packageName = installedPackage.packageName;
            appInfo.setApkPackageName(packageName);
            //获取到apk资源的路径
            String sourceDir = installedPackage.applicationInfo.sourceDir;
            File file=new File(sourceDir);
            long apkSize = file.length();//apk的大小
            appInfo.setApkSize(apkSize);
            //获取到安装应用的标记
            int flags = installedPackage.applicationInfo.flags;
            if ((flags& ApplicationInfo.FLAG_SYSTEM)!=0){
                //表示系统APP
                appInfo.setUserApp(false);
            }else {
                //表示用户APP
                appInfo.setUserApp(true);
            }

            if ((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=-0){
                //表示在SD卡
                 appInfo.setIsRom(false);
            }else {
                //表示在内存
                appInfo.setIsRom(true);
            }
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
