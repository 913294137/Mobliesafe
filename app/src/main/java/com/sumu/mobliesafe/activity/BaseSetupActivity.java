package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Sumu on 2015/11/7.
 * 设置引导页的基类，不要在清单文件中注册，因为不要界面展示
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector mDetector;
    public SharedPreferences preferences=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences=getSharedPreferences("config",MODE_APPEND);
        //监听手势滑动事件
        mDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            /**
             *
             * @param e1  表示滑动的起点
             * @param e2  表示滑动的终点
             * @param velocityX  表示水平速度
             * @param velocityY  表示垂直速度
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //判断纵向滑动幅度是否过大，过大的话不允许切换界面
                if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
                    Toast.makeText(BaseSetupActivity.this, "不能这样滑喔~", Toast.LENGTH_SHORT).show();
                    return true;
                }

                //判断滑动速度是否过慢
                if (Math.abs(velocityX) < 150) {
                    Toast.makeText(BaseSetupActivity.this, "滑动太慢了", Toast.LENGTH_SHORT).show();
                    return true;
                }

                //向右滑，上一页
                if ((e2.getRawX() - e1.getRawX()) > 200) {
                    showPreviousPage();
                    return true;
                }
                //向左滑，下一页
                if ((e1.getRawX() - e2.getRawX()) > 200) {
                    showNextPage();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    //下一页
    public void next(View view) {
        showNextPage();
    }

    public abstract void showNextPage();

    //上一页
    public void previous(View view) {
        showPreviousPage();
    }

    public abstract void showPreviousPage();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
