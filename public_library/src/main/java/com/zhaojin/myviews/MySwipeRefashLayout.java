package com.zhaojin.myviews;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

import com.example.zj_library.R;
public class MySwipeRefashLayout extends SwipeRefreshLayout {
    private MyOnRefreshListener refreshListener;
    private View mScrollableChild;

    public MySwipeRefashLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySwipeRefashLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void setListener(MyOnRefreshListener listener) {
        this.refreshListener = listener;
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (refreshListener != null) {
                    refreshListener.onRefresh();
                }
            }
        });
    }

    public void init() {
        setColorSchemeResources(R.color.blue);
        mScrollableChild = getChildAt(0);
    }

    @Override
    public boolean canChildScrollUp() {
        // TODO Auto-generated method stub
        return super.canChildScrollUp();
//        if (mScrollableChild == null) {
//            return false;
//        }
//        if (android.os.Build.VERSION.SDK_INT < 14) {
//            if (mScrollableChild instanceof AbsListView) {
//                final AbsListView absListView = (AbsListView) mScrollableChild;
//                return absListView.getChildCount() > 0
//                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
//                        .getTop() < absListView.getPaddingTop());
//            } else {
//                return super.canChildScrollUp();
//            }
//        } else {
//            return super.canChildScrollUp();
//        }
    }

    public static interface MyOnRefreshListener {
        public void onRefresh();
    }
}
