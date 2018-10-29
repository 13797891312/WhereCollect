package com.gongwu.wherecollect.afragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.ShareSpaceDetailsActivity;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.adapter.ShareSpaceListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.entity.SharedLocationBean;
import com.gongwu.wherecollect.swipetoloadlayout.OnLoadMoreListener;
import com.gongwu.wherecollect.swipetoloadlayout.OnRefreshListener;
import com.gongwu.wherecollect.swipetoloadlayout.SwipeToLoadLayout;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShareSpaceFragment extends BaseFragment implements OnRefreshListener, MyOnItemClickListener {

    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @Bind(R.id.swipe_target)
    RecyclerView mRecyclerView;

    private View view;
    private ShareSpaceListAdapter mAdapter;
    private boolean init;
    private List<SharedLocationBean> datas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_share_space, container, false);
        }
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        initEvent();
        return view;
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new ShareSpaceListAdapter(getContext(), datas) {
            @Override
            public void closeClick(int position) {
                final SharedLocationBean locationBean = datas.get(position);
                DialogUtil.show("", "确定断开与【" + locationBean.getName() + "】与全部共享人的共享?\n(断开后属于共享空间的非本人添加的物品也将被清空)", "确定", "取消", getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        closeShareUser(locationBean.getCode());
                    }
                }, null);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEvent() {
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        initData();
    }

    private void initData() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", MyApplication.getUser(getContext()).getId());
        PostListenner listener = new PostListenner(getContext()) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                datas.clear();
                List<SharedLocationBean> beans = JsonUtils.listFromJson(r.getResult(), SharedLocationBean.class);
                if (beans != null && beans.size() > 0) {
                    datas.addAll(beans);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                closeLoading(mSwipeToLoadLayout);
            }
        };
        HttpClient.getAllSharedLocations(getContext(), params, listener);
    }

    private void closeShareUser(String location_id) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", MyApplication.getUser(getContext()).getId());
        params.put("location_id", location_id);
        params.put("type", "0");
        PostListenner listener = new PostListenner(getContext()) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                EventBus.getDefault().post(new EventBusMsg.updateShareMsg());
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.closeShareUser(getContext(), params, listener);
    }

    @Override
    public void onShow() {
        if (!init) {
            if (mSwipeToLoadLayout != null) {
                mSwipeToLoadLayout.setRefreshing(true);
            }
            init = true;
        }
    }

    @Override
    public void refreshFragment() {
        mSwipeToLoadLayout.setRefreshing(true);
    }

    @Override
    public void onItemClick(int positions, View view) {
        SharedLocationBean locationBean = datas.get(positions);
        Intent intent = new Intent(getContext(), ShareSpaceDetailsActivity.class);
        intent.putExtra("locationBean", locationBean);
        getActivity().startActivityForResult(intent, 104);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.updateShareMsg msg) {
        mSwipeToLoadLayout.setRefreshing(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
