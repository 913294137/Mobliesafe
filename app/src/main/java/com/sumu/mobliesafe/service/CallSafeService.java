package com.sumu.mobliesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.sumu.mobliesafe.db.dao.BlackNumberDao;
import com.sumu.mobliesafe.receiver.InnerReceiver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CallSafeService extends Service {

    private InnerReceiver innerReceiver;
    private TelephonyManager tm;
    private BlackNumberDao dao;
    private Uri uri;

    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);
        //初始化短信广播
        innerReceiver = new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(innerReceiver, intentFilter);
        //获取到系统的电话服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyPhoneStateListener listener=new MyPhoneStateListener();
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    class MyPhoneStateListener extends PhoneStateListener {

        //电话状态改变的监听
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
//            * @see TelephonyManager#CALL_STATE_IDLE  电话闲置
//            * @see TelephonyManager#CALL_STATE_RINGING 电话铃响的状态
//            * @see TelephonyManager#CALL_STATE_OFFHOOK 电话接通
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    String mode = dao.findNumber(incomingNumber);
                    /**
                     * 黑名单拦截模式
                     * 1 全部拦截 电话拦截 + 短信拦截
                     * 2 电话拦截
                     * 3 短信拦截
                     */
                    if (mode.equals("1") || mode.equals("2")) {
                        System.out.println("挂断黑名单电话号码");
                        //挂断黑名单电话
                        uri = Uri.parse("content://call_log/calls");

                        //监听来电记录，有数据变化时执行MyContentObserver里面的onChange()
                        getContentResolver().registerContentObserver(uri, true, new MyContentObserver(new Handler(), incomingNumber));
                        //挂断电话
                        endCall();
                    }
                    break;
            }
        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        try {
            ////通过类加载器加载ServiceManager
            Class<?> loadClass = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到当前的方法
            Method method = loadClass.getDeclaredMethod("getService", String.class);

            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class MyContentObserver extends ContentObserver {
        private String incomingNumber;

        /**
         * Creates a content observer.
         *
         * @param handler        The handler to run {@link #onChange} on, or null if none.
         * @param incomingNumber
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        //当数据改变的时候调用的方法
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
        }
    }

    /**
     * 将挂断的电话记录删除掉
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber){
        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (innerReceiver != null) {
            unregisterReceiver(innerReceiver);
        }
    }
}
