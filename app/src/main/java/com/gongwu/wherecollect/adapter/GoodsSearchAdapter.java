package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Function:搜索物品列表
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class GoodsSearchAdapter extends BaseAdapter {
    Context context;
    List<ObjectBean> mlist;

    public GoodsSearchAdapter(Context context, List<ObjectBean> list) {
        this.context = context;
        this.mlist = list;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int i) {
        return mlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CustomViewHolder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.item_goods_search, null);
            holder = new CustomViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (CustomViewHolder) view.getTag();
        }
        ObjectBean bean = mlist.get(i);
        holder.nameTv.setText(bean.getName());
        holder.locationTv.setText(getLoction(bean));
        ImageLoader.load(context, holder.image, bean.getObject_url(), R.drawable.ic_img_error);
        return view;
    }

    /**
     * 拼接位置
     *
     * @return
     */
    public String getLoction(ObjectBean bean) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < StringUtils.getListSize(bean.getLocations()); i++) {
            sb.append(bean.getLocations().get(i).getName());
            if (i != bean.getLocations().size() - 1) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    public class CustomViewHolder {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.name_tv)
        TextView nameTv;
        @Bind(R.id.location_tv)
        TextView locationTv;

        public CustomViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
