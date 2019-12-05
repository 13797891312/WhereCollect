package com.gongwu.wherecollect.afragment;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.EditRemindActivity;
import com.gongwu.wherecollect.adapter.OnRemindItemClickListener;
import com.gongwu.wherecollect.adapter.RemindListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.RemindBean;
import com.gongwu.wherecollect.entity.RemindListBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.AppConstant;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.util.iconNum.SendIconNumUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 3.6提醒列表
 */
public class RemindFragment extends BaseFragment {

    static {
        ClassicsHeader.REFRESH_HEADER_PULLING = "下拉可以刷新";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = "正在刷新...";
        ClassicsHeader.REFRESH_HEADER_LOADING = "正在加载...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = "释放立即刷新";
        ClassicsHeader.REFRESH_HEADER_FINISH = "刷新完成";
        ClassicsHeader.REFRESH_HEADER_FAILED = "刷新失败";
        ClassicsHeader.REFRESH_HEADER_SECONDARY = "释放进入二楼";
        ClassicsHeader.REFRESH_HEADER_UPDATE = "上次更新 M-d HH:mm";
        ClassicsFooter.REFRESH_FOOTER_PULLING = "上拉加载更多";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新...";
        ClassicsFooter.REFRESH_FOOTER_LOADING = "正在加载...";
        ClassicsFooter.REFRESH_FOOTER_FINISH = "加载完成";
        ClassicsFooter.REFRESH_FOOTER_FAILED = "加载失败";
        ClassicsFooter.REFRESH_FOOTER_NOTHING = "没有更多数据了";
    }

    private static final String CODE_UNFINISH = "0";
    private static final String CODE_FINISHED = "1";
    private static final int START_CODE = 0x981;

