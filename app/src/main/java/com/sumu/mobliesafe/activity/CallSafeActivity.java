package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
public class CallSafeActivity extends Activity {

    private ListView listView;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout llPb;
    private EditText etPageNumber;
    private TextView tvPageNumber;
    private int mCurrentPage = 0;//当前在第几页
    private int mPageSize = 20;//一页显示多少条数据
    private int mTotalPage;//总共多少页
    private CallSafeAdapter adapter;
    private BlackNumberDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvPageNumber.setText((mCurrentPage + 1) + "/" + mTotalPage);
            llPb.setVisibility(View.INVISIBLE);
            adapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
            listView.setAdapter(adapter);
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                dao = new BlackNumberDao(CallSafeActivity.this);
                //总条数除以每页条数得到总共多少页
                if (dao.getCount() <= mPageSize) {//如果总条数只够显示一页那么mTotalPage = 1
                    mTotalPage = 1;
                } else if (dao.getCount() % mPageSize != 0) {//如果总条数/每页显示数据的条数 有余数的话则把页数+1，因为（200/20=10,199/20=9）
                    mTotalPage = dao.getCount() / mPageSize + 1;
                } else {
                    mTotalPage = dao.getCount() / mPageSize;
                }
                System.out.println("总条数:" + dao.getCount());
                System.out.println("总共多少页:" + mTotalPage);
                blackNumberInfos = dao.findPar(mCurrentPage, mPageSize);
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        listView = (ListView) findViewById(R.id.list_view);
        llPb = (LinearLayout) findViewById(R.id.ll_pb);
        llPb.setVisibility(View.VISIBLE);
        etPageNumber = (EditText) findViewById(R.id.et_page_number);
        tvPageNumber = (TextView) findViewById(R.id.tv_page_number);
    }

    /**
     * 跳转页面
     *
     * @param view
     */
    public void jump(View view) {
        String pageNumber = etPageNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(pageNumber)) {
            if (Integer.parseInt(pageNumber) > 0 && Integer.parseInt(pageNumber) <= mTotalPage) {
                mCurrentPage = Integer.valueOf(pageNumber) - 1;
                initData();
            } else {
                ToastUtils.showToast(this, "请输入正确跳转的页码");
            }
        } else {
            ToastUtils.showToast(this, "请输入要跳转的页码");
        }
    }

    /**
     * 下一页
     *
     * @param view
     */
    public void nextPage(View view) {
        if (mCurrentPage >= (mTotalPage - 1)) {
            ToastUtils.showToast(this, "已到最后一页");
            return;
        }
        mCurrentPage++;
        initData();
    }

    /**
     * 上一页
     *
     * @param view
     */
    public void prePage(View view) {
        if (mCurrentPage <= 0) {
            ToastUtils.showToast(this, "已到第一页");
            return;
        }
        mCurrentPage--;
        initData();
    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {
        public CallSafeAdapter(List<BlackNumberInfo> lists, Context context) {
            super(lists, context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
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
                        ToastUtils.showToast(CallSafeActivity.this, "删除成功");
                        lists.remove(position);
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.showToast(CallSafeActivity.this, "删除失败");
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
