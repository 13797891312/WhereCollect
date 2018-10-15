package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MessageListAdapter;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.MessageBean;
import com.gongwu.wherecollect.entity.MessageGroupBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.swipetoloadlayout.OnLoadMoreListener;
import com.gongwu.wherecollect.swipetoloadlayout.OnRefreshListener;
import com.gongwu.wherecollect.swipetoloadlayout.SwipeToLoadLayout;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhaojin.myviews.Loading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 消息中心
 */
public class MessageListActivity extends BaseViewActivity implements MyOnItemClickListener, OnRefreshListener, OnLoadMoreListener {

    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @Bind(R.id.swipe_target)
    RecyclerView mRecyclerView;

    private int page;
    private List<MessageBean> datas = new ArrayList<>();
    private MessageListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("消息中心");
        initView();
        initEvent();
        mSwipeToLoadLayout.setRefreshing(true);
    }

    private void initEvent() {
        mAdapter.setOnItemClickListener(this);
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MessageListAdapter(context, datas);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int positions, View view) {

    }

    /**
     * 获取消息列表
     */
    public void getData() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("page", "" + page);
        map.put("type", "1");
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List<MessageBean> temp = JsonUtils.listFromJsonWithSubKey(r.getResult(), MessageBean.class, "items");
                if (page == 1) {//如果是第一页就缓存下
                    datas.clear();
                } else {
                    if (StringUtils.isEmpty(temp)) {
                        page = 1;
                        Toast.makeText(context, "没有更多数据了", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                datas.addAll(temp);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                closeLoading(mSwipeToLoadLayout);
            }
        };
        HttpClient.getMessageList(context, map, listenner);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MessageListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }

    @Override
    public void onLoadMore() {
        page++;
        getData();
    }
}
