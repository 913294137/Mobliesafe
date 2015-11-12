package com.sumu.mobliesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Sumu on 2015/11/11.
 * 短信备份工具
 */
public class SmsUtils {
    /**
     * 短信备份进度回调接口
     */
    public interface BackUpCallBackSms{
        //获取备份进度
        void setProgress(int progress);
        //获取备份总量
        void setMax(int max);
    }

    /**
     * 备份短信
     *
     * @param context
     * @param backUpCallBackSms
     * @return 1 判断当前用户的手机上面是否有sd卡
     * 2 权限 ---
     * 使用内容观察者
     * 3 写短信(写到sd卡)
     */
    public static boolean backUp(Context context,BackUpCallBackSms backUpCallBackSms) {
        // 判断当前sd卡的状态
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //如果能进来就说明用户有SD卡
            ContentResolver resolver = context.getContentResolver();
            //获取短信路径
            Uri uri = Uri.parse("content://sms/");
            //type 1 接受短信  2  发送短信
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
            //获取当前一共有多少条短信
            int count = cursor.getCount();
            backUpCallBackSms.setMax(count);
            //表示备份短信的进度
            int progress=0;
            FileOutputStream os=null;
            try {
                // 把短信备份到sd卡 第二个参数表示名字
                File file = new File(Environment.getExternalStorageDirectory(), "backUp.xml");
                os = new FileOutputStream(file);
                // 得到序列化器
                // 在android系统里面所有有关xml的解析都是pull解析
                XmlSerializer serializer = Xml.newSerializer();
                // 把短信序列化到sd卡然后设置编码格式
                serializer.setOutput(os, "utf-8");
                // standalone表示当前的xml是否是独立文件 ture表示文件独立。yes
                serializer.startDocument("utf-8", true);
                // 设置开始的节点 第一个参数是命名空间。第二个参数是节点的名字
                serializer.startTag(null, "smss");
                //设置smss节点上面的属性值 第二个参数是名字。第三个参数是值
                serializer.attribute(null, "size", String.valueOf(count));
                while (cursor.moveToNext()) {
                    serializer.startTag(null,"sms");
                    serializer.startTag(null, "address");
                    serializer.text(cursor.getString(0));// 设置文本的内容
                    serializer.endTag(null, "address");
                    serializer.startTag(null, "date");
                    serializer.text(cursor.getString(1));// 设置文本的内容
                    serializer.endTag(null, "date");
                    serializer.startTag(null, "type");
                    serializer.text(cursor.getString(2));// 设置文本的内容
                    serializer.endTag(null, "type");
                    serializer.startTag(null, "body");
                    try {
                        serializer.text(Crypto.encrypt("123",cursor.getString(3)));// 设置文本的内容
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    serializer.endTag(null, "body");
                    serializer.endTag(null,"sms");
                    //每备份完一条短信，进度条++
                    progress++;
                    backUpCallBackSms.setProgress(progress);
                    SystemClock.sleep(200);
                }
                serializer.endTag(null, "smss");
                serializer.endDocument();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                cursor.close();
                if (os!=null){
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
