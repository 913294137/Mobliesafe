package com.sumu.mobliesafe.bean;

/**
 * Created by Sumu on 2015/11/9.
 */
public class BlackNumberInfo {

    private String number;//黑名单电话号码
    private String mode;//mode拦截模式：1电话拦截 2短信拦截  3全部拦截

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
