package com.gongwu.wherecollect.activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.StringUtils;
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
    @Bind(R.id.commit)
    Button commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        commit.setOnClickListener(this);
        titleLayout.setTitle("意见反馈");
        titleLayout.setBack(true, null);
        if (!TextUtils.isEmpty(MyApplication.getUser(this).getMail())) {
            phone.setText(MyApplication.getUser(this).getMail());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit:
                if (TextUtils.isEmpty(title.getText())) {
                    ToastUtil.show(this, "请填写标题", Toast.LENGTH_LONG);
                    return;
                }
                if (TextUtils.isEmpty(info.getText())) {
                    ToastUtil.show(this, "请填写反馈内容", Toast.LENGTH_LONG);
                    return;
                }
                if (StringUtils.isEmail(phone.getText().toString())) {
                    feedBack();
                } else {
                    DialogUtil.show("提醒", "如果需要反馈，请添加联系邮箱，我们每一条都会回复。", "直接提交", "填邮箱", this, new
                            DialogInterface.OnClickListener
                                    () {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    feedBack();
                                }
                            }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            phone.setFocusable(true);
                            phone.setSelection(phone.getText().length());
                            phone.requestFocus();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    InputMethodManager imanager = (InputMethodManager) context
                                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imanager.showSoftInput(phone, 0);
                                }
                            }, 100);
                        }
                    });
                }
                break;
        }
    }

    private void feedBack() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("title", title.getText().toString());
        map.put("content", String.format("%s\n%s", info.getText().toString(), "联系方式：" + phone.getText().toString()));
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "")) {
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
