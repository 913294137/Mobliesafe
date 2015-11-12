package com.sumu.mobliesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumu.mobliesafe.R;

/**
 * 设置中心Item自定义View
 * Created by Sumu on 2015/11/6.
 */
public class SettingClickView extends RelativeLayout{
    private TextView tvTitle;
    private TextView tvDesc;
    private static final String NAEMSPACE="http://schemas.android.com/apk/res-auto";
    public SettingClickView(Context context) {
        super(context);
        initView();
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();

    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView(){
        //将自定义好的布局文件设置给当前的SettingClickView
        View.inflate(getContext(), R.layout.view_setting_click,this);
        tvTitle=(TextView)findViewById(R.id.tv_title);
        tvDesc=(TextView)findViewById(R.id.tv_desc);

    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }

    public void setDesc(String desc){
        tvDesc.setText(desc);
    }




}
