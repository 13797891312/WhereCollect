package com.gongwu.wherecollect.view;
import android.view.DragEvent;
import android.view.View;
/**
 * Function:
 * Date: 2017/8/31
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public interface BaseDragView {
    public boolean onDrag(View view, DragEvent dragEvent);
}
