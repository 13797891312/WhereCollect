package com.gongwu.wherecollect.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.util.ToastUtil;
import com.umeng.socialize.Config;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

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
        Config.DEBUG = true;
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
                ToastUtil.show(this, "邮箱登陆", Toast.LENGTH_SHORT);
                break;
        }
    }

    static class UmAuthListener implements UMAuthListener {
        Context context;

        public UmAuthListener(Context context) {
            this.context = context;
        }

        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            ToastUtil.show(context, "授权成功" + map.toString(), Toast.LENGTH_LONG);
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
