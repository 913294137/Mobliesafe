package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.utils.ToastUtils;

public class EnterPswActivity extends Activity {
    @ViewInject(R.id.et_password)
    private EditText etPassword;
    @ViewInject(R.id.btn_ok)
    private Button btnOK;
    private String packageName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_psw);
        ViewUtils.inject(this);

        Intent intent = getIntent();
        if (intent != null) {
            //当前所保护的程序包名
            packageName = intent.getStringExtra("packageName");
        }
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先拿到输入框的内容
                String str = etPassword.getText().toString();
                if (str.equals("123")) {//假设程序锁密码就是123
                    ToastUtils.showToast(EnterPswActivity.this, "密码输入正确");
                    Intent intent = new Intent();
                    // 发送广播。停止保护
                    intent.setAction("com.sumu.mobliesafe.stopprotect");
                    intent.putExtra("packageName", packageName);
                    sendBroadcast(intent);
                    finish();
                } else {
                    ToastUtils.showToast(EnterPswActivity.this, "密码输入错误");
                }
            }
        });
    }

    /**
     * 数字按钮点击事件
     *
     * @param view
     */
    public void clickNumber(View view) {
        //先拿到输入框的内容
        String beforeStr = etPassword.getText().toString();
        Button button = (Button) view;
        //然后将所点击的数字加到原输入框的后面
        String afterStr = beforeStr + button.getText().toString();
        etPassword.setText(afterStr);
        //将输入框的光标移到最后
        etPassword.setSelection(afterStr.length());
    }

    /**
     * 清空输入框的内容
     *
     * @param view
     */
    public void clearAll(View view) {
        etPassword.setText("");
    }

    /**
     * 删除键
     *
     * @param view
     */
    public void deteleNumber(View view) {
        //先拿到输入框的内容
        String str = etPassword.getText().toString();
        if (str.length() == 0) {
            return;
        }
        etPassword.setText(str.substring(0, str.length() - 1));
        etPassword.setSelection(str.length()-1);
    }
    // 监听当前页面的后退健
    // <intent-filter>
    // <action android:name="android.intent.action.MAIN" />
    // <category android:name="android.intent.category.HOME" />
    // <category android:name="android.intent.category.DEFAULT" />
    // <category android:name="android.intent.category.MONKEY"/>
    // </intent-filter>
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 当用户输入后退健的时候。我们进入到桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }
}
