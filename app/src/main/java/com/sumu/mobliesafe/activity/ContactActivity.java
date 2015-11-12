package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sumu.mobliesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sumu on 2015/11/7.
 * 选择联系人界面
 */
public class ContactActivity extends Activity{
    private ListView lvContact;
    private List<Map<String,String>> contacts=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        lvContact= (ListView) findViewById(R.id.lv_contant);
        contacts=getAllContacts();
        SimpleAdapter adapter=new SimpleAdapter(this,contacts,R.layout.contact_list_item,new String[]{"name","phone"},new int[]{R.id.tv_name,R.id.tv_phone});
        lvContact.setAdapter(adapter);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phoneNumber=contacts.get(position).get("phone");
                Intent intent=new Intent();
                intent.putExtra("phoneNumber",phoneNumber);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 查询手机中的所有联系人
     * @return
     */
    private List<Map<String,String>> getAllContacts(){
        List<Map<String,String>> contacts=new ArrayList<>();
        Map<String,String> contact=null;
        Cursor cursor=getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        int contactIdIndex=0;//联系人ID
        int nameIndex=0;//联系人姓名
        if (cursor.getCount()>0){
            contactIdIndex=cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }
        while (cursor.moveToNext()){
            contact=new HashMap<>();
            String contactId=cursor.getString(contactIdIndex);
            String name=cursor.getString(nameIndex);
            contact.put("name",name);
            //查找联系人电话
            Cursor phones=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            ,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+contactId,null,null);
            int phoneIndex=0;
            if (phones.getCount()>0){
                phoneIndex=phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }
            while (phones.moveToNext()){
                String phone=phones.getString(phoneIndex);
                contact.put("phone", phone);
            }
            phones.close();
            contacts.add(contact);
        }
        cursor.close();
        return contacts;
    }
}
