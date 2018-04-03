package com.gongwu.wherecollect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author xushsha
 *         <p/>
 *         <p/>
 *         如果需要scrollview里面嵌套listview，使用该listview不会出现不会滚动的情况
 */
public class EbagListView extends ListView {

    public EbagListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EbagListView(Context context) {
        super(context);
    }

    public EbagListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
