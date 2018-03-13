package com.gongwu.wherecollect.quickadd;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.view.FlowViewGroup;
import com.gongwu.wherecollect.view.SpaceEditDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
public class QuickFunitureSelectListAdapter extends BaseAdapter {
    private Context context;
    private List<ObjectBean> mList;
    private MyOnItemClickListener onItemClickListener;

    public QuickFunitureSelectListAdapter(Context context, List<ObjectBean> mList) {
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
            convertView = View.inflate(context, R.layout.item_quick_furniture, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(mList.get(position).getName());
        if (mList.get(position).isOpen()) {//如果打开的
            holder.flowLayout.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(R.drawable.icon_next_black_bottom);
            addFlow(mList.get(position), holder.flowLayout);
        } else {
            holder.flowLayout.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.icon_next_black);
        }
        holder.nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.flowLayout.getVisibility() == View.VISIBLE) {
                    holder.flowLayout.setVisibility(View.GONE);
                    holder.imageView.setImageResource(R.drawable.icon_next_black);
                    mList.get(position).setOpen(false);
                } else {
                    holder.flowLayout.setVisibility(View.VISIBLE);
                    addFlow(mList.get(position), holder.flowLayout);
                    holder.imageView.setImageResource(R.drawable.icon_next_black_bottom);
                    mList.get(position).setOpen(true);
                }
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(mList.get(position), holder.name);
            }
        });
        return convertView;
    }

    private void showEditDialog(final ObjectBean bean, final TextView name) {
        SpaceEditDialog dialog = new SpaceEditDialog(context, bean.getName()) {
            @Override
            protected void commit(String str) {
                super.commit(str);
                name.setText(str);
                bean.setName(str);
            }
        };
        dialog.show();
    }

    /**
     * 添加小类容
     *
     * @param bean
     */
    private void addFlow(final ObjectBean bean, FlowViewGroup layout) {
        layout.removeAllViews();
        for (int i = 0; i < bean.getLayers().size(); i++) {
            final int j = i;
            final TextView text = (TextView) View.inflate(context, R.layout.quick_funiture_textview, null);
            layout.addView(text);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            lp.bottomMargin = 10;
            lp.topMargin = 10;
            lp.rightMargin = 10;
            lp.leftMargin = 10;
            text.setLayoutParams(lp);
            text.setText(bean.getLayers().get(i).getName());
            text.setBackgroundResource(R.drawable.select_color_item_quick_funiture);
            text.setSelected(bean.getLayers().get(i).getRecommend()>0);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (text.isSelected()) {//如果是已选状态
                        text.setSelected(false);//取消选择
                        bean.getLayers().get(j).setRecommend(0);
                    } else {//如果是为选状态，选中
                        text.setSelected(true);
                        bean.getLayers().get(j).setRecommend(1);
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
        @Bind(R.id.iv_edit)
        ImageView edit;
        @Bind(R.id.flow_layout)
        FlowViewGroup flowLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
