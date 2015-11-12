package com.sumu.mobliesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * ==============================
 * 作者：苏幕
 * <p/>
 * 时间：2015/11/12   22:06
 * <p/>
 * 描述：
 *         SharedPreferences封装工具
 * <p/>
 * ==============================
 */
public class SharedPreferencesUtils {
    public static final String SP_NAME = "config";

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_APPEND);
        preferences.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_APPEND);
        return preferences.getBoolean(key, defValue);
    }
}
