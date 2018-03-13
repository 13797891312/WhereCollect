package com.gongwu.wherecollect.activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.GoodsSearchAdapter;
import com.gongwu.wherecollect.adapter.SerchListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.SerchBean;
import com.gongwu.wherecollect.object.ObjectLookInfoActivity;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.zhaojin.myviews.Loading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class SearchActivity extends BaseViewActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.back)
    ImageButton back;
    @Bind(R.id.serch_edit)
    EditText serchEdit;
    @Bind(R.id.serch)
    ImageButton serch;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.clear)
    TextView clear;
    @Bind(R.id.activity_search)
    LinearLayout activitySearch;
    List<SerchBean> mlist = new ArrayList<>();
    SerchListAdapter adapter;
    @Bind(R.id.searchListView)
    ListView searchListView;
    List<ObjectBean> searchDatas = new ArrayList<>();
    GoodsSearchAdapter searchAtapter;
    @Bind(R.id.search_layout)
    LinearLayout searchLayout;
    @Bind(R.id.delete_btn)
    ImageButton deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        titleLayout.setVisibility(View.GONE);
        serchEdit.clearFocus();
        serchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(serchEdit.getText())) {
                    deleteBtn.setVisibility(View.GONE);
                } else {
                    deleteBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        serchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (serchEdit.getText().length() != 0) {
                        ContentValues cv = new ContentValues();
                        cv.put("upDateTime", System.currentTimeMillis());
                        int i = SerchBean.updateAll(SerchBean.class, cv, "title=?", serchEdit.getText().toString());
                        if (i == 0) {
                            SerchBean bean = new SerchBean();
                            bean.setTitle(serchEdit.getText().toString());
                            bean.setUpDateTime(System.currentTimeMillis());
                            bean.save();
                        }
                        searchObject(serchEdit.getText().toString());
                    }
                    return false;
                }
                return false;
            }
        });
        initList();
        initData();
    }

    private void initList() {
        searchAtapter = new GoodsSearchAdapter(this, searchDatas);
        searchListView.setAdapter(searchAtapter);
        adapter = new SerchListAdapter(this, mlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        searchListView.setOnItemClickListener(this);
    }

    private void initData() {
        mlist.clear();
        mlist.addAll(SerchBean.limit(10).order("upDateTime desc").find(SerchBean.class));
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.back, R.id.serch, R.id.clear, R.id.delete_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.serch:
                if (TextUtils.isEmpty(serchEdit.getText())) {
                    return;
                }
                ContentValues cv = new ContentValues();
                cv.put("upDateTime", System.currentTimeMillis());
                int i = SerchBean.updateAll(SerchBean.class, cv, "title=?", serchEdit.getText().toString());
                if (i == 0) {
                    SerchBean bean = new SerchBean();
                    bean.setTitle(serchEdit.getText().toString());
                    bean.setUpDateTime(System.currentTimeMillis());
                    bean.save();
                }
                searchObject(serchEdit.getText().toString());
                break;
            case R.id.clear:
                SerchBean.deleteAll(SerchBean.class);
                initData();
                break;
            case R.id.delete_btn:
                serchEdit.setText("");
                break;
        }
    }

    private void searchObject(String key) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("keyword", key);
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "搜索中...")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List<ObjectBean> temp = JsonUtils.listFromJson(r.getResult(), ObjectBean.class);
                searchDatas.clear();
                searchDatas.addAll(temp);
                searchAtapter.notifyDataSetChanged();
                if (searchDatas.isEmpty()) {
                    ToastUtil.show(SearchActivity.this, "无记录", Toast.LENGTH_SHORT);
                    searchListView.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.VISIBLE);
                } else {
                    searchLayout.setVisibility(View.GONE);
                    searchListView.setVisibility(View.VISIBLE);
                }
            }
        };
        HttpClient.searchObject(this, map, listenner);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.listView:
                mlist.get(i).setUpDateTime(System.currentTimeMillis());
                mlist.get(i).save();
                searchObject(mlist.get(i).getTitle());
                serchEdit.setText(mlist.get(i).getTitle());
                mlist.get(i).setUpDateTime(System.currentTimeMillis());
                mlist.get(i).save();
                break;
            case R.id.searchListView:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("object", searchDatas.get(i));
                startActivity(intent);
                finish();
                break;
        }
    }
}
