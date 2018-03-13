package com.gongwu.wherecollect.record;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.adapter.RoomRecordListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.RoomRecordBean;
import com.gongwu.wherecollect.util.ACacheClient;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.ErrorView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhaojin.myviews.Loading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
public class RecordListActivity extends BaseViewActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.listView)
    PullToRefreshListView listView;
    @Bind(R.id.empty)
    ErrorView empty;
    List<RoomRecordBean> mList = new ArrayList<>();
    private int page = 1;
    private RoomRecordListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("室迹通览");
        initView();
    }

    private void initView() {
        String cache = ACacheClient.getRecordList(this, MyApplication.getUser(this).getId());//取缓存
        if (TextUtils.isEmpty(cache)) {//没有缓存
            getData(true);
        } else {
            mList.addAll(JsonUtils.listFromJson(cache, RoomRecordBean.class));
            getData(false);
        }
        initListView();
    }

    private void initListView() {
        mAdapter = new RoomRecordListAdapter(this, mList);
        listView.setAdapter(mAdapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getData(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData(false);
            }
        });
    }

    /**
     * 获取物品列表
     */
    public void getData(boolean isShowDialog) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("page", "" + page);
        PostListenner listenner = new PostListenner(this, isShowDialog ? Loading.show(null, this,
                "正在加载") : null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List temp = JsonUtils.listFromJsonWithSubKey(r.getResult(), RoomRecordBean.class, "items");
                if (page == 1) {//如果是第一页就缓存下
                    ACacheClient.setRecordList(context, MyApplication.getUser(context).getId(), JsonUtils.jsonFromObject
                            (temp));
                    mList.clear();
                } else {
                    if (StringUtils.isEmpty(temp)) {
                        Toast.makeText(context, "没有更多数据了", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                mList.addAll(temp);
                mAdapter.notifyDataSetChanged();
                listView.setEmptyView(empty);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                listView.onRefreshComplete();
            }
        };
        HttpClient.getRecordList(context, map, listenner);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, RecordLookActivity.class);
        intent.putExtra("bean", mList.get(position-1));
        startActivity(intent);
    }
}
