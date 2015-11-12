package com.sumu.mobliesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.sumu.mobliesafe.bean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumu on 2015/11/9.
 */
public class BlackNumberDao {

    public BlackNUmberOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNUmberOpenHelper(context);
    }

    /**
     * @param number 黑名单号码
     * @param mode   拦截模式
     */
    public boolean add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        long rowId = db.insert("blacknumber", null, values);
        if (rowId != -1) {
            return true;
        }
        db.close();
        return false;
    }

    /**
     * 通过电话号码删除
     *
     * @param number 电话号码
     * @return
     */
    public boolean delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowNumber = db.delete("blacknumber", "number=?", new String[]{number});
        if (rowNumber != 0) {
            return true;
        }
        db.close();
        return false;
    }

    /**
     * 通过电话号码修改拦截模式
     *
     * @param number 电话号码
     * @return
     */
    public boolean changeNumberMode(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        int rowNumber = db.update("blacknumber", values, "number=?", new String[]{number});
        if (rowNumber != 0) {
            return true;
        }
        db.close();
        return false;
    }

    /**
     * 返回一个黑名单号码拦截模式
     *
     * @param number
     * @return
     */
    public String findNumber(String number) {
        String mode = "";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    /**
     * 查询所有的黑名单
     *
     * @return
     */
    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        BlackNumberInfo blackNumberInfo = null;
        Cursor cursor = db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        SystemClock.sleep(3000);//与Thread.sleep(3000)一样
        return blackNumberInfos;
    }

    /**
     * 分页加载数据
     * @param pageNumber  表示当前是哪一页
     * @param pageSize    表示每一页有多少条数据
     * @return
     *
     * limit 表示限制当前有多少数据
     * offset 表示跳过 从第几条开始
     */
    public List<BlackNumberInfo> findPar(int pageNumber,int pageSize) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        BlackNumberInfo blackNumberInfo = null;
        Cursor cursor=db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(pageSize), String.valueOf(pageSize * pageNumber)});
        while (cursor.moveToNext()) {
            blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    /**
     * 分批加载数据
     * @param startIndex  开始的位置
     * @param maxConunt    每页展示的最大的条目
     * @return
     *
     * limit 表示限制当前有多少数据
     * offset 表示跳过 从第几条开始
     */
    public List<BlackNumberInfo> findPar2(int startIndex,int maxConunt) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        BlackNumberInfo blackNumberInfo = null;
        Cursor cursor=db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(maxConunt), String.valueOf(startIndex)});
        while (cursor.moveToNext()) {
            blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    /**
     * 获取黑名单总条数
     * @return
     */
    public int getCount(){
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        cursor.moveToNext();
        int count=cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}


