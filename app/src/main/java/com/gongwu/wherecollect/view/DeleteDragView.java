package com.gongwu.wherecollect.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.gongwu.wherecollect.afragment.MainLocationFragment;
import com.gongwu.wherecollect.entity.GoodsBean;
/**
 * Function:
 * Date: 2017/8/31
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class DeleteDragView extends LinearLayout implements BaseDragView {
    public DeleteDragView(Context context) {
        this(context, null);
    }

    public DeleteDragView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        View v = ((View) dragEvent.getLocalState());
        GoodsBean bean = (GoodsBean) v.getTag();
        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DROP://在某布局结束
                //隐藏删除布局
                setVisibility(GONE);
                EditLocationPage page = (EditLocationPage) MainLocationFragment
                        .editLocationView.pageView.getPrimaryItem();
                bean.setType(0);
                page.removeGoods(bean, v);
                MainLocationFragment.editLocationView.recyclerAdapter2.addBean(bean);
                break;
            case DragEvent.ACTION_DRAG_ENTERED://进入某布局
                setVisibility(VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_EXITED://离开某布局
                setVisibility(GONE);
                break;
            case DragEvent.ACTION_DRAG_ENDED://结束
                v.setVisibility(VISIBLE);
                break;
        }
        return true;
    }
}
