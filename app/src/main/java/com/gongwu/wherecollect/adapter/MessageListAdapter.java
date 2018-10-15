package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.MessageBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private Context mContext;
    private List<MessageBean> datas;
    private MyOnItemClickListener onItemClickListener;


    public MessageListAdapter(Context mContext, List<MessageBean> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    public void setOnItemClickListener(MyOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageBean bean = datas.get(position);
        holder.msgContentTv.setText(bean.getContent());
        holder.msgTimeTv.setText(bean.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.msg_content_tv)
        TextView msgContentTv;
        @Bind(R.id.msg_time_tv)
        TextView msgTimeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }
}
