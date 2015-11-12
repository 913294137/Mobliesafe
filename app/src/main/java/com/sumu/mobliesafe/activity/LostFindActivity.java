package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumu.mobliesafe.R;

/**
 * Created by Sumu on 2015/11/6.
 */
public class LostFindActivity extends Activity{
    private SharedPreferences preferences;
    private TextView tvSafePhone;
    private ImageView IvProtect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences=getSharedPreferences("config",MODE_APPEND);
        boolean configed=preferences.getBoolean("configed",false); //判断是否进入过设置向导
        if (configed){
            setContentView(R.layout.activity_lost_find);
            //根据sp保存的号码更新安全号码
            tvSafePhone= (TextView) findViewById(R.id.tv_safe_phone);
            String phone=preferences.getString("safe_phone","");
            tvSafePhone.setText(phone);
            //根据sp开启的状态来更新锁的图标
            IvProtect= (ImageView) findViewById(R.id.iv_protect);
            boolean protect=preferences.getBoolean("protect",false);
            if (protect){
                IvProtect.setImageResource(R.drawable.lock);
            }else {
                IvProtect.setImageResource(R.drawable.unlock);
            }
        }else {
            //跳转设置向导页
            startActivity(new Intent(this,Setup1Activity.class));
            finish();
        }
    }

    /**
     * 重新进入设置向导
     * @param view
     */
    public void reEnter(View view){
        startActivity(new Intent(this,Setup1Activity.class));
        finish();
    }
}
