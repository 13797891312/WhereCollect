package com.gongwu.wherecollect.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("登录");
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

    @OnClick({R.id.wx_layout, R.id.agree, R.id.wb_layout, R.id.qq_layout, R.id.msg_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wx_layout:
                otherLogin(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.agree:
                ToastUtil.show(this, "服务条款", Toast.LENGTH_SHORT);
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
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserBean userBean) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
            login("qq",map);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            ToastUtil.show(context, "授权失败:" + throwable.toString(), Toast.LENGTH_LONG);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
        }
    }

    public void login(String type,Map<String,String> infoMap){
        Map<String, String> map = new TreeMap<>();
        map.put("avatar", infoMap.get("iconurl"));
        map.put("gender", infoMap.get("gender"));
        map.put("loginway", type);
        map.put("nickname", infoMap.get("name"));
        map.put("openid", infoMap.get("uid"));
        map.put("password", infoMap.get("uid"));
        map.put("uid", infoMap.get("uid"));
        map.put("unionid", infoMap.get("uid"));
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在登陆")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                SaveDate.getInstence(LoginActivity.this).setUser(r.getResult());
                UserBean user = JsonUtils.objectFromJson(r.getResult(), UserBean.class);
                MyApplication.setUser(user);
                EventBus.getDefault().post(user);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        HttpClient.login(this, map, listenner);
    }
}
