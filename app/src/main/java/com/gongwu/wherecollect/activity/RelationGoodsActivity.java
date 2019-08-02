package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.adapter.RelationGoodsAdapter;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.swipetoloadlayout.OnRefreshListener;
import com.gongwu.wherecollect.swipetoloadlayout.SwipeToLoadLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 3.6关联物品
 */
public class RelationGoodsActivity extends BaseViewActivity implements OnRefreshListener, MyOnItemClickListener {

    @Bind(R.id.title_text_view)
    TextView titleTv;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @Bind(R.id.swipe_target)
    RecyclerView mRecyclerView;
    @Bind(R.id.add_remind_et)
    EditText mEditText;

    private RelationGoodsAdapter mAdapter;
    private List<ObjectBean> mLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation_goods);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initView() {
        titleTv.setText(getResources().getText(R.string.act_title_relation));
        titleLayout.setVisibility(View.GONE);
        mEditText.setCursorVisible(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mAdapter = new RelationGoodsAdapter(context, mLists);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEvent() {
        mSwipeToLoadLayout.setOnRefreshListener(this);
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(this);
        }
        //软键盘点击确定
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                }
                return false;
            }
        });

    }

    @Override
    public void onItemClick(int positions, View view) {
        if (mLists != null && mLists.size() > positions) {
            Intent intent = new Intent();
            intent.putExtra("objectBean", mLists.get(positions));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @OnClick({R.id.back_bt,R.id.add_remind_et})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_bt://返回
                finish();
                break;
            case R.id.add_remind_et:
                mEditText.setCursorVisible(true);
                break;
        }
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        mSwipeToLoadLayout.setRefreshing(true);
    }

}
