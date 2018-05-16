package com.gongwu.wherecollect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.MainActivity;
import com.gongwu.wherecollect.adapter.FilterCatagoryListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.FilterCategoryBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Function:
 * Date: 2017/8/28
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class MainDrawerView extends LinearLayout {
    Context context;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.empty)
    ErrorView empty;
    FilterCatagoryListAdapter adpter;
    String lastListStr;
    private List<FilterCategoryBean> filterList = new ArrayList<>();
    private String query;

    public MainDrawerView(Context context) {
        this(context, null);
    }

    public MainDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View.inflate(context, R.layout.layout_main_drawer, this);
        ButterKnife.bind(this);
        initList();
        //        getFilterList();
    }

    private void initList() {
        adpter = new FilterCatagoryListAdapter(context, filterList);
        listView.setAdapter(adpter);
    }

    /**
     * 获取筛选条件列表
     */
    public void getFilterList() {
        if (MyApplication.getUser(context) == null) {
            return;
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        PostListenner listenner = new PostListenner(context) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                if (lastListStr != null && lastListStr.equals(r.getResult())) {
                    return;
                }
                lastListStr = r.getResult();
                filterList.clear();
                List<FilterCategoryBean> temp = JsonUtils.listFromJson(r.getResult(), FilterCategoryBean.class);
                filterList.addAll(temp);
                adpter.notifyDataSetChanged();
                listView.setEmptyView(empty);
            }
        };
        HttpClient.getFilterList(context, map, listenner);
    }

    @OnClick({R.id.reset, R.id.summit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                for (int i = 0; i < filterList.size(); i++) {
                    filterList.get(i).getSelectSubs().clear();
                }
                adpter.notifyDataSetChanged();
                break;
            case R.id.summit:
                List<Map<String, Object>> temp = new ArrayList<>();
                for (int i = 0; i < filterList.size(); i++) {
                    for (int j = 0; j < filterList.get(i).getSelectSubs().size(); j++) {
                        String query = filterList.get(i).getSelectSubs().get(j).getQuery();
                        Map<String, Object> map = JsonUtils.mapFromJson(query);
                        temp.add(map);
                    }
                }
                if (temp.size() > 1) {
                    query = JsonUtils.jsonFromObject(temp);
                } else if (temp.size() == 1) {
                    query = JsonUtils.jsonFromObject(temp.get(0));
                } else {
                    query = "";
                }
                ((MainActivity) context).idDrawerlayout.closeDrawer(Gravity.RIGHT);
                EventBus.getDefault().post(EventBusMsg.OBJECT_FITLER);
                break;
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
