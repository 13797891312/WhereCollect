package com.gongwu.wherecollect.util;
import android.content.Context;
import android.view.Gravity;
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
    public static void showTopToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 180);
        toast.show();
    }
}
