package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.FilterCategoryBean;
import com.gongwu.wherecollect.view.FlowViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
public class FilterCatagoryListAdapter extends BaseAdapter {
    private Context context;
    private List<FilterCategoryBean> mList;
    private MyOnItemClickListener onItemClickListener;

    public FilterCatagoryListAdapter(Context context, List<FilterCategoryBean> mList) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mList = mList;
    }

    public void setOnItemClickListener(MyOnItemClickListener listener) {
        this.onItemClickListener = listener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_filter_catagory, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(mList.get(position).getName());
        if (mList.get(position).getSelectSubs().isEmpty()) {//如果有选择的
            holder.flowLayout.setVisibility(View.GONE);
            //            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(R.drawable.icon_next_black);
        } else {
            holder.flowLayout.setVisibility(View.VISIBLE);
            //            holder.imageView.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.icon_next_black_bottom);
            addFlow(mList.get(position), holder.flowLayout);
        }
        holder.nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imageView.getVisibility() == View.GONE) {
                    return;
                }
                if (holder.flowLayout.getVisibility() == View.VISIBLE) {
                    holder.flowLayout.setVisibility(View.GONE);
                    holder.imageView.setImageResource(R.drawable.icon_next_black);
                } else {
                    holder.flowLayout.setVisibility(View.VISIBLE);
                    addFlow(mList.get(position), holder.flowLayout);
                    holder.imageView.setImageResource(R.drawable.icon_next_black_bottom);
                }
            }
        });
        return convertView;
    }

    /**
     * 添加小类容
     *
     * @param bean
     */
    private void addFlow(final FilterCategoryBean bean, FlowViewGroup layout) {
        layout.removeAllViews();
        for (int i = 0; i < bean.getSub().size(); i++) {
            final int j = i;
            final View view = View.inflate(context, R.layout.filter_textview, null);
            final TextView text = (TextView) view.findViewById(R.id.text);
            final ImageView image = (ImageView) view.findViewById(R.id.image);
            layout.addView(view);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            lp.bottomMargin = 8;
            lp.topMargin = 8;
            lp.rightMargin = 10;
            lp.leftMargin = 10;
            view.setLayoutParams(lp);
            text.setText(bean.getSub().get(i).getName());
            view.setSelected(bean.getSelectSubs().contains(bean.getSub().get(i)));
            text.setSelected(view.isSelected());
            image.setVisibility(view.isSelected() ? View.VISIBLE : View.INVISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (view.isSelected()) {//如果是已选状态
                        view.setSelected(false);//取消选择
                        text.setSelected(view.isSelected());
                        image.setVisibility(view.isSelected() ? View.VISIBLE : View.INVISIBLE);
                        bean.getSelectSubs().remove(bean.getSub().get(j));
                    } else {//如果是为选状态，选中
                        view.setSelected(true);
                        text.setSelected(view.isSelected());
                        image.setVisibility(view.isSelected() ? View.VISIBLE : View.INVISIBLE);
                        bean.getSelectSubs().add(bean.getSub().get(j));
                    }
                }
            });
        }
    }

    static class ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.name_layout)
        View nameLayout;
        @Bind(R.id.imageView)
        ImageView imageView;
        @Bind(R.id.flow_layout)
        FlowViewGroup flowLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
