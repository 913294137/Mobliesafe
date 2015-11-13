package com.sumu.mobliesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * ==============================
 * 作者：苏幕
 * <p/>
 * 时间：2015/11/13   20:51
 * <p/>
 * 描述：
 * <p/>
 * ==============================
 */
public class AppLockDao {

    private AppLockOpenHelper helper;

    public AppLockDao(Context context) {
        helper = new AppLockOpenHelper(context);
    }

    /**
     * 添加到程序所里面
     *
     * @param packageName 包名
     */
    public boolean addLockApp(String packageName) {
        boolean result = false;
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packageName);
        long info = db.insert("info", null, values);
        if (info > 0) {
            result = true;
        }
        db.close();
        return result;
    }


    /**
     * 从程序锁里面删除当前的包
     *
     * @param packageName
     */
    public boolean deleteLockApp(String packageName) {
        boolean result = false;
        SQLiteDatabase db = helper.getWritableDatabase();
        int info = db.delete("info", "packagename=?", new String[]{packageName});
        if (info > 0) {
            result = true;
        }
        db.close();
        return result;
    }

    /**
     * 查询当前的包是否在程序锁里面
     *
     * @param packageName
     * @return
     */
    public boolean find(String packageName) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", null, "packagename=?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部的锁定的包名
     *
     * @return
     */
    public List<String> findAllLockApp() {
        List<String> appInfos = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", new String[]{"packagename"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            appInfos.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return appInfos;
    }
}
