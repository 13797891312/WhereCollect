package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.SaveDate;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 3.6 添加提醒功能
 */
public class AddRemindActivity extends BaseViewActivity {

    private static final int START_CODE = 0x523;

    @Bind(R.id.title_text_view)
    TextView titleTv;
    @Bind(R.id.add_remind_finished_tv)
    TextView addRemindFinishedTv;
    @Bind(R.id.add_remind_et)
    EditText mEditText;
    @Bind(R.id.remind_first_switch)
    SwitchCompat mFirstSwitch;
    @Bind(R.id.remind_overdue_time_switch)
    SwitchCompat mOverdueTimeSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initView() {
        titleTv.setText(getResources().getText(R.string.add_remind_title_text));
        titleLayout.setVisibility(View.GONE);
        addRemindFinishedTv.setVisibility(View.VISIBLE);
        mEditText.setCursorVisible(false);
        mFirstSwitch.setChecked(true);
    }

    @OnClick({R.id.back_bt, R.id.remind_goods_layout, R.id.add_remind_et,
            R.id.remind_time_layout, R.id.add_remind_finished_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_bt://返回
                finish();
                break;
            case R.id.remind_goods_layout://关联物品
                startActivityForResult(new Intent(this, RelationGoodsActivity.class), START_CODE);
                break;
            case R.id.add_remind_et://标题
                mEditText.setCursorVisible(true);
                break;
            case R.id.remind_time_layout://提醒时间
                break;
            case R.id.add_remind_finished_tv://完成
                break;
        }
    }

    private void initEvent() {
        //优先处理
        mFirstSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        //过期提醒
        mOverdueTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_CODE && resultCode == RESULT_OK) {

        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AddRemindActivity.class);
        context.startActivity(intent);
    }
}
