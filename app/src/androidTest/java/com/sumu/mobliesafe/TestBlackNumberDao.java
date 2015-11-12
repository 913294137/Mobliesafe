package com.sumu.mobliesafe;

import android.content.Context;
import android.test.AndroidTestCase;

import com.sumu.mobliesafe.bean.BlackNumberInfo;
import com.sumu.mobliesafe.db.dao.BlackNumberDao;

import java.util.List;
import java.util.Random;

/**
 * Created by Sumu on 2015/11/9.
 */
public class TestBlackNumberDao extends AndroidTestCase {
    private Context context;
    @Override
    protected void setUp() throws Exception {
        this.context=getContext();
        super.setUp();
    }

    public void testAdd(){
        BlackNumberDao dao=new BlackNumberDao(context);
        Random random = new Random();
        for (int i=0;i<200;i++){
            Long number=1389654254l+i;
            dao.add(number+"",String.valueOf(random.nextInt(3)+1));
        }
    }

    public void testDelete(){
        BlackNumberDao dao=new BlackNumberDao(context);
        boolean delete=dao.delete("1389654254");
        assertEquals(true,delete);
    }

    public void testFind(){
        BlackNumberDao dao=new BlackNumberDao(context);
        String number=dao.findNumber("13896542541");
        System.out.println(number);
    }

    public void testFindAll(){
        BlackNumberDao dao=new BlackNumberDao(context);
        List<BlackNumberInfo> blackNumberInfos=dao.findAll();
        for (BlackNumberInfo blackNumberInfo:blackNumberInfos){
            System.out.println(blackNumberInfo.getMode()+"--"+blackNumberInfo.getNumber());
        }
    }

}
