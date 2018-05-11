package com.gongwu.wherecollect.ImageSelect;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ImageData;
import com.gongwu.wherecollect.util.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter {
    final String TAG = getClass().getSimpleName();
    public List<ImageData> chooseList = new ArrayList<ImageData>();
    public int selectTotal = 0;
    Activity act;
    List<ImageData> dataList;
    private int max = 10;

    public ImageGridAdapter(Activity act, List<ImageData> list, int max) {
        this.act = act;
        dataList = list;
        this.max = max;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final ImageData item = dataList.get(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(act, R.layout.item_image_grid, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.image);
            holder.selected = (ImageView) convertView
                    .findViewById(R.id.isselected);
            holder.text = (TextView) convertView
                    .findViewById(R.id.item_image_grid_text);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (item.isCamare()) {
            holder.selected.setVisibility(View.GONE);
            holder.iv.setImageResource(R.drawable.bk_camera);
            holder.iv.setScaleType(ImageView.ScaleType.CENTER);
            holder.text.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.black_12));
            return convertView;
        } else {
            holder.selected.setVisibility(View.VISIBLE);
            holder.iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        ImageLoader.loadFromFile(convertView.getContext(), new File(item.getBigUri()), holder.iv);
        if (item.isSelect()) {
            holder.selected.setSelected(true);
            holder.text.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.black_54));
        } else {
            holder.selected.setSelected(false);
            holder.text.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.black_12));
        }
        holder.iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelect(!item.isSelect());
                if (item.isSelect()) {
                    if (chooseList.size() >= ImageGridActivity.imgMax) {
                        //                        Toast.makeText(act, "最多还能选" + ImageGridActivity.max + "张图片", Toast
                        // .LENGTH_LONG).show();
                        Toast.makeText(act, "最多只能选" + max + "张图片", Toast.LENGTH_LONG).show();
                    } else {
                        holder.selected.setSelected(true);
                        holder.text.setBackgroundColor(v.getContext().getResources().getColor(R.color.black_54));
                        chooseList.add(item);
                        change(chooseList);
                    }
                } else if (!item.isSelect()) {
                    holder.selected.setSelected(false);
                    holder.text.setBackgroundColor(v.getContext().getResources().getColor(R.color.black_12));
                    chooseList.remove(item);
                    change(chooseList);
                }
            }
        });
        return convertView;
    }

    /**
     * 用于重写监听选择变动
     */
    protected void change(List<ImageData> chooseList) {
    }

    class Holder {
        private ImageView iv;
        private ImageView selected;
        private TextView text;
    }
}
