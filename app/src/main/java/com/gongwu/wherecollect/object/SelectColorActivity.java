package com.gongwu.wherecollect.object;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.EditText;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.adapter.ColorContentAdapter;
import com.gongwu.wherecollect.adapter.ColorGridAdapter;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
public class SelectColorActivity extends BaseViewActivity {
    @Bind(R.id.contentList)
    RecyclerView contentList;
    @Bind(R.id.add_edit)
    EditText addEdit;
    @Bind(R.id.gridView)
    RecyclerView gridView;
    List<String> datas = new ArrayList<>();
    List<String> selectList = new ArrayList<>();
    ColorGridAdapter colorAdapter;
    ColorContentAdapter contentAdapter;
    LinearLayoutManager mLayoutManager1;
    GridLayoutManager mLayoutManager2;
    ObjectBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleLayout.setTitle("颜色");
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
        setContentView(R.layout.activity_add_color);
        ButterKnife.bind(this);
        initView();
        initContentColor();
        getColors();
    }

    private void initView() {
        contentList.setHasFixedSize(true);
        colorAdapter = new ColorGridAdapter(this, datas, selectList);
        mLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        contentAdapter = new ColorContentAdapter(this, selectList, colorAdapter);
        contentList.setLayoutManager(mLayoutManager1);
        contentList.setAdapter(contentAdapter);
        mLayoutManager2 = new GridLayoutManager(context, 5, LinearLayoutManager.VERTICAL, false);
        gridView.setLayoutManager(mLayoutManager2);
        gridView.setAdapter(colorAdapter);
        colorAdapter.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(int positions, View view) {
                if (selectList.contains(datas.get(positions))) {
                    selectList.remove(datas.get(positions));
                } else {
                    selectList.add(datas.get(positions));
                }
                colorAdapter.notifyDataSetChanged();
                contentAdapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (selectList.size() > 0) {
                            contentList.smoothScrollToPosition(selectList.size() - 1);
                        }
                    }
                }, 100);
            }
        });
        addEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!TextUtils.isEmpty(addEdit.getText()) && (!selectList.contains(addEdit.getText().toString()))) {
                        selectList.add(addEdit.getText().toString());
                        addEdit.setText("");
                        contentAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (selectList.size() > 0) {
                                    contentList.smoothScrollToPosition(selectList.size() - 1);
                                }
                            }
                        }, 100);
                        colorAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     *
     */
    private void initContentColor() {
        if (TextUtils.isEmpty(bean.getColor()))
            return;
        String[] colors = bean.getColor().split("、");
        for (int i = 0; i < colors.length; i++) {
            selectList.add(colors[i]);
        }
        contentAdapter.notifyDataSetChanged();
        colorAdapter.notifyDataSetChanged();
    }

    /**
     * 获取颜色
     */
    private void getColors() {
        Map<String, String> map = new TreeMap<>();
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List temp = JsonUtils.listFromJson(r.getResult(), String.class);
                datas.addAll(temp);
                colorAdapter.notifyDataSetChanged();
            }
        };
        HttpClient.getColors(this, map, listenner);
    }

    private void commit() {
        //如果没有按回车键，直接输入并点左上角的勾先判断有没有输入
        String editText = addEdit.getText().toString().replace(" ", "");
        if (!TextUtils.isEmpty(editText) && (!selectList.contains(editText)
        )) {
            selectList.add(addEdit.getText().toString());
            addEdit.setText("");
            contentAdapter.notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (selectList.size() > 0) {
                        contentList.smoothScrollToPosition(selectList.size() - 1);
                    }
                }
            }, 100);
            colorAdapter.notifyDataSetChanged();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectList.size(); i++) {
            sb.append(selectList.get(i));
            if (i != selectList.size() - 1) {
                sb.append("、");
            }
        }
        bean.setColor(sb.toString());
        Intent intent = new Intent();
        intent.putExtra("bean", bean);
        setResult(100, intent);
        finish();
    }
}
