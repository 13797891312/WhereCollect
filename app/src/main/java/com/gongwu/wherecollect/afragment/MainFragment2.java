package com.gongwu.wherecollect.afragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.FeedBackActivity;
import com.gongwu.wherecollect.activity.LoginActivity;
import com.gongwu.wherecollect.activity.TestActivity;
import com.gongwu.wherecollect.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class MainFragment2 extends BaseFragment {
    View view;
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
    @Bind(R.id.tv_detail)
    TextView tvDetail;
    @Bind(R.id.tv_feedBack)
    TextView tvFeedBack;
    @Bind(R.id.tv_help)
    TextView tvHelp;
    @Bind(R.id.tv_loginOut)
    TextView tvLoginOut;

    public MainFragment2() {
        // Required empty public constructor
    }

    public static MainFragment2 newInstance() {
        MainFragment2 fragment = new MainFragment2();
        Bundle args = new Bundle();
        //        args.putString(ARG_PARAM1, param1);
        //        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        if (getArguments() != null) {
        //            mParam1 = getArguments().getString(ARG_PARAM1);
        //            mParam2 = getArguments().getString(ARG_PARAM2);
        //        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_fragment2, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.head_layout, R.id.tv_detail, R.id.tv_feedBack, R.id.tv_help, R.id.tv_loginOut})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.head_layout:
                ToastUtil.show(getActivity(), "头像", Toast.LENGTH_SHORT);
                break;
            case R.id.tv_detail:
                ToastUtil.show(getActivity(), "账号信息", Toast.LENGTH_SHORT);
                break;
            case R.id.tv_feedBack:
                intent = new Intent(getActivity(), FeedBackActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_help:
                intent = new Intent(getActivity(), TestActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_loginOut:
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
