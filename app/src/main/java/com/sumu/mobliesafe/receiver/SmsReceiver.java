package com.sumu.mobliesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.gsm.SmsMessage;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.service.LocationService;

/**
 * Created by Sumu on 2015/11/8.
 *拦截短信
 */
public class SmsReceiver extends BroadcastReceiver{
    private SharedPreferences preferences=null;

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
        mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);// 设备管理组件

        preferences=context.getSharedPreferences("config",Context.MODE_APPEND);
        Object[] objects= (Object[]) intent.getExtras().get("pdus");
        for (Object object:objects){//短信最多140字节，超出会分为多条短信
            SmsMessage smsMessage=SmsMessage.createFromPdu((byte[]) object);
            String originatingAddress=smsMessage.getOriginatingAddress();//短信来源号码
            String messageBody=smsMessage.getMessageBody();//短信内容
            if ("#*alarm*#".equals(messageBody)){
                //播放报警音乐，即使手机调为静音，也能播放音乐，因为使用的是媒体声音通道，和铃声无关
                MediaPlayer player=MediaPlayer.create(context, R.raw.alarm);
                player.setVolume(1f,1f);//将音量调到最大
                player.setLooping(true);//不停的循环
                player.start();
                abortBroadcast();//中断短信的传递，从而系统短信app就结收不到内容了
            }else if ("#*location*#".equals(messageBody)){
                Intent locationIntent=new Intent(context, LocationService.class);
                context.startService(locationIntent);
                String location=preferences.getString("location","正在获取location....");
                System.out.println(location);
                abortBroadcast();//中断短信的传递，从而系统短信app就结收不到内容了
            }else if ("#*location*#".equals(messageBody)){

                abortBroadcast();//中断短信的传递，从而系统短信app就结收不到内容了
            }else if ("#*location*#".equals(messageBody)){

                abortBroadcast();//中断短信的传递，从而系统短信app就结收不到内容了
            }

        }
    }
    /*
    // 激活设备管理器, 也可以在设置->安全->设备管理器中手动激活
    public void activeAdmin(View view) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "哈哈哈, 我们有了超级设备管理器, 好NB!");
        startActivity(intent);
    }

    // 一键锁屏
    public void lockScreen(View view) {
        if (mDPM.isAdminActive(mDeviceAdminSample)) {// 判断设备管理器是否已经激活
            mDPM.lockNow();// 立即锁屏
            mDPM.resetPassword("123456", 0);//重置密码
        } else {
            Toast.makeText(this, "必须先激活设备管理器!", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearData(View view) {
        if (mDPM.isAdminActive(mDeviceAdminSample)) {// 判断设备管理器是否已经激活
            mDPM.wipeData(0);// 清除数据,恢复出厂设置
        } else {
            Toast.makeText(this, "必须先激活设备管理器!", Toast.LENGTH_SHORT).show();
        }
    }

    public void unInstall(View view) {
        mDPM.removeActiveAdmin(mDeviceAdminSample);// 取消激活

        // 卸载程序
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
    */

}
