package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class ColorContentAdapter extends RecyclerView.Adapter<ColorContentAdapter.CustomViewHolder> {
    MyOnItemClickListener onItemClickListener;
    Context context;
    List<String> mlist;
    ColorGridAdapter gridAdapter;

    public ColorContentAdapter(Context context, List<String> list, ColorGridAdapter adapter) {
        this.context = context;
        this.mlist = list;
        this.gridAdapter = adapter;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_colors_content, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        holder.text.setText(mlist.get(position));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlist.remove(position);
                notifyDataSetChanged();
                gridAdapter.notifyDataSetChanged();
            }
        });
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
        @Bind(R.id.delete_image)
        ImageView delete;

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
