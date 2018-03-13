package com.gongwu.wherecollect.activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.afragment.MainGoodsFragment;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.ToastUtil;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class RegisteActivity extends BaseViewActivity implements TextWatcher {
    @Bind(R.id.email_edit)
    EditText emailEdit;
    @Bind(R.id.pwd_edit)
    EditText pwdEdit;
    @Bind(R.id.pwd_agin_edit)
    EditText pwdAginEdit;
    @Bind(R.id.confirm)
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registe);
        ButterKnife.bind(this);
        titleLayout.setTitle("注册");
        titleLayout.setBack(true, null);
        initView();
    }

    private void initView() {
        emailEdit.addTextChangedListener(this);
        pwdEdit.addTextChangedListener(this);
        pwdAginEdit.addTextChangedListener(this);
    }

    private void setBtn() {
        if (TextUtils.isEmpty(emailEdit.getText())
                || TextUtils.isEmpty(pwdEdit.getText())
                || TextUtils.isEmpty(pwdAginEdit.getText())) {
            confirm.setEnabled(false);
            return;
        }
        confirm.setEnabled(true);
    }

    @OnClick(R.id.confirm)
    public void onClick() {
        if (!pwdEdit.getText().toString().equals(pwdAginEdit.getText().toString())) {
            ToastUtil.show(this, "输入的密码不一致", Toast.LENGTH_LONG);
            return;
        }
        register();
    }

    public void register() {
        Map<String, String> map = new TreeMap<>();
        map.put("avatar", "http://7xroa4.com1.z0.glb.clouddn.com/default/shounaer_icon.png");
        map.put("mail", emailEdit.getText().toString());
        map.put("nickname", emailEdit.getText().toString());
        map.put("password", pwdEdit.getText().toString());
        map.put("sex", "女");
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在注册")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                logoutTest(MyApplication.getUser(RegisteActivity.this));
                SaveDate.getInstence(RegisteActivity.this).setUser(r.getResult());
                UserBean user = JsonUtils.objectFromJson(r.getResult(), UserBean.class);
                MyApplication.setUser(user);
                EventBus.getDefault().post(new EventBusMsg.ChangeUser());
                Intent intent = new Intent(RegisteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        HttpClient.register(this, map, listenner);
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
