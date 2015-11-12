package com.sumu.mobliesafe.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sumu.mobliesafe.db.dao.AddressDao;
import com.sumu.mobliesafe.utils.ToastUtils;

/**
 * Created by Sumu on 2015/11/9.
 * 监听去的的广播
 * 需要权限：android.permission.PROCESS_OUTGOING_CALLS
 */
public class OutCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number = getResultData();//获取去电电话号码
        String address= AddressDao.getAddress(number);
        ToastUtils.showToast((Activity) context,address);
    }
}
