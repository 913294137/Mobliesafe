package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.utils.SmsUtils;
import com.sumu.mobliesafe.utils.ToastUtils;

import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.OffersManager;

/**
 * Created by Sumu on 2015/11/8.
 * 高级工具
 */
public class AToolsActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 软件推荐
     * @param view
     */
    public void software(View view){
        // 调用方式一：直接打开全屏积分墙
        // OffersManager.getInstance(this).showOffersWall();

        // 调用方式二：直接打开全屏积分墙，并且监听积分墙退出的事件onDestory
        OffersManager.getInstance(this).showOffersWall(new Interface_ActivityListener() {

            /**
             * 当积分墙销毁的时候，即积分墙的Activity调用了onDestory的时候回调
             */
            @Override
            public void onActivityDestroy(Context context) {
                Toast.makeText(context, "全屏积分墙退出了", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 归属地查询
     * @param view
     */
    public void numberAddressQuery(View view){
        startActivity(new Intent(this, AddressActivity.class));
    }

    /**
     * 程序锁
     * @param view
     */
    public void appLock(View view){
        startActivity(new Intent(this, AppLockActivity.class));
    }

    /**
     * 短信备份
     * @param view
     */
    public void backUpSms(View view){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在备份短信，请稍等...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        new Thread(){
            @Override
            public void run() {
                boolean backUp = SmsUtils.backUp(AToolsActivity.this, new SmsUtils.BackUpCallBackSms() {
                    @Override
                    public void setProgress(int progress) {
                        progressDialog.setProgress(progress);
                    }

                    @Override
                    public void setMax(int max) {
                        progressDialog.setMax(max);
                    }
                });
                if (backUp){
                    Looper.prepare();
                    ToastUtils.showToast(AToolsActivity.this, "备份成功！");
                    progressDialog.dismiss();
                    Looper.loop();
                }else {
                    Looper.prepare();
                    ToastUtils.showToast(AToolsActivity.this, "备份失败！");
                    progressDialog.dismiss();
                    Looper.loop();
                }
            }
        }.start();

    }
}
