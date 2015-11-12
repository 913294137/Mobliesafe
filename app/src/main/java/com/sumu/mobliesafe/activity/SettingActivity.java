package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.service.AddressService;
import com.sumu.mobliesafe.service.CallSafeService;
import com.sumu.mobliesafe.service.RocketService;
import com.sumu.mobliesafe.utils.SystemInfoUtils;
import com.sumu.mobliesafe.view.SettingClickView;
import com.sumu.mobliesafe.view.SettingItemView;

/**
 * 设置中心
 * Created by Sumu on 2015/11/6.
 */
public class SettingActivity extends Activity{
    private SettingItemView sivUpdate;//设置升级
    private SettingItemView sivAddress;//归属地显示开关
    private SettingItemView sivRocket;//小火箭开关
    private SettingItemView sivCallSafe;//黑名单开关
    private SettingClickView scvAddressStyle;//修改风格
    private SettingClickView scvAddressLocation;//修改归属地位置
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        preferences=getSharedPreferences("config", MODE_APPEND);
        initUpdate();
        initAddress();
        initAddressStyle();
        initAddressLocation();
        initRocket();
        initCallSafe();
    }

    /**
     * 初始化自动更新开关
     */
    private void initUpdate(){
        sivUpdate= (SettingItemView) findViewById(R.id.siv_update);
        /*sivUpdate.setTitle("自动更新设置");*/
        boolean autoUpdate=preferences.getBoolean("auto_update",true);
        /*if (autoUpdate){
            sivUpdate.setDesc("自动更新已开启");
        }else {
            sivUpdate.setDesc("自动更新已关闭");
        }*/
        sivUpdate.setChecked(autoUpdate);
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前的勾选状态
                if (sivUpdate.isChecked()) {
                    sivUpdate.setChecked(false);
                    /*sivUpdate.setDesc("自动更新已关闭");*/
                    preferences.edit().putBoolean("auto_update", false).commit();
                } else {
                    sivUpdate.setChecked(true);
                    /*sivUpdate.setDesc("自动更新已开启");*/
                    preferences.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }

    /**
     * 初始化归属地开关
     */
    private void initAddress(){
        //根据归属地服务是否运行来更新checkBox
        boolean serviceRunning= SystemInfoUtils.isServiceRunning(SettingActivity.this, "com.sumu.mobliesafe.service.AddressService");
        sivAddress= (SettingItemView) findViewById(R.id.siv_address);
        sivAddress.setChecked(serviceRunning);
        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivAddress.isChecked()){
                    sivAddress.setChecked(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));//停止归属地显示服务
                }else {
                    sivAddress.setChecked(true);
                    startService(new Intent(SettingActivity.this, AddressService.class));//开启归属地显示服务
                }
            }
        });
    }

    /**
     * 初始化黑名单开关
     */
    private void initCallSafe(){
        //根据归属地服务是否运行来更新checkBox
        boolean serviceRunning= SystemInfoUtils.isServiceRunning(SettingActivity.this, "com.sumu.mobliesafe.service.CallSafeService");
        sivCallSafe= (SettingItemView) findViewById(R.id.siv_call_safe);
        sivCallSafe.setChecked(serviceRunning);
        sivCallSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivCallSafe.isChecked()){
                    sivCallSafe.setChecked(false);
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));//停止黑名单服务
                }else {
                    sivCallSafe.setChecked(true);
                    startService(new Intent(SettingActivity.this, CallSafeService.class));//开启黑名单服务
                }
            }
        });
    }

    /**
     * 修改提示框的风格
     */
    private void initAddressStyle(){
        scvAddressStyle= (SettingClickView) findViewById(R.id.scv_address_style);

        scvAddressStyle.setTitle("归属地提示框风格");
        int style=preferences.getInt("address_style",0);//读取保存的style
        scvAddressStyle.setDesc(items[style]);
        scvAddressStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChooseDialog();
            }
        });
    }
    private String[] items=new String[]{"蓝色","绿色"};
    /**
     * 弹出选择风格对话框
     */
    private void showSingleChooseDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");
        int style=preferences.getInt("address_style",0);//读取保存的style
        builder.setSingleChoiceItems(items,style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                preferences.edit().putInt("address_style",which).commit();
                scvAddressStyle.setDesc(items[which]);
                dialog.dismiss();//让dialog消失
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    /**
     *修改归属地位置
     */
    private void initAddressLocation(){
        scvAddressLocation= (SettingClickView) findViewById(R.id.scv_address_location);
        scvAddressLocation.setTitle("归属地提示框显示位置");
        scvAddressLocation.setDesc("设置归属地提示框的显示位置");
        scvAddressLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DragViewActivity.class));
            }
        });
    }

    /**
     * 小火箭浮窗开关
     */
    private void initRocket(){
        sivRocket= (SettingItemView) findViewById(R.id.siv_rocket);
        sivRocket.setChecked(false);
        final Intent rocketService=new Intent(SettingActivity.this, RocketService.class);
        sivRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivRocket.isChecked()){
                    sivRocket.setChecked(false);
                    stopService(rocketService);
                }else {
                    sivRocket.setChecked(true);
                    startService(rocketService);
                }
            }
        });
    }
}
