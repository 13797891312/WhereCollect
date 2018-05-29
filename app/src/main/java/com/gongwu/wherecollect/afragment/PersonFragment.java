package com.gongwu.wherecollect.afragment;


import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.SaveDate;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        return view;
    }

    /**
     * 刷新UI
     */
    public void refrashUi() {
        user = MyApplication.getUser(getActivity());
        if (user == null)
            return;
        ImageLoader.loadCircle(getActivity(), personIv, user.getAvatar(), R.mipmap.ic_launcher);
        userName.setText(user.getNickname());
        boolean isBreathLook = SaveDate.getInstence(getContext()).getBreathLook(user.getId());
        switchCompat.setChecked(!isBreathLook);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