    @Bind(R.id.remind_unfinish_layout)
    LinearLayout unfinishLayout;
    @Bind(R.id.remind_finished_layout)
    LinearLayout finishedLayout;
    @Bind(R.id.remind_unfinish_title_layout)
    RelativeLayout unfinishTitleLayout;
    @Bind(R.id.remind_finished_title_layout)
    RelativeLayout finishedTitleLayout;
    @Bind(R.id.remind_unfinish_recycler_view)
    RecyclerView unfinishListView;
    @Bind(R.id.remind_finished_recycler_view)
    RecyclerView finishedListView;
    @Bind(R.id.remind_unfinish_num_text_view)
    TextView unfinishNumTv;
    @Bind(R.id.remind_finished_num_text_view)
    TextView finishedNumTv;
    @Bind(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;
    @Bind(R.id.remind_un_list_layout)
    RelativeLayout unListLayout;
    @Bind(R.id.remind_list_layout)
    RelativeLayout listLayout;
    @Bind(R.id.empty_un_iv)
    ImageView emptyUnIv;
    @Bind(R.id.empty_iv)
    ImageView emptyIv;

    private RemindListAdapter mUnAdapter;
    private RemindListAdapter mAdapter;

    private View view;
    private String done = CODE_UNFINISH;
    private int page = AppConstant.DEFAULT_PAGE;
    private boolean init, loading;
    private List<RemindBean> mUnData = new ArrayList<>();
    private List<RemindBean> mData = new ArrayList<>();

    public RemindFragment() {
    }

    public static RemindFragment newInstance() {
        RemindFragment fragment = new RemindFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_remind, container, false);
        }
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        initEvent();
        if (!MyApplication.getUser(getContext()).isTest()) {
            httpPostRemindList(false);
        }
        return view;
    }

    private void initView() {
        unfinishListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        finishedListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mUnAdapter = new RemindListAdapter(getContext(), mUnData);
        mAdapter = new RemindListAdapter(getContext(), mData);
        unfinishListView.setAdapter(mUnAdapter);
        finishedListView.setAdapter(mAdapter);
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout mRefreshLayout) {
                page = AppConstant.DEFAULT_PAGE;
                httpPostRemindList(true);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout mRefreshLayout) {
                page++;
                httpPostRemindList(true);
            }
        });
        mUnAdapter.setOnItemClickListener(new OnRemindItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent = new Intent(getContext(), EditRemindActivity.class);
                intent.putExtra("remind_bean", (Serializable) mUnData.get(position));
                startActivityForResult(intent, START_CODE);
            }

            @Override
            public void onItemDeleteClick(int position, View view) {
                deleteRemind(mUnData.get(position));
            }

            @Override
            public void onItemEditFinishedClick(int position, View view) {
                setRemindDone(mUnData.get(position));
            }
        });
        mAdapter.setOnItemClickListener(new OnRemindItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent = new Intent(getContext(), EditRemindActivity.class);
                intent.putExtra("remind_bean", (Serializable) mData.get(position));
                startActivityForResult(intent, START_CODE);
            }

            @Override
            public void onItemDeleteClick(int position, View view) {
                deleteRemind(mData.get(position));
            }

            @Override
            public void onItemEditFinishedClick(int position, View view) {
                //完成的list 没有标记完成
            }
        });
    }

    @OnClick({R.id.add_remind, R.id.remind_unfinish_title_layout, R.id.remind_finished_title_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_remind:
                Intent intent = new Intent(getContext(), EditRemindActivity.class);
                startActivityForResult(intent, START_CODE);
                break;
            case R.id.remind_unfinish_title_layout:
                initViewAndData(false);
                break;
            case R.id.remind_finished_title_layout:
                initViewAndData(true);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化布局和请求数据
     */
    private void initViewAndData(boolean finished) {
        //防止用户反复点击未完成 已完成按钮
        if (loading) return;
        if (finished && done == CODE_FINISHED) return;
        if (!finished && done == CODE_UNFINISH) return;
        unListLayout.setVisibility(finished ? View.GONE : View.VISIBLE);
        listLayout.setVisibility(finished ? View.VISIBLE : View.GONE);
        if (!finished) {//未完成
            LinearLayout.LayoutParams unlp = (LinearLayout.LayoutParams) unfinishLayout.getLayoutParams();
            unlp.height = 0;
            unlp.weight = 1;
            unfinishLayout.setLayoutParams(unlp);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) finishedLayout.getLayoutParams();
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            lp.weight = 0;
            finishedLayout.setLayoutParams(lp);
            done = CODE_UNFINISH;
            if (mUnData.size() == 0) emptyUnIv.setVisibility(View.VISIBLE);
        } else {//已完成
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) unfinishLayout.getLayoutParams();
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            lp.weight = 0;
            unfinishLayout.setLayoutParams(lp);
            LinearLayout.LayoutParams unlp = (LinearLayout.LayoutParams) finishedLayout.getLayoutParams();
            unlp.height = 0;
            unlp.weight = 1;
            finishedLayout.setLayoutParams(unlp);
            done = CODE_FINISHED;
            if (mData.size() == 0) emptyIv.setVisibility(View.VISIBLE);
        }
        if (mRefreshLayout != null) {
            page = AppConstant.DEFAULT_PAGE;
            mRefreshLayout.autoRefresh();
        }
    }

    /**
     * 获取数据
     */
    private void httpPostRemindList(final boolean showToast) {
        if (MyApplication.getUser(getContext()) == null) return;
        loading = true;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(getContext()).getId());
        map.put("done", done);
        map.put("current_page", page + "");
        PostListenner listenner = new PostListenner(getContext(), null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                loading = false;
                mRefreshLayout.finishRefresh(true);
                mRefreshLayout.finishLoadMore(true);
                //清空数据
                if (page == AppConstant.DEFAULT_PAGE) {
                    if (CODE_UNFINISH.equals(done)) {
                        mUnData.clear();
                    } else {
                        mData.clear();
                    }
                }
                RemindListBean remindListBean = JsonUtils.objectFromJson(r.getResult(), RemindListBean.class);
                if (remindListBean != null) {
                    unfinishNumTv.setText(remindListBean.getUnDoneCountString());
                    finishedNumTv.setText(remindListBean.getDoneCountString());
                    //数据填充
                    if (remindListBean.getReminds() != null && remindListBean.getReminds().size() > 0) {
                        if (CODE_UNFINISH.equals(done)) {
                            mUnData.addAll(remindListBean.getReminds());
                        } else {
                            mData.addAll(remindListBean.getReminds());
                        }
                    } else {
                        if (page > AppConstant.DEFAULT_PAGE) {
                            page--;
                        }
                        if (showToast) {
                            ToastUtil.show(getContext(), getString(R.string.no_more_data), Toast.LENGTH_SHORT);
                        }
                    }
                }
                if (CODE_UNFINISH.equals(done)) {
                    setMainActTabRedNum();
                    mUnAdapter.notifyDataSetChanged();
                    emptyUnIv.setVisibility(mUnData.size() == 0 ? View.VISIBLE : View.GONE);
                } else {
                    mAdapter.notifyDataSetChanged();
                    emptyIv.setVisibility(mData.size() == 0 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                loading = false;
                mRefreshLayout.finishRefresh(true);
                mRefreshLayout.finishLoadMore(true);
            }
        };
        HttpClient.getRemindList(getContext(), map, listenner);
    }

    private void setMainActTabRedNum() {
        if (mUnData.size() > 0) {
            int num = 0;
            for (RemindBean bean : mUnData) {
                if (bean.isTimeout()) {
                    num++;
                }
            }
            EventBus.getDefault().post(new EventBusMsg.RefreshRemindRedNum(num > 0));
            SendIconNumUtil.sendIconNumNotification(num, (Application) getContext().getApplicationContext());
        }
    }

    /**
     * 删除提醒
     */
    public void deleteRemind(RemindBean remindBean) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(getContext()).getId());
        map.put("remind_id", remindBean.get_id());
        PostListenner listenner = new PostListenner(getContext(), Loading.show(null, getContext(), "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                try {
                    JSONObject jsonObject = new JSONObject(r.getResult());
                    int code = jsonObject.getInt("ok");
                    if (AppConstant.ADD_REMIND_SUCCESS == code && mRefreshLayout != null) {
                        page = AppConstant.DEFAULT_PAGE;
                        mRefreshLayout.autoRefresh();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.deteleRemind(getContext(), map, listenner);
    }

    /**
     * 标记已完成
     */
    public void setRemindDone(RemindBean remindBean) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(getContext()).getId());
        map.put("remind_id", remindBean.get_id());
        PostListenner listenner = new PostListenner(getContext(), Loading.show(null, getContext(), "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                try {
                    JSONObject jsonObject = new JSONObject(r.getResult());
                    int code = jsonObject.getInt("ok");
                    if (AppConstant.ADD_REMIND_SUCCESS == code && mRefreshLayout != null) {
                        page = AppConstant.DEFAULT_PAGE;
                        mRefreshLayout.autoRefresh();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.setRemindDone(getContext(), map, listenner);
    }

    @Override
    public void onShow() {
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_CODE && resultCode == Activity.RESULT_OK) {
            if (mRefreshLayout != null) {
                page = AppConstant.DEFAULT_PAGE;
                mRefreshLayout.autoRefresh();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnData.clear();
        mUnData = null;
        mData.clear();
        mData = null;
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.RefreshRemind msg) {
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
    }

}
