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
import com.gongwu.wherecollect.entity.ChannelBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.view.CatagreyListView;
import com.gongwu.wherecollect.view.ErrorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class SelectChannelActivity extends BaseViewActivity implements TextWatcher, AdapterView.OnItemClickListener {
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
    List<ChannelBean> mList = new ArrayList<>();
    List<ChannelBean> searchList = new ArrayList<>();
    ChannelListAdapter adapter;
    @Bind(R.id.empty)
    ErrorView empty;
    String selectChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_channel);
        ButterKnife.bind(this);
        titleLayout.setTitle("购买渠道");
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
        getData();
    }

    private void initView() {
        seachEdit.addTextChangedListener(this);
        adapter = new ChannelListAdapter(this, mList);
        serchListView.setAdapter(adapter);
        serchListView.setEmptyView(empty);
        serchListView.setOnItemClickListener(this);
    }

    private void initValues() {
        if (TextUtils.isEmpty(bean.getChannel())) {
            clear.setVisibility(View.GONE);
            return;
        }
        clear.setVisibility(View.VISIBLE);
        String[] strs = bean.getChannel().split(">");
        seachEdit.setText(strs[strs.length - 1]);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length - 1; i++) {
            sb.append(strs[i]);
            if (i != strs.length - 2) {
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
            back("");
            return;
        }
        if (!TextUtils.isEmpty(selectChannel)) {//如果直接点击的搜索的分类就直接带结果返回
            back(selectChannel);
            return;
        }
        if (guishuListView.selectGuishu != null && guishuTxt.getText().toString().equals(guishuListView.selectGuishu
                .getString())) {
            addChannel(seachEdit.getText().toString(), guishuListView.selectGuishu.getCode());
            return;
        } else if (TextUtils.isEmpty(guishuTxt.getText()) || "自定义".equals(guishuTxt.getText().toString())) {//如果归属没有添加类型
            addChannel(seachEdit.getText().toString(), null);
            return;
        }
        finish();
    }

    @OnClick({R.id.clear, R.id.guishu_layout})
    public void onClick(View v) {
        switch (v.getId()) {
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

    private void getData() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List temp = JsonUtils.listFromJson(r.getResult(), ChannelBean.class);
                mList.addAll(temp);
                adapter.notifyDataSetChanged();
                serchListView.setVisibility(View.VISIBLE);
            }
        };
        HttpClient.getChannel(this, map, listenner);
    }

    private void getSearchData(String str) {
        guishuListView.setVisibility(View.GONE);
        selectChannel = "";
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("keyword", str);
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                searchList.clear();
                ChannelBean bean = new ChannelBean();
                bean.setName("自定义");
                searchList.add(bean);
                List temp = JsonUtils.listFromJson(r.getResult(), ChannelBean.class);
                searchList.addAll(temp);
                if (!TextUtils.isEmpty(seachEdit.getText())) {
                    adapter.setmList(searchList);
                    adapter.notifyDataSetChanged();
                    serchListView.setVisibility(View.VISIBLE);
                }
            }
        };
        HttpClient.getSearchChannel(this, map, listenner);
    }

    private void addChannel(String str, String code) {
        selectChannel = "";
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        if (!TextUtils.isEmpty(code)) {
            map.put("code", code);
        }
        map.put("name", str);
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                ChannelBean bean = JsonUtils.objectFromJson(r.getResult(), ChannelBean.class);
                back(bean.getString());
            }
        };
        HttpClient.getAddChannel(this, map, listenner);
    }

    private void back(String str) {
        bean.setChannel(str);
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
            adapter.setmList(mList);
            adapter.notifyDataSetChanged();
            serchListView.setVisibility(View.VISIBLE);
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
            guishuTxt.setText(adapter.getmList().get(position).getName());
        } else {
            seachEdit.setText(adapter.getmList().get(position).getName());
            guishuTxt.setText(adapter.getmList().get(position).getParentsString());
            selectChannel = guishuTxt.getText().toString() + ">" + seachEdit.getText().toString();
        }
        guishuListView.resetData();
        seachEdit.addTextChangedListener(this);
        adapter.notifyDataSetChanged();
        serchListView.setVisibility(View.GONE);
    }
}
