package com.gongwu.wherecollect.object;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.adapter.ChannelListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.ChannelBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.CatagreyListView;
import com.gongwu.wherecollect.view.ErrorView;
import com.zhaojin.myviews.Loading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class SelectFenleiActivity extends BaseViewActivity implements TextWatcher, AdapterView.OnItemClickListener {
    ObjectBean bean;
    @Bind(R.id.seach_edit)
    EditText seachEdit;
    @Bind(R.id.guishu_txt)
    TextView guishuTxt;
    @Bind(R.id.serchListView)
    ListView serchListView;
    @Bind(R.id.guishuListView)
    CatagreyListView guishuListView;
    @Bind(R.id.clear)
    ImageView clear;
    List<ChannelBean> searchList = new ArrayList<>();
    ChannelListAdapter adapter;
    @Bind(R.id.empty)
    ErrorView empty;
    ChannelBean selectChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_fenlei);
        ButterKnife.bind(this);
        titleLayout.setTitle("选择分类");
        titleLayout.imageBtn.setVisibility(View.VISIBLE);
        titleLayout.imageBtn.setImageResource(R.drawable.icon_confirm);
        titleLayout.setBack(true, null);
        bean = (ObjectBean) getIntent().getSerializableExtra("bean");
        titleLayout.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
        initValues();
        initView();
        guishuListView.init(guishuTxt);
    }

    private void initView() {
        seachEdit.addTextChangedListener(this);
        adapter = new ChannelListAdapter(this, searchList);
        serchListView.setAdapter(adapter);
        serchListView.setOnItemClickListener(this);
    }

    private void initValues() {
        if (StringUtils.isEmpty(bean.getCategories())) {
            clear.setVisibility(View.GONE);
            return;
        }
        clear.setVisibility(View.VISIBLE);
        seachEdit.setText(bean.getCategories().get(bean.getCategories().size() - 1).getName());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bean.getCategories().size() - 1; i++) {
            sb.append(bean.getCategories().get(i).getName());
            if (i != bean.getCategories().size() - 2) {
                sb.append(">");
            }
        }
        guishuTxt.setText(sb.toString());
    }

    /**
     * 提交
     */
    private void commit() {
        if (TextUtils.isEmpty(seachEdit.getText())) {//如果是空的直接结束
            bean.setCategories(null);
            Intent intent = new Intent();
            intent.putExtra("bean", bean);
            setResult(100, intent);
            finish();
            return;
        }
        if (selectChannel != null) {//如果直接点击的搜索的分类就直接带结果返回
            back(selectChannel);
            return;
        }
        if (guishuListView.selectGuishu != null && guishuTxt.getText().toString().equals(guishuListView.selectGuishu
                .getString())) {
            addFenlei(seachEdit.getText().toString(), guishuListView.selectGuishu.getCode());
            return;
        }
        if (TextUtils.isEmpty(guishuTxt.getText())||"自定义".equals(guishuTxt.getText().toString())) {//如果归属没有添加类型
            addFenlei(seachEdit.getText().toString(), null);
            return;
        }
        finish();
    }

    /**
     * 搜索数据
     *
     * @param str
     */
    private void getSearchData(final String str) {
        serchListView.setVisibility(View.VISIBLE);
        guishuListView.setVisibility(View.GONE);
        selectChannel = null;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("keyword", str);
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                searchList.clear();
                ChannelBean bean = new ChannelBean();
                bean.setName(str);
                searchList.add(bean);
                List temp = JsonUtils.listFromJson(r.getResult(), ChannelBean.class);
                searchList.addAll(temp);
                if (!TextUtils.isEmpty(seachEdit.getText())) {
                    adapter.setmList(searchList);
                    adapter.notifyDataSetChanged();
                }
            }
        };
        HttpClient.getSearchFenlei(this, map, listenner);
    }

    /**
     * 添加新分类
     *
     * @param str
     */
    private void addFenlei(String str, String code) {
        selectChannel = null;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("category_name", str);
        if (!TextUtils.isEmpty(code)) {
            map.put("category_code", code);
        }
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在提交")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                ChannelBean temp = JsonUtils.objectFromJson(r.getResult(), ChannelBean.class);
                back(temp);
            }
        };
        HttpClient.getAddFenlei(this, map, listenner);
    }

    private void back(ChannelBean str) {
        List<BaseBean> temp = new ArrayList<>();
        temp.addAll(str.getParents());
        temp.add(str);
        bean.setCategories(temp);
        Intent intent = new Intent();
        intent.putExtra("bean", bean);
        setResult(100, intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(seachEdit.getText())) {
            clear.setVisibility(View.GONE);
        } else {
            clear.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(seachEdit.getText())) {
            searchList.clear();
            adapter.notifyDataSetChanged();
        } else {
            getSearchData(seachEdit.getText().toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        seachEdit.removeTextChangedListener(this);
        if (TextUtils.isEmpty(adapter.getmList().get(position).get_id())) {//自定义
            guishuTxt.setText("自定义");
            seachEdit.setText(adapter.getmList().get(position).getName());
            selectChannel = null;
        } else {
            seachEdit.setText(adapter.getmList().get(position).getName());
            guishuTxt.setText(adapter.getmList().get(position).getParentsString());
            selectChannel = adapter.getmList().get(position);
        }
        seachEdit.addTextChangedListener(this);
        guishuListView.resetData();
        searchList.clear();
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.clear, R.id.guishu_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear:
                seachEdit.setText("");
                break;
            case R.id.guishu_layout:
                guishuListView.setVisibility(View.VISIBLE);
                serchListView.setVisibility(View.GONE);
                guishuListView.lastList();
                break;
        }
    }
}
