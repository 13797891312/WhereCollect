package com.gongwu.wherecollect.object;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.adapter.JijieListAdapter;
import com.gongwu.wherecollect.entity.ObjectBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
public class SelectJijieActivity extends BaseViewActivity implements AdapterView.OnItemClickListener {
    ObjectBean bean;
    @Bind(R.id.listView)
    ListView listView;
    JijieListAdapter adapter;
    List<String> mlist = new ArrayList<>();
    List<String> selectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleLayout.setTitle("季节");
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
        setContentView(R.layout.activity_select_jijie);
        ButterKnife.bind(this);
        initSelect();
        initView();
    }

    private void initSelect() {
        mlist.add("春季");
        mlist.add("夏季");
        mlist.add("秋季");
        mlist.add("冬季");
        if (TextUtils.isEmpty(bean.getSeason()))
            return;
        String[] seasons = bean.getSeason().split("、");
        for (int i = 0; i < seasons.length; i++) {
            if (mlist.contains(seasons[i])) {
                selectList.add(seasons[i]);
            }
        }
    }

    private void initView() {
        adapter = new JijieListAdapter(this, mlist, selectList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void commit() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectList.size(); i++) {
            sb.append(selectList.get(i));
            if (i != selectList.size() - 1) {
                sb.append("、");
            }
        }
        bean.setSeason(sb.toString());
        Intent intent = new Intent();
        intent.putExtra("bean", bean);
        setResult(100, intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!selectList.contains(mlist.get(position))) {
            selectList.add(mlist.get(position));
        } else {
            selectList.remove(mlist.get(position));
        }
        adapter.notifyDataSetChanged();
    }
}
