package com.gongwu.wherecollect.afragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.MainActivity;
import com.gongwu.wherecollect.adapter.GoodsMainGridViewAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.object.ObjectLookInfoActivity;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.ErrorView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainGoodsFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    View view;
    @Bind(R.id.swipe_target)
    PullToRefreshGridView goodsGridView;
    @Bind(R.id.empty)
    ErrorView empty;
    int page = 1;
    private GoodsMainGridViewAdapter gridViewAdapter;
    private List<ObjectBean> mList = new ArrayList<>();

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
        String cache = SaveDate.getInstence(getActivity()).getObjectList();//取缓存
        if (TextUtils.isEmpty(cache)) {//没有缓存
            getData(true);
        } else {
            mList.addAll(JsonUtils.listFromJson(cache, ObjectBean.class));
            getData(false);
        }
        EventBus.getDefault().register(this);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        gridViewAdapter = new GoodsMainGridViewAdapter(getActivity(), mList);
        goodsGridView.setAdapter(gridViewAdapter);
        goodsGridView.setMode(PullToRefreshBase.Mode.BOTH);
        goodsGridView.getRefreshableView().setNumColumns(2);
        goodsGridView.setOnItemClickListener(this);
        goodsGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                page = 1;
                getData(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                page++;
                getData(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), ObjectLookInfoActivity.class);
        intent.putExtra("bean", mList.get(i));
        startActivity(intent);
    }

    /**
     * 获取物品列表
     */
    public void getData(boolean isShowDialog) {
        if (MyApplication.getUser(getActivity()) == null)
            return;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(getActivity()).getId());
        map.put("query", ((MainActivity) getActivity()).filterView.getQuery());
        map.put("page", "" + page);
        PostListenner listenner = new PostListenner(getActivity(), isShowDialog ? Loading.show(null, getActivity(),
                "正在加载") : null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List temp = JsonUtils.listFromJsonWithSubKey(r.getResult(), ObjectBean.class, "items");
                if (page == 1) {//如果是第一页就缓存下
                    SaveDate.getInstence(getActivity()).setObjectList(JsonUtils.jsonFromObject(temp));
                    mList.clear();
                } else {
                    if (StringUtils.isEmpty(temp)) {
                        Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                mList.addAll(temp);
                goodsGridView.setAdapter(null);
                goodsGridView.setAdapter(gridViewAdapter);
                gridViewAdapter.notifyDataSetChanged();
                goodsGridView.setEmptyView(empty);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                goodsGridView.onRefreshComplete();
            }
        };
        HttpClient.getGoodsList(getActivity(), map, listenner);
    }

    /**
     * 需要刷新列表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String string) {
        if (EventBusMsg.OBJECT_CHANGE.equals(string) || EventBusMsg.OBJECT_FITLER.equals(string)) {
            page = 0;
            goodsGridView.setRefreshing(true);
            if (EventBusMsg.OBJECT_CHANGE.equals((string))) {
                ((MainActivity) getActivity()).filterView.getFilterList();
            }
        }
    }

    //更换了账号
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ChangeUser msg) {
        page = 0;
        goodsGridView.setRefreshing(true);
        ((MainActivity) getActivity()).filterView.getFilterList();
    }
}
