package com.gongwu.wherecollect.afragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.GoodsMainRecyclerviewAdapter;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.entity.GoodsBean;
import com.gongwu.wherecollect.view.ErrorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.umeng.socialize.utils.DeviceConfig.context;
public class MainGoodsFragment extends BaseFragment {
    View view;
    @Bind(R.id.goods_recyclerview)
    RecyclerView goodsRecyclerview;
    @Bind(R.id.empty)
    ErrorView empty;
    private GoodsMainRecyclerviewAdapter recyclerAdapter;
    private List<GoodsBean> mList = new ArrayList<>();
    private GridLayoutManager mLayoutManager;

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
        ButterKnife.bind(this, view);
        initRecyclerView();
        initData();
        return view;
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            GoodsBean bean = new GoodsBean();
            bean.setName("一个不怎么用的杯子");
            bean.setLoction("客厅/茶几");
            bean.setUrl("https://timgsa.baidu" +
                    ".com/timg?image&quality=80&size=b9999_10000&sec=1505790627&di=a4757f4abab9b84bf1fbe9523631b4c3" +
                    "&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F16%2F75%2F25%2F21Y58PICuIj_1024.jpg");
            mList.add(bean);
        }
        recyclerAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        recyclerAdapter = new GoodsMainRecyclerviewAdapter(getActivity(), mList);
        mLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false);
        goodsRecyclerview.setLayoutManager(mLayoutManager);
        goodsRecyclerview.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(int positions, View view) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
