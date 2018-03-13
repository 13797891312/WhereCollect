package com.gongwu.wherecollect.activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.GetSpaceDateUtil;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.StringUtils;

import java.util.Map;
import java.util.TreeMap;
public class StartActivity extends BaseViewActivity {
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        titleLayout.setVisibility(View.GONE);
        time = System.currentTimeMillis();
        init();
    }

    private void init() {
        if (MyApplication.getUser(this) != null && StringUtils.getCurrentVersion(this) == SaveDate.getInstence(this)
                .getVersion()) {
            GetSpaceDateUtil util = new GetSpaceDateUtil() {
                @Override
                protected void onFinish() {
                    if (System.currentTimeMillis() - time > 1500) {
                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                        finish();
                    }else{
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(StartActivity.this, MainActivity.class));
                                finish();
                            }
                        },1500-(System.currentTimeMillis() - time));
                    }
                }
            };
            util.getSpaceData(this, MyApplication.getUser(this).getId());
        } else {
            loginTest();
        }
    }

    /**
     * 注册测试账号
     */
    public void loginTest() {
        Map<String, String> map = new TreeMap<>();
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                SaveDate.getInstence(context).setUser(r.getResult());
                UserBean user = JsonUtils.objectFromJson(r.getResult(), UserBean.class);
                MyApplication.setUser(user);
                if (System.currentTimeMillis() - time > 1500) {
                    startTestLogin();
                }else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startTestLogin();
                        }
                    },1500-(System.currentTimeMillis() - time));
                }
            }
        };
        HttpClient.registerTest(context, map, listenner);
    }

    private void startTestLogin(){
        if (StringUtils.getCurrentVersion(context) == SaveDate.getInstence(context)
                .getVersion()) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        } else {
            SaveDate.getInstence(context).setVersion(StringUtils.getCurrentVersion(context));
            Intent intent = new Intent(context, UpdataInfoActivity.class);
            context.startActivity(intent);
        }
        finish();
    }
}
