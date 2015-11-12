package com.sumu.mobliesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import java.util.List;

/**
 * Created by Sumu on 2015/11/8.
 * 获取经纬度坐标的service
 */
public class LocationService extends Service {
    private MyLocationListener listener;
    private LocationManager locationManager;
    private SharedPreferences preferences;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences=getSharedPreferences("config", MODE_APPEND);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> allProviders = locationManager.getAllProviders();//获取所有的位置提供者
        listener = new MyLocationListener();
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);//是否允许付费,比如使用3g网络定位
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置精确度
        String bestProvider = locationManager.getBestProvider(criteria, true);//获取最佳位置提供者
        System.out.println(bestProvider);
        //参1表示位置提供者,参2表示最短更新时间,参3表示最短更新距离
        locationManager.requestLocationUpdates(bestProvider, 0, 0, listener);
    }

    class MyLocationListener implements LocationListener {

        // 位置发生变化
        @Override
        public void onLocationChanged(Location location) {
            String j = "经度:" + location.getLongitude();
            String w = "纬度:" + location.getLatitude();
            String accuracy = "精确度:" + location.getAccuracy();
            String altitude = "海拔:" + location.getAltitude();
            System.out.println(j);
            System.out.println(w);
            System.out.println(accuracy);
            System.out.println(altitude);
            //将拿到的经纬度保存到sp中
            preferences.edit().putString("location",j+","+w).commit();
            //停掉service
            stopSelf();
        }

        // 位置提供者状态发生变化
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("onStatusChanged");
        }

        // 用户打开gps
        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("onProviderEnabled");
        }

        // 用户关闭gps
        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("onProviderDisabled");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(listener);// 当activity销毁时,停止更新位置, 节省电量
    }
}
