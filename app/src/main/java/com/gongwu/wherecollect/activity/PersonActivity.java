package com.gongwu.wherecollect.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.volley.request.QiNiuUploadUtil;
import android.widget.ImageView;
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
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.ChangeHeaderImgDialog;
import com.gongwu.wherecollect.view.ChangeSexDialog;
import com.gongwu.wherecollect.view.DateBirthdayDialog;
import com.gongwu.wherecollect.view.EditTextDialog;
import com.umeng.socialize.Config;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人信息
 */
public class PersonActivity extends BaseViewActivity {

    @Bind(R.id.person_iv)
    ImageView personIv;
    @Bind(R.id.head_layout)
    LinearLayout headLayout;
    @Bind(R.id.tv_nick)
    TextView tvNick;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.tv_birthday)
    TextView tvBirthday;
    @Bind(R.id.tv_loginOut)
    TextView tvLoginOut;
    @Bind(R.id.version_tv)
    TextView versionTv;
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
    @Bind(R.id.tv_changePWD_view)
    View tv_changePWD_view;

    private ChangeHeaderImgDialog changeHeaderdialog;

    private UserBean user;
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("个人信息");
        refreshUi();
        //版本号
        versionTv.setText(String.format("v%s", StringUtils.getCurrentVersionName(context)));
        EventBus.getDefault().register(this);
    }

    /**
     * 刷新UI
     */
    public void refreshUi() {
        user = MyApplication.getUser(context);
        if (user == null)
            return;
        ImageLoader.loadCircle(context, personIv, user.getAvatar(), R.mipmap.ic_launcher);
        tvNick.setText(user.getNickname());
        tvSex.setText(user.getGender());
        tvBirthday.setText(user.getBirthday());
        if (user.isPassLogin()) {
            tvChangePWD.setVisibility(View.VISIBLE);
            tv_changePWD_view.setVisibility(View.VISIBLE);
        } else {
            tvChangePWD.setVisibility(View.GONE);
            tv_changePWD_view.setVisibility(View.GONE);
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

    @OnClick({R.id.head_layout, R.id.nick_layout, R.id.sex_layout, R.id.birth_layout,
            R.id.tv_loginOut, R.id.wx_layout, R.id.wb_layout, R.id.qq_layout, R.id.email_layout,
            R.id.phone_layout, R.id.tv_changePWD})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.head_layout:
                changeHeaderdialog = new ChangeHeaderImgDialog(this, null, personIv) {
                    @Override
                    public void getResult(File file) {
                        super.getResult(file);
                        ImageLoader.loadCircleFromFile(context, file, personIv);
                        upLoadImg(file);
                    }
                };
                break;
            case R.id.nick_layout:
                EditTextDialog nameDialog = new EditTextDialog(context, "昵称", "请输入昵称",
                        EditTextDialog.TYPE_TEXT) {
                    @Override
                    public void result(final String result) {
                        changeInfo("nickname", result);
                    }
                };
                nameDialog.show();
                break;
            case R.id.sex_layout:
                new ChangeSexDialog((Activity) context) {
                    @Override
                    public void getResult(String str) {
                        super.getResult(str);
                        changeInfo("gender", str);
                    }
                };
                break;
            case R.id.birth_layout:
                selectDateDialog();
                break;
            case R.id.tv_loginOut:
                DialogUtil.show("提示", "退出将会清空缓存数据,确定退出？", "确定", "取消", (Activity) context, new DialogInterface.OnClickListener
                        () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        MyApplication.setUser(null);
                        SaveDate.getInstence(context).setUser("");
                        EventBus.getDefault().post(new EventBusMsg.stopService());
                        intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    }
                }, null);
                break;
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
     * 修改资料
     *
     * @param key
     * @param value
     */
    public void changeInfo(final String key, final String value) {
        final UserBean user = MyApplication.getUser(context);
        if (user == null)
            return;
        Map<String, String> map = new HashMap<>();
        map.put("uid", user.getId());
        map.put(key, value);
        PostListenner listenner = new PostListenner(context, Loading.show(null, context,
                "正在修改...")) {
            @Override
            protected void code2000(ResponseResult r) {
                super.code2000(r);
                switch (key) {
                    case "nickname":
                        tvNick.setText(value);
                        user.setNickname(value);
                        break;
                    case "gender":
                        tvSex.setText(value);
                        user.setGender(value);
                        break;
                    case "birthday":
                        tvBirthday.setText(value);
                        user.setBirthday(value);
                        break;
                    case "avatar":
                        user.setAvatar(value);
                        break;
                }
                String stringUser = JsonUtils.jsonFromObject(user);
                SaveDate.getInstence(context).setUser(stringUser);
                EventBus.getDefault().post(new EventBusMsg.ChangeUserInfo());
            }
        };
        HttpClient.editInfo(context, map, listenner);
    }

    public void selectDateDialog() {
        String birthday = "";
        if (TextUtils.isEmpty(MyApplication.getUser(context).getBirthday())) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            birthday = formatter.format(curDate);
        } else {
            birthday = MyApplication.getUser(context).getBirthday();
        }
        DateBirthdayDialog dialog = new DateBirthdayDialog(context, birthday) {
            @Override
            public void result(final int year, final int month, final int day) {
                final String bd = year + "-" + StringUtils.formatIntTime(month) + "-" +
                        StringUtils.formatIntTime(day);
                if (!TextUtils.isEmpty(MyApplication.getUser(context).getBirthday())) {
                    if (MyApplication.getUser(context).getBirthday().equals(bd)) {
                        return;
                    }
                }
                changeInfo("birthday", bd);
            }
        };
        dialog.setCancelBtnText(true);
        dialog.show();
        dialog.setDateMax();
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
                        MyApplication.getUser(context).setQq(qb);
                        break;
                    case "WECHAT":
                        UserBean.WeixinBean wb = new UserBean.WeixinBean();
                        wb.setNickname(otherMap.get("nickname"));
                        wb.setOpenid(otherMap.get("openid"));
                        wb.setUnionid(otherMap.get("openid"));
                        MyApplication.getUser(context).setWeixin(wb);
                        break;
                    case "SINA":
                        UserBean.SinaBean sb = new UserBean.SinaBean();
                        sb.setNickname(otherMap.get("nickname"));
                        sb.setOpenid(otherMap.get("openid"));
                        MyApplication.getUser(context).setSina(sb);
                        break;
                }
                SaveDate.getInstence(context).setUser(JsonUtils.jsonFromObject
                        (MyApplication.getUser(context)));
                refreshUi();
                ToastUtil.show(context, "绑定成功", Toast.LENGTH_LONG);
            }
        };
        HttpClient.bindOther(this, map, listenner);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (changeHeaderdialog != null) {
            changeHeaderdialog.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 上传图片
     */
    private void upLoadImg(final File file) {
        List<File> temp = new ArrayList<>();
        temp.add(file);
        QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(context, temp, "user/head/") {
            @Override
            protected void finish(List<String> list) {
                super.finish(list);
                changeInfo("avatar", list.get(0));
            }
        };
        uploadUtil.start();
    }

    UmAuthListener listener = new UmAuthListener(this);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
