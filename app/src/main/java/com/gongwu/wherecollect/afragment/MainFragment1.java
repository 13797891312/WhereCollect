package com.gongwu.wherecollect.afragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationEdit.LocationEditActivity;
import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.activity.LoginActivity;
import com.gongwu.wherecollect.activity.MainActivity;
import com.gongwu.wherecollect.activity.ShareSpaceDetailsActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.LocationBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.SharedLocationBean;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.view.PileAvertView;
import com.umeng.analytics.MobclickAgent;
import com.zhaojin.myviews.MyFragmentLayout_line;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment1 extends BaseFragment implements View.OnClickListener {
    public List<Fragment> fragments = new ArrayList();
    View view;
    @Bind(R.id.myFragmentLayout)
    public MyFragmentLayout_line myFragmentLayout;
    ImageButton serchBtn, filterBtn;
    RelativeLayout shijiBtn;
    //    View shijired;
    PileAvertView share_user_list_tv;
    TextView editBtn;
    private LocationBean selectBean;

    public MainFragment1() {
        // Required empty public constructor
    }

    public static MainFragment1 newInstance() {
        MainFragment1 fragment = new MainFragment1();
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
        view = inflater.inflate(R.layout.fragment_main_fragment1, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initFragment();
        serchBtn = (ImageButton) view.findViewById(R.id.serch_btn);
        filterBtn = (ImageButton) view.findViewById(R.id.fiter_btn);
        shijiBtn = (RelativeLayout) view.findViewById(R.id.shiji_layout);
        share_user_list_tv = (PileAvertView) view.findViewById(R.id.share_user_list_view);
//        shijired = view.findViewById(R.id.shiji_red_circle);
        editBtn = (TextView) view.findViewById(R.id.text_edit);
        editBtn.setOnClickListener(this);
        filterBtn.setOnClickListener(this);
        serchBtn.setOnClickListener(this);
        shijiBtn.setOnClickListener(this);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                myFragmentLayout.setCurrenItem(1);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    private void initFragment() {
        fragments.add(MainGoodsFragment.newInstance());
        fragments.add(MainLocationFragment.newInstance());
        //        fragments.add(new ChooseingExerciseFragment1(resource));
        myFragmentLayout.setScorllToNext(true);
        myFragmentLayout.setScorll(true);
        myFragmentLayout.setWhereTab(1);
        myFragmentLayout.setTabHeight((int) (3 * BaseViewActivity.getScreenScale(getActivity())), getResources()
                .getColor(R.color
                        .maincolor), true, 0);
        myFragmentLayout.setOnChangeFragmentListener(new MyFragmentLayout_line.ChangeFragmentListener() {
            @Override
            public void change(int lastPosition, int positon,
                               View lastTabView, View currentTabView) {
                if (positon == 0) {
                    serchBtn.setVisibility(View.VISIBLE);
                    filterBtn.setVisibility(View.VISIBLE);
                    shijiBtn.setVisibility(View.GONE);
                    editBtn.setVisibility(View.GONE);
                    ((MainLocationFragment) fragments.get(1)).hideObjectList();
                } else {
                    serchBtn.setVisibility(View.INVISIBLE);
                    filterBtn.setVisibility(View.INVISIBLE);
                    shijiBtn.setVisibility(View.VISIBLE);
                    editBtn.setVisibility(View.VISIBLE);
                }
                ((BaseFragment) fragments.get(positon)).onShow();
            }
        });
        myFragmentLayout.setAdapter(fragments, R.layout.tablayout_main_fragment1, 0x202);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shiji_layout:
                if (MyApplication.getUser(getActivity()).isTest()) {
                    DialogUtil.show("提醒", "该功能登陆后才可使用", "去登陆", "取消", getActivity(), new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    }, null);
                    return;
                }
//                ((MainLocationFragment) fragments.get(1)).shijiClick();
                startLocationAct();
                break;
            case R.id.serch_btn:
                ((MainActivity) getActivity()).searchClick();
                break;
            case R.id.fiter_btn:
                ((MainActivity) getActivity()).filterClick();
                break;
            case R.id.text_edit:
                if (MyApplication.getUser(getActivity()).isTest()) {
                    DialogUtil.show("注意", "目前为试用账号，登陆后将清空试用账号所有数据", "去登录", "知道了", getActivity(), new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), LocationEditActivity.class);
                            startActivity(intent);
                            MobclickAgent.onEvent(getActivity(), "050101");
                        }
                    }).setCancelable(true);
                } else {
                    Intent intent = new Intent(getActivity(), LocationEditActivity.class);
                    startActivity(intent);
                    MobclickAgent.onEvent(getActivity(), "050101");
                }
                break;
        }
    }

    private void startLocationAct() {
        SharedLocationBean locationBean = new SharedLocationBean();
        locationBean.setName(selectBean.getName());
        locationBean.setCode(selectBean.getCode());
        locationBean.setShared_users(selectBean.getSharedUsers());
        Intent intent = new Intent(getContext(), ShareSpaceDetailsActivity.class);
        intent.putExtra("locationBean", locationBean);
        startActivity(intent);
    }

    /**
     * 定位到某个物品
     *
     * @param objectBean
     */
    public void findObject(final ObjectBean objectBean) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                myFragmentLayout.setCurrenItem(1);
                ((MainLocationFragment) fragments.get(1)).findObject(objectBean);
            }
        });
    }

    /**
     * 设置左上角室迹按钮的红点展示与隐藏
     */
    public void setRedStatus(boolean isHasRed) {
//        if (shijired != null) {
//            shijired.setVisibility(isHasRed ? View.VISIBLE : View.GONE);
//        }
    }

    //呼吸查看
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.showShareImgList msg) {
        if (msg.shareUser != null && msg.shareUser.getSharedUsers() != null && msg.shareUser.getSharedUsers().size() > 0) {
            selectBean = msg.shareUser;
            share_user_list_tv.setUserImages(msg.shareUser.getSharedUsers());
            shijiBtn.setVisibility(View.VISIBLE);
        } else {
            shijiBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onShow() {
        if (myFragmentLayout.getCurrentPosition() == 1) {
            ((BaseFragment) fragments.get(1)).onShow();
        }
    }
}
