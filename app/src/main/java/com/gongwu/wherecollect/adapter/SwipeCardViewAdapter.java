package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.GlideRoundTransform;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SwipeCardViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<ObjectBean> mData;
    private int cardWidth;
    private int cardHeight;
    private boolean show;

    public SwipeCardViewAdapter(Context mContext, List<ObjectBean> mData) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        float density = dm.density;
        cardWidth = (int) (dm.widthPixels - (2 * 12 * density));
        cardHeight = (int) (dm.heightPixels - (360 * density));
        this.mContext = mContext;
        this.mData = mData;
    }

    public void remove(int index) {
        if (index > -1 && index < mData.size()) {
            mData.remove(index);
            notifyDataSetChanged();
        }
    }

    public void add(ObjectBean bean) {
        mData.add(0, bean);
        show = true;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public ObjectBean getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe_stack_layout, parent, false);
            convertView.getLayoutParams().width = cardWidth;
            holder = new ViewHolder(convertView);
            holder.mImageView.getLayoutParams().height = cardHeight;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ObjectBean bean = mData.get(position);
        holder.goodName.setText(bean.getName());
        holder.goodType.setText(bean.getRecommend_category_name());
        holder.mImageView.setImageDrawable(null);
        Glide.with(mContext)
                .load(bean.getObjectUrl())
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext, 6))
                .into(holder.mImageView);
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.portrait)
        ImageView mImageView;
        @Bind(R.id.name_tv)
        TextView goodName;
        @Bind(R.id.type_tv)
        TextView goodType;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
