package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 3.6 添加提醒功能
 */
public class AddRemindActivity extends BaseViewActivity {

    private static final int START_CODE = 0x523;

    private static final int END_YEAR = DateUtil.getNowYear() + 2;//时间选择最大年限
    private static final int END_MONTH = 11;//时间选择最大月份
    private static final int END_DAY = 31;//时间选择最大日期
    private long selectTime = 0;

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
    @Bind(R.id.remind_time_tv)
    TextView selectTimeTv;


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
                showDateDialog();
                break;
            case R.id.add_remind_finished_tv://完成
                break;
        }
    }

    /**
     * 时间选择dialog
     */
    private void showDateDialog() {
        Calendar selectDate = Calendar.getInstance();
        selectDate.setTime(new Date(selectTime == 0 ? System.currentTimeMillis() : selectTime));
        Calendar startDate = Calendar.getInstance();
        startDate.set(DateUtil.getNowYear(), DateUtil.getNowMonthNum(), DateUtil.getNowDay());
        Calendar endDate = Calendar.getInstance();
        endDate.set(END_YEAR, END_MONTH, END_DAY);
        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                selectTimeTv.setText(DateUtil.dateToString(date, DateUtil.DatePattern.ONLY_MINUTE));
                selectTime = date.getTime();
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .isCyclic(false)
                .setDate(selectDate)
                .setRangDate(startDate, endDate)
                .setLabel("年", "月", "日", "时", "分", "")
                .build();
        pvTime.show();
    }

    /**
     * 监听
     */
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
            ObjectBean selectBean = (ObjectBean) data.getSerializableExtra("objectBean");
            if (selectBean != null) {

            }
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AddRemindActivity.class);
        context.startActivity(intent);
    }
}
