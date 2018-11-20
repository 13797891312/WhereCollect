package com.gongwu.wherecollect.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.AddSharePersonOldListAdapter;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.entity.SharedLocationBean;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.ToastUtil;
import com.zsitech.oncon.barcode.core.CaptureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加共享人
 */
public class AddSharePersonActivity extends BaseViewActivity implements MyOnItemClickListener, TextWatcher {

    private final int START_CODE = 101;

    @Bind(R.id.add_share_title_tv)
    TextView titleView;
    @Bind(R.id.add_share_edit)
    EditText addShareEditView;
    @Bind(R.id.add_share_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.delete_btn)
    ImageButton deleteBtn;

    private List<SharePersonBean> datas = new ArrayList<>();
    private AddSharePersonOldListAdapter mAdapter;
    private boolean init;
    private boolean initList;
    private SharePersonBean selectBean;
    private String location_codes, content_text;
    private SharedLocationBean sharedLocationBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_share_person);
        titleLayout.setVisibility(View.GONE);
        ButterKnife.bind(this);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        titleView.setText("添加共享人");
        sharedLocationBean = (SharedLocationBean) getIntent().getSerializableExtra("locationBean");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new AddSharePersonOldListAdapter(this, datas);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEvent() {
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(this);
        }
        addShareEditView.addTextChangedListener(this);
        addShareEditView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (TextUtils.isEmpty(addShareEditView.getText().toString().trim())) {
                        ToastUtil.show(context, "输入共享人ID", Toast.LENGTH_SHORT);
                    } else {
                        //会被调用2次
                        if (!init) {
                            init = true;
                            getUserCodeData(addShareEditView.getText().toString().trim());
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.add_share_scan_ib, R.id.add_share_back_btn, R.id.delete_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_share_back_btn:
                onBackPressed();
                break;
            case R.id.add_share_scan_ib:
                startActivityForResult(new Intent(context, CaptureActivity.class), START_CODE);
                break;
            case R.id.delete_btn:
                deleteBtn.setVisibility(View.GONE);
                addShareEditView.setText("");
                if (initList) {
                    initData();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_CODE && resultCode == CaptureActivity.result) {//扫描的到结果
            String result = data.getStringExtra("result");
            addShareEditView.setText(result);
            addShareEditView.setSelection(result.length());
            getUserCodeData(result);
        }
        if (requestCode == START_CODE && resultCode == Activity.RESULT_OK) {//选择共享空间的结果
            location_codes = data.getStringExtra("location_codes");
            content_text = data.getStringExtra("content_text");
            if (!TextUtils.isEmpty(location_codes) && selectBean != null) {
                startDialog();
            }
        }
    }

    /**
     * 提示
     */
    private void startDialog() {
        String content;
        if (selectBean.isValid()) {
            content = "已与" + selectBean.getNickname() + "建立连接,直接共享" + content_text + "?";
        } else {
            content = "是否邀请@" + selectBean.getNickname() + ",并共享" + content_text + "?" + "\n（共享后，双方可同时查看和编辑该空间及空间内物品）";
        }
        DialogUtil.show("", content, "确定", "取消", (Activity) context, new DialogInterface.OnClickListener
                () {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shareOldUserLocation();
            }
        }, null);

    }

    @Override
    public void onItemClick(int positions, View view) {
        //点击+号进来的 需要选择共享的空间
        if (datas != null && datas.size() >= positions) {
            selectBean = datas.get(positions);
            //如果是从空间点进来的直接共享该空间 不用去选择空间
            if (sharedLocationBean != null) {
                location_codes = sharedLocationBean.getCode();
                content_text = sharedLocationBean.getName();
                startDialog();
                return;
            }
            Intent intent = new Intent(AddSharePersonActivity.this, SelectShareSpaceActivity.class);
            startActivityForResult(intent, START_CODE);
        }
    }

    /**
     * 请求用户共享空间
     */
    private void shareOldUserLocation() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", MyApplication.getUser(this).getId());
        params.put("be_shared_user_id", selectBean.getId());
        params.put("location_codes", location_codes);
        PostListenner listener = new PostListenner(this) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                setResult(RESULT_OK);
                finish();
            }
        };
        HttpClient.shareOldUserLocation(this, params, listener);
    }

    /**
     * 获取历史搜索的用户
     */
    private void initData() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", MyApplication.getUser(this).getId());
        PostListenner listener = new PostListenner(this) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                datas.clear();
                List<SharePersonBean> beans = JsonUtils.listFromJson(r.getResult(), SharePersonBean.class);
                if (beans != null && beans.size() > 0) {
                    datas.addAll(beans);
                }
                mAdapter.notifyDataSetChanged();
                initList = false;
            }
        };
        HttpClient.getAddSharePersonOldList(this, params, listener);
    }

    /**
     * 扫描用户二维码获取信息
     */
    private void getUserCodeData(String usid) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", MyApplication.getUser(this).getId());
        params.put("usid", usid);
        PostListenner listener = new PostListenner(this) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                datas.clear();
                SharePersonBean beans = JsonUtils.objectFromJson(r.getResult(), SharePersonBean.class);
                if (beans != null) {
                    datas.add(beans);
                }
                mAdapter.notifyDataSetChanged();
                initList = true;
                Intent intent = new Intent(AddSharePersonActivity.this, SelectShareSpaceActivity.class);
                startActivityForResult(intent, START_CODE);
            }

            @Override
            protected void error() {
                ToastUtil.show(context, "该用户不存在", Toast.LENGTH_SHORT);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                init = false;
            }
        };
        HttpClient.getUserCodeInfo(this, params, listener);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count > 0) {
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            deleteBtn.setVisibility(View.GONE);
            if (initList) {
                initData();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AddSharePersonActivity.class);
        context.startActivity(intent);
    }
}
