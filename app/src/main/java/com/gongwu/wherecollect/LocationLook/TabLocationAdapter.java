package com.gongwu.wherecollect.LocationLook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ScrollSpeedLinearLayoutManger;
import com.gongwu.wherecollect.util.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Function:
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class TabLocationAdapter extends RecyclerView.Adapter<TabLocationAdapter.CustomViewHolder> {
    MyOnItemClickListener onItemClickListener;
    Context context;
    List<ObjectBean> mlist;
    private ScrollSpeedLinearLayoutManger mLayoutManager;

    public TabLocationAdapter(Context context, List<ObjectBean> list) {
        this.context = context;
        this.mlist = list;
        getSelectPostion();
    }

    public int getSelectPostion() {
        for (int i = 0; i < StringUtils.getListSize(mlist); i++) {
            if (mlist.get(i).isSelect()) {
                return i;
            }
        }
        if (StringUtils.isEmpty(mlist)) {
            return -1;
        }
        mlist.get(0).setSelect(true);
        return 0;
    }

    public void setSelectPostion(int selectPostion) {
        for (int i = 0; i < StringUtils.getListSize(mlist); i++) {
            if (i == selectPostion) {
                mlist.get(i).setSelect(true);
            } else {
                mlist.get(i).setSelect(false);
            }
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_location_tab, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.text.setText(mlist.get(position).getName());
        if (mlist.get(position).isSelect()) {
            holder.text.setSelected(true);
            holder.selectView.setVisibility(View.VISIBLE);
        } else {
            holder.text.setSelected(false);
            holder.selectView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return StringUtils.getListSize(mlist);
    }

    /**
     * 可以返回不同类型加载不同布局
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setOnItemClickListener(MyOnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.text)
        TextView text;
        @Bind(R.id.select_text_view)
        View selectView;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getLayoutPosition(), v);
            }
        }
    }
}
