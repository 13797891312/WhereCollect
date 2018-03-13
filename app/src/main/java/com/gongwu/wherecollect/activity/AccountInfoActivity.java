package com.gongwu.wherecollect.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.BuildConfig;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.ToastUtil;
import com.umeng.socialize.Config;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class AccountInfoActivity extends BaseViewActivity {
    UmAuthListener listener = new UmAuthListener(this);
    @Bind(R.id.tv_wx)
    TextView tvWx;
    @Bind(R.id.wx_layout)
    RelativeLayout wxLayout;
    @Bind(R.id.tv_wb)
    TextView tvWb;
    @Bind(R.id.wb_layout)
    RelativeLayout wbLayout;
    @Bind(R.id.tv_email)
    TextView tvEmail;
    @Bind(R.id.qq_layout)
    RelativeLayout qqLayout;
    @Bind(R.id.tv_qq)
    TextView tvQq;
    @Bind(R.id.email_layout)
    RelativeLayout emailLayout;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.phone_layout)
    RelativeLayout phoneLayout;
    @Bind(R.id.tv_changePWD)
    TextView tvChangePWD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        ButterKnife.bind(this);
        titleLayout.setTitle("账号信息");
        titleLayout.setBack(true, null);
        EventBus.getDefault().register(this);
        refreshUi();
    }

    /**
     * 刷新UI
     */
    private void refreshUi() {
        UserBean user = MyApplication.getUser(this);
        if (user.isPassLogin()) {
            tvChangePWD.setVisibility(View.VISIBLE);
        } else {
            tvChangePWD.setVisibility(View.GONE);
        }
        if (user == null)
            return;
        if (user.getQq() != null && (!TextUtils.isEmpty(user.getQq().getOpenid()))) {
            tvQq.setText(user.getQq().getNickname());
        } else {
            tvPhone.setText("未绑定");
        }
        if (user.getWeixin() != null && (!TextUtils.isEmpty(user.getWeixin().getOpenid()))) {
            tvWx.setText(user.getWeixin().getNickname());
        } else {
            tvWx.setText("未绑定");
        }
        if (user.getSina() != null && (!TextUtils.isEmpty(user.getSina().getOpenid()))) {
            tvWb.setText(user.getSina().getNickname());
        } else {
            tvWb.setText("未绑定");
        }
        if (!TextUtils.isEmpty(user.getMail())) {
            tvEmail.setText(user.getMail());
        } else {
            tvEmail.setText("未绑定");
        }
        if (!TextUtils.isEmpty(user.getMobile())) {
            tvPhone.setText(user.getMobile());
        } else {
            tvPhone.setText("未绑定");
        }
    }

    @OnClick({R.id.wx_layout, R.id.wb_layout, R.id.qq_layout, R.id.email_layout, R.id.phone_layout, R.id.tv_changePWD})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.wx_layout:
                otherLogin(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.wb_layout:
                otherLogin(SHARE_MEDIA.SINA);
                break;
            case R.id.qq_layout:
                otherLogin(SHARE_MEDIA.QQ);
                break;
            case R.id.email_layout:
                intent = new Intent(this, BindEmailActivity.class);
                startActivity(intent);
                break;
            case R.id.phone_layout:
                intent = new Intent(this, ConfigChangePhoneActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.tv_changePWD:
                intent = new Intent(this, ConfigChangePWDActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 用户登录会收到消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ChangeUser msg) {
        refreshUi();
    }

    /**
     * 用户资料改变
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ChangeUserInfo msg) {
        refreshUi();
    }

    /**
     * 验证验证码并更换手机号
     * type:QQ,WECHAT
     */
    private void bindOther(final String type, Map<String, String> infoMap) {
        String key = "";
        final Map<String, String> otherMap = new HashMap<>();
        otherMap.put("nickname", infoMap.get("name"));
        otherMap.put("headimgurl", infoMap.get("iconurl"));
        otherMap.put("sex", infoMap.get("gender"));
        otherMap.put("openid", infoMap.get("uid"));
        switch (type) {
            case "QQ":
                key = "qq";
                break;
            case "WECHAT":
                key = "weixin";
                break;
            case "SINA":
                key = "sina";
                break;
        }
        Map<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("uid", MyApplication.getUser(this).getId());
        map.put(key, JsonUtils.jsonFromObject(otherMap));
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在发送")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                switch (type) {
                    case "QQ":
                        UserBean.QqBean qb = new UserBean.QqBean();
                        qb.setNickname(otherMap.get("nickname"));
                        qb.setOpenid(otherMap.get("openid"));
                        MyApplication.getUser(AccountInfoActivity.this).setQq(qb);
                        break;
                    case "WECHAT":
                        UserBean.WeixinBean wb = new UserBean.WeixinBean();
                        wb.setNickname(otherMap.get("nickname"));
                        wb.setOpenid(otherMap.get("openid"));
                        wb.setUnionid(otherMap.get("openid"));
                        MyApplication.getUser(AccountInfoActivity.this).setWeixin(wb);
                        break;
                    case "SINA":
                        UserBean.SinaBean sb = new UserBean.SinaBean();
                        sb.setNickname(otherMap.get("nickname"));
                        sb.setOpenid(otherMap.get("openid"));
                        MyApplication.getUser(AccountInfoActivity.this).setSina(sb);
                        break;
                }
                SaveDate.getInstence(AccountInfoActivity.this).setUser(JsonUtils.jsonFromObject
                        (MyApplication.getUser(AccountInfoActivity.this)));
                refreshUi();
                ToastUtil.show(AccountInfoActivity.this, "绑定成功", Toast.LENGTH_LONG);
            }
        };
        HttpClient.bindOther(this, map, listenner);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    public void otherLogin(SHARE_MEDIA sm) {
        UMShareAPI.get(this).getPlatformInfo(this, sm, listener);
        Config.DEBUG = BuildConfig.LOGSHOW;
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
                    bindOther("QQ", map);
                    break;
                case WEIXIN:
                    bindOther("WECHAT", map);
                    break;
                case SINA:
                    bindOther("SINA", map);
                    break;
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            ToastUtil.show(context, "授权失败:" + throwable.toString(), Toast.LENGTH_LONG);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
        }
    }
}
