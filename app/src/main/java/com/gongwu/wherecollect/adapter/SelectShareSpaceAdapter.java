package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mucll on 2018/10/14.
 */

public class SelectShareSpaceAdapter extends RecyclerView.Adapter<SelectShareSpaceAdapter.ViewHolder> {
    private Context mContext;
    private List<ObjectBean> datas;

    public SelectShareSpaceAdapter(Context context, List<ObjectBean> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_select_share_space_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ObjectBean objectBean = datas.get(position);
        holder.spaceName.setText(objectBean.getName());
        holder.selectSpaceView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                datas.get(position).setSelectSpace(isChecked);
            }
        });
        holder.selectSpaceView.setChecked(objectBean.isSelectSpace());
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.select_space_name_tv)
        TextView spaceName;
        @Bind(R.id.select_space_name_box)
        CheckBox selectSpaceView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
