package com.sumu.mobliesafe.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumu.mobliesafe.R;

/**
 * Created by Sumu on 2015/11/9.
 * 修改归属地显示位置
 */
public class DragViewActivity extends Activity {
    private TextView tvTop, tvBottom;
    private ImageView ivDrag;
    private SharedPreferences preferences;
    private int startX,startY;
    private long[] mHits=new long[2];//表示要点击的次数，2就是双击，以此类推

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);
        preferences=getSharedPreferences("config",MODE_APPEND);
        tvTop = (TextView) findViewById(R.id.tv_top);
        tvBottom = (TextView) findViewById(R.id.tv_bottom);
        ivDrag = (ImageView) findViewById(R.id.iv_drag);
        int lastX=preferences.getInt("lastX",0);
        int lastY=preferences.getInt("lastY",0);
        //onMeasure(测量View),onLayout(安放位置),onDraw(绘制)

        //不能用这个方法，因为还没有测量完成，就不能安放位置
       // ivDrag.layout(lastX,lastY,lastX+ivDrag.getWidth(),lastY+ivDrag.getHeight());

        //获取屏幕宽高
        final int width = getWindowManager().getDefaultDisplay().getWidth();
        final int height = getWindowManager().getDefaultDisplay().getHeight();

        if (lastY>height/2){//上面的显示，下面的隐藏
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        }else {
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }

        RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();//获取布局对象
        layoutParams.leftMargin=lastX;//设置左边距
        layoutParams.topMargin=lastY;//设置右边距
        ivDrag.setLayoutParams(layoutParams);//重新设置位置

        ivDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                mHits[mHits.length-1]= SystemClock.uptimeMillis();//开机后开始计时
                if (mHits[0]>=(SystemClock.uptimeMillis()-500)){
                    //如果是双击，则把图片居中
                    ivDrag.layout(width/2-ivDrag.getWidth()/2,ivDrag.getTop(),width/2+ivDrag.getWidth()/2,ivDrag.getBottom());
                }
            }
        });

        //设置触摸监听
        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //初始化起点坐标
                        startX= (int) event.getRawX();
                        startY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX= (int) event.getRawX();
                        int endY= (int) event.getRawY();

                        //计算偏移量
                        int dx=endX-startX;
                        int dy=endY-startY;

                        //更新左上右下距离
                        int left=ivDrag.getLeft()+dx;
                        int right=ivDrag.getRight()+dx;

                        int top=ivDrag.getTop()+dy;
                        int bottom=ivDrag.getBottom()+dy;
                        //判段是否超出屏幕高度，注意高多了一个状态栏的高度
                        if (left<0||right>width||top<0||bottom>height-20){
                            break;
                        }

                        //根据图片位置，决定提示框的显示与隐藏
                        if (top>height/2){//上面的显示，下面的隐藏
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        }else {
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        }

                        //更新界面
                        ivDrag.layout(left,top,right,bottom);
                        //重新初始化起始坐标
                        startX= (int) event.getRawX();
                        startY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //记录界面坐标
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putInt("lastX",ivDrag.getLeft());
                        edit.putInt("lastY",ivDrag.getTop());
                        edit.commit();
                        break;
                }
                return false;//事件要往下传递,让双击事件可以相应
            }
        });
    }
}
