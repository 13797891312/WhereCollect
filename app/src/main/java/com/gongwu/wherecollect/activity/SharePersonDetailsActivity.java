package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.SharePersonDetailsSpaceListAdapter;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.entity.SharedLocationBean;
import com.gongwu.wherecollect.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 共享人详情
 */
public class SharePersonDetailsActivity extends BaseViewActivity {
    @Bind(R.id.cutlink_ib)
    ImageButton cutlinkIb;
    @Bind(R.id.share_person_iv)
    ImageView share_person_iv;
    @Bind(R.id.share_user_name)
    TextView share_user_name;
    @Bind(R.id.share_user_id_tv)
    TextView share_user_id_tv;
    @Bind(R.id.share_person_details_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.base_layout)
    LinearLayout base_layout;

    private SharePersonDetailsSpaceListAdapter mAdapter;
    private SharePersonBean sharePersonBean;
    private List<SharedLocationBean> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_person_details);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        cutlinkIb.setVisibility(View.VISIBLE);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("");
        base_layout.setDividerPadding(0);
        base_layout.setDividerDrawable(null);
        sharePersonBean = (SharePersonBean) getIntent().getSerializableExtra("sharePersonBean");
        if (sharePersonBean != null) {
            ImageLoader.loadCircle(this, share_person_iv, sharePersonBean.getAvatar(), R.mipmap.ic_launcher);
            share_user_name.setText(sharePersonBean.getNickname());
            share_user_id_tv.setText("ID: " + sharePersonBean.getUsid());
            datas.addAll(sharePersonBean.getShared_locations());
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SharePersonDetailsSpaceListAdapter(this, datas);
        mRecyclerView.setAdapter(mAdapter);
    }

    public static void start(Context context, SharePersonBean bean) {
        Intent intent = new Intent(context, SharePersonDetailsActivity.class);
        if (bean != null) {
            intent.putExtra("sharePersonBean", bean);
        }
        context.startActivity(intent);
    }
}
