package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.view.GoodsImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mucll on 2018/3/16.
 */

public class AddMoreGoodsListAdapter extends BaseAdapter {
    private Context context;
    private List<ObjectBean> mDatas;

    public AddMoreGoodsListAdapter(Context context, List<ObjectBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public ObjectBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_add_more_goods_list_layout, null);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ObjectBean bean = getItem(position);
        if (!TextUtils.isEmpty(bean.getName())) {
            holder.goodsNameTv.setText(bean.getName());
        }
        holder.goodsIv.setHead(position + "", bean.getName(),
                TextUtils.isEmpty(bean.getObject_url()) ? "" : bean.getObject_url());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.item_goods_iv)
        GoodsImageView goodsIv;
        @Bind(R.id.item_goods_name_tv)
        TextView goodsNameTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
