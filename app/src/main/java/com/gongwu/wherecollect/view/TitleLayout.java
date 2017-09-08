package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Function:
 * Date: 2017/8/25
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class TitleLayout extends LinearLayout {
    @Bind(R.id.textBtn)
    public TextView textBtn;
    Context context;
    @Bind(R.id.back_btn)
    ImageButton backBtn;
    @Bind(R.id.title_tv)
    TextView titleTv;

    public TitleLayout(Context context) {
        this(context, null);
    }

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View.inflate(context, R.layout.layout_title_bar_all, this);
        ButterKnife.bind(this);
    }

    public void setTitle(String title) {
        titleTv.setText(title);
    }

    /**
     * @param canBank
     * @param listener 可以为空
     */
    public void setBack(boolean canBank, OnClickListener listener) {
        if (canBank) {
            backBtn.setVisibility(VISIBLE);
            if (listener == null) {
                listener = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Activity) context).finish();
                    }
                };
            }
            backBtn.setOnClickListener(listener);
        } else {
            backBtn.setVisibility(GONE);
            canBank = false;
        }
    }

    /**
     * 右侧文字按钮
     *
     * @param listener
     */
    public void setTextBtnListener(String text, OnClickListener listener) {
        textBtn.setOnClickListener(listener);
        textBtn.setText(text);
        textBtn.setVisibility(VISIBLE);
    }
}
