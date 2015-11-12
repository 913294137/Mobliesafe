package com.sumu.mobliesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sumu on 2015/11/9.
 * 黑名单数据库
 */
public class BlackNUmberOpenHelper extends SQLiteOpenHelper{
    public BlackNUmberOpenHelper(Context context) {
        super(context, "safe.db", null, 1);
    }

    /**
     * blacknumber 表名
     *  _id自增长
     *  number黑名单号码
     *  mode拦截模式：1电话拦截 2短信拦截  3全部拦截
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement,number varchar(20),mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
