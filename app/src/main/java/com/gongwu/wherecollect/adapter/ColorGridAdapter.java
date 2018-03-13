package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
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
public class ColorGridAdapter extends RecyclerView.Adapter<ColorGridAdapter.CustomViewHolder> {
    MyOnItemClickListener onItemClickListener;
    Context context;
    List<String> mlist;
    List<String> selectList;

    public ColorGridAdapter(Context context, List<String> list, List<String> selectList) {
        this.context = context;
        this.mlist = list;
        this.selectList = selectList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_colors_gridview, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.text.setText(mlist.get(position));
        holder.text.setSelected(selectList.contains(mlist.get(position)));
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

    @Override
    public void onViewAttachedToWindow(CustomViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    public void setOnItemClickListener(MyOnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.text)
        TextView text;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getLayoutPosition(), v);
            }
        }
    }
}
