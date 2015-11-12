package com.sumu.mobliesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.gsm.SmsMessage;

import com.sumu.mobliesafe.db.dao.BlackNumberDao;

/**
 * Created by Sumu on 2015/11/10.
 * 黑名单短信拦截广播
 */
public class InnerReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objects= (Object[]) intent.getExtras().get("pdus");
        for (Object object:objects) {//短信最多140字节，超出会分为多条短信
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
            String originatingAddress = smsMessage.getOriginatingAddress();//短信来源号码
            String messageBody = smsMessage.getMessageBody();//短信内容
            BlackNumberDao dao=new BlackNumberDao(context);
            //通过短信号码查询拦截的模式
            String mode = dao.findNumber(originatingAddress);
            if (mode.equals("1") || mode.equals("3")) {
                abortBroadcast();
            }
            //智能拦截模式
            if (messageBody.contains("fapiao")){
                abortBroadcast();
            }
        }
    }
}
