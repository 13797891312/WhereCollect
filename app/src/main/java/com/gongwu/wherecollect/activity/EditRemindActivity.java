package com.gongwu.wherecollect.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.RemindBean;
import com.gongwu.wherecollect.entity.RemindDetailsBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.AppConstant;
import com.gongwu.wherecollect.util.DateUtil;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.zhaojin.myviews.Loading;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 3.6 添加提醒功能
 */
public class EditRemindActivity extends BaseViewActivity {

    private static final int START_CODE = 0x523;
    private static final int START_REMARKS_CODE = 0x423;

    private static final int CHECK_TRUE_CODE = 1;
//    private static final int END_YEAR = DateUtil.getNowYear() + 2;//时间选择最大年限
//    private static final int END_MONTH = 11;//时间选择最大月份
//    private static final int END_DAY = 31;//时间选择最大日期

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
    @Bind(R.id.remind_remarks_content_tx)
    TextView remarksTv;
    @Bind(R.id.remind_goods_layout)
    RelativeLayout addRemindGoodsLayout;
    @Bind(R.id.remind_goods_details_layout)
    RelativeLayout remindGoodsDetailsLayout;
    @Bind(R.id.goods_iv)
    ImageView goodsIv;
    @Bind(R.id.goods_name_tv)
    TextView goodsNameTv;
    @Bind(R.id.goods_classify_tv)
    TextView goodsClassifyTv;
    @Bind(R.id.goods_location_tv)
    TextView goodsLocationTv;
    @Bind(R.id.edit_remind_detail_layout)
    LinearLayout editDetailLayout;
    @Bind(R.id.edit_remind_submit_tv)
    TextView editSubmitTv;
    @Bind(R.id.goods_location_btn)
    ImageView locationIv;
    @Bind(R.id.no_url_img_tv)
    TextView imgTv;

