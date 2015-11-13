package com.sumu.mobliesafe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.fragment.LockFragment;
import com.sumu.mobliesafe.fragment.UnLockFragment;

public class AppLockActivity extends FragmentActivity implements View.OnClickListener {
    @ViewInject(R.id.tv_unlock)
    private TextView tvUnlock;
    @ViewInject(R.id.tv_lock)
    private TextView tvLock;
    @ViewInject(R.id.fl_content)
    private FrameLayout flContent;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private UnLockFragment unLockFragment;
    private LockFragment lockFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUI();
    }

    private void initUI() {
        ViewUtils.inject(this);
        tvLock.setOnClickListener(this);
        tvUnlock.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        unLockFragment = UnLockFragment.newInstance();
        transaction.replace(R.id.fl_content,unLockFragment).commit();
    }

    @Override
    public void onClick(View v) {
        transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.tv_unlock:
                tvUnlock.setBackgroundResource(R.drawable.tab_left_pressed);
                tvLock.setBackgroundResource(R.drawable.tab_right_default);
                unLockFragment = UnLockFragment.newInstance();
                transaction.replace(R.id.fl_content,unLockFragment);
                break;
            case R.id.tv_lock:
                tvUnlock.setBackgroundResource(R.drawable.tab_left_default);
                tvLock.setBackgroundResource(R.drawable.tab_right_pressed);
                lockFragment = LockFragment.newInstance();
                transaction.replace(R.id.fl_content,lockFragment);
                break;
        }
        transaction.commit();
    }
}
