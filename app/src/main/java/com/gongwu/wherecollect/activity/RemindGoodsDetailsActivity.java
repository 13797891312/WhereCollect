package com.gongwu.wherecollect.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gongwu.wherecollect.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 提醒详情
 */
public class RemindGoodsDetailsActivity extends BaseViewActivity {
    @Bind(R.id.textBtn)
    TextView textBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_goods_details);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.goods_remind_details_text));
        initEvent();
    }

    private void initEvent() {
        textBtn.setTextColor(getResources().getColor(R.color.maincolor));
        titleLayout.setTextBtnListener("编辑", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
