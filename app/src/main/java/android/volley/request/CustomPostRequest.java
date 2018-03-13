package android.volley.request;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.gongwu.wherecollect.BuildConfig;
import com.gongwu.wherecollect.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Function: 自定义网络请求request类
 * Date: 2016-08-20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class CustomPostRequest extends StringRequest {
    Map<String, String> map;

    public CustomPostRequest(String func, Map<String, String> params,
                             PostListenner listener) {
        super(Method.POST, HttpClient.url + func, listener.listener,listener.errorListener);
        List<String> nullKey = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (TextUtils.isEmpty(entry.getValue())) {
                nullKey.add(entry.getKey());
            }
        }
        for (String key : nullKey) {
            params.remove(key);
        }
        this.map = params;
        this.setRetryPolicy(new DefaultRetryPolicy(
                20000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                0,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        if (BuildConfig.LOGSHOW) {
            LogUtil.e(HttpClient.url + func);
            StringBuffer sb = new StringBuffer();
            Set<String> key = params.keySet();
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sb.append(entry.getKey());
                    sb.append("=");
                    sb.append(entry.getValue() == null ? "null" : entry
                            .getValue());
                    sb.append("&");
                }
                if (sb.length() > 0) {
                    sb.delete(sb.length() - 1, sb.length());
                }
                LogUtil.e(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Map<String, String> getParams() {
        return map;
    }
}

