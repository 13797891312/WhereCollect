package com.gongwu.wherecollect.afragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.FeedBackActivity;
import com.gongwu.wherecollect.activity.MessageListActivity;
import com.gongwu.wherecollect.activity.MyShareActivity;
import com.gongwu.wherecollect.activity.PersonActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.ShareUtil;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.HighOpinionDialog;
import com.gongwu.wherecollect.view.UserCodeDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.deanwild.materialshowcaseview.PrefsManager;

/**
 * 新用户界面
 */
public class PersonFragment extends BaseFragment {

    @Bind(R.id.person_iv)
    ImageView personIv;
    @Bind(R.id.switch_compat)
    SwitchCompat switchCompat;
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.user_id_tv)
    TextView userId;
    @Bind(R.id.refresh_help_iv)
    ImageView refreshHelpIv;

    private UserBean user;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_person, container, false);
        }
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        refreshUi();
        initEvent();
        return view;
    }

    public static PersonFragment newInstance() {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void initEvent() {
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveDate.getInstence(getContext()).setBreathLook(user.getId(), !isChecked);
                EventBusMsg.GoodsIsCloseBreathLook msg = new EventBusMsg.GoodsIsCloseBreathLook(!isChecked);
                EventBus.getDefault().post(msg);
            }
        });
    }

    @OnClick({R.id.person_layout, R.id.feedback_layout, R.id.message_list_layout,
            R.id.user_code_layout, R.id.my_share_layout, R.id.refresh_help_iv
            , R.id.person_high_opinion_layout, R.id.user_share_app})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.person_layout://个人信息
                intent = new Intent(getActivity(), PersonActivity.class);
                startActivity(intent);
                break;
            case R.id.feedback_layout://意见反馈
                intent = new Intent(getActivity(), FeedBackActivity.class);
                startActivity(intent);
                break;
            case R.id.message_list_layout://消息中心
                MessageListActivity.start(getContext());
                break;
            case R.id.user_code_layout://二维码
                Glide.with(getContext()).load(user.getAvatar()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        UserCodeDialog userCodeDialog = new UserCodeDialog(getActivity());
                        userCodeDialog.showDialog(user.getUsid(), resource);
                    }
                }); //方法中设置asBitmap可以设置回调类型
                break;
            case R.id.my_share_layout://共享空间
                MyShareActivity.start(getContext());
                break;
            case R.id.refresh_help_iv:
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
                refreshHelpIv.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ToastUtil.show(getContext(), "操作成功", Toast.LENGTH_SHORT);
                        PrefsManager.resetAll(getContext());
                        refreshHelpIv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.refresh_help_gray));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
            case R.id.person_high_opinion_layout:
                HighOpinionDialog dialog = new HighOpinionDialog(getActivity()) {
                    @Override
                    public void startApp() {
                        openApplicationMarket("com.gongwu.wherecollect");
                    }

                    @Override
                    public void startAct() {
                        Intent intent = new Intent(getActivity(), FeedBackActivity.class);
                        startActivity(intent);
                    }
                };
                dialog.showDialog();
                break;
            case R.id.user_share_app:
                ShareUtil.openShareDialog(getActivity());
                break;
            default:
                break;
        }
    }

    /**
     * 通过包名 在应用商店打开应用
     *
     * @param packageName 包名
     */
    private void openApplicationMarket(String packageName) {
        try {
            String str = "market://details?id=" + packageName;
            Intent localIntent = new Intent(Intent.ACTION_VIEW);
            localIntent.setData(Uri.parse(str));
            startActivity(localIntent);
        } catch (Exception e) {
            // 打开应用商店失败 可能是没有手机没有安装应用市场
            e.printStackTrace();
            Toast.makeText(getContext(), "打开应用商店失败", Toast.LENGTH_SHORT).show();
            // 调用系统浏览器进入商城
            String url = "http://app.mi.com/detail/163525?ref=search";
            openLinkBySystem(url);
        }
    }

    /**
     * 调用系统浏览器打开网页
     *
     * @param url 地址
     */
    private void openLinkBySystem(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    /**
     * 刷新UI
     */
    public void refreshUi() {
        user = MyApplication.getUser(getActivity());
        if (user == null)
            return;
        ImageLoader.loadCircle(getActivity(), personIv, user.getAvatar(), R.mipmap.ic_launcher);
        userName.setText(user.getNickname());
        boolean isBreathLook = SaveDate.getInstence(getContext()).getBreathLook(user.getId());
        switchCompat.setChecked(!isBreathLook);
        if (!TextUtils.isEmpty(user.getOpenid())) {
            getUserInfoData();
        }
    }

    private void getUserInfoData() {
        Map<String, String> map = new HashMap<>();
        map.put("uid", user.getId());
        map.put("password", user.getOpenid());
        PostListenner listenner = new PostListenner(getContext(), null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                UserBean userBean = JsonUtils.objectFromJson(r.getResult(), UserBean.class);
                if (userBean != null) {
                    userId.setText("ID: " + userBean.getUsid());
                }
            }
        };
        HttpClient.getUserInfo(getContext(), user.getId(), map, listenner);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onShow() {
        if (user != null && !TextUtils.isEmpty(user.getOpenid())) {
            getUserInfoData();
        }
    }
}
