package com.gongwu.wherecollect.afragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.SharePersonDetailsActivity;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.adapter.SharePersonListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.swipetoloadlayout.OnRefreshListener;
import com.gongwu.wherecollect.swipetoloadlayout.SwipeToLoadLayout;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.view.CloseShareDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 共享人
 */
public class SharePersonFragment extends BaseFragment implements OnRefreshListener, MyOnItemClickListener {

    private final int START_CODE = 102;

    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @Bind(R.id.swipe_target)
    RecyclerView mRecyclerView;

    private View view;
    private boolean init;
    private List<SharePersonBean> datas = new ArrayList<>();
    private SharePersonListAdapter mAdapter;
    private CloseShareDialog closeShareDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_share_person, container, false);
        }
        ButterKnife.bind(this, view);
        initView();
        initEvent();
        return view;
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new SharePersonListAdapter(getContext(), datas) {
            @Override
            public void closeClick(int position) {
                startDialog(position);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    private void startDialog(final int position) {

        closeShareDialog = new CloseShareDialog(getActivity()) {
            @Override
            public void saveData() {
                closeShareUser(datas.get(position).getId(), "1");
            }

            @Override
            public void deleteData() {
                closeShareUser(datas.get(position).getId(), "0");
            }
        };
    }

    private void closeShareUser(String shareUserId, String type) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", MyApplication.getUser(getContext()).getId());
        params.put("be_shared_user_id", shareUserId);
        params.put("type", type);
        PostListenner listener = new PostListenner(getContext()) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                if (mSwipeToLoadLayout != null) {
                    mSwipeToLoadLayout.setRefreshing(true);
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.closeShareUser(getContext(), params, listener);
    }

    private void initEvent() {
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        getSharePersonList();
    }

    private void getSharePersonList() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", MyApplication.getUser(getContext()).getId());
        PostListenner listener = new PostListenner(getContext()) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                datas.clear();
                List<SharePersonBean> beans = JsonUtils.listFromJson(r.getResult(), SharePersonBean.class);
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
        HttpClient.getAllSharedUsers(getContext(), params, listener);
    }

    @Override
    public void onItemClick(int positions, View view) {
        Intent intent = new Intent(getContext(), SharePersonDetailsActivity.class);
        intent.putExtra("sharePersonBean", datas.get(positions));
        getActivity().startActivityForResult(intent, 102);
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

}
