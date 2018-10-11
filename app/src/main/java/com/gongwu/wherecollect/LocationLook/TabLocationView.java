package com.gongwu.wherecollect.LocationLook;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ScrollSpeedLinearLayoutManger;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:
 * Date: 2017/11/14
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class TabLocationView extends RecyclerView {
    List<ObjectBean> mlist=new ArrayList<>();
    Context context;
    public TabLocationAdapter adapter;
    private ScrollSpeedLinearLayoutManger mLayoutManager;
    private AdapterView.OnItemClickListener listener;

    public TabLocationView(Context context) {
        this(context, null);
    }

    public TabLocationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mLayoutManager = new ScrollSpeedLinearLayoutManger(context, LinearLayoutManager.HORIZONTAL,
                false);
        mLayoutManager.setSpeedSlow();
        setLayoutManager(mLayoutManager);
        adapter = new TabLocationAdapter(context, mlist);
        setAdapter(adapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 刷新
     */
    public void notifyView() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void init(List<ObjectBean> list) {
        mlist = list;
        setHasFixedSize(true);
        adapter = new TabLocationAdapter(context, mlist);
        setAdapter(adapter);
        adapter.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(int positions, View view) {
                if (positions == adapter.getSelectPostion())
                    return;
                adapter.setSelectPostion(positions);
                adapter.notifyDataSetChanged();
                if (listener != null) {
                    listener.onItemClick(null, view, positions, view.getId());
                }
            }
        });
    }

    /**
     * 获取当前选择的项
     *
     * @return
     */
    public ObjectBean getCurrentLocation() {
        return mlist.get(adapter.getSelectPostion()==-1?null:adapter.getSelectPostion());
    }

    /**
     * 获取选择的项
     *
     * @return
     */
    public int getSelection() {
        if (adapter == null) {
            return -1;
        }
        return adapter.getSelectPostion();
    }

    public void setSelection(int position) {
        if (adapter != null) {
            adapter.setSelectPostion(position);
        }
    }
}
