package com.gongwu.wherecollect.afragment;

import android.support.v4.app.Fragment;

import com.gongwu.wherecollect.swipetoloadlayout.SwipeToLoadLayout;

public class BaseFragment extends Fragment {
    public BaseFragment() {
    }

    public void LoginListener() {
    }

    public void onShow() {
    }

    public void refreshFragment() {
    }


    public void closeLoading(SwipeToLoadLayout mSwipeToLoadLayout) {
        if (mSwipeToLoadLayout != null) {
            mSwipeToLoadLayout.setRefreshing(false);
            mSwipeToLoadLayout.setLoadingMore(false);
        }
    }
}
