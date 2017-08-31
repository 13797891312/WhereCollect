package com.gongwu.wherecollect.view;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.adapter.EditLocationGoodsAdapter;
import com.gongwu.wherecollect.adapter.EditLocationTabAdapter;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.entity.GoodsBean;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.ScrollSpeedLinearLayoutManger;
import com.zhaojin.myviews.TagViewPager;

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
public class EditLocationView extends LinearLayout implements TagViewPager.OnSelectedListoner {
    public EditLocationTabAdapter recyclerAdapter1;
    public EditLocationGoodsAdapter recyclerAdapter2;
    Context context;
    @Bind(R.id.rec_tab)
    RecyclerView recTab;
    @Bind(R.id.tagViewPager_edit_location)
    TagViewPager pageView;
    @Bind(R.id.rec_goods)
    RecyclerView recGoods;
    List<String> list1 = new ArrayList<>();//头部
    List<GoodsBean> list2 = new ArrayList<>();//尾部物品
    List<String> list3 = new ArrayList<>();//中间viewpager
    MyDragListener dragListener = new MyDragListener();
    @Bind(R.id.delete_Layout)
    LinearLayout deleteLayout;
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
        deleteLayout.setOnDragListener(dragListener);
        initRec1();
        initRec2();
        initViewPager();
    }

    /**
     * 初始化中间的viewPager
     */
    private void initViewPager() {
        list3.add("第一页");
        list3.add("第二页");
        list3.add("第三页");
        pageView.setOnSelectedListoner(this);
        pageView.init(0, 0);
        pageView.setAutoNext(false, 0);
        pageView.setOnGetView(new TagViewPager.OnGetView() {
            @Override
            public View getView(ViewGroup container, int position) {
                EditLocationPage v = new EditLocationPage(context);
                v.setOnDragListener(dragListener);
                v.setDragListenerFromView(dragListener);
                container.addView(v);
                return v;
            }
        });
        pageView.setAdapter(list3.size(), 0);
    }

    /**
     * 初始化tab
     */
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
                if (positions == recyclerAdapter1.getSelectPostion())
                    return;
                recyclerAdapter1.setSelectPostion(positions);
                recyclerAdapter1.notifyDataSetChanged();
                pageView.setCurrentItem(positions);
            }
        });
    }

    /**
     * 物品栏
     */
    private void initRec2() {
        for (int i = 0; i < 20; i++) {
            GoodsBean bean = new GoodsBean();
            list2.add(bean);
        }
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

    /**
     * pageView滑动切换
     *
     * @param position 当前切换到哪项
     */
    @Override
    public void onSelected(int position) {
        recyclerAdapter1.setSelectPostion(position);
        recyclerAdapter1.notifyDataSetChanged();
    }

    /**
     * 拖拽删除布局
     *
     * @param view
     * @param dragEvent
     */
    private void deleteLayoutListener(View view, DragEvent dragEvent) {
    }

    /**
     * 拖拽到viewpager布局
     *
     * @param view
     * @param dragEvent
     */
    private void viewPagerListener(View view, DragEvent dragEvent) {
        View v = ((View) dragEvent.getLocalState());
        GoodsBean bean = (GoodsBean) v.getTag();
        EditLocationPage page = (EditLocationPage) pageView.getPrimaryItem();
        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                //判断viewpager是否有这个数据，有的话说明是在viewpger内抬起的，就显示删除布局
                if (page.queryGoods(bean)) {
                    deleteLayout.setVisibility(View.VISIBLE);
                } else {
                    deleteLayout.setVisibility(View.GONE);
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED://进入某布局
                break;
            case DragEvent.ACTION_DRAG_LOCATION://位置
                break;
            case DragEvent.ACTION_DROP://在某布局结束
                ((ViewGroup) v.getParent()).removeView(v);
                bean.setType(1);//设置有归属
                bean.setX(dragEvent.getX() - bean.getWidth() / 2);//有点偏移需要减去自身宽的一半
                bean.setY(dragEvent.getY() - BaseViewActivity.getStateHeight(context));
                page.addGoods(bean);
                recyclerAdapter2.removeBean(bean);
                //落下后删除布局隐藏
                if (deleteLayout.getVisibility() == View.VISIBLE) {
                    deleteLayout.setVisibility(View.GONE);
                }
                break;
            case DragEvent.ACTION_DRAG_EXITED://离开某布局
                break;
            case DragEvent.ACTION_DRAG_ENDED://结束
                v.setVisibility(VISIBLE);
                break;
        }
    }

    /**
     * 拖拽监听
     * 总监听，所有拖拽事件逻辑都在这监听里
     */
    public class MyDragListener implements OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            LogUtil.e(view + "----" + dragEvent.getAction());
            return ((BaseDragView) view).onDrag(view, dragEvent);
        }
    }
}
