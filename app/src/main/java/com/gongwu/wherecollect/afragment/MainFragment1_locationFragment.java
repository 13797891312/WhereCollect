package com.gongwu.wherecollect.afragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gongwu.wherecollect.R;
public class MainFragment1_locationFragment extends BaseFragment {
    View view;

    public MainFragment1_locationFragment() {
        // Required empty public constructor
    }

    public static MainFragment1_locationFragment newInstance() {
        MainFragment1_locationFragment fragment = new MainFragment1_locationFragment();
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
        view = inflater.inflate(R.layout.fragment_main_fragment1_location, container, false);
        return view;
    }
}
