package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.GoodsBean;
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
public class EditLocationGoodsAdapter extends RecyclerView.Adapter<EditLocationGoodsAdapter.CustomViewHolder> {
    MyOnItemClickListener onItemClickListener;
    Context context;
    List<GoodsBean> mlist;

    public EditLocationGoodsAdapter(Context context, List<GoodsBean> list) {
        this.context = context;
        this.mlist = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_editlocation_goods_recyclerview, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.image.setImageResource(mlist.get(position).getImageResouseId());
    }

    @Override
    public int getItemCount() {
        return StringUtils.getListSize(mlist);
    }

    /**
     * 删除一项
     *
     * @param bean
     */
    public void removeBean(GoodsBean bean) {
        mlist.remove(bean);
        notifyDataSetChanged();
    }

    /**
     * 添加一项
     *
     * @param bean
     */
    public void addBean(GoodsBean bean) {
        mlist.add(bean);
        notifyDataSetChanged();
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

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View
            .OnLongClickListener {
        @Bind(R.id.image)
        ImageView image;

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
        public boolean onLongClick(View view) {
            view.setTag(mlist.get(getLayoutPosition()));
            view.startDrag(null, new View.DragShadowBuilder(view), view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }
}
