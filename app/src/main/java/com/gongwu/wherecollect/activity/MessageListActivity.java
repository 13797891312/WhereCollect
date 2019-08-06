package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MessageListAdapter;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.MessageBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.swipetoloadlayout.OnLoadMoreListener;
import com.gongwu.wherecollect.swipetoloadlayout.OnRefreshListener;
import com.gongwu.wherecollect.swipetoloadlayout.SwipeToLoadLayout;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.ErrorView;

import org.greenrobot.eventbus.EventBus;

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
    @Bind(R.id.empty)
    ErrorView emptyView;
    @Bind(R.id.empty_img)
    ImageView empty_img;

    private static final int DEFAULT_PAGE = 1;
    private int page = DEFAULT_PAGE;
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
        empty_img.setVisibility(View.VISIBLE);
        emptyView.setErrorMsg("暂无消息");
    }

    @Override
    public void onItemClick(int positions, View view) {
        final MessageBean messageBean = datas.get(positions);
        String okStr = "";
        String okUrl = "";
        String cancelStr = "";
        String cancelUrl = "";
        if (messageBean.getButtons().size() > 0) {
            for (int i = 0; i < messageBean.getButtons().size(); i++) {
                if (messageBean.getButtons().get(i).getColor().equals("SUCCESS")) {
                    okStr = messageBean.getButtons().get(i).getText();
                    okUrl = TextUtils.isEmpty(messageBean.getButtons().get(i).getApi_url()) ? "" :
                            messageBean.getButtons().get(i).getApi_url();
                }
                if (messageBean.getButtons().get(i).getColor().equals("DANGER")
                        || messageBean.getButtons().get(i).getColor().equals("DEFAULT")) {
                    cancelStr = messageBean.getButtons().get(i).getText();
                    cancelUrl = TextUtils.isEmpty(messageBean.getButtons().get(i).getApi_url()) ? "" :
                            messageBean.getButtons().get(i).getApi_url();
                }
            }
        } else {
            LogUtil.e("消息没有buttons");
            return;
        }
        final String finalOkUrl = okUrl;
        final String finalCancelUrl = cancelUrl;
        DialogUtil.show("", messageBean.getContent(), okStr, cancelStr, this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!messageBean.isIs_read() && !TextUtils.isEmpty(finalOkUrl)) {
                    postShareHttp(finalOkUrl);
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!messageBean.isIs_read() && !TextUtils.isEmpty(finalCancelUrl)) {
                    postShareHttp(finalCancelUrl);
                }
            }
        });
    }

    private void postShareHttp(String url) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                EventBus.getDefault().post(new EventBusMsg.updateShareMsg());
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.dealWithShareRequest(context, url, map, listenner);
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
                if (page == DEFAULT_PAGE) {//如果是第一页就缓存下
                    datas.clear();
                } else {
                    if (StringUtils.isEmpty(temp)) {
                        page --;
                        Toast.makeText(context, getString(R.string.no_more_data), Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                datas.addAll(temp);
                mAdapter.notifyDataSetChanged();
                emptyView.setVisibility(datas.size() > 0 ? View.GONE : View.VISIBLE);
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
        page = DEFAULT_PAGE;
        getData();
    }

    @Override
    public void onLoadMore() {
        page++;
        getData();
    }
}
