package com.gongwu.wherecollect.util;
/**
 * Function: 防止按钮快速点击的工具类
 * Date: 2016-08-20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class NoFastClickUtils {
    private static long lastClickTime = 0;//上次点击的时间
    private static int spaceTime = 500;//时间间隔

    public static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();//当前系统时间
        boolean isAllowClick;//是否允许点击
        if (currentTime - lastClickTime > spaceTime) {
            isAllowClick = false;
        } else {
            isAllowClick = true;
        }
        lastClickTime = currentTime;
        return isAllowClick;
    }
}