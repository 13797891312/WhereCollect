package com.gongwu.wherecollect.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gongwu.wherecollect.entity.GoodsBean;
/**
 * Function:
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class GoodsImageView extends ImageView implements View.OnLongClickListener {
    private GoodsBean bean;

    public GoodsImageView(Context context) {
        this(context, null);
    }

    public GoodsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnLongClickListener(this);
    }

    public void setGoods(GoodsBean bean) {
        this.bean = bean;
        setImageResource(bean.getImageResouseId());
        setX(bean.getX());
        setY(bean.getY());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(bean.getWidth(), bean.getHeight());
        setLayoutParams(layoutParams);
    }

    @Override
    public boolean onLongClick(View view) {
        this.setTag(bean);
        startDrag(null, new DragShadowBuilder(this), this, 0);
        setVisibility(INVISIBLE);
        return true;
    }
}
