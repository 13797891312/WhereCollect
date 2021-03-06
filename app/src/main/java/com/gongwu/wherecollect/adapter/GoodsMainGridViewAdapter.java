package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Function:主页物品查看列表
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class GoodsMainGridViewAdapter extends RecyclerView.Adapter<GoodsMainGridViewAdapter.CustomViewHolder> {
    Context context;
    List<ObjectBean> mlist;

    public GoodsMainGridViewAdapter(Context context, List<ObjectBean> list) {
        this.context = context;
        this.mlist = list;
    }

    /**
     * 拼接位置
     *
     * @return
     */
    public String getLoction(ObjectBean bean) {
        if (StringUtils.isEmpty(bean.getLocations())) {
            return "未归位";
        }
        Collections.sort(bean.getLocations(), new Comparator<BaseBean>() {
            @Override
            public int compare(BaseBean lhs, BaseBean rhs) {
                return lhs.getLevel() - rhs.getLevel();
            }
        });
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < StringUtils.getListSize(bean.getLocations()); i++) {
            sb.append(bean.getLocations().get(i).getName());
            if (i != bean.getLocations().size() - 1) {
                sb.append("/");
            }
        }
        return sb.length() == 0 ? "未归位" : sb.toString();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goods_main_gridview, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int i) {
        ObjectBean bean = mlist.get(i);
        holder.image.refreshDrawableState();
        holder.image.setImageDrawable(null);
        holder.image.setBackgroundResource(0);
        if (!TextUtils.isEmpty(bean.getObject_url()) && bean.getObject_url().contains("http")) {
            ImageLoader.load(context, holder.image, bean.getObject_url());
            holder.imgTv.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(bean.getObject_url()) && !bean.getObject_url().contains("/")) {
            holder.image.setBackgroundColor(Color.parseColor(bean.getObject_url()));
            holder.imgTv.setVisibility(View.VISIBLE);
            holder.imgTv.setText(bean.getName());
        } else {
            holder.image.setBackgroundColor(context.getResources().getColor(R.color.goods_color_1));
            holder.imgTv.setVisibility(View.VISIBLE);
            holder.imgTv.setText(bean.getName());
        }
        if (i == mlist.size() - 1) {
            holder.footer.setVisibility(View.VISIBLE);
        } else {
            holder.footer.setVisibility(View.GONE);
        }
        if (bean.getIs_share() == 1) {
            holder.isShare.setVisibility(View.VISIBLE);
        } else {
            holder.isShare.setVisibility(View.GONE);
        }
        holder.nameTv.setText(bean.getName());
        holder.locationTv.setText(getLoction(bean));
    }

    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.name_tv)
        TextView nameTv;
        @Bind(R.id.footer)
        View footer;
        @Bind(R.id.location_tv)
        TextView locationTv;
        @Bind(R.id.no_url_img_tv)
        TextView imgTv;
        @Bind(R.id.goods_is_share_layout)
        View isShare;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setTag(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    public MyOnItemClickListener onItemClickListener;

    public void setOnItemClickListener(MyOnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
