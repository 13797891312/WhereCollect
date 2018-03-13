package com.zhaojin.myviews;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
/**
 * Hacky fix for Issue #4 and
 * http://code.google.com/p/android/issues/detail?id=18990
 * <p>
 * ScaleGestureDetector seems to mess up the touch events, which means that
 * ViewGroups which make use of onInterceptTouchEvent throw a lot of
 * IllegalArgumentException: pointerIndex out of range.
 * <p>
 * There's not much I can do in my code for now, but we can mask the result by
 * just catching the problem and ignoring it.
 *
 * @author Chris Banes
 */
public class HackyViewPager extends ViewPager {
    public static boolean isItercept = false;//是否下放触摸事件，false为不下放，true为强制下放事件

    public HackyViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if (!isItercept) {
                return super.onInterceptTouchEvent(ev);
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
