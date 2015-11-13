package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.db.dao.AntivirusDao;
import com.sumu.mobliesafe.utils.MD5Utils;

import java.util.List;

/**
 * 病毒查杀
 */
public class AntivirusActivity extends Activity {
    private static final int BEGIN = 1;//开始扫描
    private static final int SCANNING = 2;//扫描中
    private static final int END = 3;//扫描结束
    @ViewInject(R.id.iv_act_scanning)
    private ImageView ivActScanning;
    @ViewInject(R.id.ll_content)
    private LinearLayout llContent;
    @ViewInject(R.id.tv_init_virus)
    private TextView tvInitVirus;
    @ViewInject(R.id.progressBar)
    private ProgressBar progressBar;
    @ViewInject(R.id.scrollView)
    private ScrollView scrollView;
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        ViewUtils.inject(this);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BEGIN:
                    tvInitVirus.setText("初始化8核杀毒引擎中...");
                    break;
                case SCANNING:
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tvInitVirus.setText("正在扫描:" + scanInfo.appName);
                    TextView textView = new TextView(AntivirusActivity.this);
                    if (scanInfo.desc) {
                        textView.setTextColor(Color.RED);
                        textView.setText(scanInfo.appName + ":有病毒");
                    } else {
                        textView.setTextColor(Color.BLACK);
                        textView.setText(scanInfo.appName + ":扫描安全");
                    }
                    llContent.addView(textView);
                    //自动滚动
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            //一直往下面进行滚动
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    break;
                case END:
                    tvInitVirus.setText("扫描完成");
                    // 当扫描结束的时候。停止动画
                    ivActScanning.clearAnimation();
                    break;
            }
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                message = Message.obtain();
                message.what = BEGIN;
                handler.sendMessage(message);
                PackageManager packageManager = getPackageManager();
                //获取到所有安装的应用程序
                List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
                progressBar.setMax(packageInfos.size());
                int count=0;//扫描的应用程序个数
                for (PackageInfo packageInfo : packageInfos) {
                    ScanInfo scanInfo = new ScanInfo();
                    //获取当前手机上app的名字
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    scanInfo.appName = appName;
                    //获取包名
                    String packageName = packageInfo.applicationInfo.packageName;
                    scanInfo.appPackage = packageName;
                    //获取到每个应用程序的目录
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    //获取到文件的md5
                    String md5 = MD5Utils.getFileMd5(sourceDir);
                    //判断当前文件是否在病毒数据库中
                    String desc = AntivirusDao.checkFileVirus(md5);
                    //如果当前的描述信息等null说明没有病毒
                    if (desc == null) {
                        scanInfo.desc = false;
                    } else {
                        scanInfo.desc = true;
                    }
                    count++;
                    progressBar.setProgress(count);
                    message=Message.obtain();
                    message.what = SCANNING;
                    message.obj = scanInfo;
                    handler.sendMessage(message);
                }
                message=Message.obtain();
                message.what = END;
                handler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 查杀每个文件的对象
     */
    private class ScanInfo {
        private String appName;
        private String appPackage;
        private boolean desc;//是否有病毒
    }

    private void initUI() {
        Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        //不停的循环动画
        animation.setRepeatCount(Animation.INFINITE);
        ivActScanning.startAnimation(animation);
    }
}
