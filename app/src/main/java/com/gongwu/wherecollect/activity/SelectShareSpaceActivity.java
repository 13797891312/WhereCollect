package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.SelectShareSpaceAdapter;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.ToastUtil;

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
    private SharePersonBean sharePersonBean;

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
        sharePersonBean = (SharePersonBean) getIntent().getSerializableExtra("sharePersonBean");
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
        int count = 0;
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).isSelectSpace()) {
                count++;
            }
        }
        ToastUtil.show(context, count + "", Toast.LENGTH_SHORT);

    }

    public static void start(Context context, SharePersonBean bean) {
        Intent intent = new Intent(context, SelectShareSpaceActivity.class);
        if (bean != null) {
            intent.putExtra("sharePersonBean", bean);
        }
        context.startActivity(intent);

    }
}
