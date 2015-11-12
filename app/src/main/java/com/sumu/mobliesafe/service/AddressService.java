package com.sumu.mobliesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.db.dao.AddressDao;

/**
 * Created by Sumu on 2015/11/8.
 * 来电提醒的服务
 */
public class AddressService extends Service {
    private TelephonyManager telephonyManager;
    private MyListener myListener;
    private OutCallReceiver receiver;
    private SharedPreferences preferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("config", MODE_APPEND);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myListener = new MyListener();
        telephonyManager.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);//监听打电话的状态
        receiver = new OutCallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, intentFilter);//动态注册广播
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://电话铃声响了
                    String address = AddressDao.getAddress(incomingNumber);//根据来电号码查询归属地
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://电话闲置状态
                    if (null != manager && null != view) {
                        manager.removeView(view);//从window中移除view
                        view = null;
                    }
                    break;
            }
        }
    }

    /**
     * Created by Sumu on 2015/11/9.
     * 监听去的的广播
     * 需要权限：android.permission.PROCESS_OUTGOING_CALLS
     */
    class OutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();//获取去电电话号码
            String address = AddressDao.getAddress(number);
            showToast(address);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        telephonyManager.listen(myListener, PhoneStateListener.LISTEN_NONE);//停止监听打电话
        unregisterReceiver(receiver);//销毁广播
    }

    private WindowManager manager;
    private View view;

    /**
     * 自定义归属地浮窗
     */
    private void showToast(String address) {
        //可以在第三方app中弹出自己的浮窗
        manager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;// 电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
        //将重心位置设置为左上方，也就是(0,0),从左上方开始，而不是默认的重心位置
        params.gravity= Gravity.LEFT|Gravity.TOP;
        int lastX = preferences.getInt("lastX", 0);
        int lastY = preferences.getInt("lastY", 0);
        //设置浮窗的位置，基于左上方的偏移量
        params.x=lastX;
        params.y=lastY;

        params.setTitle("Toast");
        view = View.inflate(this, R.layout.toast_address, null);
        TextView tvText = (TextView) view.findViewById(R.id.tv_number);
        LinearLayout llBack = (LinearLayout) view.findViewById(R.id.ll_back);
        int style = preferences.getInt("address_style", 0);//读取保存的style
        int[] items = new int[]{R.drawable.call_locate_blue, R.drawable.call_locate_green};
        llBack.setBackgroundResource(items[style]);
        tvText.setText(address);
        manager.addView(view, params);//将view添加到屏幕上(window)
    }
}
