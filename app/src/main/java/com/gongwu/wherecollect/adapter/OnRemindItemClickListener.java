package com.gongwu.wherecollect.adapter;

import android.view.View;

/**
 * Created by zhaojin on 2016/4/15.
 */
public interface OnRemindItemClickListener {
    void onItemClick(int position, View view);

    void onItemDeleteClick(int position, View view);

    void onItemEditFinishedClick(int position, View view);
}

