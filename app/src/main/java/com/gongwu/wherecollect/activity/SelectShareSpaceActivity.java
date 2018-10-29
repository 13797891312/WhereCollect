package com.gongwu.wherecollect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.SelectShareSpaceAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.LocationBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.SharedLocationBean;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.ToastUtil;

import java.io.Serializable;
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

    private List<LocationBean> datas = new ArrayList<>();
    private boolean addMore = false;
    private UserBean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_share_space);
        ButterKnife.bind(this);
        user = MyApplication.getUser(this);
        initView();
        initData();
    }

    private void initData() {
        datas.clear();
        final String cache = SaveDate.getInstence(this).getSpace();
        List<SharedLocationBean> shareSpaceBeans = (List<SharedLocationBean>) getIntent().getSerializableExtra("shareSpaceBeans");
        if (!TextUtils.isEmpty(cache)) {
            List<LocationBean> temp = JsonUtils.listFromJson(cache, LocationBean.class);
            for (int i = 0; i < temp.size(); i++) {
                LocationBean objectBean = temp.get(i);
                if (!objectBean.getUser_id().equals(user.getId())) {
                    temp.remove(i);
                    i--;
                }
            }
            if (shareSpaceBeans != null && shareSpaceBeans.size() > 0) {
                titleLayout.setTitle("批量添加共享空间");
                addMore = true;
                for (int i = 0; i < temp.size(); i++) {
                    LocationBean objectBean = temp.get(i);
                    for (int j = 0; j < shareSpaceBeans.size(); j++) {
                        if (objectBean.getName().equals(shareSpaceBeans.get(j).getName())) {
                            temp.remove(i);
                            i--;
                        }
                    }
                }
            }
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
        List<SharedLocationBean> beans = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).isSelectSpace()) {
                location_codes += datas.get(i).getCode() + "%";
                content_text += "【" + datas.get(i).getName() + "】,";
                if (addMore) {
                    SharedLocationBean bean = new SharedLocationBean();
                    bean.setCode(datas.get(i).getCode());
                    bean.setName(datas.get(i).getName());
                    bean.setId(datas.get(i).getId());
                    bean.setUser_id(datas.get(i).getUser_id());
                    beans.add(bean);
                }
            }
        }
        if (beans.size() == 0) {
            ToastUtil.show(this,"请选择共享的空间", Toast.LENGTH_SHORT);
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("location_codes", location_codes.substring(0, location_codes.length() - 1));
        intent.putExtra("content_text", content_text.substring(0, content_text.length() - 1));
        if (addMore) {
            intent.putExtra("result_bean", (Serializable) beans);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

}
