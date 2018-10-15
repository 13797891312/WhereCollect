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
import com.gongwu.wherecollect.util.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 共享人列表 adapter
 */

public class SharePersonListAdapter extends RecyclerView.Adapter<SharePersonListAdapter.ViewHolder> {

    private Context mContext;
    private List<SharePersonBean> datas;

    public SharePersonListAdapter(Context mContext, List<SharePersonBean> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_share_person_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SharePersonBean bean = datas.get(position);
        ImageLoader.loadCircle(mContext, holder.userIconIv, bean.getAvatar(), R.mipmap.ic_launcher);
        holder.userNameTv.setText(bean.getNickname());
        String s = "";
        for (int i = 0; i < bean.getShared_locations().size(); i++) {
            s += bean.getShared_locations().get(i).getName() + " ";
        }
        holder.userSpaceTv.setText(s);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.share_person_list_user_icon_iv)
        ImageView userIconIv;
        @Bind(R.id.share_person_list_user_name_tv)
        TextView userNameTv;
        @Bind(R.id.share_person_list_user_space_tv)
        TextView userSpaceTv;
        @Bind(R.id.close_share_person_iv)
        ImageView closeIv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
