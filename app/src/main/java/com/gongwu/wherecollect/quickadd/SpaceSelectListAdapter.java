package com.gongwu.wherecollect.quickadd;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
public class SpaceSelectListAdapter extends BaseAdapter {
    private Context context;
    private List<ObjectBean> mList;

    public SpaceSelectListAdapter(Context context, List<ObjectBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return StringUtils.getListSize(mList);
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_quick_space_select, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ObjectBean item = mList.get(position);
        holder.name.setText(item.getName());
        holder.tvNum.setText(item.getRecommend() + "");
        holder.tvNum.setEnabled(item.getRecommend() > 0);
        holder.name.setEnabled(item.getRecommend() > 0);
        holder.ivJian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getRecommend() >= 1) {
                    item.setRecommend(item.getRecommend() - 1);
                    notifyDataSetChanged();
                    ((QuickSpaceSelectListActivity) context).count -= 1;
                    ((QuickSpaceSelectListActivity) context).setBtnStatus();
                }
            }
        });
        holder.ivJia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getRecommend() < 10) {
                    item.setRecommend(item.getRecommend() + 1);
                    notifyDataSetChanged();
                    ((QuickSpaceSelectListActivity) context).count += 1;
                    ((QuickSpaceSelectListActivity) context).setBtnStatus();
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.iv_jian)
        ImageView ivJian;
        @Bind(R.id.tv_num)
        TextView tvNum;
        @Bind(R.id.iv_jia)
        ImageView ivJia;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