    private long selectTime = 0;
    private boolean edit = false;
    private ObjectBean selectGoods;
    private RemindDetailsBean detailsBean;
    private ObjectBean locationBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_remind);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {
        RemindBean remindBean = (RemindBean) getIntent().getSerializableExtra("remind_bean");
        if (remindBean != null) {
            titleTv.setText(getResources().getText(R.string.remind_details_title_text));
            addRemindFinishedTv.setVisibility(View.GONE);
            getRemindDetailsHttpGet(remindBean);
        } else {
            initEvent();
        }
    }

    private void initView() {
        titleTv.setText(getResources().getText(R.string.add_remind_title_text));
        titleLayout.setVisibility(View.GONE);
        remindGoodsDetailsLayout.setVisibility(View.GONE);
        addRemindFinishedTv.setVisibility(View.VISIBLE);
        mOverdueTimeSwitch.setChecked(true);
    }

    @OnClick({R.id.back_bt, R.id.remind_goods_layout, R.id.remind_time_layout,
            R.id.add_remind_finished_tv, R.id.remind_remarks_layout,
            R.id.edit_remind_delete_tv, R.id.edit_remind_submit_tv,
            R.id.goods_location_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_bt://返回
                finish();
                break;
            case R.id.remind_goods_layout://关联物品
                startActivityForResult(new Intent(this, RelationGoodsListActivity.class), START_CODE);
                break;
            case R.id.remind_time_layout://提醒时间
                StringUtils.hideKeyboard(mEditText);
                showDateDialog();
                break;
            case R.id.add_remind_finished_tv://完成
                submitRemindHttpPost();
                break;
            case R.id.remind_remarks_layout://说明备注
                Intent intent = new Intent(context, RemindRemarksActivity.class);
                if (!TextUtils.isEmpty(remarksTv.getText().toString())) {
                    intent.putExtra("remind_remarks", remarksTv.getText().toString());
                }
                startActivityForResult(intent, START_REMARKS_CODE);
                break;
            case R.id.edit_remind_delete_tv://删除
                deleteRemind();
                break;
            case R.id.edit_remind_submit_tv://标记已完成
                setRemindDone();
                break;
            case R.id.goods_location_btn:
                Intent intentOne = new Intent(context, MainActivity.class);
                intentOne.putExtra("object", locationBean);
                startActivity(intentOne);
                break;
            default:
                break;
        }
    }

    /**
     * 获取提醒详情
     */
    public void getRemindDetailsHttpGet(final RemindBean remindBean) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("remind_id", remindBean.get_id());
        if (!TextUtils.isEmpty(remindBean.getAssociated_object_id())) {
            map.put("associated_object_id", remindBean.getAssociated_object_id());
        }
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                editDetailLayout.setVisibility(View.VISIBLE);
                if (remindBean.getDone() == 1) {
                    editSubmitTv.setVisibility(View.GONE);
                }
                detailsBean = JsonUtils.objectFromJson(r.getResult(), RemindDetailsBean.class);
                //设置物品
                if (detailsBean.getAssociated_object() != null) {
                    setSelectGoods(detailsBean.getAssociated_object());
                    selectGoods = detailsBean.getAssociated_object();
                }
                //标题
                mEditText.setText(detailsBean.getTitle());
                //时间
                if (detailsBean.getTips_time() != 0) {
                    selectTimeTv.setText(DateUtil.dateToString(new Date(detailsBean.getTips_time()), DateUtil.DatePattern.ONLY_MINUTE));
                    selectTime = detailsBean.getTips_time();
                }
                //优化
                mFirstSwitch.setChecked(detailsBean.getFirst() == CHECK_TRUE_CODE);
                //重复提醒
                mOverdueTimeSwitch.setChecked(detailsBean.getRepeat() == CHECK_TRUE_CODE);
                //备注
                remarksTv.setText(detailsBean.getDescription());
                initEvent();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.getRemindDetails(context, map, listenner);
    }

    /**
     * 删除提醒
     */
    public void deleteRemind() {
        if (detailsBean == null) return;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("remind_id", detailsBean.get_id());
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                try {
                    JSONObject jsonObject = new JSONObject(r.getResult());
                    int code = jsonObject.getInt("ok");
                    if (AppConstant.ADD_REMIND_SUCCESS == code) {
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.deteleRemind(context, map, listenner);
    }

    /**
     * 标记已完成
     */
    public void setRemindDone() {
        if (detailsBean == null) return;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("remind_id", detailsBean.get_id());
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                try {
                    JSONObject jsonObject = new JSONObject(r.getResult());
                    int code = jsonObject.getInt("ok");
                    if (AppConstant.ADD_REMIND_SUCCESS == code) {
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.setRemindDone(context, map, listenner);
    }

    /**
     * 添加提醒
     */
    private void submitRemindHttpPost() {
        if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
            ToastUtil.show(context, getResources().getString(R.string.add_remind_title_hint), Toast.LENGTH_SHORT);
            return;
        }
        if (selectTime == 0) {
            ToastUtil.show(context, getResources().getString(R.string.add_remind_time_hint), Toast.LENGTH_SHORT);
            return;
        } else if (selectTime < System.currentTimeMillis()) {
            ToastUtil.show(context, getResources().getString(R.string.add_remind_time_hint_two), Toast.LENGTH_SHORT);
            return;
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("title", mEditText.getText().toString().trim());
        map.put("description", remarksTv.getText().toString().trim());
        map.put("tips_time", selectTime + "");
        map.put("first", mFirstSwitch.isChecked() ? "1" : "0");
        map.put("repeat", mOverdueTimeSwitch.isChecked() ? "1" : "0");
        if (addRemindGoodsLayout.getVisibility() == View.GONE) {
            map.put("associated_object_id", selectGoods != null ? selectGoods.getId() : "");
            map.put("associated_object_url", selectGoods != null ? selectGoods.getObject_url() : "");
        }
        if (!TextUtils.isEmpty(AppConstant.DEVICE_TOKEN)) {
            map.put("device_token", AppConstant.DEVICE_TOKEN);
        }
        if (detailsBean != null) {
            map.put("remind_id", detailsBean.get_id());
        }
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                try {
                    JSONObject jsonObject = new JSONObject(r.getResult());
                    int code = jsonObject.getInt("ok");
                    if (AppConstant.ADD_REMIND_SUCCESS == code) {
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        if (detailsBean != null) {
            HttpClient.updateRemind(context, map, listenner);
        } else {
            HttpClient.addRemind(context, map, listenner);
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
//        Calendar endDate = Calendar.getInstance();//限制选择最大时间
//        endDate.set(END_YEAR, END_MONTH, END_DAY);
        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                editSubmitBtEnable();
                selectTime = date.getTime();
                //去掉分
//                selectTime = date.getTime() / 1000 / (60 * 60) * (60 * 60) * 1000;
                selectTimeTv.setText(DateUtil.dateToString(new Date(selectTime), DateUtil.DatePattern.ONLY_MINUTE));
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .isCyclic(false)
                .setDate(selectDate)
                .setRangDate(startDate, null)
                .setLabel("年", "月", "日", "时", "分", "")
                .build();
        pvTime.show();
    }

    private void editSubmitBtEnable() {
        if (!edit && detailsBean != null) {
            edit = true;
            //隐藏编辑操作按钮
            editDetailLayout.setVisibility(View.GONE);
            //显示编辑完成按钮
            addRemindFinishedTv.setVisibility(View.VISIBLE);
            //隐藏位置按钮
            locationIv.setVisibility(View.GONE);
        }
    }

    /**
     * 监听
     */
    private void initEvent() {
        //优先处理
        mFirstSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StringUtils.hideKeyboard(mEditText);
                editSubmitBtEnable();
            }
        });
        //过期提醒
        mOverdueTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StringUtils.hideKeyboard(mEditText);
                editSubmitBtEnable();
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editSubmitBtEnable();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_CODE && resultCode == RESULT_OK) {
            ObjectBean objectBean = (ObjectBean) data.getSerializableExtra("objectBean");
            if (objectBean != null) {
                selectGoods = objectBean;
                editSubmitBtEnable();
                setSelectGoods(selectGoods);
                //新建不显示跳转位置按钮
                locationIv.setVisibility(View.GONE);
            }
        }
        if (requestCode == START_REMARKS_CODE && resultCode == RESULT_OK) {
            remarksTv.setText(data.getStringExtra("remind_remarks"));
            if (detailsBean != null && !remarksTv.getText().toString().trim().equals(detailsBean.getDescription())) {
                editSubmitBtEnable();
            }
        }
    }

    /**
     * 初始化关联物品数据
     */
    private void setSelectGoods(ObjectBean selectGoods) {
        //图片文字
        imgTv.setVisibility(View.GONE);
        //关联物品跳转按钮
        addRemindGoodsLayout.setVisibility(View.GONE);
        //显示物品布局
        remindGoodsDetailsLayout.setVisibility(View.VISIBLE);
        //名称
        goodsNameTv.setText(String.format(getString(R.string.remind_goods_name_text), selectGoods.getName()));
        //位置
        String location = StringUtils.getGoodsLoction(selectGoods);
        goodsLocationTv.setText(String.format(getString(R.string.remind_goods_location_text), location));
        //分类
        goodsClassifyTv.setText(String.format(getString(R.string.remind_goods_classify_text), StringUtils.getGoodsClassify(selectGoods)));
        //判断图片类型 是网络图片还是默认颜色
        if (!TextUtils.isEmpty(selectGoods.getObjectUrl()) && selectGoods.getObjectUrl().contains("http")) {
            ImageLoader.load(context, goodsIv, selectGoods.getObject_url());
        } else if (!TextUtils.isEmpty(selectGoods.getObject_url()) && !selectGoods.getObject_url().contains("/")) {
            goodsIv.setBackgroundResource(0);
            goodsIv.setBackgroundColor(Color.parseColor(selectGoods.getObject_url()));
            imgTv.setVisibility(View.VISIBLE);
            imgTv.setText(selectGoods.getName());
        } else {
            goodsIv.setBackgroundColor(getResources().getColor(R.color.colorf9));
        }
        //标题
        if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
            mEditText.setText(selectGoods.getName());
        }
        //位置跳转按钮显示
        if (!TextUtils.isEmpty(location)) {
            locationBean = selectGoods;
            locationIv.setVisibility(View.VISIBLE);
        } else {
            locationIv.setVisibility(View.GONE);
        }
    }
}