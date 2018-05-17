package com.gongwu.wherecollect.quickadd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationEdit.LocationEditActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.QuickAddRequest;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuickFurnitureSelectListActivity extends BaseViewActivity {
    @Bind(R.id.commit)
    public Button commit;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.last_tv)
    TextView lastTv;
    QuickFunitureSelectListAdapter mAdapter;
    private List<ObjectBean> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_furniture_select_list);
        ButterKnife.bind(this);
        titleLayout.setTitle("");
        titleLayout.setVisibility(View.GONE);
        titleLayout.setBack(false, null);
        titleLayout.titleTv.setTextColor(getResources().getColor(R.color.maincolor));
        mList = (List<ObjectBean>) getIntent().getSerializableExtra("list");
        initView();
    }

    private void initView() {
        mAdapter = new QuickFunitureSelectListAdapter(this, mList);
        listview.setAdapter(mAdapter);
    }

    @OnClick({R.id.commit, R.id.last_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit:
                commit();
                break;
            case R.id.last_tv:
                finish();
                break;
        }
    }

    /**
     * 提交
     */
    private void commit() {
        List<QuickAddRequest> temp = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            QuickAddRequest request = new QuickAddRequest(mList.get(i).getCode(), mList.get(i).getName());
            List<String> funitureCodes = new ArrayList<>();
            for (int j = 0; j < StringUtils.getListSize(mList.get(i).getLayers()); j++) {
                if (mList.get(i).getLayers().get(j).getRecommend() > 0) {
                    funitureCodes.add(mList.get(i).getLayers().get(j).getCode());
                }
            }
            request.setFurnitures(funitureCodes);
            temp.add(request);
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("data", JsonUtils.jsonFromObject(temp));
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                Intent intent = new Intent(context, LocationEditActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                EventBus.getDefault().post(new EventBusMsg.RequestSpace());
                finish();
            }
        };
        HttpClient.commitQuickAdd(this, map, listenner);
        MobclickAgent.onEvent(context, "060102");
    }

    @Override
    public void onBackPressed() {
    }
}
