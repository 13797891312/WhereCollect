package com.gongwu.wherecollect.afragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationLook.TabLocationView;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.AddSharePersonActivity;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareListFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private final int START_CODE = 0x189;

    @Bind(R.id.my_share_title_view)
    TabLocationView mTabView;
    @Bind(R.id.my_share_view_page)
    ViewPager mViewPager;
    @Bind(R.id.add_my_share_tv)
    TextView addShareBtn;

    private List<BaseFragment> fragments = null;
    private CustomPagerAdapter mAdapter;


    private View view;
    private boolean init, onShow;

    public ShareListFragment() {
    }

    public static ShareListFragment newInstance() {
        ShareListFragment fragment = new ShareListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_share_list, container, false);
        }
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    private void initEvent() {
        mViewPager.addOnPageChangeListener(this);
        mTabView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewPager.setCurrentItem(mTabView.getSelection());
            }
        });
    }

    private void initView() {
        fragments = new ArrayList<>();
        List<ObjectBean> beanList = new ArrayList<>();
        fragments.add(new SharePersonFragment());
        fragments.add(new ShareSpaceFragment());
        ObjectBean objectBean1 = new ObjectBean();
        objectBean1.setName("共享人");
        ObjectBean objectBean2 = new ObjectBean();
        objectBean2.setName("共享空间");
        beanList.add(objectBean1);
        beanList.add(objectBean2);
        mTabView.init(beanList);
        mAdapter = new CustomPagerAdapter(getActivity().getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        mTabView.setSelection(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fragments.get(0).onShow();
            }
        }, 500);

    }

    @Override
    public void onShow() {
        onShow = true;
        if (!init) {
            init = true;
            initView();
            initEvent();
        }
    }

    @OnClick({R.id.add_my_share_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_my_share_tv:
                Intent intent = new Intent(getContext(), AddSharePersonActivity.class);
                startActivityForResult(intent, START_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_CODE && resultCode == Activity.RESULT_OK) {
            fragments.get(0).refreshFragment();
            fragments.get(1).refreshFragment();
        }
        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            fragments.get(0).refreshFragment();
        }
        if (requestCode == 104 && resultCode == Activity.RESULT_OK) {
            fragments.get(1).refreshFragment();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mTabView.setSelection(position);
        mTabView.adapter.notifyDataSetChanged();
        fragments.get(position).onShow();
        isShowAddShareBtn(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void isShowAddShareBtn(int position) {
        switch (position) {
            case 0:
                addShareBtn.setVisibility(View.VISIBLE);
                break;
            case 1:
                addShareBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onShow = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    class CustomPagerAdapter extends FragmentPagerAdapter {

        private List<BaseFragment> mFragments;

        public CustomPagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
            super(fm);
            this.mFragments = fragments;
            fm.beginTransaction().commitAllowingStateLoss();
        }

        @Override
        public BaseFragment getItem(int position) {
            return this.mFragments.get(position);
        }

        @Override
        public int getCount() {
            return this.mFragments.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.updateShareMsg msg) {
    }
}
