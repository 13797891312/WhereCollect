package com.gongwu.wherecollect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.SelectShareSpaceAdapter;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择共享空间
 */
public class SelectShareSpaceActivity extends BaseViewActivity {

    @Bind(R.id.select_share_space_recycler_view)
    RecyclerView mRecyclerView;
    private SelectShareSpaceAdapter mAdapter;

    private List<ObjectBean> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_share_space);
        ButterKnife.bind(this);
        initView();
        initEvent();
        initData();
    }

    private void initData() {
        datas.clear();
        final String cache = SaveDate.getInstence(this).getSpace();
        if (!TextUtils.isEmpty(cache)) {
            List<ObjectBean> temp = JsonUtils.listFromJson(cache, ObjectBean.class);
            datas.addAll(temp);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        titleLayout.setTitle("选择共享空间");
        titleLayout.setBack(true, null);
        titleLayout.selectSpaceView.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SelectShareSpaceAdapter(context, datas);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEvent() {

    }

    @OnClick({R.id.select_space_ib})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_space_ib:
                selectSpaceDataToPost();
                break;
            default:
                break;
        }
    }

    private void selectSpaceDataToPost() {
        String location_codes = "";
        String content_text = "";
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).isSelectSpace()) {
                location_codes += datas.get(i).getCode() + "%";
                content_text += "【" + datas.get(i).getName() + "】,";
            }
        }
        Intent intent = new Intent();
        intent.putExtra("location_codes", location_codes.substring(0, location_codes.length() - 1));
        intent.putExtra("content_text", content_text.substring(0, content_text.length() - 1));
        setResult(RESULT_OK, intent);
        finish();
    }

}
