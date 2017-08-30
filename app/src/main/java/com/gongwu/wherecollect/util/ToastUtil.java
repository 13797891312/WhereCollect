package com.gongwu.wherecollect.util;
import android.content.Context;
import android.widget.Toast;
/**
 * Function:
 * Date: 2017/8/29
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ToastUtil {
    public static void show(Context context, String text, int time) {
        Toast.makeText(context, text, time).show();
    }
}
