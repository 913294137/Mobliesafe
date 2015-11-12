package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.bean.TaskInfo;
import com.sumu.mobliesafe.engine.TaskInfoParser;
import com.sumu.mobliesafe.utils.SystemInfoUtils;
import com.sumu.mobliesafe.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerActivity extends Activity {
    @ViewInject(R.id.tv_task_process_count)
    private TextView tvTaskProessCount;
    @ViewInject(R.id.tv_task_memory)
    private TextView tvTaskMemory;
    @ViewInject(R.id.list_view)
    private ListView listView;
    private List<TaskInfo> taskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;
    private TaskManagerAdapter adapter;
    private long availMem;
    private long totalMem;
    private int processCount;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        ViewUtils.inject(this);
        preferences = getSharedPreferences("config", MODE_APPEND);
        initUI();
        initData();
    }

    /**
     * 区别：
     * <p/>
     * ActivityManager 活动管理器(任务管理器)
     * <p/>
     * packageManager 包管理器
     */
    private void initUI() {
        initTextView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAtPosition = listView.getItemAtPosition(position);
                if (itemAtPosition != null && itemAtPosition instanceof TaskInfo) {
                    TaskInfo taskInfo = (TaskInfo) itemAtPosition;
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder.cbAppStatus.isChecked()) {
                        holder.cbAppStatus.setChecked(false);
                        taskInfo.setCheck(false);
                    } else {
                        holder.cbAppStatus.setChecked(true);
                        taskInfo.setCheck(true);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取最新的运行中进程数，以及内存情况，并更新TextView
     */
    private void initTextView() {
        processCount = SystemInfoUtils.getProcessCount(TaskManagerActivity.this);
        availMem = SystemInfoUtils.getAvailMem(TaskManagerActivity.this);
        totalMem = SystemInfoUtils.getTotalMem(TaskManagerActivity.this);
        tvTaskProessCount.setText("运行中进程:" + processCount + "个");
        tvTaskMemory.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, availMem) + "/"
                + Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                taskInfos = TaskInfoParser.getTaskInfos(TaskManagerActivity.this);
                userTaskInfos = new ArrayList<>();
                systemTaskInfos = new ArrayList<>();
                for (TaskInfo taskInfo : taskInfos) {
                    if (taskInfo.isUserApp()) {
                        userTaskInfos.add(taskInfo);
                    } else {
                        systemTaskInfos.add(taskInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new TaskManagerAdapter();
                        listView.setAdapter(adapter);
                        initTextView();
                    }
                });
            }
        }.start();
    }

    /**
     * 全选
     *
     * @param view
     */
    public void selectAll(View view) {
        for (TaskInfo taskInfo : userTaskInfos) {
            taskInfo.setCheck(true);
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            taskInfo.setCheck(true);
        }
        //一定要注意，数据发生改变后一定要刷新界面
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     *
     * @param view
     */
    public void selectOppsite(View view) {
        for (TaskInfo taskInfo : userTaskInfos) {
            //取反，选中则变为未选中，未选中则变为选中
            taskInfo.setCheck(!taskInfo.isCheck());
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            taskInfo.setCheck(!taskInfo.isCheck());
        }
        //一定要注意，数据发生改变后一定要刷新界面
        adapter.notifyDataSetChanged();
    }

    /**
     * 清理进程
     *
     * @param view
     */
    public void killProcess(View view) {
        //想杀死进程，就必须得到进程管理器
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //清理进程的集合
        List<TaskInfo> taskInfos = new ArrayList<>();

        //清理的总共的进程个数
        int totalCount = 0;
        //清理的进程的大小
        int killMen = 0;
        for (TaskInfo taskInfo : userTaskInfos) {
            if (taskInfo.isCheck()) {
                taskInfos.add(taskInfo);
                totalCount++;
                killMen += taskInfo.getMemorySize();
            }
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            if (taskInfo.isCheck()) {
                taskInfos.add(taskInfo);
                totalCount++;
                killMen += taskInfo.getMemorySize();
            }
        }
        //注意，迭代的过程中，不能修改集合的大小
        for (TaskInfo taskInfo : taskInfos) {
            //判读是否是用户APP
            if (taskInfo.isUserApp()) {
                userTaskInfos.remove(taskInfo);
                //杀死进程 参数表示包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            } else {
                systemTaskInfos.remove(taskInfo);
                //杀死进程 参数表示包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }
        //刷新界面
        adapter.notifyDataSetChanged();
        ToastUtils.showToast(TaskManagerActivity.this, "共清理了" + totalCount + "个进程,释放了"
                + Formatter.formatFileSize(TaskManagerActivity.this, killMen) + "内存");
        tvTaskProessCount.setText("运行中进程:" + (processCount-totalCount) + "个");
        tvTaskMemory.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, (availMem+killMen)) + "/"
                + Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
        initData();
    }

    /**
     * 设置界面
     * @param view
     */
    public void openSetting(View view){
        Intent intent=new Intent(TaskManagerActivity.this,TaskManagerSettingActivity.class);
        startActivity(intent);
    }


    private class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (preferences.getBoolean("is_show_system",false)){
                return userTaskInfos.size()+1+systemTaskInfos.size()+1;
            }else {
                return userTaskInfos.size()+1;
            }
        }

        @Override
        public Object getItem(int position) {
            if (position == 0 || position == (userTaskInfos.size() + 1)) {
                return null;
            }
            TaskInfo taskInfo = null;
            if (position < (userTaskInfos.size() + 1)) {
                taskInfo = userTaskInfos.get(position - 1);
            } else if (position > (userTaskInfos.size() + 1)) {
                    taskInfo = systemTaskInfos.get(position - userTaskInfos.size() - 2);

            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序(" + userTaskInfos.size() + ")");
                return textView;
            } else if (position == (userTaskInfos.size() + 1)) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序(" + systemTaskInfos.size() + ")");
                return textView;
            }
            ViewHolder viewHolder = null;
            if (convertView != null && convertView instanceof LinearLayout) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);
                viewHolder = new ViewHolder();
                viewHolder.ivAppIcon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                viewHolder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
                viewHolder.tvAppMemorySize = (TextView) convertView.findViewById(R.id.tv_app_memory_size);
                viewHolder.cbAppStatus = (CheckBox) convertView.findViewById(R.id.cb_app_status);
                convertView.setTag(viewHolder);
            }
            TaskInfo taskInfo = (TaskInfo) getItem(position);
            if (taskInfo!=null) {
                viewHolder.ivAppIcon.setImageDrawable(taskInfo.getIcon());
                viewHolder.tvAppName.setText(taskInfo.getAppName());
                viewHolder.tvAppMemorySize.setText("占用内存:" + Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemorySize()));
                viewHolder.cbAppStatus.setChecked(taskInfo.isCheck());
            }
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvAppMemorySize;
        CheckBox cbAppStatus;
    }
}
