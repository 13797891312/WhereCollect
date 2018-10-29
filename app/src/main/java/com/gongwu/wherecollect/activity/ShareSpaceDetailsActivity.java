package com.gongwu.wherecollect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.adapter.ShareSpaceDetailsListAdapter;
import com.gongwu.wherecollect.adapter.ShareSpaceListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.entity.SharedLocationBean;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.swipetoloadlayout.OnRefreshListener;
import com.gongwu.wherecollect.swipetoloadlayout.SwipeToLoadLayout;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 共享空间详情
 */
public class ShareSpaceDetailsActivity extends BaseViewActivity implements OnRefreshListener, MyOnItemClickListener {

    private final int START_CODE = 0x179;

    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @Bind(R.id.swipe_target)
    RecyclerView mRecyclerView;
    @Bind(R.id.add_share_tv)
    TextView addShareTv;

    private List<SharePersonBean> datas = new ArrayList<>();
    private List<SharedLocationBean> locationBeans = new ArrayList<>();
    private ShareSpaceDetailsListAdapter mAdapter;
    private SharedLocationBean locationBean;
    private String content;
    private SharePersonBean managerUser;
    private UserBean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_space_details);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        user = MyApplication.getUser(this);
        initView();
        initEvent();
    }

    private void initView() {
        locationBean = (SharedLocationBean) getIntent().getSerializableExtra("locationBean");
        titleLayout.setTitle(locationBean.getName() + ">共享详情");
        titleLayout.setBack(true, null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ShareSpaceDetailsListAdapter(this, datas, user.getId()) {
            @Override
            public void closeClick(final int position) {
                DialogUtil.show("", "确定断开【" + locationBean.getName() + "】的共享?\n(断开后属于共享空间的非本人添加的物品也将被清空)", "确定", "取消", ShareSpaceDetailsActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        closeShareSpaceHttp(position);
                    }
                }, null);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEvent() {
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setRefreshing(true);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        initData();
    }

    private void initData() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", user.getId());
        PostListenner listener = new PostListenner(this) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                datas.clear();
                locationBeans.clear();
                content = "";
                List<SharedLocationBean> beans = JsonUtils.listFromJson(r.getResult(), SharedLocationBean.class);
                if (beans != null && beans.size() > 0) {
                    for (int i = 0; i < beans.size(); i++) {
                        content += beans.get(i).getName() + " ";
                        if (beans.get(i).getCode().equals(locationBean.getCode())) {
                            datas.addAll(beans.get(i).getShared_users());
                            managerUser = beans.get(i).getUser();
                            if (user.getId().equals(managerUser.getId())) {
                                addShareTv.setVisibility(View.VISIBLE);
                            }
                            if (mAdapter != null) {
                                mAdapter.setManager(managerUser);
                            }
                        }
                    }
                }
                if (datas.size() > 0) {
                    locationBeans.addAll(beans);
                    mAdapter.setContent(content);
                    mAdapter.notifyDataSetChanged();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                closeLoading(mSwipeToLoadLayout);
            }
        };
        HttpClient.getAllSharedLocations(this, params, listener);
    }

    /**
     * 断开连接
     */
    private void closeShareSpaceHttp(int position) {
        final SharePersonBean personBean = datas.get(position);
        Map<String, String> params = new HashMap<>();
        params.put("uid", user.getId());
        params.put("be_shared_user_id", personBean.getUid());
        params.put("type", "0");
        params.put("location_id", locationBean.getCode());
        PostListenner listener = new PostListenner(this) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                EventBus.getDefault().post(new EventBusMsg.updateShareMsg());
                if (personBean.getUid().equals(user.getId())) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    mSwipeToLoadLayout.setRefreshing(true);
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.closeShareUser(this, params, listener);
    }

    @Override
    public void onItemClick(int positions, View view) {
        //获取点击的是哪个item用户 如果是自己则提示
        SharePersonBean sharePersonBean = datas.get(positions);
        if (!sharePersonBean.getId().equals(user.getId())) {
            sharePersonBean.setShared_locations(locationBeans);
            Intent intent = new Intent(context, SharePersonDetailsActivity.class);
            intent.putExtra("sharePersonBean", sharePersonBean);
            startActivityForResult(intent, 106);
        } else {
            DialogUtil.show("", "请在“我的-共享管理”中管理个人共享信息", "好的", "", this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }, null);
        }
    }

    @OnClick({R.id.add_share_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_share_tv:
                Intent intent = new Intent(this, AddSharePersonActivity.class);
                intent.putExtra("locationBean", locationBean);
                startActivityForResult(intent, START_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == RESULT_OK) {
            mSwipeToLoadLayout.setRefreshing(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.updateShareMsg msg) {
        mSwipeToLoadLayout.setRefreshing(true);
    }
}
