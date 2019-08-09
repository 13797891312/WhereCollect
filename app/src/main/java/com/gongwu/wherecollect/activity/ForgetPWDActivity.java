package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.ToastUtil;
import com.zhaojin.myviews.Loading;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Function: 忘记密码
 * Date: 2016-08-20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ForgetPWDActivity extends BaseViewActivity implements TextWatcher {
    @Bind(R.id.new_phone)
    EditText newPhone;
    @Bind(R.id.number)
    EditText number_et;
    @Bind(R.id.send_bt)
    Button sendBt;
    @Bind(R.id.submit_bt)
    Button submitBt;
    @Bind(R.id.phone_layout)
    TextInputLayout phoneLayout;
    @Bind(R.id.new_pwd)
    EditText newPwd;
    private Context context;
    private int type = 0;//0为新绑定，1为修改手机号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        ButterKnife.bind(this);
        titleLayout.setTitle("修改密码");
        titleLayout.setBack(true, null);
        context = this;
        type = getIntent().getIntExtra("type", 0);
        initView();
        initData();
    }

    private void initData() {
    }

    @OnClick({R.id.submit_bt, R.id.send_bt})
    public void onClick(View view) {
        final String mobile = newPhone.getText().toString().trim();
        if (mobile.length() != 11) {
            Toast.makeText(context, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.send_bt:
                getCode();
                break;
            case R.id.submit_bt:
                if (newPhone.getText().length() != 11) {
                    ToastUtil.show(this, "请输入正确的手机号", Toast.LENGTH_LONG);
                    return;
                }
                if (number_et.getText().length() != 6) {
                    ToastUtil.show(this, "请输入正确的验证码", Toast.LENGTH_LONG);
                    return;
                }
                if (newPwd.getText().length() < 6 || newPwd.getText().length() > 16) {
                    ToastUtil.show(this, "请输入正确的密码", Toast.LENGTH_LONG);
                    return;
                }
                changePWD();
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        if (MyApplication.getUser(this) == null) return;
        Map<String, String> map = new HashMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在发送")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                setBtDisEnble();
            }
        };
        HttpClient.getCode(this, map, listenner, newPhone.getText().toString());
    }

    /**
     * 验证验证码并更换手机号
     */
    private void changePWD() {
        Map<String, String> map = new HashMap<>();
        map.put("password", newPwd.getText().toString());
        map.put("code", number_et.getText().toString());
        map.put("phone", newPhone.getText().toString());
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在提交")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                ToastUtil.show(ForgetPWDActivity.this, "修改成功", Toast.LENGTH_LONG);
                finish();
            }
        };
        HttpClient.forgetPWD(this, map, listenner);
    }

    private void initView() {
        if (type == 1) {
            titleLayout.setTitle("更换手机");
            submitBt.setText("下一步");
            phoneLayout.setHint("原手机号");
        } else {
            phoneLayout.setHint("新手机号");
        }
        newPhone.addTextChangedListener(this);
        number_et.addTextChangedListener(this);
        newPwd.addTextChangedListener(this);
    }

    private void showBt() {
        if (TextUtils.isEmpty(newPhone.getText().toString()) ||
                TextUtils.isEmpty(number_et.getText().toString())
                || TextUtils.isEmpty(newPwd.getText().toString())) {
            submitBt.setEnabled(false);
        } else {
            submitBt.setEnabled(true);
        }
    }

    private void setBtDisEnble() {
        // TODO Auto-generated method stub
        sendBt.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                sendBt.setText(millisUntilFinished / 1000 + "秒后再次获取");
            }

            public void onFinish() {
                sendBt.setText("获取验证码");
                sendBt.setEnabled(true);
            }
        }.start();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        showBt();
        if ("获取验证码".equals(sendBt.getText().toString())) {
            if (newPhone.getText().length() != 11) {
                sendBt.setEnabled(false);
            } else {
                sendBt.setEnabled(true);
            }
        }
    }
}
