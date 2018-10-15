package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.SharedLocationBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SharePersonDetailsSpaceListAdapter extends RecyclerView.Adapter<SharePersonDetailsSpaceListAdapter.ViewHolder> {

    private Context mContext;
    private List<SharedLocationBean> datas;

    public SharePersonDetailsSpaceListAdapter(Context mContext, List<SharedLocationBean> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_share_person_details_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SharedLocationBean bean = datas.get(position);
        holder.space_name_tv.setText(bean.getName());
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.share_user_details_space_name_tv)
        TextView space_name_tv;
        @Bind(R.id.close_share_user_details_space_iv)
        ImageView close_space_iv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
