package com.gongwu.wherecollect.activity;

import android.os.Bundle;

import com.gongwu.wherecollect.R;

import butterknife.ButterKnife;

/**
 * 添加提醒
 */
public class AddGoodsRemindActivity extends BaseViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods_remind);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.add_goods_remind_text));
    }
}
