package com.sumu.mobliesafe.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.bean.AppInfo;
import com.sumu.mobliesafe.engine.AppInfoParser;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements View.OnClickListener{
    @ViewInject(R.id.list_view)
    private ListView listView;
    @ViewInject(R.id.tv_rom)
    private TextView tvRom;
    @ViewInject(R.id.tv_sd)
    private TextView tvSd;
    @ViewInject(R.id.tv_app)
    private TextView tvApp;
    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private PopupWindow popupWindow;
    private AppInfo appInfoClick;
    private AppManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        ViewUtils.inject(this);
        initUI();
        initData();
    }

    private void initUI() {
        //内存可用空间
        long romFreeSpace = Environment.getDataDirectory().getFreeSpace();
        //SD卡可用空间
        long sdFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        //格式化大小 Formatter.formatFileSize(this,romFreeSpace)
        tvRom.setText("内存可用:" + Formatter.formatFileSize(this, romFreeSpace));
        tvSd.setText("SD卡可用:" + Formatter.formatFileSize(this, sdFreeSpace));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             *
             * @param view
             * @param firstVisibleItem 第一个可见的条的位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount   总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                popupWindowDismiss();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem < userAppInfos.size() + 1) {
                        tvApp.setText("用户程序(" + userAppInfos.size() + ")");
                    } else {
                        tvApp.setText("系统程序(" + systemAppInfos.size() + ")");
                    }
                }
            }
        });
        //用户程序的集合
        userAppInfos = new ArrayList<AppInfo>();
        //系统程序的集合
        systemAppInfos = new ArrayList<AppInfo>();
        adapter = new AppManagerAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAtPosition = listView.getItemAtPosition(position);
                appInfoClick = (AppInfo) itemAtPosition;
                if (itemAtPosition != null && itemAtPosition instanceof AppInfo) {
                    View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);
                    LinearLayout ll_uninstall= (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                    LinearLayout ll_run= (LinearLayout) contentView.findViewById(R.id.ll_run);
                    LinearLayout ll_share= (LinearLayout) contentView.findViewById(R.id.ll_share);
                    LinearLayout ll_detail= (LinearLayout) contentView.findViewById(R.id.ll_detail);
                    ll_uninstall.setOnClickListener(AppManagerActivity.this);
                    ll_run.setOnClickListener(AppManagerActivity.this);
                    ll_share.setOnClickListener(AppManagerActivity.this);
                    ll_detail.setOnClickListener(AppManagerActivity.this);

                    popupWindowDismiss();

                    popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, view.getHeight());
                    //需要注意：使用PopupWindow 必须设置背景。不然没有动画
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    int[] location = new int[2];
                    //获取view展示到窗体上面的位置
                    view.getLocationInWindow(location);
                    popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 100, location[1]);

                    Animation animation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(500);

                    Animation animation1 = new AlphaAnimation(0f, 1f);
                    animation1.setDuration(500);

                    AnimationSet animationSet=new AnimationSet(false);
                    animationSet.addAnimation(animation);
                    animationSet.addAnimation(animation1);
                    contentView.startAnimation(animationSet);

                }
            }
        });
    }

    /**
     * 关闭popupWindow
     */
    private void popupWindowDismiss() {
        if (popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow=null;
        }
    }
    private static final int USER_APP_INFO=0;
    private static final int UN_USER_APP_INFO=1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case USER_APP_INFO:
                    if (msg.obj!=null && msg.obj instanceof AppInfo){
                        userAppInfos.add((AppInfo) msg.obj);
                    }
                    break;
                case UN_USER_APP_INFO:
                    if (msg.obj!=null && msg.obj instanceof AppInfo){
                        systemAppInfos.add((AppInfo) msg.obj);
                    }
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //获取所以安装到手机上面的应用
                appInfos = AppInfoParser.getAppInfos(AppManagerActivity.this);
                //appInfos拆成 用户程序的集合 + 系统程序的集合
                for (AppInfo appInfo : appInfos) {
                    if (appInfo.isUserApp()) {
                        //用户程序
                        Message.obtain(mHandler,USER_APP_INFO,appInfo).sendToTarget();
                    } else {
                        Message.obtain(mHandler,UN_USER_APP_INFO,appInfo).sendToTarget();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                //卸载
                case R.id.ll_uninstall:
                    Intent uninstall_localIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + appInfoClick.getApkPackageName()));
                    startActivityForResult(uninstall_localIntent, 0);
                    break;
                //运行
                case R.id.ll_run:
                    Intent localIntent=getPackageManager().getLaunchIntentForPackage(appInfoClick.getApkPackageName());
                    startActivity(localIntent);
                    break;
                //分享
                case R.id.ll_share:
                    Intent share_localIntent = new Intent("android.intent.action.SEND");
                    share_localIntent.setType("text/plain");
                    share_localIntent.putExtra("android.intent.extra.SUBJECT", "分享");
                    share_localIntent.putExtra("android.intent.extra.TEXT",
                            "Hi！推荐您使用软件：" + appInfoClick.getApkName()+"下载地址:"+"https://play.google.com/store/apps/details?id="+appInfoClick.getApkPackageName());
                    startActivity(Intent.createChooser(share_localIntent, "分享"));
                    break;
                //详情
                case R.id.ll_detail:
                    Intent detail_intent = new Intent();
                    detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                    detail_intent.setData(Uri.parse("package:" + appInfoClick.getApkPackageName()));
                    startActivity(detail_intent);
                    break;
            }
        popupWindowDismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0){
            initData();
        }
    }

    class AppManagerAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return userAppInfos.size() + systemAppInfos.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0 || position == (userAppInfos.size() + 1)) {
                return null;
            }
            AppInfo appInfo = null;
            if (position < userAppInfos.size() + 1) {
                //把多出来的特殊的条目减掉
                appInfo = userAppInfos.get(position - 1);
            } else if (position > userAppInfos.size() + 1) {
                appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序(" + userAppInfos.size() + ")");
                return textView;
            } else if (position == (userAppInfos.size() + 1)) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序(" + systemAppInfos.size() + ")");
                return textView;
            }
            ViewHolder viewHolder = null;
            if (convertView != null && convertView instanceof LinearLayout) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);
                viewHolder = new ViewHolder();
                viewHolder.ivApkIcon = (ImageView) convertView.findViewById(R.id.iv_apk_icon);
                viewHolder.tvApkName = (TextView) convertView.findViewById(R.id.tv_apk_name);
                viewHolder.tvApkLocation = (TextView) convertView.findViewById(R.id.tv_apk_location);
                viewHolder.tvApkSize = (TextView) convertView.findViewById(R.id.tv_apk_size);
                convertView.setTag(viewHolder);
            }
            AppInfo appInfo = null;
            /*if (position < userAppInfos.size() + 1) {
                //把多出来的特殊的条目减掉
                appInfo = userAppInfos.get(position - 1);
            } else if (position > userAppInfos.size() + 1) {
                appInfo = systemAppInfos.get(position-userAppInfos.size()-2);
            }*/
            appInfo = (AppInfo) getItem(position);
            System.out.println("---->" + appInfo.toString());
            viewHolder.ivApkIcon.setBackground(appInfo.getIcon());
            viewHolder.tvApkName.setText(appInfo.getApkName());
            viewHolder.tvApkSize.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));
            if (appInfo.isRom()) {
                viewHolder.tvApkLocation.setText("手机内存");
            } else {
                viewHolder.tvApkLocation.setText("外部存储");
            }
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivApkIcon;
        TextView tvApkName;
        TextView tvApkLocation;
        TextView tvApkSize;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        popupWindowDismiss();
    }
}
