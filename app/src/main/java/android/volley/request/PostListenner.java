package android.volley.request;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.zhaojin.myviews.Loading;

import org.json.JSONObject;

/**
 * Created by zhaojin on 2016/7/22.
 */
public class PostListenner {
    public Listener<String> listener;
    public Response.ErrorListener errorListener;
    private Loading loading;
    Context context;

    public PostListenner(Context context) {
        this(context, null);
    }

    public PostListenner(final Context context, final Loading loading) {
        this.context = context;
        this.loading = loading;
        this.errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    error();
//                    Toast.makeText(context, VolleyErrorHelper.getMessage(error, context),
//                            Toast
//                                    .LENGTH_SHORT).show();
                    PostListenner.this.onFinish();
                    dissLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                    dissLoading();
                }
            }
        };
        this.listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    PostListenner.this.onSuccess(response);
                    LogUtil.e(response);
                    JSONObject object = null;
                    object = new JSONObject(response);
                    ResponseResult rr = JsonUtils.objectFromJson(response, ResponseResult.class);
                    try {
                        rr.setResult(object.getString("data"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LogUtil.i("i", rr.getResult());
                    dissLoading();
                    if (rr.getCode().equals("ERROR")){
                        codeOther(rr);
                        return;
                    }
                    if (Integer.valueOf(rr.getCode()) == 200) {
                        code2000(rr);
                    } else {
//                        ErrorCodeUtil.toastError(context, Integer.valueOf(rr.getCode()), rr
//                                .getMsg());
                        codeOther(rr);
                    }
                } catch (Exception e) {
                    codeOther(null);
                    e.printStackTrace();
                    dissLoading();
                }
            }
        };
    }

    private void dissLoading() {
        if (loading != null) {
            try {
                loading.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void code2000(ResponseResult r) {
    }

    protected void error() {
    }

    protected void codeOther(ResponseResult r) {
        try {
            onFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onFinish() {
    }

    protected void onSuccess(String s) {
        onFinish();
    }
}
