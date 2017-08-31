package com.gongwu.wherecollect.activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.afragment.BaseFragment;
import com.gongwu.wherecollect.afragment.MainFragment1;
import com.gongwu.wherecollect.afragment.MainFragment2;
import com.gongwu.wherecollect.afragment.MainLocationFragment;
import com.zhaojin.myviews.MyFragmentLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
public class MainActivity extends BaseViewActivity {
    public List<Fragment> fragments = new ArrayList();
    @Bind(R.id.myFragmentLayout)
    MyFragmentLayout myFragmentLayout;
    @Bind(R.id.activity_main)
    RelativeLayout activityMain;
    @Bind(R.id.id_drawerlayout)
    DrawerLayout idDrawerlayout;
    private int tabImages[][] = {
            {R.drawable.icon_1_select_new, R.drawable.icon_1_unselect_new},
            {R.drawable.icon_2_select_new, R.drawable.icon_2_unselect_new}};
    private String title[] = {"查看", "我的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        titleLayout.setTitle("主界面");
        titleLayout.setBack(false, null);
        titleLayout.setVisibility(View.GONE);
        initView();
    }

    private void initView() {
        final int mainColor = getResources().getColor(R.color.maincolor);
        final int black26Color = getResources().getColor(R.color.black_26);
        fragments.add(MainFragment1.newInstance());
        fragments.add(MainFragment2.newInstance());
        myFragmentLayout = (MyFragmentLayout) this.findViewById(R.id.myFragmentLayout);
        myFragmentLayout.setScorllToNext(true);
        myFragmentLayout.setScorll(true);
        myFragmentLayout.setWhereTab(0);
        myFragmentLayout.setOnChangeFragmentListener(new MyFragmentLayout.ChangeFragmentListener() {
            @Override
            public void change(int lastPosition, int positon,
                               View lastTabView, View currentTabView) {
                // TODO Auto-generated method stub
                titleLayout.setTitle(title[positon]);
                ((TextView) lastTabView.findViewById(R.id.tab_text))
                        .setTextColor(black26Color);
                ((ImageView) lastTabView.findViewById(R.id.tab_img))
                        .setImageResource(tabImages[lastPosition][1]);
                ((TextView) currentTabView.findViewById(R.id.tab_text))
                        .setTextColor(mainColor);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myFragmentLayout = null;
        MainLocationFragment.editLocationView = null;
    }
}
