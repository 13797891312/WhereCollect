package com.gongwu.wherecollect.quickadd;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.umeng.analytics.MobclickAgent;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class QuickSpaceSelectListActivity extends BaseViewActivity {
    @Bind(R.id.commit)
    public Button commit;
    public int count;//总共选了多少个
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.cancel)
    TextView cancel;
    SpaceSelectListAdapter mAdapter;
    private List<ObjectBean> mlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_space_select_list);
        ButterKnife.bind(this);
        titleLayout.setTitle("");
        titleLayout.setVisibility(View.GONE);
        titleLayout.setBack(false, null);
        titleLayout.titleTv.setTextColor(getResources().getColor(R.color.maincolor));
        initView();
        setBtnStatus();
        initData();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        mAdapter = new SpaceSelectListAdapter(this, mlist);
        listview.setAdapter(mAdapter);
    }

    @OnClick({R.id.commit, R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit:
                List<ObjectBean> temp = new ArrayList<>();
                for (int i = 0; i < mlist.size(); i++) {
                    for (int j = 0; j < mlist.get(i).getRecommend(); j++) {
                        temp.add(mlist.get(i));
                    }
                }
                Intent intent = new Intent(context, QuickFurnitureSelectListActivity.class);
                intent.putExtra("list", ((ArrayList) temp));
                startActivity(intent);
                break;
            case R.id.cancel:
                MobclickAgent.onEvent(context, "060101");
                finish();
                break;
        }
    }

    /**
     * 加载数据
     */
    private void initData() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                if (mlist == null) {
                    return;
                }
                List<ObjectBean> temp = JsonUtils.listFromJson(r.getResult(), ObjectBean.class);
                mlist.clear();
                mlist.addAll(temp);
                mAdapter.notifyDataSetChanged();
                count = mlist.size();
                setBtnStatus();
            }
        };
        HttpClient.getQuickData(this, map, listenner);
    }

    @Override
    public void onBackPressed() {
    }

    public void setBtnStatus() {
        commit.setEnabled(count > 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.RequestSpace msg) {
        finish();
    }
}
