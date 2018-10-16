package com.gongwu.wherecollect.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.ShareSpaceDetailsListAdapter;
import com.gongwu.wherecollect.adapter.ShareSpaceListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.entity.SharedLocationBean;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.swipetoloadlayout.OnRefreshListener;
import com.gongwu.wherecollect.swipetoloadlayout.SwipeToLoadLayout;
import com.gongwu.wherecollect.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 共享空间详情
 */
public class ShareSpaceDetailsActivity extends BaseViewActivity implements OnRefreshListener {

    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @Bind(R.id.swipe_target)
    RecyclerView mRecyclerView;

    private List<SharePersonBean> datas = new ArrayList<>();
    private ShareSpaceDetailsListAdapter mAdapter;
    private SharedLocationBean locationBean;
    private String context;
    private UserBean manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_space_details);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initView() {
        locationBean = (SharedLocationBean) getIntent().getSerializableExtra("locationBean");
        titleLayout.setTitle(locationBean.getName() + ">共享详情");
        titleLayout.setBack(true, null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ShareSpaceDetailsListAdapter(this, datas, manager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEvent() {
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setRefreshing(true);
    }

    @Override
    public void onRefresh() {
        initData();
    }

    private void initData() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", MyApplication.getUser(this).getId());
        PostListenner listener = new PostListenner(this) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                datas.clear();
                List<SharedLocationBean> beans = JsonUtils.listFromJson(r.getResult(), SharedLocationBean.class);
                if (beans != null && beans.size() > 0) {
                    for (int i = 0; i < beans.size(); i++) {
                        context += beans.get(i).getName() + " ";
                        if (beans.get(i).getCode().equals(locationBean.getCode())) {
                            datas.addAll(beans.get(i).getShared_users());
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                closeLoading(mSwipeToLoadLayout);
            }
        };
        HttpClient.getAllSharedLocations(this, params, listener);
    }
}
