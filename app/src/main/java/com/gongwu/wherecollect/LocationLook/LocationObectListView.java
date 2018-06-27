package com.gongwu.wherecollect.LocationLook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.object.ObjectLookInfoActivity;
import com.gongwu.wherecollect.util.AnimationUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.StringUtils;
import com.zhaojin.myviews.HackyViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:
 * Date: 2017/11/14
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class LocationObectListView extends RecyclerView {
    public ObjectListAdapter adapter;
    public List<ObjectBean> list = new ArrayList<>();
    Context context;
    private GridLayoutManager mLayoutManager;
    private OnitemClickLisener listener;

    public LocationObectListView(Context context) {
        this(context, null);
    }

    public LocationObectListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mLayoutManager = new GridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(mLayoutManager);
        adapter = new ObjectListAdapter(context, list);
        EventBus.getDefault().register(this);
        setAdapter(adapter);
    }

    public void setOnItemClickListener(OnitemClickLisener listener) {
        this.listener = listener;
    }

    public void init() {
        setHasFixedSize(true);
        adapter.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(int positions, View view) {
                if (adapter.selectPostion == positions) {
                    Intent intent = new Intent(context, ObjectLookInfoActivity.class);
                    intent.putExtra("bean", list.get(positions));
                    context.startActivity(intent);
                    LocationPage locationPage = MainLocationFragment.pageMap.get(selectPosition);
                    if (locationPage != null) {
                        locationPage.cancelFind();
                    }
                    return;
                }
                adapter.selectPostion = positions;
                adapter.notifyDataSetChanged();
                if (listener != null) {
                    listener.itemClick(positions, list.get(positions), view);
                }
            }
        });
    }

    /**
     * 获取数据并刷新UI
     */
    private int selectPosition;

    public void notifyData(List<ObjectBean> temp, int position) {
        if (temp != null) {
            list.clear();
            list.addAll(temp);
            this.selectPosition = position;
        }
        adapter.selectPostion = -1;
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            HackyViewPager.isItercept = false;
        } else {
            HackyViewPager.isItercept = true;
        }
        return super.onInterceptTouchEvent(e);
    }

    public void findObject(ObjectBean objectBean) {
        setVisibility(View.VISIBLE);

        ((FrameLayout) this.getParent()).setVisibility(VISIBLE);
        AnimationUtil.upSlide(((FrameLayout) this.getParent()), 150);
        for (int i = 0; i < StringUtils.getListSize(list); i++) {
            if (list.get(i).get_id().equals(objectBean.get_id())) {
                adapter.onItemClickListener.onItemClick(i, null);
                this.scrollToPosition(i);
            }
        }
    }

    public static interface OnitemClickLisener {
        public void itemClick(int position, ObjectBean bean, View view);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String str) {
        if (EventBusMsg.REFRESH_GOODS.contains(str)) {
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.DeleteGoodsMsg deleteGoodsMsg) {
        for (int i = 0; i < list.size(); i++) {
            ObjectBean bean = list.get(i);
            if (bean.get_id().equals(deleteGoodsMsg.goodsId)) {
                list.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
