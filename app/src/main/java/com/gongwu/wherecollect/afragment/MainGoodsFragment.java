package com.gongwu.wherecollect.afragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.AddChangWangGoodActivity;
import com.gongwu.wherecollect.activity.MainActivity;
import com.gongwu.wherecollect.adapter.GoodsMainGridViewAdapter;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ChangWangBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.ShareUserBean;
import com.gongwu.wherecollect.object.ObjectLookInfoActivity;
import com.gongwu.wherecollect.quickadd.QuickSpaceSelectListActivity;
import com.gongwu.wherecollect.util.AppConstant;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.ErrorView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainGoodsFragment extends BaseFragment implements MyOnItemClickListener {
    View view;
    @Bind(R.id.empty)
    ErrorView empty;
    @Bind(R.id.empty_good_layout)
    View emptyGoodLayout;
    @Bind(R.id.add_changwang_tv)
    TextView addCWGoodView;
    @Bind(R.id.empty_img)
    ImageView empty_img;
    @Bind(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;
    @Bind(R.id.swipe_target)
    RecyclerView mRecyclerView;

    int page = 1;
    private String changWangCode, goodType;
    private GoodsMainGridViewAdapter gridViewAdapter;
    private List<ObjectBean> mList = new ArrayList<>();
    private List<ShareUserBean> shareUserBeans = new ArrayList<>();


    public MainGoodsFragment() {
    }

    public static MainGoodsFragment newInstance() {
        MainGoodsFragment fragment = new MainGoodsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_fragment1_goods, container, false);
        ButterKnife.bind(this, view);
        String cache = SaveDate.getInstence(getActivity()).getObjectList();//取缓存
        if (TextUtils.isEmpty(cache)) {//没有缓存
            getData(true);
        } else {
            mList.addAll(JsonUtils.listFromJson(cache, ObjectBean.class));
            getData(false);
        }
        //判断常忘物品
        if (!MyApplication.getUser(getContext()).isTest()) {
            getCangWangList();
        }
        EventBus.getDefault().register(this);
        empty_img.setVisibility(View.VISIBLE);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        gridViewAdapter = new GoodsMainGridViewAdapter(getActivity(), mList);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(gridViewAdapter);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout mRefreshLayout) {
                page = AppConstant.DEFAULT_PAGE;
                getData(false);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout mRefreshLayout) {
                page++;
                getData(false);
            }
        });
        gridViewAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.empty_good_layout, R.id.add_changwang_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.empty_good_layout:
                if (!TextUtils.isEmpty(changWangCode)) {
                    AddChangWangGoodActivity.start(getContext(), goodType, changWangCode);
                }else{
                    Intent intent = new Intent(getActivity(), QuickSpaceSelectListActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.add_changwang_tv:
                AddChangWangGoodActivity.start(getContext(), goodType, changWangCode);
                break;
            default:
                break;
        }
    }

    /**
     * 获取物品列表
     */
    public void getData(boolean isShowDialog) {
        if (MyApplication.getUser(getActivity()) == null) return;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(getActivity()).getId());
        map.put("query", ((MainActivity) getActivity()).filterView.getQuery());
        map.put("page", "" + page);
        PostListenner listenner = new PostListenner(getActivity(), isShowDialog ? Loading.show(null, getActivity(), "正在加载") : null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                mRefreshLayout.finishRefresh(true);
                mRefreshLayout.finishLoadMore(true);
                List temp = JsonUtils.listFromJsonWithSubKey(r.getResult(), ObjectBean.class, "items");
                List shareUserdata = JsonUtils.listFromJsonWithSubKey(r.getResult(), ShareUserBean.class, "users");
                if (page == 1) {//如果是第一页就缓存下
                    SaveDate.getInstence(getActivity()).setObjectList(JsonUtils.jsonFromObject(temp));
                    mList.clear();
                } else {
                    if (StringUtils.isEmpty(temp)) {
                        Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                    }
                }
                mList.addAll(temp);
                shareUserBeans.clear();
                shareUserBeans.addAll(shareUserdata);
                gridViewAdapter.notifyDataSetChanged();
                setViewEmpty();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mRefreshLayout.finishRefresh(true);
                mRefreshLayout.finishLoadMore(true);
            }
        };
        HttpClient.getGoodsList(getActivity(), map, listenner);
    }

    //获取常忘物品list
    private void getCangWangList() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(getActivity()).getId());
        PostListenner listenner = new PostListenner(getActivity(), null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List<ChangWangBean> changWangBeans = JsonUtils.listFromJson(r.getResult(), ChangWangBean.class);
                addChangWangData(changWangBeans);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.getCangWangList(getActivity(), map, listenner);
    }

    //判断是否引导添加常忘物品
    private void addChangWangData(List<ChangWangBean> changWangBeans) {
        if (changWangBeans != null && changWangBeans.size() > 0) {
            ChangWangBean aiWangBean = changWangBeans.get(0);
            //name=爱忘爆款
            if (aiWangBean.getObject_count() > aiWangBean.getComplete()) {
                goodType = aiWangBean.getName();
                changWangCode = aiWangBean.getCode();
                addCWGoodView.setVisibility(View.VISIBLE);
            } else if (changWangBeans.size() > 1) {
                //name=热门备余物
                ChangWangBean reMenBean = changWangBeans.get(1);
                if (reMenBean.getObject_count() > reMenBean.getComplete()) {
                    goodType = reMenBean.getName();
                    changWangCode = reMenBean.getCode();
                    addCWGoodView.setVisibility(View.VISIBLE);
                } else {
                    changWangCode = null;
                }
            }
        }
        setViewEmpty();
    }

    private void setViewEmpty() {
        if (mList.size() > 0) {
            addCWGoodView.setVisibility(!TextUtils.isEmpty(changWangCode) ? View.VISIBLE : View.GONE);
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
            addCWGoodView.setVisibility(View.GONE);
            emptyGoodLayout.setVisibility(!TextUtils.isEmpty(changWangCode) ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 需要刷新列表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String string) {
        if (EventBusMsg.OBJECT_CHANGE.equals(string) || EventBusMsg.OBJECT_FITLER.equals(string)) {
            page = 0;
            if (mRefreshLayout != null) {
                mRefreshLayout.autoRefresh();
            }
            if (EventBusMsg.OBJECT_CHANGE.equals((string))) {
                ((MainActivity) getActivity()).filterView.getFilterList();
            }
        }
    }

    //更换了账号
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ChangeUser msg) {
        page = 0;
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
        changWangCode = null;
        if (!MyApplication.getUser(getContext()).isTest()) {
            getCangWangList();
        }
        ((MainActivity) getActivity()).filterView.getFilterList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.updateShareMsg msg) {
        if (addCWGoodView != null) {
            addCWGoodView.setVisibility(View.GONE);
        }
        changWangCode = null;
        page = 0;
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
        if (!MyApplication.getUser(getContext()).isTest()) {
            getCangWangList();
        }
    }

    @Override
    public void onShow() {
        if (addCWGoodView != null) {
            addCWGoodView.setVisibility(View.GONE);
        }
        changWangCode = null;
        page = 0;
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
        if (!MyApplication.getUser(getContext()).isTest()) {
            getCangWangList();
        }
    }

    @Override
    public void onItemClick(int position, View view) {
        ObjectBean objectBean = mList.get(position);
        Intent intent = new Intent(getActivity(), ObjectLookInfoActivity.class);
        intent.putExtra("bean", objectBean);
        if (objectBean.getIs_share() == 1 && objectBean.getShare_users() != null && objectBean.getShare_users().size() > 0) {
            List<String> share_users = objectBean.getShare_users();
            List<ShareUserBean> shareUserData = new ArrayList<>();
            for (int a = 0; a < share_users.size(); a++) {
                String shareUserId = share_users.get(a);
                for (int b = 0; b < shareUserBeans.size(); b++) {
                    ShareUserBean bean = shareUserBeans.get(b);
                    if (bean.get_id().equals(shareUserId)) {
                        shareUserData.add(bean);
                    }
                }
            }
            intent.putExtra("shareUsers", (Serializable) shareUserData);
        }
        startActivity(intent);
    }
}
