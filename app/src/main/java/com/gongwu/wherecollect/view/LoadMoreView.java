package com.gongwu.wherecollect.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;

import swipetoloadlayout.SwipeLoadMoreTrigger;
import swipetoloadlayout.SwipeTrigger;
/**
 * Created by mucll on 2017/9/7.
 */
public class LoadMoreView extends RelativeLayout implements SwipeLoadMoreTrigger, SwipeTrigger {
    //    private ImageView shangla;
    private TextView textView;
    private ProgressBar progressBar;
    //    private ObjectAnimator anim;
    private boolean isRelease;
    private Context context;
    private View view;

    public LoadMoreView(Context context) {
        this(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initview();
    }

    private void initview() {
        view = View.inflate(context, R.layout.item_foot, null);
        //        shangla = (ImageView) view.findViewById(R.id.foot_iv);
        textView = (TextView) view.findViewById(R.id.foot_tv);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        //        shangla = new ImageView(getContext());
        //        textView = new TextView(getContext());
        //        //添加线性的父布局
        //        LinearLayout ll = new LinearLayout(getContext());
        //        RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
        // .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //        llParams.addRule(CENTER_IN_PARENT);
        //        ll.setLayoutParams(llParams);
        //        ll.setPadding(10, 10, 10, 10);
        //        shangla.setImageResource(R.drawable.icon);
        //        ll.addView(shangla);
        //        ll.addView(textView);
        addView(view);
    }

    @Override
    public void onLoadMore() {
        //上啦到一定位置之后调用此方法
        //        shangla.setImageResource(R.drawable.icon);
        //        anim = ObjectAnimator.ofFloat(shangla, "rotation", shangla.getRotation(), shangla.getRotation() +
        // 360f)
        //                .setDuration(500);
        //        anim.setRepeatCount(ValueAnimator.INFINITE);
        //        anim.setRepeatMode(ValueAnimator.RESTART);
        //        anim.start();
        textView.setText("正在加载");
    }

    @Override
    public void onPrepare() {
        //上拉之前调用此方法
        isRelease = false;
    }

    @Override
    public void onMove(int yScroll, boolean isComplete, boolean b1) {
        // 当上拉到一定位置之后 显示刷新
        if (!isComplete) {
            if (yScroll < getHeight()) {
                textView.setText("上拉加载");
            } else {
                textView.setText("松开加载更多");
            }
        }
    }

    @Override
    public void onRelease() {
        //上拉到一定距离之后松开刷新
        isRelease = true;
    }

    @Override
    public void onComplete() {
        textView.setText("加载完成");
    }

    @Override
    public void onReset() {
        //重置
    }
}