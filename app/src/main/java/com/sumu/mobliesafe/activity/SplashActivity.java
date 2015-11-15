package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.utils.StreamUtils;

import net.youmi.android.AdManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_URL_ERROR = 1;
    private static final int CODE_NET_ERROR = 2;
    private static final int CODE_JSON_ERROR = 3;
    private static final int CODE_ENTER_HOME = 4;
    private TextView tvVersion,tvProgress;
    private String mVersionName;//版本名
    private int mVersionCode;//版本号
    private String mDesc;//版本描述
    private String mDownloadUrl;//下载地址

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "url错误", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;

            }
        }
    };

    private SharedPreferences preferences;
    private RelativeLayout rlRoot=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化有米广告
        AdManager.getInstance(this).init("48cdfb9d2ba36f47", "8c53e53914fb688a",false);

        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvProgress= (TextView) findViewById(R.id.tv_progress);
        tvVersion.setText("版本号：" + getVersionName());
        preferences=getSharedPreferences("config",MODE_APPEND);
        copyDB("address.db");//拷贝电话归属地数据库
        copyDB("antivirus.db");//拷贝病毒数据库
        //判断是否需要自动更新
        if (preferences.getBoolean("auto_update",true)){
            checkVersion();
        }else {
            //延时两秒后，发送消息
            mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME,2000);
        }
        rlRoot= (RelativeLayout) findViewById(R.id.rl_root);

        //渐变的动画效果
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1f);
        animation.setDuration(2000);
        rlRoot.startAnimation(animation);
        //创建快捷方式
        //createShortcut();
    }

    /**
     * 创建快捷方式
     */
    private void createShortcut() {
        Intent intent = new Intent();

        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //如果设置为true表示可以创建重复的快捷方式
        intent.putExtra("duplicate", false);

        PackageManager packageManager = getPackageManager();
        ApplicationInfo application=null;
        try {
            application=packageManager.getApplicationInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        /**
         * 1 干什么事情
         * 2 你叫什么名字
         * 3你长成什么样子
         */
        if (application != null) {
            BitmapDrawable bitmapDrawable= (BitmapDrawable) application.loadIcon(packageManager);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,bitmapDrawable.getBitmap());
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, application.loadLabel(packageManager));
        }
        //干什么事情
        /**
         * 这个地方不能使用显示意图
         * 必须使用隐式意图
         */
        Intent shortcut_intent = new Intent();

        shortcut_intent.setAction("aaa.bbb.ccc");

        shortcut_intent.addCategory("android.intent.category.DEFAULT");

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut_intent);

        sendBroadcast(intent);
    }

    /**
     * 获取版本名
     *
     * @return
     */
    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            //获取包的信息
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            //获取包的信息
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 从服务器获取版本信息进行校验
     */
    private void checkVersion() {
        final long startTime = System.currentTimeMillis();
        new Thread() {
            @Override
            public void run() {
                Message message = Message.obtain();
                HttpURLConnection connection = null;
                try {
                    //本地用localhost,模拟器用10.0.2.2;
                    URL url = new URL("http://192.168.0.103:8080/update.json");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");//设置请求方法
                    connection.setConnectTimeout(5000);//设置连接超时
                    connection.setReadTimeout(5000);//设置响应超时,连接上了，但服务器为返回数据
                    connection.connect();

                    if (connection.getResponseCode() == 200) {
                        InputStream inputStream = connection.getInputStream();
                        String result = StreamUtils.readFromStream(inputStream);
                        JSONObject jsonObject = new JSONObject(result);
                        mVersionName = jsonObject.getString("versionName");
                        mVersionCode = jsonObject.getInt("versionCode");
                        mDesc = jsonObject.getString("description");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        if (mVersionCode > getVersionCode()) {//判断是否有更新
                            //服务器versionCode大于本地的versionCode
                            //说明有更新,弹出升级升级对话框
                            message.what = CODE_UPDATE_DIALOG;
                        } else {
                            //没有版本更新
                            message.what = CODE_ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    message.what = CODE_URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    message.what = CODE_NET_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    message.what = CODE_JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime=System.currentTimeMillis();
                    long timeUsed=endTime-startTime;//访问网络话费的时间
                    if (timeUsed<2000){

                        //强制休眠一段时间，保证闪屏页展示2秒钟
                        try {
                            Thread.sleep(2000-timeUsed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(message);
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }.start();
    }

    /**
     * 升级对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本：" + mVersionName);
        builder.setMessage(mDesc);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        //设置取消的监听，用户点击返回键时会触发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String target= Environment.getExternalStorageDirectory()+"/update.apk";
            tvProgress.setVisibility(View.VISIBLE);//显示进度
            //xUtils
            HttpUtils utils=new HttpUtils();
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
                //下载文件的进度
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);

                    tvProgress.setText("下载进度:"+current*100/total+"%");
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Toast.makeText(SplashActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
                    //跳转到系统的下载页面
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                    intent.setDataAndType(Uri.fromFile(responseInfo.result),"application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);//如果用户安装取消的话，则返回
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(SplashActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(SplashActivity.this,"没有SD卡",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0){
            enterHome();
        }
    }

    /**
     * 进入主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        new Intent();
        startActivity(intent);
        finish();
    }

    /**
     * 拷贝数据库
     */
    private void copyDB(String DBName) {
        File destFile = new File(getFilesDir(), DBName);//要拷贝的目标地址
        if (destFile.exists()){//判断数据库是否已经存在
            return;
        }
        FileOutputStream outputStream=null;
        InputStream inputStream=null;
        try {
            inputStream=getAssets().open(DBName);
            outputStream=new FileOutputStream(destFile);
            int len=0;
            byte[] buffer=new byte[1024];
            while ((len=inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
