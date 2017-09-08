package com.gongwu.wherecollect.afragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.AccountInfoActivity;
import com.gongwu.wherecollect.activity.FeedBackActivity;
import com.gongwu.wherecollect.activity.LoginActivity;
import com.gongwu.wherecollect.activity.WebActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.ChangeHeaderImgDialog;
import com.gongwu.wherecollect.view.ChangeSexDialog;
import com.gongwu.wherecollect.view.DateBirthdayDialog;
import com.gongwu.wherecollect.view.EditTextDialog;
import com.zhaojin.myviews.Loading;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private ChangeHeaderImgDialog changeHeaderdialog;

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
        refrashUi();
        return view;
    }

    /**
     * 刷新UI
     */
    public void refrashUi() {
        UserBean user = MyApplication.getUser(getActivity());
        if (user == null)
            return;
        ImageLoader.loadCircle(getActivity(), personIv, user.getAvatar(), R.drawable.ic_launcher);
        tvNick.setText(user.getNickname());
        tvSex.setText(user.getGender());
        tvBirthday.setText(user.getBirthday());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.head_layout, R.id.nick_layout, R.id.sex_layout, R.id.birth_layout, R.id.tv_detail, R.id
            .tv_feedBack, R.id
            .tv_help, R.id
            .tv_loginOut})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.head_layout:
                changeHeaderdialog = new ChangeHeaderImgDialog(getActivity(), this, personIv) {
                    @Override
                    public void getResult(File file) {
                        super.getResult(file);
                        ImageLoader.loadCircleFromFile(getActivity(), file, personIv);
                    }
                };
                break;
            case R.id.nick_layout:
                EditTextDialog nameDialog = new EditTextDialog(getActivity(), "昵称", "请输入昵称",
                        EditTextDialog.TYPE_TEXT) {
                    @Override
                    public void result(final String result) {
                        changeInfo("nickname", result);
                    }
                };
                nameDialog.show();
                break;
            case R.id.sex_layout:
                new ChangeSexDialog(getActivity()) {
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
            case R.id.tv_detail:
                intent = new Intent(getActivity(), AccountInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_feedBack:
                intent = new Intent(getActivity(), FeedBackActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_help:
                WebActivity.start(getActivity(), "帮助中心", "http://www.shouner.com/help");
                break;
            case R.id.tv_loginOut:
                MyApplication.setUser(null);
                SaveDate.getInstence(getActivity()).setUser("");
                intent = new Intent(getActivity(), LoginActivity.class);
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
        final UserBean user = MyApplication.getUser(getActivity());
        if (user == null)
            return;
        Map<String, String> map = new HashMap<>();
        map.put("uid", user.getId());
        map.put(key, value);
        PostListenner listenner = new PostListenner(getActivity(), Loading.show(null, getActivity(),
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
                }
                String stringUser = JsonUtils.jsonFromObject(user);
                SaveDate.getInstence(getActivity()).setUser(stringUser);
            }
        };
        HttpClient.editInfo(getActivity(), map, listenner);
    }

    public void selectDateDialog() {
        String birthday = "";
        if (TextUtils.isEmpty(MyApplication.getUser(getActivity()).getBirthday())) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            birthday = formatter.format(curDate);
        } else {
            birthday = MyApplication.getUser(getActivity()).getBirthday();
        }
        DateBirthdayDialog dialog = new DateBirthdayDialog(getActivity(), birthday) {
            @Override
            public void result(final int year, final int month, final int day) {
                final String bd = year + "-" + StringUtils.formatIntTime(month) + "-" +
                        StringUtils.formatIntTime(day);
                if (!TextUtils.isEmpty(MyApplication.getUser(getActivity()).getBirthday())) {
                    if (MyApplication.getUser(getActivity()).getBirthday().equals(bd)) {
                        return;
                    }
                }
                changeInfo("birthday", bd);
            }
        };
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (changeHeaderdialog != null) {
            changeHeaderdialog.onActivityResult(requestCode, resultCode, data);
        }
    }
}
