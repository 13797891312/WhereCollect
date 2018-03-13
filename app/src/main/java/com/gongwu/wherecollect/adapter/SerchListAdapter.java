package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.SerchBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
public class SerchListAdapter extends BaseAdapter {
    private Context context;
    private List<SerchBean> mList;

    public SerchListAdapter(Context context, List<SerchBean> mList) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
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
            convertView = View.inflate(context, R.layout.item_serch_list, null);
            holder = new HolderView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        holder.nameTv.setText(mList.get(position).getTitle());
        return convertView;
    }

    static class HolderView {
        @Bind(R.id.name_tv)
        TextView nameTv;

        HolderView(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
