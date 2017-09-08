package com.gongwu.wherecollect.activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.EditText;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.ToastUtil;
import com.zhaojin.myviews.Loading;

import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
public class FeedBackActivity extends BaseViewActivity implements View.OnClickListener {
    @Bind(R.id.title)
    EditText title;
    @Bind(R.id.info)
    EditText info;
    @Bind(R.id.phone)
    EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        titleLayout.textBtn.setVisibility(View.VISIBLE);
        titleLayout.textBtn.setOnClickListener(this);
        titleLayout.setTitle("意见反馈");
        titleLayout.setBack(true, null);
        if (!TextUtils.isEmpty(MyApplication.getUser(this).getMobile())) {
            phone.setText(MyApplication.getUser(this).getMobile());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textBtn:
                if (TextUtils.isEmpty(title.getText())) {
                    ToastUtil.show(this, "请填写标题", Toast.LENGTH_LONG);
                    return;
                }
                if (TextUtils.isEmpty(info.getText())) {
                    ToastUtil.show(this, "请填写反馈内容", Toast.LENGTH_LONG);
                    return;
                }
                feedBack();
                break;
        }
    }

    private void feedBack() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("title", title.getText().toString());
        map.put("content", String.format("%s\n%s", info.getText().toString(), "联系方式：" + phone.getText().toString()));
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在登陆")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                ToastUtil.show(FeedBackActivity.this, "反馈成功", Toast.LENGTH_LONG);
                finish();
            }
        };
        HttpClient.feedBack(this, map, listenner);
    }
}
