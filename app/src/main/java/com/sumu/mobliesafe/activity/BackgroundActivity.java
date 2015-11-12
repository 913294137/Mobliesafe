package com.sumu.mobliesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.sumu.mobliesafe.R;

/**
 * Created by Sumu on 2015/11/9.
 * 烟雾背景
 */
public class BackgroundActivity extends Activity {
    private ImageView ivBottom, ivTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg);
        ivBottom = (ImageView) findViewById(R.id.iv_bottom);
        ivTop = (ImageView) findViewById(R.id.iv_top);
        //渐变动画
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(1000);
        animation.setFillAfter(true);//动画结束后保持状态

        ivTop.startAnimation(animation);
        ivBottom.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);//延时1s后再结束activity
    }
}
