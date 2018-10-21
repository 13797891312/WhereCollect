package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ShareUserBean;
import com.gongwu.wherecollect.util.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ObjectShowShareUsersAdapter extends BaseAdapter {
    private Context mContext;
    private List<ShareUserBean> datas;
    private String userId;

    public ObjectShowShareUsersAdapter(Context mContext, List<ShareUserBean> datas, String userId) {
        this.mContext = mContext;
        this.datas = datas;
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_object_show_share_users_layout, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ShareUserBean data = datas.get(position);
        ImageLoader.loadCircle(mContext, holder.userIconIv, data.getAvatar(), R.mipmap.ic_launcher);
        holder.userName.setText(data.getNickname());
        if (userId.equals(data.get_id())) {
            holder.lockIv.setVisibility(View.VISIBLE);
        } else {
            holder.lockIv.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.show_share_users_icon_iv)
        ImageView userIconIv;
        @Bind(R.id.show_share_users__name_tv)
        TextView userName;
        @Bind(R.id.show_share_users_lock_iv)
        ImageView lockIv;

        public ViewHolder(View itemView) {
            itemView.setTag(this);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
