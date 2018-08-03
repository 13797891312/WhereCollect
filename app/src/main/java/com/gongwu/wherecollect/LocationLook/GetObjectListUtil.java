package com.gongwu.wherecollect.LocationLook;

import android.content.Context;
import android.text.TextUtils;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;

import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Function:
 * Date: 2018/1/17
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class GetObjectListUtil {
    Context context;

    public GetObjectListUtil(Context context) {
        this.context = context;
    }

    /**
     * 先取缓存，再从网络获取物品列表
     */
    public void getObjectList(final String code) {
        List<ObjectBean> list = MainLocationFragment.objectMap.get(code);
        if (list == null) {
            final String cache = SaveDate.getInstence(context).getObjectWithCode(code);
            if (!TextUtils.isEmpty(cache)&&!cache.equals("[]")&&cache.contains("items")) {
                String json = cache.replaceAll("\"channel\"",
                        "\"channels\"").replaceAll("\"color\"", "\"colors\"");
                list = JsonUtils.listFromJsonWithSubKey(json, ObjectBean.class, "items");
                MainLocationFragment.objectMap.put(code, list);
                getObjectListDataListener(list);
            }
            getNetObjectData(cache, code);
        } else {
            getObjectListDataListener(list);
        }
    }

    private int page = 1;
    private List<ObjectBean> datas = new ArrayList<>();

    /**
     * 网络获取物品总览
     *
     * @param cache
     * @param code
     */
    public void getNetObjectData(final String cache, final String code) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("user_id", MyApplication.getUser(context).getId());
        map.put("code", code);
        map.put("page", page + "");
        PostListenner listenner = new PostListenner(context) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                if (r.getResult().equals(cache)) {//如果和缓存一样则不做处理
                    return;
                }
                List<ObjectBean> list = JsonUtils.listFromJsonWithSubKey(r.getResult().replaceAll("\"channel\"",
                        "\"channels\"").replaceAll("\"color\"", "\"colors\""), ObjectBean.class, "items");
                if (page == 1) {
                    datas.clear();
                    datas.addAll(list);
                    if (list.size() == 50) {
                        page = page + 1;
                        getNetObjectData(cache, code);
                    } else {
                        SaveDate.getInstence(context).setObjectWithCode(code, JsonUtils.jsonFromObject(datas));
                        MainLocationFragment.objectMap.put(code, datas);
                        getObjectListDataListener(datas);
                    }
                } else {
                    datas.addAll(list);
                    if (list.size() == 50) {
                        page = page + 1;
                        getNetObjectData(cache, code);
                    } else {
                        SaveDate.getInstence(context).setObjectWithCode(code, JsonUtils.jsonFromObject(datas));
                        MainLocationFragment.objectMap.put(code, datas);
                        getObjectListDataListener(datas);
                    }
                }

            }
        };
        HttpClient.getObjectListWithSpaceCode(context, map, listenner);
    }

    /**
     * 获取到物品总览数据后回调
     */
    protected void getObjectListDataListener(List<ObjectBean> list) {
    }
}
