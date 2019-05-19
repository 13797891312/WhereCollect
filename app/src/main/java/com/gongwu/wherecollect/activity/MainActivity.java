package com.gongwu.wherecollect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.afragment.BaseFragment;
import com.gongwu.wherecollect.afragment.MainFragment1;
import com.gongwu.wherecollect.afragment.MainFragment2;
import com.gongwu.wherecollect.afragment.PersonFragment;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.MessageBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.object.AddGoodsActivity;
import com.gongwu.wherecollect.service.TimerService;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.PermissionUtil;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.MainDrawerView;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.zhaojin.myviews.MyFragmentLayout1;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseViewActivity {
    public List<Fragment> fragments = new ArrayList();
    @Bind(R.id.id_drawerlayout)
    public DrawerLayout idDrawerlayout;
    @Bind(R.id.filterView)
    public MainDrawerView filterView;
    @Bind(R.id.myFragmentLayout)
    MyFragmentLayout1 myFragmentLayout;
    @Bind(R.id.activity_main)
    RelativeLayout activityMain;
    @Bind(R.id.add_btn)
    ImageButton addBtn;
    long exitTime;
    private int tabImages[][] = {
            {R.drawable.icon_tab1_active, R.drawable.icon_tab1_normal},
            {R.drawable.icon_tab2_active, R.drawable.icon_tab2_normal}};
    private String title[] = {"查看", "我的"};
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        titleLayout.setTitle("主界面");
        titleLayout.setBack(false, null);
        titleLayout.setVisibility(View.GONE);
        EventBus.getDefault().register(this);
        initBugly();
        checkSDcard();
        initView();
        //启动Android定时器，并且启动服务 请求消息接口
        TimerService.getConnet(this);
        //        test();
    }

    private void initBugly() {
        Beta.initDelay = 1 * 500;
        Beta.autoCheckUpgrade = true;
        Bugly.init(getApplicationContext(), "2c5269d1f5", true);
        Beta.checkUpgrade(false, false);//检测更新
    }

    /**
     * 检测是否有SD卡或者储存权限
     */
    private void checkSDcard() {
        File file = new File(MyApplication.CACHEPATH);
        if (!file.canWrite()) {
            new PermissionUtil(this, getResources().getString(R.string.permission_sdcard));
        }
    }

    private void initView() {
        idDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        idDrawerlayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                idDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                filterView.getFilterList();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                idDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        fragments.add(MainFragment1.newInstance());
//        fragments.add(MainFragment2.newInstance());
        fragments.add(PersonFragment.newInstance());
        myFragmentLayout = (MyFragmentLayout1) this.findViewById(R.id.myFragmentLayout);
        myFragmentLayout.setScorllToNext(false);
        myFragmentLayout.setScorll(true);
        myFragmentLayout.setWhereTab(0);
        myFragmentLayout.setOnChangeFragmentListener(new MyFragmentLayout1.ChangeFragmentListener() {
            @Override
            public void change(int lastPosition, int positon,
                               View lastTabView, View currentTabView) {
                // TODO Auto-generated method stub
                titleLayout.setTitle(title[positon]);
                ((ImageView) lastTabView.findViewById(R.id.tab_img))
                        .setImageResource(tabImages[lastPosition][1]);
                ((ImageView) currentTabView.findViewById(R.id.tab_img))
                        .setImageResource(tabImages[positon][0]);
                ((BaseFragment) fragments.get(positon)).onShow();
            }
        });
        myFragmentLayout.setAdapter(fragments, R.layout.tablayout_main_activity, 0x101);
        myFragmentLayout.overrideTabClickListenner(1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getUser(context).isTest()) {
                    startActivity(new Intent(context, LoginActivity.class));
                } else {
                    myFragmentLayout.setCurrenItem(1);
                }
            }
        });
    }

    public void searchClick() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void filterClick() {
        idDrawerlayout.openDrawer(Gravity.RIGHT);
    }

    private boolean isMessage = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.GetMessageList msg) {
        if (isMessage) {
            return;
        }
        final MessageBean messageBean = msg.messageBean;
        String okStr = "";
        String okUrl = "";
        String cancelStr = "";
        String cancelUrl = "";
        if (messageBean.getButtons().size() > 0) {
            for (int i = 0; i < messageBean.getButtons().size(); i++) {
                if (messageBean.getButtons().get(i).getColor().equals("SUCCESS")) {
                    okStr = messageBean.getButtons().get(i).getText();
                    okUrl = TextUtils.isEmpty(messageBean.getButtons().get(i).getApi_url()) ? "" :
                            messageBean.getButtons().get(i).getApi_url();
                }
                if (messageBean.getButtons().get(i).getColor().equals("DANGER")
                        || messageBean.getButtons().get(i).getColor().equals("DEFAULT")) {
                    cancelStr = messageBean.getButtons().get(i).getText();
                    cancelUrl = TextUtils.isEmpty(messageBean.getButtons().get(i).getApi_url()) ? "" :
                            messageBean.getButtons().get(i).getApi_url();
                }
            }
        } else {
            LogUtil.e("消息没有buttons");
            return;
        }
        final String finalOkUrl = okUrl;
        final String finalCancelUrl = cancelUrl;
        isMessage = true;
        dialog = DialogUtil.showMsg("", messageBean.getContent(), okStr, cancelStr, this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isMessage = false;
                if (!messageBean.isIs_read() && !TextUtils.isEmpty(finalOkUrl)) {
                    postShareHttp(finalOkUrl);
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isMessage = false;
                if (!messageBean.isIs_read() && !TextUtils.isEmpty(finalCancelUrl)) {
                    postShareHttp(finalCancelUrl);
                }
            }
        });
    }

    private void postShareHttp(String url) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                EventBus.getDefault().post(new EventBusMsg.updateShareMsg());
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.dealWithShareRequest(context, url, map, listenner);
    }

    /**
     * 用户登录会收到消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ChangeUser msg) {
        ((PersonFragment) fragments.get(1)).refreshUi();
        myFragmentLayout.setCurrenItem(0);
        filterView.getFilterList();
        MainLocationFragment.locationMap.clear();
        MainLocationFragment.pageMap.clear();
        MainLocationFragment.mlist.clear();
//        ((MainFragment2) fragments.get(1)).setRedStatus();
    }

    /**
     * 停止服务
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.stopService msg) {
        //停止由AlarmManager启动的循环
        TimerService.stop(this);
        //停止由服务启动的循环
        Intent intent = new Intent(this, TimerService.class);
        stopService(intent);
    }

    /**
     * 启动服务
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.startService msg) {
        //启动Android定时器，并且启动服务
        TimerService.getConnet(this);
    }

    /**
     * 室迹暂存或者保存过要显示小红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.RecordChange msg) {
        switch (msg.isSave) {
            case 0:
//                ((MainFragment2) fragments.get(1)).setRedStatus();
                break;
            case 1:
//                ((MainFragment1) fragments.get(0)).setRedStatus(true);//暂存，显示红点
                break;
            case 2:
//                ((MainFragment1) fragments.get(0)).setRedStatus(false);//取消暂存，隐藏红点
                break;
        }
    }

    /**
     * 设置红点是否显示,fragment2里的检测会回调这个方法
     *
     * @param isRed
     */
    public void setRed(boolean isRed) {
        myFragmentLayout.getTabLayout().findViewById(R.id.my_red).setVisibility(isRed ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ObjectBean objectBean = (ObjectBean) intent.getSerializableExtra("object");//需要定位的物品
        if (objectBean != null) {
            ((MainFragment1) fragments.get(0)).findObject(objectBean);
        }
    }

    @Override
    protected void onDestroy() {
        //停止由AlarmManager启动的循环
        TimerService.stop(this);
        //停止由服务启动的循环
        Intent intent = new Intent(this, TimerService.class);
        stopService(intent);
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        myFragmentLayout = null;

    }

    @OnClick(R.id.add_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                if (MyApplication.getUser(context).isTest()) {
                    DialogUtil.show("注意", "目前为试用账号，登录后将清空试用账号所有数据", "去登录", "知道了", MainActivity.this, new
                            DialogInterface
                                    .OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, AddGoodsActivity.class);
                            startActivity(intent);
                        }
                    }).setCancelable(true);
                } else {
//                    Intent intent = new Intent(context, ObjectsAddActivity.class);
//                    startActivity(intent);
                    AddGoodsActivity.start(context);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            exitTime = System.currentTimeMillis();
            ToastUtil.show(this, "再次点击退出应用", Toast.LENGTH_SHORT);
        } else {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            isMessage = true;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isMessage = false;
    }
}
