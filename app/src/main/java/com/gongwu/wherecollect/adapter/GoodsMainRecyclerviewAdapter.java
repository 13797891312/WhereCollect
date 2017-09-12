package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.GoodsBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Function:主页物品查看列表
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class GoodsMainRecyclerviewAdapter extends RecyclerView.Adapter<GoodsMainRecyclerviewAdapter.CustomViewHolder> {
    MyOnItemClickListener onItemClickListener;
    Context context;
    List<GoodsBean> mlist;

    public GoodsMainRecyclerviewAdapter(Context context, List<GoodsBean> list) {
        this.context = context;
        this.mlist = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_goods_main_recyclerview, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        GoodsBean bean = mlist.get(position);
        holder.nameTv.setText(bean.getName());
        holder.locationTv.setText(bean.getLoction());
        ImageLoader.load(context, holder.image, bean.getUrl(), R.drawable.ic_img_error);
        if (position == mlist.size() - 1) {
            holder.footer.setVisibility(View.VISIBLE);
        } else {
            holder.footer.setVisibility(View.GONE);
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
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.name_tv)
        TextView nameTv;
        @Bind(R.id.footer)
        View footer;
        @Bind(R.id.location_tv)
        TextView locationTv;

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
