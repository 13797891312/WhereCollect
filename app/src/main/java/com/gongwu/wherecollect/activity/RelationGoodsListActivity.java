package com.gongwu.wherecollect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.adapter.RelationGoodsAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
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
 * 3.6关联物品
 */
public class RelationGoodsListActivity extends BaseViewActivity implements OnRefreshListener, MyOnItemClickListener, OnLoadMoreListener {

    @Bind(R.id.title_text_view)
    TextView titleTv;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @Bind(R.id.swipe_target)
    RecyclerView mRecyclerView;
    @Bind(R.id.relation_goods_et)
    EditText mEditText;

    private int page = AppConstant.DEFAULT_PAGE;
    private RelationGoodsAdapter mAdapter;
    private List<ObjectBean> mLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation_goods);
        ButterKnife.bind(this);
        initView();
        initEvent();
        if (mSwipeToLoadLayout != null) {
            mSwipeToLoadLayout.setRefreshing(true);
        }
    }

    /**
     * 初始化view
     */
    private void initView() {
        titleTv.setText(getResources().getText(R.string.act_title_relation));
        titleLayout.setVisibility(View.GONE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mAdapter = new RelationGoodsAdapter(context, mLists);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 获取数据
     */
    private void initData() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("category_code", "all");
        map.put("keyword", mEditText.getText().toString());
        map.put("current_page", page + "");
        PostListenner listenner = new PostListenner(context, null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                closeLoading(mSwipeToLoadLayout);
                JSONObject jsonObject;
                String json = null;
                try {
                    jsonObject = new JSONObject(r.getResult());
                    json = jsonObject.getString("items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<ObjectBean> lists = JsonUtils.listFromJson(TextUtils.isEmpty(json) ? "" : json, ObjectBean.class);
                if (page == AppConstant.DEFAULT_PAGE) {
                    mLists.clear();
                }
                if (lists != null && lists.size() > 0) {
                    mLists.addAll(lists);
                } else {
                    if (page > AppConstant.DEFAULT_PAGE) page--;
                    ToastUtil.show(context, getString(R.string.no_more_data), Toast.LENGTH_SHORT);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                closeLoading(mSwipeToLoadLayout);
            }
        };
        HttpClient.getRelationGoodsList(context, map, listenner);
    }

    /**
     * 监听
     */
    private void initEvent() {
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(this);
        }
        //软键盘点击确定
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    page = AppConstant.DEFAULT_PAGE;
                    if (mSwipeToLoadLayout != null) {
                        mSwipeToLoadLayout.setRefreshing(true);
                    }
                }
                return false;
            }
        });

    }

    @Override
    public void onItemClick(int positions, View view) {
        if (mLists != null && mLists.size() > positions) {
            Intent intent = new Intent();
            intent.putExtra("objectBean", mLists.get(positions));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @OnClick({R.id.back_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_bt://返回
                finish();
                break;
        }
    }

    public void closeLoading(SwipeToLoadLayout mSwipeToLoadLayout) {
        if (mSwipeToLoadLayout != null) {
            mSwipeToLoadLayout.setRefreshing(false);
            mSwipeToLoadLayout.setLoadingMore(false);
        }
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        page = AppConstant.DEFAULT_PAGE;
        initData();
    }

    @Override
    public void onLoadMore() {
        page++;
        initData();
    }
}
