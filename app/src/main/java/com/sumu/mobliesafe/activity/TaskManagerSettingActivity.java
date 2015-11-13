package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.service.KillProcessService;
import com.sumu.mobliesafe.utils.SystemInfoUtils;

/**
 * 进程管理设置界面
 */
public class TaskManagerSettingActivity extends Activity {
    @ViewInject(R.id.cb_status)
    private CheckBox cbStatus;
    @ViewInject(R.id.cb_status_kill_process)
    private CheckBox cbStatusKillProcess;
    private SharedPreferences preferences;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
        ViewUtils.inject(this);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //判断当前杀进程服务是否开启
        if (SystemInfoUtils.isServiceRunning(TaskManagerSettingActivity.this, "com.sumu.mobliesafe.service.KillProcessService")) {
            cbStatusKillProcess.setChecked(true);
        } else {
            cbStatusKillProcess.setChecked(false);
        }
    }

    private void initUI() {
        preferences = getSharedPreferences("config", MODE_APPEND);
        //设置之前的选中状态
        cbStatus.setChecked(preferences.getBoolean("is_show_system", false));
        cbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("is_show_system", isChecked).commit();
            }
        });
        cbStatusKillProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                intent = new Intent(TaskManagerSettingActivity.this, KillProcessService.class);
                if (isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
    }
}
