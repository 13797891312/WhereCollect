package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RelationGoodsAdapter extends RecyclerView.Adapter<RelationGoodsAdapter.ViewHolder> {

    private Context mContext;
    private List<ObjectBean> mData;

    public RelationGoodsAdapter(Context mContext, List<ObjectBean> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_relation_goods_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        ObjectBean bean = mData.get(i);
        if (!TextUtils.isEmpty(bean.getObject_url()) && bean.getObject_url().contains("http")) {
            holder.image.setImageDrawable(null);
            holder.image.setBackgroundResource(0);
            ImageLoader.load(mContext, holder.image, bean.getObject_url());
            holder.imgTv.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(bean.getObject_url())&&!bean.getObject_url().contains("/")) {
            holder.image.setImageDrawable(null);
            holder.image.setBackgroundResource(0);
            holder.image.setBackgroundColor(Color.parseColor(bean.getObject_url()));
            holder.imgTv.setVisibility(View.VISIBLE);
            holder.imgTv.setText(bean.getName());
        } else {
            holder.image.setImageDrawable(null);
            holder.image.setBackgroundResource(0);
            holder.image.setBackgroundColor(mContext.getResources().getColor(R.color.goods_color_1));
            holder.imgTv.setVisibility(View.VISIBLE);
            holder.imgTv.setText(bean.getName());
        }
        holder.mGoodsNameTv.setText(bean.getName());
        holder.mGoodsLocationTv.setText(getLoction(bean));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();

    }

    /**
     * 拼接位置
     *
     * @return
     */
    public String getLoction(ObjectBean bean) {
        if (StringUtils.isEmpty(bean.getLocations())) {
            return "";
        }
        Collections.sort(bean.getLocations(), new Comparator<BaseBean>() {
            @Override
            public int compare(BaseBean lhs, BaseBean rhs) {
                return lhs.getLevel() - rhs.getLevel();
            }
        });
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < StringUtils.getListSize(bean.getLocations()); i++) {
            sb.append(bean.getLocations().get(i).getName());
            if (i != bean.getLocations().size() - 1) {
                sb.append("/");
            }
        }
        return sb.length() == 0 ? "" : sb.toString();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.item_relation_goods_iv)
        ImageView image;
        @Bind(R.id.no_url_img_tv)
        TextView imgTv;
        @Bind(R.id.item_relation_goods_name_tv)
        TextView mGoodsNameTv;
        @Bind(R.id.item_relation_goods_location_tv)
        TextView mGoodsLocationTv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getLayoutPosition(), v);
            }
        }
    }

    public MyOnItemClickListener onItemClickListener;

    public void setOnItemClickListener(MyOnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
