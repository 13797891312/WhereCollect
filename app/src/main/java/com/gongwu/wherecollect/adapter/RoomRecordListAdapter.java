package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.RoomRecordBean;
import com.gongwu.wherecollect.util.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class RoomRecordListAdapter extends BaseAdapter {
    Context context;
    List<RoomRecordBean> mlist;
    SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日,E");
    SimpleDateFormat stringformat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public RoomRecordListAdapter(Context context, List<RoomRecordBean> list) {
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
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.item_roomrecord_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        RoomRecordBean bean = mlist.get(i);
        holder.roomNameTv.setText(bean.getName());
        holder.memoTv.setText(TextUtils.isEmpty(bean.getChange_tex())?"":bean.getChange_tex());
        try {
            holder.timeTv.setText(format.format(stringformat.parse(bean.getCreated_at())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ImageLoader.load(context, holder.imageview, bean.getImage_url());
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.imageview)
        ImageView imageview;
        @Bind(R.id.roomName_tv)
        TextView roomNameTv;
        @Bind(R.id.time_tv)
        TextView timeTv;
        @Bind(R.id.memo_tv)
        TextView memoTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
