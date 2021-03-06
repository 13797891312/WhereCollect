package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.BuildConfig;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.umeng.socialize.Config;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseViewActivity {
    UmAuthListener listener = new UmAuthListener(this);
    @Bind(R.id.wx_layout)
    RelativeLayout wxLayout;
    @Bind(R.id.agree)
    TextView agree;
    @Bind(R.id.wb_layout)
    LinearLayout wbLayout;
    @Bind(R.id.qq_layout)
    LinearLayout qqLayout;
    @Bind(R.id.msg_layout)
    LinearLayout msgLayout;
    @Bind(R.id.other_layout)
    LinearLayout otherLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        titleLayout.setBack(false, null);
        titleLayout.setTitle("登录");
        titleLayout.textBtnLeft.setText("试用一下");
        titleLayout.textBtnLeft.setVisibility(View.VISIBLE);
        titleLayout.textBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getUser(context) == null) {
                    loginTest();
                } else {
                    EventBus.getDefault().post(new EventBusMsg.SelectMainTagPosition(0));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    public void otherLogin(SHARE_MEDIA sm) {
        UMShareAPI.get(this).getPlatformInfo(this, sm, listener);
    }

    @OnClick({R.id.wx_layout, R.id.agree, R.id.phone_layout, R.id.wb_layout, R.id.qq_layout, R.id.msg_layout, R.id.other_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wx_layout:
                otherLogin(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.agree:
                WebActivity.start(this, "收哪儿服务条款", "http://www.shouner.com/privacy");
                break;
            case R.id.phone_layout:
                Intent intent1 = new Intent(this, LoginPhoneActivity.class);
                startActivity(intent1);
                break;
            case R.id.wb_layout:
                otherLogin(SHARE_MEDIA.SINA);
                break;
            case R.id.qq_layout:
                otherLogin(SHARE_MEDIA.QQ);
                break;
            case R.id.msg_layout:
                Intent intent = new Intent(this, LoginEmailActivity.class);
                startActivity(intent);
                break;
            case R.id.other_btn:
                otherLayout.setVisibility(View.VISIBLE);
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

    public void login(String type, final Map<String, String> infoMap) {
        Map<String, String> map = new TreeMap<>();
        map.put("avatar", infoMap.get("iconurl"));
        map.put("gender", infoMap.get("gender"));
        map.put("loginway", type);
        map.put("nickname", infoMap.get("name"));
        map.put("openid", infoMap.get("uid"));
        map.put("password", infoMap.get("uid"));
        map.put("uid", infoMap.get("uid"));
        map.put("unionid", infoMap.get("uid"));
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                final UserBean user = JsonUtils.objectFromJson(r.getResult(), UserBean.class);
                if (TextUtils.isEmpty(user.getMax_version())) {
                    startMainActivity(user, infoMap.get("uid"));
                } else {
                    DialogUtil.show("提示", TextUtils.isEmpty(user.getLogin_messag()) ? "您的帐号已经在高版本使用过,请使用IOS版" : user.getLogin_messag(), "继续", "取消", LoginActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startMainActivity(user, infoMap.get("uid"));
                        }
                    }, null);
                }
            }
        };
        HttpClient.login(this, map, StringUtils.getCurrentVersionName(context), listenner);
    }

    private void startMainActivity(UserBean user, String uid) {
        logoutTest(MyApplication.getUser(context));
        user.setOpenid(uid);
        SaveDate.getInstence(LoginActivity.this).setUser(JsonUtils.jsonFromObject(user));
        MyApplication.setUser(user);
        EventBus.getDefault().post(user);
        EventBus.getDefault().post(new EventBusMsg.ChangeUser());
        //先停止在开始
        EventBus.getDefault().post(new EventBusMsg.stopService());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new EventBusMsg.startService());
            }
        }, 1000);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 注册测试账号
     */
    private void loginTest() {
        Map<String, String> map = new TreeMap<>();
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "初始化...")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                SaveDate.getInstence(context).setUser(r.getResult());
                UserBean user = JsonUtils.objectFromJson(r.getResult(), UserBean.class);
                MyApplication.setUser(user);
                EventBus.getDefault().post(user);
                EventBus.getDefault().post(new EventBusMsg.ChangeUser());
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                ((LoginActivity) context).finish();
            }
        };
        HttpClient.registerTest(context, map, listenner);
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

    @Override
    public void onBackPressed() {
    }

    class UmAuthListener implements UMAuthListener {
        Context context;

        public UmAuthListener(Context context) {
            this.context = context;
        }

        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            LogUtil.e(map.toString());
            switch (share_media) {
                case QQ:
                    login("qq", map);
                    break;
                case WEIXIN:
                    login("wechat", map);
                    break;
                case SINA:
                    login("sina", map);
                    break;
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            DialogUtil.show("提示", "授权失败,请检查是否装有第三方应用", "好的", "", LoginActivity.this, null, null);
//            ToastUtil.show(context, "授权失败:" + throwable.toString(), Toast.LENGTH_LONG);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
        }
    }
}
