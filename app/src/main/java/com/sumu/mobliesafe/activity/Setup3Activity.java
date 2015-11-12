package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.utils.ToastUtils;

/**
 * Created by Sumu on 2015/11/6.
 */
public class Setup3Activity extends BaseSetupActivity {
    private static final int CONTACT_BACK = 1;
    private EditText etContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        etContact = (EditText) findViewById(R.id.et_contact);
        String phone=preferences.getString("safe_phone","");
        if (!TextUtils.isEmpty(phone)){
            etContact.setText(phone);
        }
    }

    public void showNextPage() {
        String phone = etContact.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToast(this, "安全号码不能为空！");
            return;
        }
        preferences.edit().putString("safe_phone",phone).commit();
        startActivity(new Intent(this, Setup4Activity.class));
        finish();
        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入动画和退出动画
    }

    public void showPreviousPage() {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_pervious_in, R.anim.tran_pervious_out);
    }

    /**
     * 选择联系人
     *
     * @param view
     */
    public void selectContact(View view) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivityForResult(intent, CONTACT_BACK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_BACK && resultCode == Activity.RESULT_OK) {
            etContact.setText(data.getStringExtra("phoneNumber"));//将选择的联系人号码填写到输入框中
        }
    }
}
