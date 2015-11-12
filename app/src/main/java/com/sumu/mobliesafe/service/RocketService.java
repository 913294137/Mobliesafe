package com.sumu.mobliesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sumu.mobliesafe.R;
import com.sumu.mobliesafe.activity.BackgroundActivity;

public class RocketService extends Service {
    public RocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private WindowManager manager;
    private View view;
    private int startX, startY;
    private int width, height;
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();
        //可以在第三方app中弹出自己的浮窗
        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        //将重心位置设置为左上方，也就是(0,0),从左上方开始，而不是默认的重心位置
        params.gravity = Gravity.LEFT + Gravity.TOP;
        params.setTitle("Toast");
        view = View.inflate(this, R.layout.rocket, null);
        // 初始化火箭帧动画
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_rocket);
        imageView.setBackgroundResource(R.drawable.anim_rocket);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();

        //获取屏幕宽高
        width = manager.getDefaultDisplay().getWidth();
        height = manager.getDefaultDisplay().getHeight();

        manager.addView(view, params);//将view添加到屏幕上(window)
        //设置触摸监听
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        //计算偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;

                        // 更新浮窗位置
                        params.x += dx;
                        params.y += dy;

                        // 防止坐标偏离屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        // 防止坐标偏离屏幕
                        if (params.x > width - view.getWidth()) {
                            params.x = width - view.getWidth();
                        }

                        if (params.y > height - view.getHeight()) {
                            params.y = height - view.getHeight();
                        }
                        //更新界面
                        manager.updateViewLayout(view, params);
                        //重新初始化起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.x > width / 4 && params.x < width / 4 * 3 && params.y > height / 4 * 3) {
                            System.out.println("发射火箭！");
                            sendRocket();
                            // 启动烟雾效果
                            Intent intent = new Intent(RocketService.this, BackgroundActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//启动一个栈来存放activity
                            startActivity(intent);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int y = msg.arg1;
            params.y = y;
            manager.updateViewLayout(view, params);
        }
    };

    /**
     * 发射火箭
     */
    private void sendRocket() {
        //设置火箭居中
        params.x = width / 2 - view.getWidth() / 2;
        params.y = height / 5 * 4;
        manager.updateViewLayout(view, params);

        new Thread() {
            @Override
            public void run() {
                super.run();
                int pos = params.y;
                for (int i = 0; i < 10; i++) {
                    //等待一段时间，用于控制火箭的速度
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int y = pos - pos / 10 * i;
                    Message message = Message.obtain();
                    message.arg1 = y;
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager != null && view != null) {
            manager.removeView(view);
        }
    }
}
