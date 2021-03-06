package com.gongwu.wherecollect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.gongwu.wherecollect.util.StringUtils;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginEmailActivity extends BaseViewActivity implements TextWatcher {
    @Bind(R.id.email_edit)
    EditText emailEdit;
    @Bind(R.id.pwd_edit)
    EditText pwdEdit;
    @Bind(R.id.confirm)
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);
        ButterKnife.bind(this);
        titleLayout.setTitle("登录");
        titleLayout.setBack(true, null);
        EventBus.getDefault().register(this);
        initView();
    }

    @OnClick(R.id.confirm)
    public void onClick() {
        login();
    }

    public void login() {
        Map<String, String> map = new TreeMap<>();
        map.put("mail", emailEdit.getText().toString());
        map.put("password", pwdEdit.getText().toString());
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                final UserBean user = JsonUtils.objectFromJson(r.getResult(), UserBean.class);
                if (TextUtils.isEmpty(user.getMax_version())) {
                    startMainActivity(user);
                } else {
                    DialogUtil.show("提示", TextUtils.isEmpty(user.getLogin_messag()) ? "您的帐号已经在高版本使用过,请使用IOS版" : user.getLogin_messag(), "继续", "取消", LoginEmailActivity.this, new DialogInterface.OnClickListener() {
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
        HttpClient.login(this, map, StringUtils.getCurrentVersionName(context), listenner);
    }

    private void startMainActivity(UserBean user) {
        logoutTest(MyApplication.getUser(context));
        user.setOpenid(pwdEdit.getText().toString());
        user.setPassLogin(true);
        SaveDate.getInstence(LoginEmailActivity.this).setUser(JsonUtils.jsonFromObject(user));
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
        Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initView() {
        emailEdit.addTextChangedListener(this);
        pwdEdit.addTextChangedListener(this);
    }

    private void setBtn() {
        if (TextUtils.isEmpty(emailEdit.getText()) || TextUtils.isEmpty(pwdEdit.getText())) {
            confirm.setEnabled(false);
            return;
        }
        confirm.setEnabled(true);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        setBtn();
    }

    @OnClick({R.id.tv_regist, R.id.tv_forgetPWD})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_regist:
                intent = new Intent(LoginEmailActivity.this, RegisteActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forgetPWD:
                intent = new Intent(LoginEmailActivity.this, ForgetPWDActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ChangeUser msg) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
