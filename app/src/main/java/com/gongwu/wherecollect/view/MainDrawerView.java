package com.gongwu.wherecollect.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.gongwu.wherecollect.R;
/**
 * Function:
 * Date: 2017/8/28
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class MainDrawerView extends LinearLayout {
    Context context;

    public MainDrawerView(Context context) {
        this(context, null);
    }

    public MainDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View.inflate(context, R.layout.layout_main_drawer, this);
    }
}
