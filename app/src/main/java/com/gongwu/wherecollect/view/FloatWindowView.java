package com.gongwu.wherecollect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;

/**
 * @ClassName: FloatWindowView
 * @Description: 全局悬浮按钮
 */
public class FloatWindowView extends LinearLayout {

    private TextView nameTv;

    public FloatWindowView(Context context) {
        super(context);
        initView(context, null);
    }

    public FloatWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet object) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_float_window_layout, this);
        nameTv = view.findViewById(R.id.float_name_tv);
    }

    public void setNameTv(String text) {
        if (nameTv != null) {
            nameTv.setText(text);
        }
    }
}
