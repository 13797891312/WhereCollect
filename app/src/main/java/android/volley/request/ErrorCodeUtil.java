package android.volley.request;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
/**
 * Created by zhaojin on 2016/8/19.
 */
public class ErrorCodeUtil {
    public static void toastError(Context context, int code, String msg) {
        switch (code) {
            case 2000:
                return;
            case 20007:
                Toast.makeText(context, "密码错误", Toast.LENGTH_LONG).show();
                return;
            case 1000://token失效
            case 1001://token失效
            case 1002://token失效
            case 1003://token失效
            case 1004://token失效
            case 1005://token失效
            case 1006://token失效
                Toast.makeText(context, "非法请求", Toast.LENGTH_LONG).show();
                return;
            case 1007://token失效
            case 1008://token失效
                Toast.makeText(context, "登录失效，请重新登录", Toast.LENGTH_LONG).show();
                return;
        }
        if (TextUtils.isEmpty(msg)) {
            msg = "暂无数据";
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
