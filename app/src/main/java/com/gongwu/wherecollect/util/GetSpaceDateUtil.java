package com.gongwu.wherecollect.util;
import android.app.Activity;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;

import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Function:
 * Date: 2018/1/23
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class GetSpaceDateUtil {
    public void getSpaceData(final Activity activity, String uid) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", uid);
        PostListenner listenner = new PostListenner(activity) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List<ObjectBean> temp = JsonUtils.listFromJson(r.getResult(), ObjectBean.class);
                SaveDate.getInstence(activity).setSpace(r.getResult());
                MainLocationFragment.mlist.clear();
                MainLocationFragment.mlist.addAll(temp);
                getResult(temp);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                GetSpaceDateUtil.this.onFinish();
            }
        };
        HttpClient.getLocationList(activity, map, listenner);
    }

    /**
     * 用来回调
     *
     * @param temp
     */
    protected void getResult(List<ObjectBean> temp) {
    }

    /**
     * 用来回调
     */
    protected void onFinish() {
    }
}
