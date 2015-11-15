package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.bean.AppInfo;
import com.sumu.mobliesafe.bean.TrafficInfo;
import com.sumu.mobliesafe.engine.AppInfoParser;

import java.util.ArrayList;
import java.util.List;

public class TrafficManagerActivity extends Activity {
    @ViewInject(R.id.tv_rx_bytes)
    private TextView tvRxBytes;
    @ViewInject(R.id.tv_tx_bytes)
    private TextView tvTxBytes;
    @ViewInject(R.id.list_view)
    private ListView listView;
    private List<TrafficInfo> trafficInfos;
    private MyTrafficManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);
        ViewUtils.inject(this);
        initUI();
        initData();
    }

    private void initUI() {
        //获取到手机的下载的流量
        long mobileRxBytes = TrafficStats.getTotalRxBytes();
        //获取到手机的上传的流量
        long mobileTxBytes = TrafficStats.getTotalTxBytes();
        tvRxBytes.setText("总下载:" + Formatter.formatFileSize(this, mobileRxBytes));
        tvTxBytes.setText("总上传:" + Formatter.formatFileSize(this, mobileTxBytes));
        //流量统计集合
        trafficInfos = new ArrayList<>();
        adapter = new MyTrafficManagerAdapter();
        listView.setAdapter(adapter);
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                List<AppInfo> appInfos = AppInfoParser.getAppInfos(TrafficManagerActivity.this);
                for (AppInfo appInfo : appInfos) {
                    int uid = appInfo.getUid();
                    //获取当前应用的下载流量
                    long uidRxBytes = TrafficStats.getUidRxBytes(uid);
                    //获取当前应用的上传流量
                    long uidTxPackets = TrafficStats.getUidTxPackets(uid);
                    TrafficInfo trafficInfo = new TrafficInfo();
                    trafficInfo.setIcon(appInfo.getIcon());
                    trafficInfo.setName(appInfo.getApkName());
                    trafficInfo.setRxBytes(uidRxBytes);
                    trafficInfo.setTxBytes(uidTxPackets);
                    Message.obtain(mHandler, 0, trafficInfo).sendToTarget();
                }
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj != null && msg.obj instanceof TrafficInfo) {
                adapter.addDataAndRefresh((TrafficInfo) msg.obj);
            }
        }
    };

    private class MyTrafficManagerAdapter extends BaseAdapter {

        public void addDataAndRefresh(TrafficInfo trafficInfo) {
            trafficInfos.add(trafficInfo);
            adapter.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return trafficInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return trafficInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(TrafficManagerActivity.this, R.layout.item_traffic, null);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tvRx = (TextView) convertView.findViewById(R.id.tv_rx);
                viewHolder.tvTx = (TextView) convertView.findViewById(R.id.tv_tx);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TrafficInfo trafficInfo = (TrafficInfo) getItem(position);
            viewHolder.ivIcon.setImageDrawable(trafficInfo.getIcon());
            viewHolder.tvName.setText(trafficInfo.getName());
            viewHolder.tvRx.setText("下载:" + Formatter.formatFileSize(TrafficManagerActivity.this, trafficInfo.getRxBytes()));
            viewHolder.tvTx.setText("上传:" + Formatter.formatFileSize(TrafficManagerActivity.this, trafficInfo.getTxBytes()));
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvRx;
        TextView tvTx;
    }
}
