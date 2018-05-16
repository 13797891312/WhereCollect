package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mucll on 2018/3/16.
 */

public class AddMoreGoodsListAdapter extends RecyclerView.Adapter<AddMoreGoodsListAdapter.MyViewHolder> {
    private Context context;
    private List<ObjectBean> mDatas;

    public AddMoreGoodsListAdapter(Context context, List<ObjectBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_add_more_goods_list_layout, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ObjectBean bean = mDatas.get(position);
        if (!TextUtils.isEmpty(bean.getName())) {
            holder.goodsNameTv.setText(bean.getName());
        }
        if (TextUtils.isEmpty(bean.getObject_url())) {
            bean.setObject_url(StringUtils.getResCode(position));
            holder.goodsIv.setImageDrawable(null);
            holder.goodsIv.setBackgroundColor(Color.parseColor(bean.getObject_url()));
            holder.goodsHintTv.setVisibility(View.VISIBLE);
            holder.goodsHintTv.setText(bean.getName());
        } else if (bean.getObject_url().contains("#")) {
            holder.goodsIv.setImageDrawable(null);
            holder.goodsIv.setBackgroundColor(Color.parseColor(bean.getObject_url()));
            holder.goodsHintTv.setVisibility(View.VISIBLE);
            holder.goodsHintTv.setText(bean.getName());
        } else{
            ImageLoader.placeholderLoad(context, holder.goodsIv, bean.getObject_url(), R.drawable.ic_img_error);
            holder.goodsHintTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.item_goods_iv)
        ImageView goodsIv;
        @Bind(R.id.item_goods_hint_tv)
        TextView goodsHintTv;
        @Bind(R.id.item_goods_name_tv)
        TextView goodsNameTv;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setTag(this);
            view.setOnClickListener(this);
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
