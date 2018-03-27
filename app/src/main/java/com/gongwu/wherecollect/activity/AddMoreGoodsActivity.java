package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.AddMoreGoodsListAdapter;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.view.AddGoodsDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 批量添加
 */
public class AddMoreGoodsActivity extends BaseViewActivity {
    @Bind(R.id.textBtn)
    TextView addGoodsTv;
    @Bind(R.id.more_goods_list_view)
    ListView mListView;
    @Bind(R.id.more_commit_btn)
    Button commitBtn;
    private ObjectBean bean;
    private AddGoodsDialog mDialog;
    private List<ObjectBean> mDatas;
    private AddMoreGoodsListAdapter mAdapter;
    private int currentItem = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_goods);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.add_more_goods_text));
        mDatas = new ArrayList<>();
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        addGoodsTv.setText("添加");
        addGoodsTv.setVisibility(View.VISIBLE);
        mAdapter = new AddMoreGoodsListAdapter(context, mDatas);
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
        bean = (ObjectBean) getIntent().getSerializableExtra("bean");
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentItem = position;
                mDialog.setObjectBean(mDatas.get(position));
                mDialog.show();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });
    }

    @OnClick({R.id.textBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textBtn:
                //添加
                mDialog = new AddGoodsDialog(context) {
                    @Override
                    public void result(ObjectBean bean) {
                        if (currentItem != -1) {
                            mDatas.set(currentItem, bean);
                            currentItem = -1;
                        } else {
                            mDatas.add(bean);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void cancel() {
                        currentItem = -1;
                    }
                };
                mDialog.setObjectBean(null);
                mDialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mDialog != null) {
            mDialog.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void start(Context context, ObjectBean objectBean) {
        Intent intent = new Intent(context, AddMoreGoodsActivity.class);
        if (objectBean != null) {
            intent.putExtra("bean", objectBean);
        }
        context.startActivity(intent);
    }
}
