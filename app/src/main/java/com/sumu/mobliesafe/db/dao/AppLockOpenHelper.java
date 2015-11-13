package com.sumu.mobliesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sumu on 2015/11/9.
 * 已加锁数据库
 */
public class AppLockOpenHelper extends SQLiteOpenHelper{
    public AppLockOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table info (_id integer primary key autoincrement,packagename varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
