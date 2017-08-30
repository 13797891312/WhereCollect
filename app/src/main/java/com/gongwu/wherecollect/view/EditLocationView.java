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
    Context context;
    @Bind(R.id.rec_tab)
    RecyclerView recTab;
    @Bind(R.id.tagViewPager_edit_location)
    TagViewPager pageView;
    @Bind(R.id.rec_goods)
    RecyclerView recGoods;
    EditLocationTabAdapter recyclerAdapter1;
    EditLocationGoodsAdapter recyclerAdapter2;
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
        recGoods.setOnDragListener(dragListener);
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
        pageView.setOnDragListener(dragListener);
        pageView.setOnSelectedListoner(this);
        pageView.init(0, 0);
        pageView.setAutoNext(false, 0);
        pageView.setOnGetView(new TagViewPager.OnGetView() {
            @Override
            public View getView(ViewGroup container, int position) {
                EditLocationPage v = new EditLocationPage(context);
                container.addView(v);
                return v;
            }
        });
        pageView.setAdapter(list3.size(), 0);
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
                if (positions == recyclerAdapter1.getSelectPostion())
                    return;
                recyclerAdapter1.setSelectPostion(positions);
                recyclerAdapter1.notifyDataSetChanged();
                pageView.setCurrentItem(positions);
            }
        });
    }

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

    @Override
    public void onSelected(int position) {
        recyclerAdapter1.setSelectPostion(position);
        recyclerAdapter1.notifyDataSetChanged();
    }

    public class MyDragListener implements OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            View v = ((View) dragEvent.getLocalState());
            GoodsBean bean = (GoodsBean) v.getTag();
            if (view.getId() == R.id.delete_Layout) {//如果拖拽到了删除里
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP://在某布局结束
                        deleteLayout.setVisibility(GONE);
                        EditLocationPage page = (EditLocationPage) pageView.getPrimaryItem();
                        bean.setType(0);
                        page.removeGoods(bean, v);
                        recyclerAdapter2.addBean(bean);
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED://进入某布局
                        deleteLayout.setVisibility(VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED://离开某布局
                        deleteLayout.setVisibility(GONE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED://结束
                        v.setVisibility(VISIBLE);
                        break;
                }
            } else if (view.getId() == R.id.tagViewPager_edit_location) {//如果拖拽到了viewpager里
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED://进入某布局
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION://位置
                        break;
                    case DragEvent.ACTION_DROP://在某布局结束
                        ((ViewGroup) v.getParent()).removeView(v);
                        bean.setType(1);//设置有归属
                        bean.setX(dragEvent.getX() - bean.getWidth() / 2);//有点偏移需要减去自身宽的一半
                        bean.setY(dragEvent.getY() - BaseViewActivity.getStateHeight(context));
                        EditLocationPage page = (EditLocationPage) ((TagViewPager) view).getPrimaryItem();
                        page.addGoods(bean);
                        recyclerAdapter2.removeBean(bean);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED://离开某布局
                        break;
                    case DragEvent.ACTION_DRAG_ENDED://结束
                        v.setVisibility(VISIBLE);
                        break;
                }
            } else if (view.getId() == R.id.rec_goods) {//如果是拖拽到下面的物品栏里
                if (bean.getType() == 0)
                    return false;//如果本来就是物品栏的什么都不做
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED://进入某布局
                        if (bean.getType() == 0)
                            return false;//如果本来就是物品栏的什么都不做
                        deleteLayout.setVisibility(VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED://结束
                        v.setVisibility(VISIBLE);
                        if (bean.getType() == 0)
                            return false;//如果本来就是物品栏的什么都不做
                        break;
                }
            }
            return true;
        }
    }
}
