package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.bean.CacheInfo;
import com.sumu.mobliesafe.utils.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存清理
 */
public class CleanCacheActivity extends Activity {

    private PackageManager packageManager;
    private List<CacheInfo> cacheInfos;
    @ViewInject(R.id.list_view)
    private ListView listView;
    private CleanCacheAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        ViewUtils.inject(this);
        initUI();
    }

    private void initUI() {
        //缓存的集合
        cacheInfos = new ArrayList<>();
        packageManager = getPackageManager();
        adapter=  new CleanCacheAdapter();
        listView.setAdapter(adapter);
        /**
         * 接收2个参数
         * 第一个参数接收一个包名
         * 第二个参数接收aidl的对象
         */
//		    public abstract void getPackageSizeInfo(String packageName,
//		            IPackageStatsObserver observer);
//		packageManager.getPackageSizeInfo();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            getCacheSize(packageInfo);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

//    System.out.println("收到消息了"+msg.what);
//    adapter = new CleanCacheAdapter();
//    listView.setAdapter(adapter);

    public static final int HANDLER_UPDATE_CACHE_MSG = 0XF0F1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(HANDLER_UPDATE_CACHE_MSG == msg.what){
                if(msg.obj!=null && msg.obj instanceof CacheInfo){
                    CacheInfo info = (CacheInfo) msg.obj;
                    adapter.addDataAndRefresh(info);
                }
            }
        }
    };

    private class CleanCacheAdapter extends BaseAdapter {

        public CleanCacheAdapter addDataAndRefresh(CacheInfo i){
            cacheInfos.add(i);
            notifyDataSetChanged();
            return this;
        }

        @Override
        public int getCount() {
            return cacheInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(CleanCacheActivity.this, R.layout.item_clean, null);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.appName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.cacheSize = (TextView) convertView.findViewById(R.id.tv_cache_size);
                viewHolder.clean = (ImageView) convertView.findViewById(R.id.iv_clean);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final CacheInfo cacheInfo = (CacheInfo) getItem(position);
            viewHolder.icon.setImageDrawable(cacheInfo.getIcon());
            viewHolder.appName.setText(cacheInfo.getAppName());
            viewHolder.cacheSize.setText("缓存大小:" + Formatter.formatFileSize(CleanCacheActivity.this, cacheInfo.getCacheSize()));
            viewHolder.clean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //启动到某个系统应用页面
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);//有无没影响
                    intent.setData(Uri.parse("package:" + cacheInfo.getPackageName()));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView appName;
        TextView cacheSize;
        ImageView clean;
    }

    /**
     * 获取到缓存的大小
     *
     * @param packageInfo
     */
    private void getCacheSize(PackageInfo packageInfo) {
        try {
            // Class<?> clazz = getClassLoader().loadClass("PackageManager");
            //通过反射获取到当前的方法
            //Method method = clazz.getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            /**
             * 第一个参数表示当前的这个方法由谁调用的
             * 第二个参数表示包名
             */
            method.invoke(packageManager, packageInfo.applicationInfo.packageName, new MyIPackageStatsObserver(packageInfo));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
        private PackageInfo packageInfo;

        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            //获取到当前手机应用的缓存大小
            long cacheSize = pStats.cacheSize;
            //如果当前的缓存大小大于0的话，则表示有缓存
            if (cacheSize > 0) {
                CacheInfo cacheInfo = new CacheInfo();
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                String packageName = packageInfo.packageName;
                cacheInfo.setAppName(appName);
                cacheInfo.setPackageName(packageName);
                cacheInfo.setIcon(icon);
                cacheInfo.setCacheSize(cacheSize);
                Message.obtain(mHandler,HANDLER_UPDATE_CACHE_MSG,cacheInfo).sendToTarget();
                System.out.println("---读取缓存中------>" + cacheSize + "---cacheInfos.size()--" + cacheInfos.size());
            }
        }
    }

    /**
     * 全部清除
     *
     * @param view
     */
    public void cleanAll(View view) {
        //获取到当前应用程序里面所有的方法
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods) {
            //判断当前的方法名字
            if (method.getName().equals("freeStorageAndNotify")) {
                try {
                    method.invoke(packageManager, Integer.MAX_VALUE, new MyIPackageDataObserver());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        ToastUtils.showToast(CleanCacheActivity.this, "全部清除");
    }

    private class MyIPackageDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }
}
