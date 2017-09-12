package com.gongwu.wherecollect.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
/**
 * @ClassName: ErrorView
 * @Description: 用于没有数据或加载错误时显示
 */
public class ErrorView extends LinearLayout {
    private TextView error_tv;

    public ErrorView(Context context) {
        super(context);
        initView(context, null);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet object) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_error, this);
        error_tv = (TextView) view.findViewById(R.id.error_msg_tv);
    }

    /**
     * @param msg
     * @Title: setErrorMsg
     * @Description: 设置错误提示语
     * @return: void
     */
    public void setErrorMsg(String msg) {
        error_tv.setText(msg);
    }
}
