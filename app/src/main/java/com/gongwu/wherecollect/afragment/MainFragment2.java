package com.gongwu.wherecollect.afragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.volley.request.QiNiuUploadUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.AccountInfoActivity;
import com.gongwu.wherecollect.activity.FeedBackActivity;
import com.gongwu.wherecollect.activity.LoginActivity;
import com.gongwu.wherecollect.activity.MainActivity;
import com.gongwu.wherecollect.activity.WebActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.record.RecordListActivity;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.ShareUtil;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.ChangeHeaderImgDialog;
import com.gongwu.wherecollect.view.ChangeSexDialog;
import com.gongwu.wherecollect.view.DateBirthdayDialog;
import com.gongwu.wherecollect.view.EditTextDialog;
import com.zhaojin.myviews.Loading;

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
    String token = "";
    @Bind(R.id.roomrecord_red)
    View roomrecordRed;
    @Bind(R.id.tv_share)
    TextView tvShare;
    @Bind(R.id.share_red)
    View shareRed;
    @Bind(R.id.version_tv)
    TextView versionTv;
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
        setRedStatus();
        versionTv.setText(String.format("收哪儿v%s（%d）", StringUtils.getCurrentVersionName(getActivity()), StringUtils
                .getCurrentVersion
                        (getActivity())));
        return view;
    }

    /**
     * 刷新UI
     */
    public void refrashUi() {
        UserBean user = MyApplication.getUser(getActivity());
        if (user == null)
            return;
        ImageLoader.loadCircle(getActivity(), personIv, user.getAvatar(), R.mipmap.ic_launcher);
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
            .tv_loginOut, R.id.layout_share, R.id.layout_roomrecord})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.head_layout:
                changeHeaderdialog = new ChangeHeaderImgDialog(getActivity(), this, personIv) {
                    @Override
                    public void getResult(File file) {
                        super.getResult(file);
                        ImageLoader.loadCircleFromFile(getActivity(), file, personIv);
                        upLoadImg(file);
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
                DialogUtil.show("提示", "退出将会清空缓存数据,确定退出？", "确定", "取消", getActivity(), new DialogInterface.OnClickListener
                        () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        MyApplication.setUser(null);
                        SaveDate.getInstence(getActivity()).setUser("");
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                }, null);
                break;
            case R.id.layout_roomrecord:
                if (roomrecordRed.getVisibility() == View.VISIBLE) {
                    SaveDate.getInstence(getActivity()).setRecordRed(MyApplication.getUser(getActivity()).getId(),
                            true);
                    setRedStatus();
                }
                intent = new Intent(getActivity(), RecordListActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_share:
                if (shareRed.getVisibility() == View.VISIBLE) {
                    showFirstShare();
                } else {
                    ShareUtil.openShareDialog(getActivity());
                }
                if (SaveDate.getInstence(getActivity()).getRecordSaved(MyApplication.getUser(getActivity()).getId())) {
                    SaveDate.getInstence(getActivity()).setShareClicked(MyApplication.getUser(getActivity()).getId(),
                            true);
                    setRedStatus();
                }
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
                    case "avatar":
                        user.setAvatar(value);
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

    /**
     * 上传图片
     */
    private void upLoadImg(final File file) {
        List<File> temp = new ArrayList<>();
        temp.add(file);
        QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(getActivity(), temp, "user/head/") {
            @Override
            protected void finish(List<String> list) {
                super.finish(list);
                changeInfo("avatar", list.get(0));
            }
        };
        uploadUtil.start();
    }

    /**
     * 查询小红点的显示与否
     */
    public boolean setRedStatus() {
        if(MyApplication.getUser(getActivity()).isTest()){
            ((MainActivity) getActivity()).setRed(true);
            return true;
        }
        boolean isHasRed = false;
        UserBean userBean = MyApplication.getUser(getActivity());
        if (SaveDate.getInstence(getActivity()).getRecordSaved(userBean.getId())//保存过室迹
                && !SaveDate.getInstence(getActivity()).getRecordRed(userBean.getId())) {//没消除过红点
            roomrecordRed.setVisibility(View.VISIBLE);
            isHasRed = true;
        } else {
            roomrecordRed.setVisibility(View.GONE);
        }
        if (SaveDate.getInstence(getActivity()).getRecordSaved(userBean.getId())
                && !SaveDate.getInstence(getActivity()).getShareClicked(userBean.getId())) {//如果点击过分享
            shareRed.setVisibility(View.VISIBLE);
            isHasRed = true;
        } else {
            shareRed.setVisibility(View.GONE);
        }
        ((MainActivity) getActivity()).setRed(isHasRed);
        return isHasRed;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }

    /**
     * 第一次用本功能的提示
     */
    private void showFirstShare() {
        View view = View.inflate(getActivity(), R.layout.layout_popwindow_share_help, null);
        ((TextView) view.findViewById(R.id.textview)).setText(Html.fromHtml("所以如果喜欢，<font " +
                "color='#25B65A'>也请将我们推荐给你身边的好友</font>，一起见证我们的成长。"));
        PopupWindow popupWindow = new PopupWindow(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams
                .WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.pop_bottomin_bottomout);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(getActivity().findViewById(R.id.base_layout), Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
                ShareUtil.openShareDialog(getActivity());
            }
        });
        setBackgroundAlpha(0.5f);
    }
}
