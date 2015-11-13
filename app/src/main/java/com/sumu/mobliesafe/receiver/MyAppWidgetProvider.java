package com.sumu.mobliesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.sumu.mobliesafe.service.KillProcessWidgetService;

/**
 * 创建桌面小部件的步骤：
 * 1 需要在清单文件里面配置元数据
 * 2 需要配置当前元数据里面要用到xml
 *      res/xml
 * 3 需要配置一个广播接受者
 * 4 实现一个桌面小部件的xml
 * (根据需求。桌面小控件涨什么样子。就实现什么样子)
 * 桌面小部件
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

    /**
     * 广播生命周期不能超过10s,所以这里面不能做耗时操作
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        System.out.println("onReceive");
    }


    /**
     * 每次有新的桌面小控件生成的时候都会调用
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        System.out.println("onUpdate");
    }

    /**
     * 每次删除桌面小控件的时候都会调用的方法
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onDeleted(context, appWidgetIds);
        System.out.println("onDeleted");
    }

    /**
     * 第一次创建的时候才会调用当前的生命周期的方法
     */
    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        super.onEnabled(context);
        System.out.println("onEnabled");
        Intent killProcessWidgetService=new Intent(context, KillProcessWidgetService.class);
        context.startService(killProcessWidgetService);
    }

    /**
     * 当桌面上面所有的桌面小控件都删除的时候才调用当前这个方法
     */
    @Override
    public void onDisabled(Context context) {
        // TODO Auto-generated method stub
        super.onDisabled(context);
        System.out.println("onDisabled");
        Intent killProcessWidgetService=new Intent(context, KillProcessWidgetService.class);
        context.stopService(killProcessWidgetService);
    }


}
