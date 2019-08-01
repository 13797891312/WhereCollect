package com.gongwu.wherecollect.afragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.AddRemindActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RemindFragment extends BaseFragment {

    private View view;

    public RemindFragment() {
    }

    public static RemindFragment newInstance() {
        RemindFragment fragment = new RemindFragment();
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
            view = inflater.inflate(R.layout.fragment_remind, container, false);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.add_remind})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_remind:
                AddRemindActivity.start(getContext());
                break;
            default:
                break;
        }
    }

    @Override
    public void onShow() {

    }
}
