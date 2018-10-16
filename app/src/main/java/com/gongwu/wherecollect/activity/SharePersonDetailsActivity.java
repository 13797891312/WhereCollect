package com.gongwu.wherecollect.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.SharePersonDetailsSpaceListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.entity.SharedLocationBean;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.view.CloseShareDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private int deleteSpacePosition = -1;
    private String location_codes, content_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_person_details);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        cutlinkIb.setVisibility(View.VISIBLE);
        titleLayout.setBack(true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
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
        mAdapter = new SharePersonDetailsSpaceListAdapter(this, datas,MyApplication.getUser(context).getId()) {
            @Override
            public void closeSpace(int position) {
                closeSpaceDialog(position);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick({R.id.cutlink_ib, R.id.add_space_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cutlink_ib:
                startDialog();
                break;
            case R.id.add_space_iv:
                Intent intent = new Intent(this, SelectShareSpaceActivity.class);
                intent.putExtra("shareSpaceBeans", (Serializable) datas);
                startActivityForResult(intent, 103);
                break;
            default:
                break;
        }
    }

    /**
     * 断开用户选项
     */
    private void startDialog() {
        CloseShareDialog closeShareDialog = new CloseShareDialog(this) {
            @Override
            public void saveData() {
                startSaveHintDialog();
            }

            @Override
            public void deleteData() {
                startDeletaHintDialog();
            }
        };
    }

    /**
     * 断开用户不保留数据
     */
    private void startDeletaHintDialog() {
        DialogUtil.show("", "确定断开与@" + sharePersonBean.getNickname() + "的全部共享?\n(断开后属于共享空间的非本人添加的物品也将被清空)", "确定", "取消", this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                closeShareUser(sharePersonBean.getId(), "0", "");
            }
        }, null);
    }

    /**
     * 断开用户保留数据
     */
    private void startSaveHintDialog() {
        DialogUtil.show("", "确定与@" + sharePersonBean.getNickname() + "断开全部共享?\n(待对方同意后会保留双方已添加的数据)", "确定", "取消", this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                closeShareUser(sharePersonBean.getId(), "1", "");
            }
        }, null);
    }

    /**
     * 断开空间
     */
    private void closeSpaceDialog(int position) {
        deleteSpacePosition = position;
        DialogUtil.show("", "确定断开【" + datas.get(deleteSpacePosition).getName() + "】的共享?\n(断开后属于共享空间的非本人添加的物品也将被清空)", "确定", "取消", this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                closeShareUser(sharePersonBean.getId(), "0", datas.get(deleteSpacePosition).getCode());
            }
        }, null);
    }

    /**
     * 断开接口
     *
     * @param location_id 断开空间的话 传code
     */
    private void closeShareUser(String shareUserId, String type, final String location_id) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", MyApplication.getUser(this).getId());
        params.put("be_shared_user_id", shareUserId);
        params.put("type", type);
        if (!TextUtils.isEmpty(location_id)) {
            params.put("location_id", location_id);
        }
        PostListenner listener = new PostListenner(this) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                if (!TextUtils.isEmpty(location_id)) {
                    datas.remove(deleteSpacePosition);
                    mAdapter.notifyDataSetChanged();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.closeShareUser(this, params, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 103 && resultCode == Activity.RESULT_OK) {//选择共享空间的结果
            location_codes = data.getStringExtra("location_codes");
            content_text = data.getStringExtra("content_text");
            List<SharedLocationBean> beans = (List<SharedLocationBean>) data.getSerializableExtra("result_bean");
            startShareSpaceDialog(beans);
        }
    }

    /**
     * 确认是否共享空间
     */
    private void startShareSpaceDialog(final List<SharedLocationBean> beans) {
        String content = "已与@" + sharePersonBean.getNickname() + "建立过连接,直接共享" + content_text + "?";
        DialogUtil.show("", content, "确定", "取消", (Activity) context, new DialogInterface.OnClickListener
                () {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shareOldUserLocation(beans);
            }
        }, null);

    }

    /**
     * 调用共享接口
     */
    private void shareOldUserLocation(final List<SharedLocationBean> beans) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", MyApplication.getUser(this).getId());
        params.put("be_shared_user_id", sharePersonBean.getId());
        params.put("location_codes", location_codes);
        PostListenner listener = new PostListenner(this) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                datas.addAll(beans);
                mAdapter.notifyDataSetChanged();
            }
        };
        HttpClient.shareOldUserLocation(this, params, listener);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
