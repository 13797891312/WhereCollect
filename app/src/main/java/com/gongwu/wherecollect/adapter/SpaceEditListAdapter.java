package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.LocationBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.SpaceEditDialog;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mChenys on 2017/2/15.
 */
public class SpaceEditListAdapter extends RecyclerView.Adapter<SpaceEditListAdapter.MyViewHolder> {
    List<LocationBean> mData;
    Context context;

    public SpaceEditListAdapter(Context context, List<LocationBean> mData) {
        this.mData = mData;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_space_edit_list, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SpaceEditListAdapter.MyViewHolder holder, final int position) {
        holder.name.setText(mData.get(position).getName());
        holder.itemView.setBackgroundColor(Color.WHITE);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(holder.getLayoutPosition());
            }
        });
        holder.delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRight(holder.getLayoutPosition());
            }
        });
        if (mData.get(position).isIs_share()) {
            holder.delete_img.setVisibility(View.INVISIBLE);
        }else{
            holder.delete_img.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 用来回调
     *
     * @param position
     */
    protected void openRight(int position) {
    }

    @Override
    public int getItemCount() {
        //注意:这里最少有一个,因为有多了一个添加按钮
        return StringUtils.getListSize(mData);
    }

    private void showDialog(final int position) {
        SpaceEditDialog dialog = new SpaceEditDialog(context, mData.get(position).getName()) {
            @Override
            protected void commit(String str) {
                super.commit(str);
                changeNameSpace(str, position);
            }
        };
        dialog.show();
    }

    /**
     * 空间改名字
     */
    private void changeNameSpace(final String name, final int position) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("name", name);
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在修改")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                mData.get(position).setName(name);
                notifyDataSetChanged();
                EventBus.getDefault().post(EventBusMsg.SPACE_EDIT);
            }
        };
        HttpClient.editSpace(context, map, listenner, mData.get(position).getCode());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.delete_img)
        ImageView delete_img;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}