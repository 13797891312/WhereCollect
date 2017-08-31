package com.gongwu.wherecollect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.GoodsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:位置编辑viewpager页面
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class EditLocationPage extends RelativeLayout {
    Context context;
    List<GoodsBean> mlist = new ArrayList<>();

    public EditLocationPage(Context context) {
        this(context, null);
    }

    public EditLocationPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
    }

    public void addGoods(GoodsBean bean) {
        mlist.add(bean);
        GoodsImageView iv = new GoodsImageView(context);
        if (listener != null) {
            iv.setOnDragListener(listener);
        }
        iv.setGoods(bean);
        iv.setBackgroundColor(getResources().getColor(R.color.black));
        addView(iv);
    }

    public void removeGoods(GoodsBean bean, View v) {
        removeView(v);
        mlist.remove(bean);
    }

    public boolean queryGoods(GoodsBean bean) {
        return mlist.contains(bean);
    }

    private EditLocationView.MyDragListener listener;

    public void setDragListenerFromView(EditLocationView.MyDragListener listener) {
        this.listener = listener;
    }
}
