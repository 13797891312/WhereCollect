package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.entity.SharedLocationBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.view.PileAvertView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 共享空间列表adapter
 */

public class ShareSpaceListAdapter extends RecyclerView.Adapter<ShareSpaceListAdapter.ViewHolder> {

    private Context mContext;
    private List<SharedLocationBean> datas;

    public ShareSpaceListAdapter(Context mContext, List<SharedLocationBean> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_share_location_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SharedLocationBean bean = datas.get(position);
        holder.spaceNameTv.setText(bean.getName());
        holder.mPileAvertView.setAvertImages(bean.getShared_users());
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.share_space_list_name_tv)
        TextView spaceNameTv;
        @Bind(R.id.pile_avert_view)
        PileAvertView mPileAvertView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
