package com.gongwu.wherecollect.afragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.AddRemindActivity;
import com.gongwu.wherecollect.adapter.RemindListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.RemindBean;
import com.gongwu.wherecollect.entity.RemindListBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.swipetoloadlayout.OnLoadMoreListener;
import com.gongwu.wherecollect.swipetoloadlayout.OnRefreshListener;
import com.gongwu.wherecollect.swipetoloadlayout.SwipeToLoadLayout;
import com.gongwu.wherecollect.util.AppConstant;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

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
public class RemindFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    private static final String CODE_UNFINISH = "0";
    private static final String CODE_FINISHED = "1";

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
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @Bind(R.id.remind_unfinish_num_text_view)
    TextView unfinishNumTv;
    @Bind(R.id.remind_finished_num_text_view)
    TextView finishedNumTv;

    private RemindListAdapter mUnAdapter;
    private RemindListAdapter mAdapter;

    private View view;
    private String done = CODE_UNFINISH;
    private int page = AppConstant.DEFAULT_PAGE;
    private boolean init = false;
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
        initView();
        initEvent();
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
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
    }

    @OnClick({R.id.add_remind, R.id.remind_unfinish_title_layout, R.id.remind_finished_title_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_remind:
                AddRemindActivity.start(getContext());
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
        if (finished && done == CODE_FINISHED) return;
        if (!finished && done == CODE_UNFINISH) return;
        unfinishListView.setVisibility(finished ? View.GONE : View.VISIBLE);
        finishedListView.setVisibility(finished ? View.VISIBLE : View.GONE);
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
        }
        if (mSwipeToLoadLayout != null) {
            page = AppConstant.DEFAULT_PAGE;
            mSwipeToLoadLayout.setRefreshing(true);
        }
    }

    /**
     * 获取数据
     */
    private void httpPostRemindList() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(getContext()).getId());
        map.put("done", done);
        map.put("current_page", page + "");
        PostListenner listenner = new PostListenner(getContext(), null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                initRecyclerLayout();
                closeLoading(mSwipeToLoadLayout);
                RemindListBean remindListBean = JsonUtils.objectFromJson(r.getResult(), RemindListBean.class);
                if (remindListBean != null) {
                    unfinishNumTv.setVisibility(View.VISIBLE);
                    unfinishNumTv.setText(remindListBean.getUnDoneCountString());
                    finishedNumTv.setVisibility(View.VISIBLE);
                    finishedNumTv.setText(remindListBean.getDoneCountString());
                    if (remindListBean.getReminds() != null && remindListBean.getReminds().size() > 0) {
                        if (CODE_UNFINISH.equals(done)) {
                            if (page == AppConstant.DEFAULT_PAGE) {
                                mUnData.clear();
                            }
                            mUnData.addAll(remindListBean.getReminds());
                            mUnAdapter.notifyDataSetChanged();
                        } else {
                            if (page == AppConstant.DEFAULT_PAGE) {
                                mData.clear();
                            }
                            mData.addAll(remindListBean.getReminds());
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (page > AppConstant.DEFAULT_PAGE) {
                            page--;
                        }
                        ToastUtil.show(getContext(), getString(R.string.no_more_data), Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                closeLoading(mSwipeToLoadLayout);
            }
        };
        HttpClient.getRemindList(getContext(), map, listenner);
    }

    private void initRecyclerLayout() {
        if (!init) {
            init = true;
            unfinishListView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams unlp = (LinearLayout.LayoutParams) unfinishLayout.getLayoutParams();
            unlp.height = 0;
            unlp.weight = 1;
            unfinishLayout.setLayoutParams(unlp);
        }

    }

    @Override
    public void onShow() {
        if (!init) {
            if (mSwipeToLoadLayout != null) {
                mSwipeToLoadLayout.setRefreshing(true);
            }
        }
    }

    @Override
    public void onRefresh() {
        if (mSwipeToLoadLayout != null) {
            page = AppConstant.DEFAULT_PAGE;
            httpPostRemindList();
        }
    }

    @Override
    public void onLoadMore() {
        if (mSwipeToLoadLayout != null) {
            page++;
            httpPostRemindList();
        }
    }
}
