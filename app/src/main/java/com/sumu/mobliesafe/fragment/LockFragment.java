package com.sumu.mobliesafe.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.bean.AppInfo;
import com.sumu.mobliesafe.db.dao.AppLockDao;
import com.sumu.mobliesafe.engine.AppInfoParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 已加锁界面
 */
public class LockFragment extends Fragment {
    private View view;
    private TextView tvAppNumber;
    private ListView listView;
    private List<AppInfo> appInfos;
    private LockAdapter adapter;
    private AppLockDao dao;
    private List<AppInfo> LockAppInfos;

    public static LockFragment newInstance() {
        LockFragment fragment = new LockFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lock, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);
        tvAppNumber = (TextView) view.findViewById(R.id.tv_app_number);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewUtils.inject(getActivity());
        adapter = new LockAdapter();
        // 初始化一个加锁的集合
        LockAppInfos = new ArrayList<AppInfo>();
        listView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread() {
            @Override
            public void run() {
                appInfos = AppInfoParser.getAppInfos(getActivity());
                // 获取到程序锁的dao
                dao = new AppLockDao(getActivity());
                for (AppInfo appInfo : appInfos) {
                    // 判断当前的应用是否在程序锁的数据里面
                    if (dao.find(appInfo.getApkPackageName())) {
                        // 如果查询到说明在程序锁的数据库里面
                        Message.obtain(mHandler,0,appInfo).sendToTarget();
                    }
                }
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj != null && msg.obj instanceof AppInfo) {
                LockAppInfos.add((AppInfo) msg.obj);
                adapter.notifyDataSetChanged();
            }
        }
    };


    private class LockAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            tvAppNumber.setText("已加锁(" + LockAppInfos.size() + ")个");
            return LockAppInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return LockAppInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final AppInfo appInfo;
            final View view;
            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.item_unlock, null);
                viewHolder = new ViewHolder();
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name);
                viewHolder.ivLock = (ImageView) view.findViewById(R.id.iv_lock);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            appInfo = (AppInfo) getItem(position);
            viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
            viewHolder.tvName.setText(appInfo.getApkName());
            viewHolder.ivLock.setImageResource(R.drawable.btn_unlock_selector);
            viewHolder.ivLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 将已加锁的程序从数据库中删除
                    boolean result = dao.deleteLockApp(appInfo.getApkPackageName());
                    if (result) {
                        // 初始化一个位移动画
                        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.0f
                                , Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                        animation.setDuration(500);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // 从当前的页面移除对象
                                LockAppInfos.remove(position);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        view.startAnimation(animation);
                    }
                }
            });
            return view;
        }
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        ImageView ivLock;
    }

}
