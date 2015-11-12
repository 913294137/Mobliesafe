package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.utils.SmsUtils;
import com.sumu.mobliesafe.utils.ToastUtils;

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
     * 归属地查询
     * @param view
     */
    public void numberAddressQuery(View view){
        startActivity(new Intent(this, AddressActivity.class));
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
