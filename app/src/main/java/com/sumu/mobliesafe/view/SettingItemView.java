package com.sumu.mobliesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumu.mobliesafe.R;

/**
 * 设置中心Item自定义View
 * Created by Sumu on 2015/11/6.
 */
public class SettingItemView extends RelativeLayout{
    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox cbStatus;
    private String mTitle,mDescOn,mDescOff;
    private static final String NAEMSPACE="http://schemas.android.com/apk/res-auto";
    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTitle=attrs.getAttributeValue(NAEMSPACE,"setting_title");
        mDescOn=attrs.getAttributeValue(NAEMSPACE,"desc_on");
        mDescOff=attrs.getAttributeValue(NAEMSPACE,"desc_off");
        initView();

    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView(){
        //将自定义好的布局文件设置给当前的SettingItemView
        View.inflate(getContext(), R.layout.view_setting_item,this);
        tvTitle=(TextView)findViewById(R.id.tv_title);
        tvDesc=(TextView)findViewById(R.id.tv_desc);
        cbStatus=(CheckBox)findViewById(R.id.cb_status);
        setTitle(mTitle);
    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }

    public void setDesc(String desc){
        tvDesc.setText(desc);
    }

    /**
     * 返回勾选状态
     * @return
     */
    public boolean isChecked(){
        return cbStatus.isChecked();
    }

    /**
     * 设置勾选状态
     * @param check
     */
    public void setChecked(boolean check){
        cbStatus.setChecked(check);
        //根据选择的状态，更新文本描述
        if (check){
            setDesc(mDescOn);
        }else {
            setDesc(mDescOff);
        }
    }

}
