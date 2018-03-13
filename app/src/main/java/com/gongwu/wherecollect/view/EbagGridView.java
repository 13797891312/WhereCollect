package com.gongwu.wherecollect.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;
/**
 * @author xushsha 如果scrollview中需要嵌套gridview，使用该gridview
 */
public class EbagGridView extends GridView {

    private Bitmap rowBg; // 单行背景

    public EbagGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EbagGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EbagGridView(Context context) {
        super(context);
    }

    public void setRowBg(Bitmap bitmap) {
        rowBg = bitmap;
    }

    public void setRowBg(int resId) {
        rowBg = BitmapFactory.decodeResource(getResources(), resId);
    }

    // 绘制行背景
//    private static final int offsetX = (int) Math.round((-0.04) * MainHolder.instance().getScreenWidth());
//    private static final int offsetY = (int) Math.round((-0.08) * MainHolder.instance().getScreenHeight());
    private static final int offsetX = (int) Math.round((-0.04) * 1);
    private static final int offsetY = (int) Math.round((-0.08) * 1);

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (rowBg != null && getChildCount() > 0) {
            int numColumn = this.getNumColumns(); // 列数
            int rowCount = (getChildCount() - 1) / numColumn + 1;// 行数
            for (int i = 0; i < rowCount; i++) {
                int x = this.getChildAt(i * numColumn).getLeft();
                int y = this.getChildAt(i * numColumn).getBottom();
                canvas.drawBitmap(rowBg, x + offsetX, y + offsetY, null);
            }
        }
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
