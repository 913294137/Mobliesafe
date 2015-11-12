package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sumu.mobliesafe.R;

public class TaskManagerSettingActivity extends Activity {
    @ViewInject(R.id.cb_status)
    private CheckBox cbStatus;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
        ViewUtils.inject(this);
        initUI();
    }

    private void initUI() {
        preferences = getSharedPreferences("config", MODE_APPEND);
        //设置之前的选中状态
        cbStatus.setChecked(preferences.getBoolean("is_show_system",false));
        cbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("is_show_system",isChecked).commit();
            }
        });
    }
}
