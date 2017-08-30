package com.gongwu.wherecollect.view;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.EditLocationGoodsAdapter;
import com.gongwu.wherecollect.adapter.EditLocationTabAdapter;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.ScrollSpeedLinearLayoutManger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Function:
 * Date: 2017/8/29
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class EditLocationView extends LinearLayout {
    Context context;
    @Bind(R.id.rec_tab)
    RecyclerView recTab;
    @Bind(R.id.pageView)
    ViewPager pageView;
    @Bind(R.id.rec_goods)
    RecyclerView recGoods;
    EditLocationTabAdapter recyclerAdapter1;
    EditLocationGoodsAdapter recyclerAdapter2;
    List<String> list1 = new ArrayList<>();
    List<String> list2 = new ArrayList<>();
    MyDragListener dragListener = new MyDragListener();
    private ScrollSpeedLinearLayoutManger mLayoutManager1;
    private GridLayoutManager mLayoutManager2;

    public EditLocationView(Context context) {
        this(context, null);
    }

    public EditLocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View.inflate(context, R.layout.layout_edit_location_view, this);
        ButterKnife.bind(this);
        recTab.setHasFixedSize(true);
        recGoods.setHasFixedSize(true);
        initRec1();
        initRec2();
        pageView.setOnDragListener(dragListener);
    }

    private void initRec1() {
        list1.add("客厅");
        list1.add("厨房");
        list1.add("卧室");
        recyclerAdapter1 = new EditLocationTabAdapter(context, list1);
        mLayoutManager1 = new ScrollSpeedLinearLayoutManger(context, LinearLayoutManager.HORIZONTAL,
                false);
        mLayoutManager1.setSpeedSlow();
        recTab.setLayoutManager(mLayoutManager1);
        recTab.setAdapter(recyclerAdapter1);
        recyclerAdapter1.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(int positions, View view) {
                recyclerAdapter1.setSelectPostion(positions);
                recyclerAdapter1.notifyDataSetChanged();
            }
        });
    }

    private void initRec2() {
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        list2.add("测试");
        recyclerAdapter2 = new EditLocationGoodsAdapter(context, list2);
        mLayoutManager2 = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
        recGoods.setLayoutManager(mLayoutManager2);
        recGoods.setAdapter(recyclerAdapter2);
        recyclerAdapter2.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(int positions, View view) {
            }
        });
    }

    public static class MyDragListener implements OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            View v = ((View) dragEvent.getLocalState());
            LogUtil.e(dragEvent.getX() + "---" + dragEvent.getY());
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED://开始
                    LogUtil.e("ACTION_DRAG_STARTED");
                    v.setVisibility(INVISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED://进入某布局
                    LogUtil.e("ACTION_DRAG_ENTERED");
                    break;
                case DragEvent.ACTION_DRAG_LOCATION://位置
                    LogUtil.e("ACTION_DRAG_LOCATION");
                    break;
                case DragEvent.ACTION_DROP://在某布局结束
                    LogUtil.e("ACTION_DROP");
                    break;
                case DragEvent.ACTION_DRAG_EXITED://离开某布局
                    LogUtil.e("ACTION_DRAG_EXITED");
                    break;
                case DragEvent.ACTION_DRAG_ENDED://结束
                    LogUtil.e("ACTION_DRAG_ENDED");
                    v.setVisibility(VISIBLE);
                    break;
            }
            return true;
        }
    }
}
