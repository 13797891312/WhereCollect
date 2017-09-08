package com.gongwu.wherecollect.activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
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
/**
 * Function: 绑定邮箱
 * Date: 2016-08-20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class BindEmailActivity extends BaseViewActivity implements TextWatcher {
    @Bind(R.id.email_edit)
    EditText emailEdit;
    @Bind(R.id.pwd_edit)
    EditText pwdEdit;
    @Bind(R.id.confirm)
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_email);
        ButterKnife.bind(this);
        titleLayout.setTitle("绑定邮箱");
        titleLayout.setBack(true, null);
        initView();
    }

    @OnClick(R.id.confirm)
    public void onClick() {
        if(pwdEdit.getText().length()<6||pwdEdit.getText().length()>16){
            ToastUtil.show(this,"密码为6-16位，请输入正确的密码", Toast.LENGTH_LONG);
            return;
        }
        bind();
    }

    public void bind() {
        Map<String, String> map = new TreeMap<>();
        map.put("mail", emailEdit.getText().toString());
        map.put("password", pwdEdit.getText().toString());
        map.put("type", "MAIL");
        map.put("uid", MyApplication.getUser(this).getId());
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在登陆")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                MyApplication.getUser(BindEmailActivity.this).setMail(emailEdit.getText().toString());
                SaveDate.getInstence(BindEmailActivity.this).setUser(JsonUtils.jsonFromObject
                        (MyApplication.getUser(BindEmailActivity.this)));
                EventBus.getDefault().post(MyApplication.getUser(BindEmailActivity.this));
                finish();
            }
        };
        HttpClient.bindOther(this, map, listenner);
    }

    private void initView() {
        emailEdit.addTextChangedListener(this);
        pwdEdit.addTextChangedListener(this);
    }

    private void setBtn() {
        if (TextUtils.isEmpty(emailEdit.getText())
                || TextUtils.isEmpty(pwdEdit.getText())) {
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

    ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
