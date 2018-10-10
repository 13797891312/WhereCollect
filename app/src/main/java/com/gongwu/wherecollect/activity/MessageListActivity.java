package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.StringUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhaojin.myviews.Loading;

import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 消息中心
 */
public class MessageListActivity extends BaseViewActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.message_list_view)
    PullToRefreshListView mListView;

    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("消息中心");
        initView();
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(this);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
//                getData(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
//                getData(false);
            }
        });
    }

    private void initView() {
//        mAdapter = new RoomRecordListAdapter(this, mList);
//        listView.setAdapter(mAdapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    /**
     * 获取物品列表
     */
    public void getData(boolean isShowDialog) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("page", "" + page);
        PostListenner listenner = new PostListenner(this, isShowDialog ? Loading.show(null, this,
                "正在加载") : null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
//                List temp = JsonUtils.listFromJsonWithSubKey(r.getResult(), RoomRecordBean.class, "items");
                if (page == 1) {//如果是第一页就缓存下
//                    ACacheClient.setRecordList(context, MyApplication.getUser(context).getId(), JsonUtils.jsonFromObject
//                            (temp));
//                    mList.clear();
                } else {
                    if (StringUtils.isEmpty(null)) {
                        Toast.makeText(context, "没有更多数据了", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
//                mList.addAll(temp);
//                mAdapter.notifyDataSetChanged();
//                mListView.setEmptyView(empty);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mListView.onRefreshComplete();
            }
        };
        HttpClient.getRecordList(context, map, listenner);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MessageListActivity.class);
        context.startActivity(intent);
    }
}
