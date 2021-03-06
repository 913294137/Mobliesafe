package com.sumu.mobliesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sumu on 2015/11/8.
 * 归属地查询工具
 */
public class AddressDao {
    //注意该路径必须是data/data目录的文件，否则数据库访问不到
    private static final String PATH = "data/data/com.sumu.mobliesafe/files/address.db";

    public static String getAddress(String number) {
        String address = "未知号码";
        //获取数据库对象
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        if (number.matches("^1[3-8]\\d{8}$")) {//匹配手机号码
            Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{number.substring(0, 7)});
            if (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
            cursor.close();
        } else if (number.matches("^\\d+$")) {//匹配数字
            switch (number.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address="模拟器";
                    break;
                case 5:
                    address="客服电话";
                    break;
                default:
                    //01088881234
                    //052388881234
                    if (number.startsWith("0") && number.length()>10){//可能是长途电话
                        //有些区号是四位有些是三位
                        //先查询四位的
                        Cursor cursor=database.rawQuery("select location from data2 where area=?",new String[]{number.substring(1,4)});
                        if (cursor.moveToNext()){
                            address=cursor.getString(0);
                        }else {
                            cursor.close();
                            //查询三位
                            cursor=database.rawQuery("select location from data2 where area=?",new String[]{number.substring(1,3)});
                            if (cursor.moveToNext()){
                                address=cursor.getString(0);
                            }
                            cursor.close();
                        }
                    }
            }
        }
        database.close();
        return address;
    }
}
