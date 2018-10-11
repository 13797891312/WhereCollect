package com.gongwu.wherecollect.swipetoloadlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;


/**
 * Created by mucll on 2017/9/7.
 */

public class RefreshHeadView extends RelativeLayout implements SwipeRefreshTrigger, SwipeTrigger {
    private TextView textView;
    private ProgressBar progressBar;
    private boolean isRelease;
    private Context context;
    private View view;

    public RefreshHeadView(Context context) {
        this(context, null);
    }

    public RefreshHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initview();
    }

    private void initview() {
        view = View.inflate(context, R.layout.item_foot, null);
        textView = (TextView) view.findViewById(R.id.foot_tv);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        addView(view);
    }

    @Override
    public void onRefresh() {
        textView.setText("正在加载");
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onMove(int yScrolled, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            if (yScrolled >= getHeight()) {
                textView.setText("释放刷新");
            } else {
                textView.setText("下拉刷新");
            }
        }
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
        textView.setText("完成加载");
    }

    @Override
    public void onReset() {

    }
}