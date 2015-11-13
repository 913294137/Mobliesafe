package com.sumu.mobliesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * ==============================
 * 作者：苏幕
 * <p/>
 * 时间：2015/11/13   15:40
 * <p/>
 * 描述：
 * <p/>
 * ==============================
 */
public class AntivirusDao {
    //注意该路径必须是data/data目录的文件，否则数据库访问不到
    private static final String PATH = "data/data/com.sumu.mobliesafe/files/antivirus.db";

    /**
     * 检查当前的MD5值是否在病毒数据库中
     *
     * @param md5
     * @return
     */
    public static String checkFileVirus(String md5) {
        String desc = null;
        //获取数据库对象
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        //查询当前传过来的md5是否在病毒数据库里面
        Cursor cursor = database.rawQuery("select desc from datable where md5=?", new String[]{md5});
        if (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        database.close();
        return desc;
    }

    /**
     * 添加病毒数据库
     *
     * @param md5  特征码
     * @param desc 描述信息
     */
    public static void addVirus(String md5, String desc) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/com.itheima.mobileguard/files/antivirus.db", null,
                SQLiteDatabase.OPEN_READWRITE);

        ContentValues values = new ContentValues();
        values.put("md5", md5);
        values.put("type", 6);
        values.put("name", "Android.Troj.AirAD.a");
        values.put("desc", desc);
        db.insert("datable", null, values);
        db.close();
    }
}
