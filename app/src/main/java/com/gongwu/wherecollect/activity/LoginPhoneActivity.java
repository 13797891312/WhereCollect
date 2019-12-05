package com.gongwu.wherecollect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.ToastUtil;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginPhoneActivity extends BaseViewActivity implements TextWatcher {

    @Bind(R.id.phone_et)
    EditText mPhoneEditText;
    @Bind(R.id.number)
    EditText number_et;
    @Bind(R.id.send_bt)
    Button sendBt;
    @Bind(R.id.submit_bt)
    Button submitBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);
        ButterKnife.bind(this);
        titleLayout.setTitle("登录");
        titleLayout.setBack(true, null);
        initView();
    }

    private void initView() {
        mPhoneEditText.addTextChangedListener(this);
        number_et.addTextChangedListener(this);
    }

    @OnClick({R.id.submit_bt, R.id.send_bt})
    public void onClick(View view) {
        final String mobile = mPhoneEditText.getText().toString().trim();
        if (mobile.length() != 11) {
            Toast.makeText(context, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.send_bt:
                getCode();
                break;
            case R.id.submit_bt:
                if (mPhoneEditText.getText().length() != 11) {
                    ToastUtil.show(this, "请输入正确的手机号", Toast.LENGTH_LONG);
                    return;
                }
                if (number_et.getText().length() != 6) {
                    ToastUtil.show(this, "请输入正确的验证码", Toast.LENGTH_LONG);
                    return;
                }
                login();
                break;
        }
    }

    public void login() {
        Map<String, String> map = new TreeMap<>();
        map.put("phone", mPhoneEditText.getText().toString());
        map.put("code", number_et.getText().toString());
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                final UserBean user = JsonUtils.objectFromJson(r.getResult(), UserBean.class);
                if (TextUtils.isEmpty(user.getMax_version())) {
                    startMainActivity(user);
                } else {
                    DialogUtil.show("提示", TextUtils.isEmpty(user.getLogin_messag()) ? "您的帐号已经在高版本使用过,请使用IOS版" : user.getLogin_messag(), "继续", "取消", LoginPhoneActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startMainActivity(user);
                        }
                    }, null);
                }
            }

            @Override
            protected void otherCode() {
                super.otherCode();
                Toast.makeText(context, "账号密码错误", Toast.LENGTH_SHORT).show();
            }

        };
        HttpClient.loginByPhone(this, map, listenner);
    }

    private void startMainActivity(UserBean user) {
        logoutTest(MyApplication.getUser(context));
        user.setPassLogin(true);
        user.setId(user.getId());
        user.setTestId("");
        SaveDate.getInstence(LoginPhoneActivity.this).setUser(JsonUtils.jsonFromObject(user));
        MyApplication.setUser(user);
        EventBus.getDefault().post(new EventBusMsg.ChangeUser());
        //先停止请求消息接口的服务 在开始
        EventBus.getDefault().post(new EventBusMsg.stopService());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new EventBusMsg.startService());
            }
        }, 1000);
        Intent intent = new Intent(LoginPhoneActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        Map<String, String> map = new HashMap<>();
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在发送")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                setBtDisEnble();
            }
        };
        HttpClient.getCode(this, map, listenner, mPhoneEditText.getText().toString());
    }

    private void setBtDisEnble() {
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        showBt();
        if ("获取验证码".equals(sendBt.getText().toString())) {
            if (mPhoneEditText.getText().length() != 11) {
                sendBt.setEnabled(false);
            } else {
                sendBt.setEnabled(true);
            }
        }
    }

    private void showBt() {
        if (TextUtils.isEmpty(mPhoneEditText.getText().toString()) ||
                TextUtils.isEmpty(number_et.getText().toString())) {
            submitBt.setEnabled(false);
        } else {
            submitBt.setEnabled(true);
        }
    }

    /**
     * 注销测试账号
     */
    private void logoutTest(UserBean testUser) {
        if (testUser == null) {
            return;
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", testUser.getId());
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
            }
        };
        HttpClient.logoutTest(this, map, listenner);
    }
}
