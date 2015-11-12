package com.sumu.mobliesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.sumu.mobliesafe.R;

/**
 * Created by Sumu on 2015/11/6.
 */
public class Setup4Activity extends BaseSetupActivity{
    private CheckBox cbProtect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        cbProtect= (CheckBox) findViewById(R.id.cb_protect);
        boolean protect=preferences.getBoolean("protect",false);
        //根据保存的状态，
        cbProtect.setChecked(protect);
        if (protect){
            cbProtect.setText("防盗保护已经开启");
        }else {
            cbProtect.setText("防盗保护没有开启");
        }
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cbProtect.setText("防盗保护已经开启");
                }else {
                    cbProtect.setText("防盗保护没有开启");
                }
                preferences.edit().putBoolean("protect",isChecked).commit();
            }
        });
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(this,LostFindActivity.class));
        finish();
        preferences.edit().putBoolean("configed",true).commit();//更新sp，表示已经进入过设置向导
        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入动画和退出动画
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this,Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_pervious_in, R.anim.tran_pervious_out);
    }
}
