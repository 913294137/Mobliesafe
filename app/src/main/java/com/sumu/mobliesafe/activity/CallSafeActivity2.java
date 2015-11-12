package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sumu.mobliesafe.adapter.MyBaseAdapter;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.bean.BlackNumberInfo;
import com.sumu.mobliesafe.db.dao.BlackNumberDao;
import com.sumu.mobliesafe.utils.ToastUtils;

import java.util.List;

/**
 * 通讯卫士界面
 */
public class CallSafeActivity2 extends Activity {

    private ListView listView;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout llPb;
    private CallSafeAdapter adapter;
    private BlackNumberDao dao;
    private int mStartIndex=0;//开始的位置
    private int maxCount=20;//每页展示20条数据

    private int totalNumber;//一共有多少条数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe2);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            llPb.setVisibility(View.INVISIBLE);
            if (adapter==null){
                adapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity2.this);
                listView.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void initData() {
        dao = new BlackNumberDao(CallSafeActivity2.this);
        new Thread() {
            @Override
            public void run() {
                if (blackNumberInfos==null) {
                    blackNumberInfos = dao.findPar2(mStartIndex, maxCount);
                }else {
                    blackNumberInfos.addAll(dao.findPar2(mStartIndex,maxCount));
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        listView = (ListView) findViewById(R.id.list_view);
        llPb = (LinearLayout) findViewById(R.id.ll_pb);
        llPb.setVisibility(View.VISIBLE);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            //状态改变时候回调的方法

            /**
             *
             * @param view
             * @param scrollState  表示滚动的状态
             *
             *                     AbsListView.OnScrollListener.SCROLL_STATE_IDLE 闲置状态
             *                     AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 手指触摸的时候的状态
             *                     AbsListView.OnScrollListener.SCROLL_STATE_FLING 惯性
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                totalNumber = dao.getCount();
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        //listView的最后一条显示的数据位置
                        int lastVisiblePosition = listView.getLastVisiblePosition();
                        if (lastVisiblePosition == blackNumberInfos.size() - 1) {
                            //加载更多的数据，更改加载数据的开始位置
                            if (mStartIndex >= totalNumber) {
                                ToastUtils.showToast(CallSafeActivity2.this, "没有更多数据...");
                                return;
                            }
                            mStartIndex += maxCount;
                            initData();
                        }
                        break;
                }
            }
            //listview滚动的时候调用的方法
            //时时调用。当我们的手指触摸的屏幕的时候就调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * 添加黑名单
     */
    public void addBlackNumber(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialogView=View.inflate(this,R.layout.dialog_add_black_number,null);
        final EditText etNumber= (EditText) dialogView.findViewById(R.id.et_number);
        Button btnOk= (Button) dialogView.findViewById(R.id.btn_ok);
        Button btnCancel= (Button) dialogView.findViewById(R.id.btn_cancel);
        final CheckBox cbPhone= (CheckBox) dialogView.findViewById(R.id.cb_phone);
        final CheckBox cbSms= (CheckBox) dialogView.findViewById(R.id.cb_sms);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number=etNumber.getText().toString().trim();
                if (TextUtils.isEmpty(number)){
                    ToastUtils.showToast(CallSafeActivity2.this,"请输入黑名单号码");
                    return;
                }
                String mode="";
                if (cbPhone.isChecked() && cbSms.isChecked()){
                    mode="1";
                }else if (cbPhone.isChecked()){
                    mode="2";
                }else if (cbSms.isChecked()){
                    mode="3";
                }else {
                    ToastUtils.showToast(CallSafeActivity2.this,"请勾选拦截模式");
                    return;
                }
                //将电话号码和拦截模式添加到数据库
                boolean result = dao.add(number, mode);
                if (result){
                    BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
                    blackNumberInfo.setNumber(number);
                    blackNumberInfo.setMode(mode);
                    blackNumberInfos.add(0,blackNumberInfo);
                    ToastUtils.showToast(CallSafeActivity2.this, "添加成功");
                }else {
                    ToastUtils.showToast(CallSafeActivity2.this,"添加失败");
                    return;
                }
                handler.sendEmptyMessage(0);
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(dialogView);
        dialog.show();
    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {
        public CallSafeAdapter(List<BlackNumberInfo> lists, Context context) {
            super(lists, context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity2.this, R.layout.item_call_safe, null);
                viewHolder = new ViewHolder();
                viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
                viewHolder.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
                viewHolder.ivDelete= (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvNumber.setText(lists.get(position).getNumber());
            String mode = lists.get(position).getMode();
            if (mode.equals("1")) {
                mode = "来电+短信拦截";
            } else if (mode.equals("2")) {
                mode = "电话拦截";
            } else if (mode.equals("3")) {
                mode = "短信拦截";
            }
            viewHolder.tvMode.setText(mode);
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean result = dao.delete(lists.get(position).getNumber());
                    if (result) {
                        ToastUtils.showToast(CallSafeActivity2.this, "删除成功");
                        lists.remove(position);
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.showToast(CallSafeActivity2.this, "删除失败");
                    }
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tvNumber;
        TextView tvMode;
        ImageView ivDelete;
    }
}
