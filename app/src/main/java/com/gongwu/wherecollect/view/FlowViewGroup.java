package com.gongwu.wherecollect.view;
import android.content.Context;
import android.os.Build;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;
public class FlowViewGroup extends ViewGroup {
  
    public FlowViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);  
    }  
  
    public FlowViewGroup(Context context, AttributeSet attrs) {  
        this(context, attrs, 0);  
    }  
  
    public FlowViewGroup(Context context) {  
        this(context, null);  
    }  
  
  
    @Override  
    protected LayoutParams generateDefaultLayoutParams() {  
        return new MarginLayoutParams(-2, -2);
    }  
  
    private Map<View, ChildViewPosition> map;
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        if (map == null) {  
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                map = new ArrayMap<View, ChildViewPosition>();
            } else {  
                map = new HashMap<View, ChildViewPosition>();
            }  
        } else {  
            map.clear();  
        }  
        //计算所有ziView的大小  
        measureChildren(widthMeasureSpec, heightMeasureSpec);  
  
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);  
        int sizeHight = MeasureSpec.getSize(heightMeasureSpec);  
  
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);  
        int hightMode = MeasureSpec.getMode(heightMeasureSpec);  
  
        int lineWidth = 0;  
        int lineHight = 0;  
  
        int width = 0;  
        int hight = 0;  
  
        int childCount = getChildCount();  
  
        for (int i = 0; i < childCount; i++) {  
            View childView = getChildAt(i);  
  
            MarginLayoutParams lp;  
            if (childView.getLayoutParams() instanceof MarginLayoutParams) {  
                lp = (MarginLayoutParams) childView.getLayoutParams();  
            } else {  
                lp = new MarginLayoutParams(childView.getLayoutParams());  
            }  
  
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;  
            int childHight = childView.getMeasuredHeight() + lp.bottomMargin + lp.topMargin;  
            //判断是非需要换行  
            if (lineWidth + childWidth > sizeWidth) {  
                //需要换行  
  
                //计算ViewGroup需要的宽高  
                width = Math.max(width, lineWidth);  
                hight += lineHight;  
  
                //换行重置当前行高度与宽度  
                lineHight = childHight;  
                lineWidth = childWidth;  
                map.put(childView, new ChildViewPosition(0 + lp.leftMargin, hight + lp.topMargin, childWidth - lp.rightMargin, hight + childHight - lp.bottomMargin));  
  
  
            } else {  
                //不需要换行  
  
                map.put(childView, new ChildViewPosition(lineWidth + lp.leftMargin, hight + lp.topMargin, lineWidth + childWidth - lp.rightMargin, hight + childHight - lp.bottomMargin));  
  
                //叠加当前行的宽度  
                lineWidth += childWidth;  
                //计算当前行最大高度  
                lineHight = Math.max(lineHight, childHight);  
  
  
            }  
            //加上最后一行的宽高的计算出ViewGroup需要的宽高  
            if (i == childCount - 1) {  
                width = Math.max(width, lineWidth);  
                hight += lineHight;  
            }  
        }  
  
        setMeasuredDimension(  
                widthMode == MeasureSpec.EXACTLY ? sizeWidth : width,  
                hightMode == MeasureSpec.EXACTLY ? sizeHight : hight  
        );  
    }  
  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) {  
        for (View key : map.keySet()) {  
            ChildViewPosition position = map.get(key);  
            key.layout(position.left, position.top, position.right, position.bottom);  
        }  
  
    }  
  
    private class ChildViewPosition {  
        public ChildViewPosition(int left, int top, int right, int bottom) {  
            this.left = left;  
            this.right = right;  
            this.top = top;  
            this.bottom = bottom;  
        }  
  
        int left;  
        int right;  
        int top;  
        int bottom;  
    }  
}  