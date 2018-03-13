package com.gongwu.wherecollect.furnitureEdit;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.FilterCategoryBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.FlowViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Function:系统家具列表
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class FurnitureSysListViewAdapter extends BaseAdapter {
    Context context;
    List<ObjectBean> mlist;

    public FurnitureSysListViewAdapter(Context context, List<ObjectBean> list) {
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
            view = View.inflate(context, R.layout.item_furniture_list, null);
            holder = new CustomViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (CustomViewHolder) view.getTag();
        }
        ObjectBean bean = mlist.get(i);
        holder.nameTv.setText(bean.getName());
        ImageLoader.load(context, holder.image, 2, bean.getBackground_url(), R.drawable.ic_img_error);
        addFlow(bean,holder.tagLayout);
        return view;
    }

    /**
     * 添加小类容
     *
     * @param bean
     */
    private void addFlow(final ObjectBean bean, FlowViewGroup layout) {
        layout.removeAllViews();
        for (int i = 0; i < StringUtils.getListSize(bean.getTags()); i++) {
            final TextView text = (TextView) View.inflate(context, R.layout.flow_textview, null);
            layout.addView(text);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            lp.bottomMargin = 5;
            lp.topMargin = 5;
            lp.rightMargin = 20;
            text.setLayoutParams(lp);
            text.setText(bean.getTags().get(i));
            text.setBackgroundResource(R.drawable.select_color_item);
            text.setTextColor(Color.parseColor("#999999"));
        }
    }

    public class CustomViewHolder {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.name_tv)
        TextView nameTv;
        @Bind(R.id.tag_layout)
        FlowViewGroup tagLayout;

        public CustomViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
