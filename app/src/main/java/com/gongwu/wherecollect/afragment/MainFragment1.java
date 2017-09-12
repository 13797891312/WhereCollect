package com.gongwu.wherecollect.afragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.MainActivity;
import com.zhaojin.myviews.MyFragmentLayout_line;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
public class MainFragment1 extends BaseFragment implements View.OnClickListener {
    public List<Fragment> fragments = new ArrayList();
    View view;
    @Bind(R.id.myFragmentLayout)
    MyFragmentLayout_line myFragmentLayout;
    ImageButton serchBtn;

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
        initFragment();
        serchBtn = (ImageButton) view.findViewById(R.id.serch_btn);
        serchBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initFragment() {
        fragments.add(MainGoodsFragment.newInstance());
        fragments.add(MainLocationFragment.newInstance());
        //        fragments.add(new ChooseingExerciseFragment1(resource));
        myFragmentLayout.setScorllToNext(true);
        myFragmentLayout.setScorll(true);
        myFragmentLayout.setWhereTab(1);
        myFragmentLayout.setTabHeight(10, getResources().getColor(R.color.maincolor), true, 0);
        myFragmentLayout.setOnChangeFragmentListener(new MyFragmentLayout_line.ChangeFragmentListener() {
            @Override
            public void change(int lastPosition, int positon,
                               View lastTabView, View currentTabView) {
            }
        });
        myFragmentLayout.setAdapter(fragments, R.layout.tablayout_main_fragment1, 0x202);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.serch_btn:
                ((MainActivity) getActivity()).searchClick();
                break;
        }
    }
}
