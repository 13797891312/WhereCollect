package com.zhaojin.myviews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

/**
 * @author 赵进
 * @步骤1 在布局中添加此布局，一般这里选是垂直布局
 * @步骤2 写一个tabLayout布局用来当做tab布局，布局格式参照tablayout.xml记住里面的子控件都用一样的ID，
 * 方便用lastTabView和currentTabView找到
 * @步骤3 代码中找到此布局，代码中设置是否可以滑动切换fragmentLayout.setScorllToNext(false)，
 * 设置点击tab切换时是否带滑动setScorll,设置tab位于viewpager上方还是下方setWhereTab
 * @步骤4 设置切换时的监听，主要用来改变tab项的状态，比如字体颜色，背景颜色等，fragmentLayout.
 * setOnChangeFragmentListener(new ChangeFragmentListener() {
 * @步骤5 设置适配器，fragmentLayout.setAdapter(fList,R.layout.tablayout);
 */
public class MyFragmentLayout extends LinearLayout implements
        OnPageChangeListener {
    private List<Fragment> list;
    private Fragment_viewpager_Adapter fragmentAdapter;
    private FragmentActivity context;
    private ViewPager viewPager;
    private LinearLayout tabLayout;
    private int position = 0;
    private boolean isScorll = false;
    private boolean isScorllToNext = true;
    private int whereTab = 0;
    private ChangeFragmentListener changeListener;

    public interface ChangeFragmentListener {
        /**
         * @param positon        切换到哪项
         * @param lastTabView    上一项的tab视图，用来改变没选中tab状态
         * @param currentTabView 当前想的tab视图,用来改变选中的tab样式
         */
        public void change(int lastPositon, int positon, View lastTabView,
                           View currentTabView);
    }

    public void setOnChangeFragmentListener(ChangeFragmentListener listener) {
        this.changeListener = listener;
    }

    public MyFragmentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        if (!isInEditMode()) {
            this.context = (FragmentActivity) context;
        }
    }

    public MyFragmentLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        if (!isInEditMode()) {
            this.context = (FragmentActivity) context;
        }
    }

    public View getTabLayout() {
        return tabLayout;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    /**
     * @param list        fragment数据
     * @param tabLayoutId tab布局 id
     * @param id          任意 int型，不能重复
     */
    public void setAdapter(List<Fragment> list, int tabLayoutId, int id) {
        this.setOrientation(LinearLayout.VERTICAL);
        tabLayout = (LinearLayout) View.inflate(context, tabLayoutId, null);
        tabLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        this.list = list;
        fragmentAdapter = new Fragment_viewpager_Adapter(
                context.getSupportFragmentManager());
        viewPager = new MyViewPager(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        viewPager.setLayoutParams(params);
        viewPager.setId(id);
        if (whereTab == 0) {
            this.addView(viewPager);
            this.addView(tabLayout);
        } else {
            this.addView(tabLayout);
            this.addView(viewPager);
        }
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            View view = tabLayout.getChildAt(i);
            view.setClickable(true);
            view.setOnClickListener(new tabClickLisener(i));
        }
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    public void overrideTabClickListenner(int position, OnClickListener onClickListener) {
        tabLayout.getChildAt(position).setOnClickListener(onClickListener);
    }

    ;

    private class Fragment_viewpager_Adapter extends FragmentStatePagerAdapter {

        public Fragment_viewpager_Adapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            return list.get(arg0);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            super.destroyItem(container, position, object);
//			((ViewGroup) list.get(position).getView()).removeAllViews();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return PagerAdapter.POSITION_NONE;
        }
    }

    class tabClickLisener implements OnClickListener {
        private int position;

        public tabClickLisener(int position) {
            // TODO Auto-generated constructor stub
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (list.size() == tabLayout.getChildCount()) {
                viewPager.setCurrentItem(position, isScorll);
            } else
                Toast.makeText(context, "page项数量不等于tab项数量", Toast.LENGTH_SHORT)
                        .show();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        if (changeListener != null) {
            changeListener.change(position, arg0,
                    tabLayout.getChildAt(position), tabLayout.getChildAt(arg0));
        }
        position = arg0;
    }

    public int getCurrentPosition() {
        return position;
    }

    public boolean isScorll() {
        return isScorll;
    }

    /**
     * @param isScorll 设置点击tab时fragment切换是否带滑动，默认不带
     */
    public void setScorll(boolean isScorll) {
        this.isScorll = isScorll;
    }

    public void setCurrenItem(int position) {
        viewPager.setCurrentItem(position, isScorll);
    }

    public boolean isScorllToNext() {
        return isScorllToNext;
    }

    public int getWhereTab() {
        return whereTab;
    }

    /**
     * @param whereTab 设置tab位于viewpager上方还是下方，0代表下方，1代表上方
     */
    public void setWhereTab(int whereTab) {
        this.whereTab = whereTab;
    }

    /**
     * @param isScorllToNext 是否可以滑动切换，默认为true
     */
    public void setScorllToNext(boolean isScorllToNext) {
        this.isScorllToNext = isScorllToNext;
    }

    class MyViewPager extends ViewPager {

        public MyViewPager(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        public boolean onTouchEvent(MotionEvent arg0) {
            // TODO Auto-generated method stub
            if (isScorllToNext) {
                return super.onTouchEvent(arg0);
            }
            return false;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (isScorllToNext == false) {
                return false;
            } else {
                return super.onInterceptTouchEvent(ev);
            }
        }
    }
}
