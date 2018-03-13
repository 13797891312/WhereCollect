package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
public class JijieListAdapter extends BaseAdapter {
    private Context context;
    private List<String> mList;
    private List<String> selectList;

    public JijieListAdapter(Context context, List<String> mList, List<String> selectList) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mList = mList;
        this.selectList = selectList;
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
            convertView = View.inflate(context, R.layout.item_jijie_list, null);
            holder = new HolderView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        holder.nameTv.setText(mList.get(position));
        if (selectList.contains(mList.get(position))) {
            holder.selectIv.setVisibility(View.VISIBLE);
        } else {
            holder.selectIv.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class HolderView {
        @Bind(R.id.name_tv)
        TextView nameTv;
        @Bind(R.id.select_iv)
        ImageView selectIv;

        HolderView(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
