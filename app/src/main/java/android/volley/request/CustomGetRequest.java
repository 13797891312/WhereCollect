package android.volley.request;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.gongwu.wherecollect.util.LogUtil;

import java.util.Map;
/**
 * Function: 自定义网络请求request类
 * Date: 2016-08-20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class CustomGetRequest extends StringRequest {
    public CustomGetRequest(String func, Map<String, String> map, PostListenner listener) {
        super(Method.GET, getMethedUrl(func, map), listener.listener, listener.errorListener);
        this.setRetryPolicy(new DefaultRetryPolicy(
                20000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                0,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
    }

    public static String getMethedUrl(String func, Map<String, String> map) {
        String url;
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            sb.append(key).append("=");
            try {
                sb.append(map.get(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
            sb.append("&");
        }
        if (!TextUtils.isEmpty(sb.toString())) {
            sb.delete(sb.length() - 1, sb.length());
        }
        url = HttpClient.url + func + "?" + sb.toString();
        LogUtil.e("url", url);
        return url;
    }
}
