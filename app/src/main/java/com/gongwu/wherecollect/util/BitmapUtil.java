package com.gongwu.wherecollect.util;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
/**
 * Function:
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class BitmapUtil {
    public static Bitmap viewToBitmap(View v) {
        int h = v.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap
                .Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }
}
