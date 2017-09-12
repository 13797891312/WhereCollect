package com.gongwu.wherecollect.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.GoodsBean;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Function:
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class GoodsImageView extends LinearLayout implements View.OnLongClickListener, BaseDragView, View
        .OnClickListener {
    @Bind(R.id.goods_iv)
    ImageView goodsIv;
    @Bind(R.id.zoom_iv)
    ImageView zoomIv;
    float x, y;
    private GoodsBean bean;
    boolean isEdit=true;

    public GoodsImageView(Context context) {
        this(context, null);
    }

    public GoodsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnLongClickListener(this);
        View view = View.inflate(getContext(), R.layout.item_goods_view, this);
        ButterKnife.bind(this, view);
        setBackgroundColor(getResources().getColor(R.color.trans));
        initZoom();
    }

    /**
     *
     * @param isEdit 是否是编辑状态
     */
    public void setEditable(boolean isEdit){
        this.isEdit=isEdit;
        if(isEdit){
            zoomIv.setVisibility(VISIBLE);
        }else{
            zoomIv.setVisibility(GONE);
        }
    }

    public void setGoods(GoodsBean bean) {
        this.bean = bean;
        goodsIv.setImageResource(bean.getImageResouseId());
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

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        return true;
    }

    private void initZoom() {
        zoomIv.setOnClickListener(this);
        zoomIv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewGroup.LayoutParams lp = getLayoutParams();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x = event.getX();
                    y = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float tempX = lp.width + (event.getX() - x);
                    float tempY = lp.height + (event.getY() - y);
                    if (tempX < 50)
                        tempX = 50;
                    if (tempY < 50)
                        tempY = 50;
                    lp.width = (int) tempX;
                    lp.height = (int) tempY;
                    x = event.getX();
                    x = event.getY();
                }
                if (lp.height > ((ViewGroup) getParent()).getHeight())
                    lp.height = ((ViewGroup) getParent()).getHeight();
                if (lp.width > ((ViewGroup) getParent()).getWidth())
                    lp.width = ((ViewGroup) getParent()).getWidth();
                setLayoutParams(lp);
                bean.setHeight(lp.height);
                bean.setWidth(lp.width);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
    }
}
