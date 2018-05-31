package com.gongwu.wherecollect.afragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.FeedBackActivity;
import com.gongwu.wherecollect.activity.PersonActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.SaveDate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonFragment extends BaseFragment {

    @Bind(R.id.person_iv)
    ImageView personIv;
    @Bind(R.id.switch_compat)
    SwitchCompat switchCompat;
    @Bind(R.id.user_name)
    TextView userName;

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

    @OnClick({R.id.person_layout, R.id.feedback_layout})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.person_layout:
                intent = new Intent(getActivity(), PersonActivity.class);
                startActivity(intent);
                break;
            case R.id.feedback_layout:
                intent = new Intent(getActivity(), FeedBackActivity.class);
                startActivity(intent);
                break;
        }
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
}
