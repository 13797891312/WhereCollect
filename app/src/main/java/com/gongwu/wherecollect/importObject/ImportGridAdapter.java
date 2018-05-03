package com.gongwu.wherecollect.importObject;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.ToastUtil;

import java.util.List;
import java.util.Map;

public class ImportGridAdapter extends BaseAdapter {
    Activity act;
    List<ObjectBean> dataList;
    Map<String, ObjectBean> chooseMap;

    public ImportGridAdapter(Activity act, List<ObjectBean> list, Map<String, ObjectBean> chooseMap) {
        this.act = act;
        dataList = list;
        this.chooseMap = chooseMap;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null) {
            count = dataList.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final ObjectBean item = dataList.get(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(act, R.layout.item_import_grid, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.image);
            holder.nameTV = (TextView) convertView.findViewById(R.id.item_image_name_text);
            holder.selected = (ImageView) convertView
                    .findViewById(R.id.isselected);
            holder.text = (TextView) convertView
                    .findViewById(R.id.item_image_grid_text);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.selected.setVisibility(View.VISIBLE);
        holder.iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (item.getObject_url().contains("http")) {
            ImageLoader.load(convertView.getContext(), holder.iv, item.getObject_url(), R.drawable.icon_placeholder);
            holder.nameTV.setVisibility(View.GONE);
        } else {
            holder.nameTV.setVisibility(View.VISIBLE);
            holder.nameTV.setText(item.getName());
            holder.iv.setImageDrawable(null);
            holder.iv.setBackgroundColor(Color.parseColor(item.getObject_url()));
        }
        if (chooseMap.containsKey(item.get_id())) {
            holder.selected.setVisibility(View.VISIBLE);
            holder.text.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.black_54));
        } else {
            holder.selected.setVisibility(View.GONE);
            holder.text.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.black_12));
        }
        holder.text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chooseMap.containsValue(item)) {
                    if (chooseMap.size() >= 10) {
                        ToastUtil.show(act, "一次只能导入10个物品", Toast.LENGTH_SHORT);
                        return;
                    }
                    chooseMap.put(item.get_id(), item);
                    holder.selected.setVisibility(View.VISIBLE);
                    holder.text.setBackgroundColor(v.getContext().getResources().getColor(R.color.black_54));
                    change();
                } else {
                    chooseMap.remove(item.get_id());
                    holder.selected.setVisibility(View.GONE);
                    holder.text.setBackgroundColor(v.getContext().getResources().getColor(R.color.black_12));
                    change();
                }
            }
        });
        return convertView;
    }

    /**
     * 用于重写监听选择变动
     */
    protected void change() {
    }

    class Holder {
        private ImageView iv;
        private ImageView selected;
        private TextView text;
        private TextView nameTV;
    }
}
