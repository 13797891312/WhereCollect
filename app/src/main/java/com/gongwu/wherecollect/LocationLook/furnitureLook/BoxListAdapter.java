package com.gongwu.wherecollect.LocationLook.furnitureLook;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.entity.ObjectBean;
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
public class BoxListAdapter extends RecyclerView.Adapter<BoxListAdapter.CustomViewHolder> {
    public int selectPostion = -1;
    public MyOnItemClickListener onItemClickListener;
    Context context;
    List<ObjectBean> mlist;

    public BoxListAdapter(Context context, List<ObjectBean> list) {
        this.context = context;
        this.mlist = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_box_list_view, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if (position == selectPostion) {
            holder.name.setBackgroundResource(R.drawable.icon_text_select);
        } else {
            holder.name.setBackgroundColor(Color.WHITE);
        }
        holder.name.setText(mlist.get(position).getName());
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

    protected void onLongClick(int position) {
    }

    public ObjectBean getSelectObject() {
        if (selectPostion == -1) {
            return null;
        }
        return mlist.get(selectPostion);
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View
            .OnLongClickListener {
        @Bind(R.id.title)
        TextView name;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getLayoutPosition(), v);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            BoxListAdapter.this.onLongClick(getLayoutPosition());
            return true;
        }
    }
}
