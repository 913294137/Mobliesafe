package com.sumu.mobliesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**获取焦点的TextView
 * Created by Sumu on 2015/11/5.
 */
public class FocusedTextView extends TextView{

    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context) {
        super(context);
    }

    /**
     * 表示有没有获得焦点
     *
     * 跑马灯要运行,首先调用此函数判断是否有焦点，是true跑马灯才有效果
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
