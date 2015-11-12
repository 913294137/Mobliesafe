package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.db.dao.AddressDao;

/**
 * Created by Sumu on 2015/11/8.
 * 归属地查询页面
 */
public class AddressActivity extends Activity {
    private EditText etPhone;
    private TextView tvResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        etPhone = (EditText) findViewById(R.id.et_phone);
        tvResult= (TextView) findViewById(R.id.tv_result);
        etPhone.addTextChangedListener(new TextWatcher() {
            //文字发生变化时的回掉
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String address=AddressDao.getAddress(s.toString());
                tvResult.setText(address);
            }
            //文字变化前的回调
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //文字变化结束之后的回调
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 开始查询
     * @param view
     */
    public void query(View view){
        String number = etPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(number)) {
            String address=AddressDao.getAddress(number);
            tvResult.setText(address);
        }else {
            //当没有输入内容时，输入框左右移动动画
            Animation animation= AnimationUtils.loadAnimation(this,R.anim.shake);
            etPhone.startAnimation(animation);
            vibrate();
        }
    }

    /**
     * 手机震动
     */
    private void vibrate(){
        Vibrator vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //先等待1s,再震动2s,再等待1s,再震动3s,参数2：-1表示只执行一次，不循环，0表示一直循环（表示从set第几个位置开始循环）
        vibrator.vibrate(new long[]{1000,2000,1000,3000},-1);

        //vibrator.cancel();取消震动
    }
}
