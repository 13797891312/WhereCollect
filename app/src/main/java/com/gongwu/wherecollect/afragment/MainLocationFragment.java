package com.gongwu.wherecollect.afragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.view.EditLocationView;

import butterknife.ButterKnife;
public class MainLocationFragment extends BaseFragment {
    public static EditLocationView editLocationView;
    View view;

    public MainLocationFragment() {
        // Required empty public constructor
    }

    public static MainLocationFragment newInstance() {
        MainLocationFragment fragment = new MainLocationFragment();
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
        editLocationView = (EditLocationView) view.findViewById(R.id.editLocationView);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
