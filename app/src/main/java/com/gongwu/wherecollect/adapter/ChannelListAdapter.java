package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ChannelBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
public class ChannelListAdapter extends BaseAdapter {
    private Context context;
    private List<ChannelBean> mList;

    public ChannelListAdapter(Context context, List<ChannelBean> mList) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    public List<ChannelBean> getmList() {
        return mList;
    }

    public void setmList(List<ChannelBean> mList) {
        this.mList = mList;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        HolderView holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_channel_list, null);
            holder = new HolderView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        ChannelBean item = mList.get(position);
        holder.nameTv.setText(mList.get(position).getName());
        holder.guishuTv.setText(item.getParentsString());
        return convertView;
    }

    static class HolderView {
        @Bind(R.id.name_tv)
        TextView nameTv;
        @Bind(R.id.guishu_tv)
        TextView guishuTv;

        HolderView(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
