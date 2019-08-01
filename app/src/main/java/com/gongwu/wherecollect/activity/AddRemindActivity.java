package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gongwu.wherecollect.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddRemindActivity extends BaseViewActivity {

    @Bind(R.id.title_text_view)
    TextView titleTv;
    @Bind(R.id.add_remind_finished_tv)
    TextView addRemindFinishedTv;
    @Bind(R.id.back_bt)
    ImageButton backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleTv.setText(getResources().getText(R.string.add_remind_title_text));
        titleLayout.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
        addRemindFinishedTv.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.back_bt, R.id.add_remind_finished_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_bt:
                finish();
                break;
            case R.id.add_remind_finished_tv:
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AddRemindActivity.class);
        context.startActivity(intent);
    }
}
