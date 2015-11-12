package com.sumu.mobliesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.utils.ToastUtils;
import com.sumu.mobliesafe.view.SettingItemView;

/**
 * Created by Sumu on 2015/11/6.
 */
public class Setup2Activity extends BaseSetupActivity {
    private SettingItemView sivSim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        sivSim = (SettingItemView) findViewById(R.id.siv_sim);
        String sim = preferences.getString("sim", null);
        if (!TextUtils.isEmpty(sim)) {
            sivSim.setChecked(true);
        } else {
            sivSim.setChecked(false);
        }
        sivSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivSim.isChecked()) {
                    sivSim.setChecked(false);
                    preferences.edit().remove("sim").commit();
                } else {
                    sivSim.setChecked(true);
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = telephonyManager.getSimSerialNumber();//获取sim序列号
                    System.out.println("--------->" + simSerialNumber);
                    preferences.edit().putString("sim", simSerialNumber).commit();
                }
            }
        });
    }

    @Override
    public void showNextPage() {
        //如果sim卡没有绑定，就不允许进入下一个页面
        if (!sivSim.isChecked()){
            ToastUtils.showToast(this,"必须绑定sim卡");
            return;
        }
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入动画和退出动画
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_pervious_in, R.anim.tran_pervious_out);
    }


}
