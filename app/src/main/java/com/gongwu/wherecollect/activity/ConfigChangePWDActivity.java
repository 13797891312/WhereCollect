package com.gongwu.wherecollect.activity;
import android.content.Context;
import android.os.Bundle;
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
import com.gongwu.wherecollect.util.SaveDate;
import com.zhaojin.myviews.Loading;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Function: 修改密码
 * Date: 2016-08-20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ConfigChangePWDActivity extends BaseViewActivity {
    @Bind(R.id.oldPassword)
    EditText oldPassword;
    @Bind(R.id.newPassword)
    EditText newPassword;
    @Bind(R.id.rePassword)
    EditText rePassword;
    @Bind(R.id.confirm)
    Button confirm;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_change_pwd);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("修改密码");
        context = this;
        initView();
    }

    @OnClick(R.id.confirm)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                String oldPasswordst = oldPassword.getText().toString().trim();
                String newPasswordst = newPassword.getText().toString().trim();
                String rePasswordst = rePassword.getText().toString().trim();
                if (TextUtils.isEmpty(oldPasswordst) && TextUtils.isEmpty(newPasswordst) &&
                        TextUtils.isEmpty(rePasswordst)) {
                    Toast.makeText(this, "您有一项为空，请填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPasswordst.equals(rePasswordst)) {
                    Toast.makeText(this, "新密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPasswordst.length() < 6 || newPasswordst.length() > 16) {
                    Toast.makeText(this, "密码长度为6-16位", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (oldPasswordst.equals(newPasswordst)) {
                    Toast.makeText(this, "新密码不能与原密码相同", Toast.LENGTH_SHORT).show();
                    return;
                }
                ChangePassword(oldPasswordst, newPasswordst);
                break;
        }
    }

    /**
     * 修改密码
     * @param oldPasswordst
     * @param newPasswordst
     */
    private void ChangePassword(String oldPasswordst, String newPasswordst) {
        UserBean user = MyApplication.getUser(this);
        Map<String, String> map = new HashMap<>();
        map.put("uid", user.getId());
        map.put("original_password", oldPasswordst);
        map.put("password", newPasswordst);
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在修改")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                SaveDate.getInstence(context).setUser("");
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        HttpClient.changePWD(this, map, listenner);
    }

    private void initView() {
        oldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showBt();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showBt();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        rePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showBt();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showBt() {
        if (TextUtils.isEmpty(oldPassword.getText().toString()) ||
                TextUtils.isEmpty(newPassword.getText().toString()) ||
                TextUtils.isEmpty(rePassword.getText().toString()) ||
                newPassword.getText().toString().length() < 6 ||
                rePassword.getText().toString().length() < 6) {
            confirm.setEnabled(false);
        } else {
            confirm.setEnabled(true);
        }
    }
}
