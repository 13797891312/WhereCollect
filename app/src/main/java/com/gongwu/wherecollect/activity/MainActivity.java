package com.gongwu.wherecollect.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.afragment.BaseFragment;
import com.gongwu.wherecollect.afragment.MainFragment1;
import com.gongwu.wherecollect.afragment.MainFragment2;
import com.gongwu.wherecollect.afragment.MainLocationFragment;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.PermissionUtil;
import com.zhaojin.myviews.MyFragmentLayout;
import com.zhaojin.myviews.MyFragmentLayout1;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
public class MainActivity extends BaseViewActivity {
    public List<Fragment> fragments = new ArrayList();
    @Bind(R.id.myFragmentLayout)
    MyFragmentLayout1 myFragmentLayout;
    @Bind(R.id.activity_main)
    RelativeLayout activityMain;
    @Bind(R.id.id_drawerlayout)
    DrawerLayout idDrawerlayout;
    private int tabImages[][] = {
            {R.drawable.icon_tab1_active, R.drawable.icon_tab1_normal},
            {R.drawable.icon_tab2_active, R.drawable.icon_tab2_normal}};
    private String title[] = {"查看", "我的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        titleLayout.setTitle("主界面");
        titleLayout.setBack(false, null);
        titleLayout.setVisibility(View.GONE);
        EventBus.getDefault().register(this);
        checkSDcard();
        initView();
        if (MyApplication.getUser(this) == null) {
            //TODO  没有登陆
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            //TODO 有登陆过
        }
    }

    /**
     * 检测是否有SD卡或者储存权限
     */
    private void checkSDcard() {
        File file = new File(MyApplication.CACHEPATH);
        if (!file.exists()) {
            new PermissionUtil(this, getResources().getString(R.string.permission_sdcard));
        }
    }

    private void initView() {
        fragments.add(MainFragment1.newInstance());
        fragments.add(MainFragment2.newInstance());
        myFragmentLayout = (MyFragmentLayout1) this.findViewById(R.id.myFragmentLayout);
        myFragmentLayout.setScorllToNext(true);
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
                if (positon == 0) {
                    idDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
                } else {
                    idDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            }
        });
        myFragmentLayout.setAdapter(fragments, R.layout.tablayout_main_activity, 0x101);
    }

    public void searchClick() {
        idDrawerlayout.openDrawer(Gravity.RIGHT);
    }

    /**
     * 用户登录会收到消息
     *
     * @param userBean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserBean userBean) {
        ((MainFragment2) fragments.get(1)).refrashUi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        myFragmentLayout = null;
        MainLocationFragment.editLocationView = null;
    }
}
