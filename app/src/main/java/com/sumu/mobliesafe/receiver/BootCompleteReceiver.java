package com.sumu.mobliesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by Sumu on 2015/11/7.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_APPEND);
        String sim = preferences.getString("sim", null);//获取绑定的sim卡
        boolean protect = preferences.getBoolean("protect", false);
        //只有在防盗保护开启的前提下才进行sim卡判断
        if (protect) {
            if (!TextUtils.isEmpty(sim)) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String currentSim = telephonyManager.getSimSerialNumber();//拿到当前手机的sim卡
                if (sim.equals("12233")) {
                    System.out.println("手机安全！");
                } else {
                    System.out.println("手机危险！");
                    String phone =preferences.getString("safe_phone", "");//读取保存的安全号码


                    //发送短信给安全号码
                    SmsManager smsManager=SmsManager.getDefault();
                    smsManager.sendTextMessage(phone,null,"sim card changed!",null,null);
                }
            }
        }
    }
}
