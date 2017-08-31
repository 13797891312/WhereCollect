package com.gongwu.wherecollect.afragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gongwu.wherecollect.R;
public class MainGoodsFragment extends BaseFragment {
    View view;

    public MainGoodsFragment() {
        // Required empty public constructor
    }

    public static MainGoodsFragment newInstance() {
        MainGoodsFragment fragment = new MainGoodsFragment();
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
        view = inflater.inflate(R.layout.fragment_main_fragment1_goods, container, false);
        return view;
    }
}
