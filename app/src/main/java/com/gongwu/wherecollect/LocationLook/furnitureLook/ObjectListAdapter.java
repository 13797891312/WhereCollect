package com.gongwu.wherecollect.LocationLook.furnitureLook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.adapter.MyOnItemLongClickListener;
import com.gongwu.wherecollect.entity.GoodsBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Function:
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ObjectListAdapter extends RecyclerView.Adapter<ObjectListAdapter.CustomViewHolder> {
    public MyOnItemClickListener onItemClickListener;
    MyOnItemLongClickListener mOnItemLongClickListener;
    Context context;
    List<ObjectBean> mlist;

    public ObjectListAdapter(Context context, List<ObjectBean> list) {
        this.context = context;
        this.mlist = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_furniture_object_recyclerview, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) ((BaseViewActivity.getScreenWidth(((Activity) context)) - 16 * BaseViewActivity.getScreenScale(
                        ((Activity)
                                context)))
                        / 4));
        view.findViewById(R.id.image).setLayoutParams(lp);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if (((FurnitureLookActivity) context).selectObject != null && mlist.get(position).get_id().equals((
                (FurnitureLookActivity) context).selectObject.get_id())) {
            holder.linearLayout.setBackgroundResource(R.drawable.shape_maincolor_stock);
        } else {
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.trans));
        }
        ObjectBean tempBean = mlist.get(position);
        if (refresh) {
            holder.imageHintTv.setVisibility(View.VISIBLE);
            holder.imageHintTv.setText(tempBean.getName());
            holder.image.setImageDrawable(null);
            if (tempBean.getObject_url().contains("http")) {
                holder.image.setBackgroundColor(context.getResources().getColor(StringUtils.getResId(position)));
            } else {
                holder.image.setBackgroundColor(Color.parseColor(tempBean.getObject_url()));
            }
        } else {
            if (tempBean.getObject_url().contains("http")) {
                ImageLoader.load(context, holder.image, tempBean.getObject_url(), R.drawable.ic_img_error);
                holder.imageHintTv.setVisibility(View.GONE);
            } else {
                holder.imageHintTv.setVisibility(View.VISIBLE);
                holder.imageHintTv.setText(tempBean.getName());
                holder.image.setImageDrawable(null);
                holder.image.setBackgroundColor(Color.parseColor(tempBean.getObject_url()));
            }
        }
        holder.itemView.setOnLongClickListener(new MyLongClickListener(holder.getLayoutPosition()));
    }

    @Override
    public int getItemCount() {
        return StringUtils.getListSize(mlist);
    }

    private boolean refresh = false;

    public void refreshData() {
        this.refresh = !this.refresh;
        notifyDataSetChanged();
    }

    public void defaultData() {
        this.refresh = false;
        notifyDataSetChanged();
    }


    /**
     * 删除一项
     *
     * @param bean
     */
    public void removeBean(GoodsBean bean) {
        mlist.remove(bean);
        notifyDataSetChanged();
    }

    /**
     * 添加一项
     *
     * @param bean
     */
    public void addBean(ObjectBean bean) {
        mlist.add(bean);
        notifyDataSetChanged();
    }

    /**
     * 可以返回不同类型加载不同布局
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setOnItemClickListener(MyOnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setmOnItemLongClickListener(MyOnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.image)
        ImageView image;
        //        @Bind(R.id.object_name_tv)
//        TextView name;
        @Bind(R.id.linearLayout)
        RelativeLayout linearLayout;
        @Bind(R.id.image_hint_tv)
        TextView imageHintTv;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getLayoutPosition(), v);
            }
        }
    }

    class MyLongClickListener implements View.OnLongClickListener {
        private int position;

        public MyLongClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(position, null);
            }
            return true;
        }
    }
}
