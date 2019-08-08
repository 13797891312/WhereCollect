package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.RemindBean;
import com.gongwu.wherecollect.util.DateUtil;
import com.gongwu.wherecollect.util.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RemindListAdapter extends RecyclerView.Adapter<RemindListAdapter.ViewHolder> {

    private Context mContext;
    private List<RemindBean> mData;

    public RemindListAdapter(Context mContext, List<RemindBean> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_remind_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        RemindBean remindBean = mData.get(i);
        if (TextUtils.isEmpty(remindBean.getAssociated_object_url())) {
            holder.imgLayout.setVisibility(View.GONE);
        } else {
            holder.imgLayout.setVisibility(View.VISIBLE);
        }
        holder.imgIv.setImageDrawable(null);
        if (TextUtils.isEmpty(remindBean.getAssociated_object_url())) {
            holder.imgIv.setVisibility(View.GONE);
        } else {
            holder.imgIv.setVisibility(View.VISIBLE);
            ImageLoader.placeholderLoad(mContext, holder.imgIv, remindBean.getAssociated_object_url(), R.drawable.ic_img_error);
        }
        holder.remindNameTv.setText(remindBean.getTitle());
        holder.remindTimeTv.setText(DateUtil.dateToString(remindBean.getTips_time(), DateUtil.DatePattern.ONLY_MINUTE));
        holder.remindDescriptionTv.setText(TextUtils.isEmpty(remindBean.getDescription()) ? "" : remindBean.getDescription());
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.item_remind_img_layout)
        RelativeLayout imgLayout;
        @Bind(R.id.item_remind_goods_iv)
        ImageView imgIv;
        @Bind(R.id.no_url_img_tv)
        TextView imgTv;
        @Bind(R.id.item_remind_name_tv)
        TextView remindNameTv;
        @Bind(R.id.item_remind_time_tv)
        TextView remindTimeTv;
        @Bind(R.id.item_remind_description_tv)
        TextView remindDescriptionTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    public MyOnItemClickListener onItemClickListener;

    public void setOnItemClickListener(MyOnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
