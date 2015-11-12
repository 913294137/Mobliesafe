package com.sumu.mobliesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sumu on 2015/11/5.
 * 读取流的工具
 */
public class StreamUtils {
    /**
     * 将输入流读取成String后返回
     * @param inputStream
     * @return
     */
    public static String readFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        int len=0;
        byte[] buffer=new byte[1024];
        while ((len=inputStream.read(buffer))!=-1){
            byteArrayOutputStream.write(buffer,0,len);
        }
        String result=byteArrayOutputStream.toString();
        byteArrayOutputStream.close();
        inputStream.close();
        return result;
    }
}
