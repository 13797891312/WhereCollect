package com.gongwu.wherecollect.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.afragment.MainLocationFragment;
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
public class EditLocationPage extends RelativeLayout implements BaseDragView {
    Context context;
    List<GoodsBean> mlist = new ArrayList<>();
    private OnDragListener listener;

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
        addView(iv);
    }

    public void removeGoods(GoodsBean bean, View v) {
        removeView(v);
        mlist.remove(bean);
    }

    public boolean queryGoods(GoodsBean bean) {
        return mlist.contains(bean);
    }

    public void setDragListenerFromView(OnDragListener listener) {
        this.listener = listener;
    }

    public void ondrag(View view, DragEvent dragEvent) {
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        View v = ((View) dragEvent.getLocalState());
        GoodsBean bean = (GoodsBean) v.getTag();
        EditLocationPage page = (EditLocationPage) MainLocationFragment.editLocationView.pageView.getPrimaryItem();
        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                //判断viewpager是否有这个数据，有的话说明是在viewpger内抬起的，就显示删除布局
                if (page.queryGoods(bean)) {
                    MainLocationFragment.editLocationView.deleteLayout.setVisibility(View.VISIBLE);
                } else {
                    MainLocationFragment.editLocationView.deleteLayout.setVisibility(View.GONE);
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED://进入某布局
                break;
            case DragEvent.ACTION_DRAG_LOCATION://位置
                break;
            case DragEvent.ACTION_DROP://在某布局结束
                ((ViewGroup) v.getParent()).removeView(v);
                bean.setType(1);//设置有归属
                bean.setX(dragEvent.getX() - bean.getWidth() / 2);//有点偏移需要减去自身宽的一半
                bean.setY(dragEvent.getY() - BaseViewActivity.getStateHeight(context));
                page.addGoods(bean);
                MainLocationFragment.editLocationView.recyclerAdapter2.removeBean(bean);
                //落下后删除布局隐藏
                if (MainLocationFragment.editLocationView.deleteLayout.getVisibility() == View.VISIBLE) {
                    MainLocationFragment.editLocationView.deleteLayout.setVisibility(View.GONE);
                }
                break;
            case DragEvent.ACTION_DRAG_EXITED://离开某布局
                break;
            case DragEvent.ACTION_DRAG_ENDED://结束
                v.setVisibility(VISIBLE);
                break;
        }
        return true;
    }
}
