package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.gongwu.wherecollect.entity.SharePersonBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/10/12.
 */

public class AddSharePersonOldListAdapter extends RecyclerView.Adapter<AddSharePersonOldListAdapter.ViewHolder> {
    private Context mContext;
    private List<SharePersonBean> datas;

    public AddSharePersonOldListAdapter(Context mContext, List<SharePersonBean> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
