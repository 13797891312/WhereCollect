package com.gongwu.wherecollect.LocationEdit;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Toast;

import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.adapter.SpaceEditListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.LocationBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.SpaceEditDialog;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Function: 空间编辑列表
 * Date: 2016-08-20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class SpaceEditActivity extends BaseViewActivity {
    @Bind(R.id.space_recyclerView)
    SwipeMenuRecyclerView spaceRecyclerView;
    SpaceEditListAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_edit);
        ButterKnife.bind(this);
        titleLayout.setTitle("空间编辑");
        titleLayout.setBack(true, null);
        titleLayout.textBtn.setText("添加");
        titleLayout.textBtn.setVisibility(View.VISIBLE);
        titleLayout.textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSpace();
            }
        });
        initView();
    }

    private void initView() {
        //创建adapter
        myAdapter = new SpaceEditListAdapter(this, MainLocationFragment.mlist) {
            @Override
            protected void openRight(int position) {
                super.openRight(position);
                spaceRecyclerView.smoothOpenRightMenu(position);
            }
        };
        // 创建菜单：
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(SpaceEditActivity.this);
                // 各种文字和图标属性设置。
                deleteItem.setBackground(new ColorDrawable(Color.RED));
                deleteItem.setHeight((int) (48 * BaseViewActivity.getScreenScale(SpaceEditActivity.this)) - 1);
                deleteItem.setWidth((int) (60 * BaseViewActivity.getScreenScale(SpaceEditActivity.this)));
                deleteItem.setText("删除");
                deleteItem.setTextColor(Color.WHITE);
                deleteItem.setTextSize(15);
                rightMenu.addMenuItem(deleteItem); // 在Item左侧添加一个菜单。
            }
        };
        spaceRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        // 菜单点击监听。
        SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu();
                int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
                deleteData(adapterPosition);
            }
        };
        spaceRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();
                // Item被拖拽时，交换数据，并更新adapter。
                MainLocationFragment.mlist.add(toPosition, MainLocationFragment.mlist.remove(fromPosition));
                myAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            }
        };
        /**
         * Item的拖拽/侧滑删除时，手指状态发生变化监听。
         */
        OnItemStateChangedListener mOnItemStateChangedListener = new OnItemStateChangedListener() {
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                    // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#bbeeeeee"));
                } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                    // 在手松开的时候还原背景。
                    viewHolder.itemView.setBackgroundColor(Color.WHITE);
                    changePositionSpace();
                }
            }
        };
        spaceRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener);
        spaceRecyclerView.setOnItemMoveListener(mItemMoveListener);
        spaceRecyclerView.setLongPressDragEnabled(true);// 开启长按拖拽
        spaceRecyclerView.setItemViewSwipeEnabled(false);
        //设置默认的布局方式
        spaceRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置adapter
        spaceRecyclerView.setAdapter(myAdapter);
    }

    /**
     * 删除空间
     */
    private void deleteData(final int position) {
        if (MainLocationFragment.mlist.get(position).isIs_share()) {
            ToastUtil.show(context, "分享的空间无法删除", Toast.LENGTH_SHORT);
            return;
        }
        DialogUtil.show("提示", String.format("是否删除 %s ？删除后该空间内所有内容的位置将会归为未定义", MainLocationFragment.mlist.get(position)
                        .getName()), "确定",
                "取消", this, new
                        DialogInterface
                                .OnClickListener
                                () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, String> map = new TreeMap<>();
                                map.put("code", MainLocationFragment.mlist.get(position).getCode());
                                map.put("uid", MyApplication.getUser(SpaceEditActivity.this).getId());
                                PostListenner listenner = new PostListenner(SpaceEditActivity.this, Loading.show(null,
                                        SpaceEditActivity.this, "正在删除")) {
                                    @Override
                                    protected void code2000(final ResponseResult r) {
                                        super.code2000(r);
                                        MainLocationFragment.mlist.remove(position);
                                        myAdapter.notifyItemRemoved(position);
                                        //后台数据可能没改过来，需要缓一下 物品位置才清空
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                EventBus.getDefault().post(EventBusMsg.SPACE_EDIT);
                                                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                                            }
                                        }, 1500);
                                    }
                                };
                                HttpClient.deleteLocation(SpaceEditActivity.this, map, listenner);
                            }
                        }, null);
    }

    /**
     * 添加空间
     */
    private void addSpace() {
        SpaceEditDialog dialog = new SpaceEditDialog(context, "") {
            @Override
            protected void commit(String str) {
                super.commit(str);
                Map<String, String> map = new TreeMap<>();
                map.put("uid", MyApplication.getUser(SpaceEditActivity.this).getId());
                map.put("location_name", str);
                PostListenner listenner = new PostListenner(SpaceEditActivity.this, Loading.show(null,
                        SpaceEditActivity.this, "")) {
                    @Override
                    protected void code2000(final ResponseResult r) {
                        super.code2000(r);
                        LocationBean bean = JsonUtils.objectFromJson(r.getResult(), LocationBean.class);
                        MainLocationFragment.mlist.add(bean);
                        myAdapter.notifyDataSetChanged();
                        EventBus.getDefault().post(EventBusMsg.SPACE_EDIT);
                    }
                };
                HttpClient.addLocation(SpaceEditActivity.this, map, listenner);
            }
        };
        dialog.show();
    }

    /**
     * 切换位置
     */
    private void changePositionSpace() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < StringUtils.getListSize(MainLocationFragment.mlist); i++) {
            sb.append(MainLocationFragment.mlist.get(i).getCode());
            sb.append(",");
        }
        if (sb.length() != 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("location_codes", sb.toString());
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                EventBus.getDefault().post(EventBusMsg.SPACE_EDIT);
            }
        };
        HttpClient.updataSpacePosition(this, map, listenner);
    }
}
