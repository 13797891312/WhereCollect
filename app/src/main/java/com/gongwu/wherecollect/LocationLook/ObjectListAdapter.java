package com.gongwu.wherecollect.LocationLook;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
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
    Context context;
    List<ObjectBean> mlist;
    public int selectPostion=-1;

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
        if(position==selectPostion){
            holder.linearLayout.setBackgroundResource(R.drawable.shape_maincolor_stock);
        }else{
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.trans));
        }
        ImageLoader.load(context, holder.image,mlist.get(position).getObject_url(), R.drawable.ic_img_error);
    }

    @Override
    public int getItemCount() {
        return StringUtils.getListSize(mlist);
    }

    public ObjectBean getItem(int position){
        if(StringUtils.isEmpty(mlist))return null;
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

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.linearLayout)
        LinearLayout linearLayout;

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