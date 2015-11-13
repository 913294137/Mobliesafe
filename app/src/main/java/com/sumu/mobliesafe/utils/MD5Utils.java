package com.sumu.mobliesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sumu on 2015/11/6.
 */
public class MD5Utils {
    /**
     * MD5加密
     *
     * @param password
     * @return
     */
    public static String encode(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");//获取MD5加密算法对象
            byte[] digest = messageDigest.digest(password.getBytes());//对字符串加密，返回字节数组
            StringBuffer buffer = new StringBuffer();
            for (byte b : digest) {
                int i = b & 0xff;//获取字节的第八位有效值
                String hexString = Integer.toHexString(i);//将整数转为16进制
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;//如果是1为的话，补0
                }
                buffer.append(hexString);
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取到文件的MD5(病毒特征码)
     *
     * @param sourceDir
     * @return
     */
    public static String getFileMd5(String sourceDir) {
        File file = new File(sourceDir);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            //获取数字摘要
            MessageDigest messageDigest=MessageDigest.getInstance("md5");
            while ((len = fis.read(buffer)) != -1) {
                messageDigest.update(buffer,0,len);
            }
            byte[] digest = messageDigest.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int i = b & 0xff;//获取字节的第八位有效值
                String hexString = Integer.toHexString(i);//将整数转为16进制
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;//如果是1为的话，补0
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
