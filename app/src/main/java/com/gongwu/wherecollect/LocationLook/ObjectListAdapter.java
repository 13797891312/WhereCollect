package com.gongwu.wherecollect.LocationLook;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.entity.GoodsBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;
import com.nineoldandroids.animation.ObjectAnimator;

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
    Context context;
    List<ObjectBean> mlist;
    public int selectPostion = -1;

    public ObjectListAdapter(Context context, List<ObjectBean> list) {
        this.context = context;
        this.mlist = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_editlocation_goods_recyclerview, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if (position == selectPostion) {
            holder.linearLayout.setBackgroundResource(R.drawable.shape_maincolor_stock);
        } else {
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.trans));
        }
        ObjectBean tempBean = mlist.get(position);
        if (refresh) {
            holder.imageNameTv.setVisibility(View.VISIBLE);
            holder.imageNameTv.setText(tempBean.getName());
            holder.image.setImageDrawable(null);
            if (tempBean.getObject_url().contains("http")) {
                holder.image.setBackgroundColor(context.getResources().getColor(StringUtils.getResId(position)));
            } else {
                holder.image.setBackgroundColor(Color.parseColor(tempBean.getObject_url()));
            }
        } else {
            if (tempBean.getObject_url().contains("http")) {
                ImageLoader.load(context, holder.image, tempBean.getObject_url(), R.drawable.ic_img_error);
                holder.imageNameTv.setVisibility(View.GONE);
            } else {
                holder.imageNameTv.setVisibility(View.VISIBLE);
                holder.imageNameTv.setText(tempBean.getName());
                holder.image.setImageDrawable(null);
                holder.image.setBackgroundColor(Color.parseColor(tempBean.getObject_url()));
            }
        }
        if (statrAnim) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1f);//初始化操作，参数传入0和1，即由透明度0变化到透明度为1
            alphaAnimation.setFillAfter(true);//动画结束后保持状态
            alphaAnimation.setDuration(1500);//动画持续时间，单位为毫秒
            holder.image.startAnimation(alphaAnimation);//开始动画
        }
    }

    @Override
    public int getItemCount() {
        return StringUtils.getListSize(mlist);
    }

    public ObjectBean getItem(int position) {
        if (StringUtils.isEmpty(mlist)) return null;
        return mlist.get(position);
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

    private boolean refresh = false;
    private boolean statrAnim = false;

    public void refreshData() {
        this.refresh = !this.refresh;
        this.statrAnim = true;
        notifyDataSetChanged();
    }

    public void defaultData() {
        this.refresh = false;
        this.statrAnim = false;
        notifyDataSetChanged();
    }

    /**
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

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.linearLayout)
        RelativeLayout linearLayout;
        @Bind(R.id.item_image_name_tv)
        TextView imageNameTv;

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

}
