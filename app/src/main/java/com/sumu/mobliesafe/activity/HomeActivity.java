package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.utils.MD5Utils;

/**
 * 主页面
 * Created by Sumu on 2015/11/5.
 */
public class HomeActivity extends Activity {
    private GridView gvHome;

    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计"
            , "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] mPics = new int[]{R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan
            , R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings};
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("config", MODE_APPEND);
        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showPasswordDialog();
                        break;
                    case 1:
                        startActivity(new Intent(HomeActivity.this,CallSafeActivity2.class));
                        break;
                    case 2:
                        startActivity(new Intent(HomeActivity.this,AppManagerActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(HomeActivity.this,TaskManagerActivity.class));
                        break;
                    case 4:
                        break;
                    case 5:
                        startActivity(new Intent(HomeActivity.this,AntivirusActivity.class));
                        break;
                    case 6:
                        break;
                    case 7:
                        startActivity(new Intent(HomeActivity.this,AToolsActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                }
            }
        });
    }

    /**
     * 显示密码弹窗
     */
    private void showPasswordDialog() {
        //判断是否设置密码
        String savePassword = preferences.getString("password", null);
        if (!TextUtils.isEmpty(savePassword)) {
            //输入密码弹窗
            showPasswordInputDialog();
        } else {
            //如果没有设置过，弹出设置密码的弹窗
            showPasswordSetDialog();
        }
    }

    /**
     * 输入密码弹窗
     */
    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dialog_input_password, null);
        //将之定义的布局文件设置给对话框，设置边距为0，保证2.x的版本效果
        dialog.setView(view, 0, 0, 0, 0);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = MD5Utils.encode(etPassword.getText().toString());
                String savePassword=preferences.getString("password", null);
                if (!TextUtils.isEmpty(password)){
                        if (password.equals(savePassword)){
                            dialog.dismiss();
                            //跳转到手机防盗页面
                            startActivity(new Intent(HomeActivity.this,LostFindActivity.class));
                        }else {
                            Toast.makeText(HomeActivity.this, "密码错误错误！", Toast.LENGTH_SHORT).show();
                        }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 设置密码的弹窗
     */
    private void showPasswordSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dialog_set_password, null);
        //将之定义的布局文件设置给对话框，设置边距为0，保证2.x的版本效果
        dialog.setView(view, 0, 0, 0, 0);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        final EditText etPasswordConfirm = (EditText) view.findViewById(R.id.et_password_confirm);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordConfirm)) {
                    if (password.equals(passwordConfirm)) {
                        Toast.makeText(HomeActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        preferences.edit().putString("password", MD5Utils.encode(password)).commit();
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.ivItem = (ImageView) convertView.findViewById(R.id.iv_item);
                viewHolder.tvItem = (TextView) convertView.findViewById(R.id.tv_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvItem.setText(mItems[position]);
            viewHolder.ivItem.setImageResource(mPics[position]);
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView ivItem;
        TextView tvItem;
    }
}
