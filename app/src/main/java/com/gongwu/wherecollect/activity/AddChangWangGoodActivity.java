package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.SwipeCardViewAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.importObject.ImportSelectFurnitureActivity;
import com.gongwu.wherecollect.swipecardview.SwipeFlingAdapterView;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.ToastUtil;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddChangWangGoodActivity extends BaseViewActivity implements SwipeFlingAdapterView.onFlingListener {

    private static final String HAVA_GOOD = "add";//有
    private static final String NOT_HAVA_GOOD = "view";//没有
    private static final String REGRETS_GOOD = "delete";//反悔
    private static final String BLANK_GOOD = "none";//未选择

    SwipeFlingAdapterView mSwipeView;
    @Bind(R.id.chang_wang_layout)
    RelativeLayout contentLayout;
    @Bind(R.id.image_back)
    ImageButton backView;
    @Bind(R.id.title_view)
    TextView titleView;
    @Bind(R.id.goods_type)
    TextView goods_type;
    @Bind(R.id.progressbar_text_view)
    TextView pbarTextView;
    @Bind(R.id.progressbar_view)
    ProgressBar mProgressBar;
    @Bind(R.id.cardView_other)
    CardView cardViewOther;
    @Bind(R.id.cardView)
    CardView cardView;


    private List<ObjectBean> changWangList = new ArrayList<>();
    private List<ObjectBean> selectedList = new ArrayList<>();
    private Map<String, ObjectBean> addGoodList = new LinkedHashMap<>();
    private SwipeCardViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chang_wang_good);
        ButterKnife.bind(this);
        initView();
        initSwipeView();
        getCangWangDetailList();
    }

    private void initView() {
        titleView.setText("有没有");
        titleLayout.setVisibility(View.GONE);
        String goodType = getIntent().getStringExtra("goodType");
        goods_type.setText(goodType);
        backView.setImageDrawable(getResources().getDrawable(R.drawable.icon_card_act_finish));
        mAdapter = new SwipeCardViewAdapter(this, changWangList);
        cardView.setRadius(24);//设置图片圆角的半径大小
        cardView.setCardElevation(8);//设置阴影部分大小
        cardView.setContentPadding(10, 10, 10, 10);//设置图片距离阴影大小
        cardViewOther.setRadius(24);//设置图片圆角的半径大小
        cardViewOther.setCardElevation(8);//设置阴影部分大小
        cardViewOther.setContentPadding(10, 10, 10, 10);//设置图片距离阴影大小
        if (goodType.contains("热门")) cardViewOther.setVisibility(View.GONE);
    }

    private void initSwipeView() {
        if (mSwipeView != null) {
            contentLayout.removeView(mSwipeView);
        }
        mSwipeView = new SwipeFlingAdapterView(context);
        mSwipeView.setMaxVisible(4);
        mSwipeView.setMinStackInAdapter(4);
        contentLayout.addView(mSwipeView);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSwipeView.getLayoutParams();
        int topDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
        lp.setMargins(0, topDip, 0, 0);
        lp.height = 1600;
        mSwipeView.setLayoutParams(lp);
        mSwipeView.setAdapter(mAdapter);
        mSwipeView.setIsNeedSwipe(true);
        mSwipeView.setFlingListener(this);
    }

    @OnClick({R.id.image_back, R.id.no_good_view, R.id.back_good_view, R.id.yes_good_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                if (addGoodList.size() > 0) {
                    ImportSelectFurnitureActivity.start(context, new ArrayList<>(addGoodList.values()));
                }
                EventBus.getDefault().post(new EventBusMsg.updateShareMsg());
                finish();
                break;
            case R.id.no_good_view:
                mSwipeView.swipeLeft();
                break;
            case R.id.back_good_view:
                if (selectedList.size() > 0) {
                    ObjectBean regrets = selectedList.get(selectedList.size() - 1);
                    regrets.setOpt(BLANK_GOOD);
                    setCangWangDetail(regrets, REGRETS_GOOD);
                } else {
                    ToastUtil.show(this, "暂无物品反悔", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.yes_good_view:
                mSwipeView.swipeRight();
                break;
            default:
                break;
        }
    }

    @Override
    public void removeFirstObjectInAdapter() {
    }

    @Override
    public void onLeftCardExit(Object dataObject) {
        if (dataObject != null) {
            ObjectBean objectBean = (ObjectBean) dataObject;
            objectBean.setOpt(NOT_HAVA_GOOD);
            setCangWangDetail(objectBean, objectBean.getOpt());
        }
    }

    @Override
    public void onRightCardExit(Object dataObject) {
        if (dataObject != null) {
            ObjectBean objectBean = (ObjectBean) dataObject;
            objectBean.setOpt(HAVA_GOOD);
            setCangWangDetail(objectBean, objectBean.getOpt());
        }
    }

    @Override
    public void onAdapterAboutToEmpty(int itemsInAdapter) {
        LogUtil.e("tag", itemsInAdapter + "," + itemsInAdapter);
    }

    @Override
    public void onScroll(float progress, float scrollXProgress) {

    }

    //获取常忘物品详情list
    private void getCangWangDetailList() {
        if (MyApplication.getUser(context) == null) return;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("code", getIntent().getStringExtra("code"));
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                changWangList.clear();
                List<ObjectBean> objectBeans = JsonUtils.listFromJsonWithSubKey(r.getResult(), ObjectBean.class, "objects");
                if (objectBeans != null && objectBeans.size() > 0) {
                    for (ObjectBean objectBean : objectBeans) {
                        if (BLANK_GOOD.equals(objectBean.getOpt())) {
                            changWangList.add(objectBean);
                        } else {
                            selectedList.add(objectBean);
                        }
                    }
                }
                mProgressBar.setMax(objectBeans.size());
                mProgressBar.setProgress(selectedList.size() + 1);
                pbarTextView.setText(mProgressBar.getProgress() + "/" + mProgressBar.getMax());
                mAdapter.notifyDataSetChanged();
                if (changWangList.size() == 0) {
                    EventBus.getDefault().post(new EventBusMsg.updateShareMsg());
                    finish();
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.getCangWangDetailList(context, map, listenner);
    }

    //设置常忘物品有没有
    private void setCangWangDetail(final ObjectBean object, final String option) {
        if (MyApplication.getUser(context) == null) return;
        mSwipeView.setIsNeedSwipe(false);
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("object_id", object.getId());
        map.put("option", option);
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                try {
                    mSwipeView.setIsNeedSwipe(true);
                    //添加
                    if (HAVA_GOOD.equals(option)) {
                        ObjectBean newBean = new ObjectBean();
                        newBean.set_id(object.get_id());
                        newBean.setName(object.getName());
                        newBean.setObject_url(object.getObject_url());
                        newBean.setUpdated_at(object.getUpdated_at());
                        newBean.setCreated_at(object.getCreated_at());
                        newBean.setOpt(HAVA_GOOD);
                        addGoodList.put(newBean.getId(), newBean);
                        JSONObject jsonObject = new JSONObject(r.getResult());
                        String id = jsonObject.getString("id");
                        if (!TextUtils.isEmpty(id)) {
                            addGoodList.get(object.getId()).set_id(id);
                        }
                    }
                    //反悔
                    if (REGRETS_GOOD.equals(option)) {
                        if (addGoodList.containsKey(object.getId())) {
                            addGoodList.remove(object.getId());
                        }
                        changWangList.add(0, object);
                        selectedList.remove(object);
                        mProgressBar.setProgress(mProgressBar.getMax() - changWangList.size() + 1);
                        pbarTextView.setText(mProgressBar.getProgress() + "/" + mProgressBar.getMax());
                        initSwipeView();
                    }
                    //接口成功还没有删除做了标记的物品,反悔的物品不删除
                    if (!REGRETS_GOOD.equals(option) && changWangList.size() > 0) {
                        mProgressBar.setProgress(mProgressBar.getProgress() + 1);
                        pbarTextView.setText(mProgressBar.getProgress() + "/" + mProgressBar.getMax());
                        selectedList.add(changWangList.get(0));
                        //删除标记物品
                        mAdapter.remove(0);
                    }
                    //删除做了标记的物品后
                    if (changWangList.size() == 0) {
                        if (addGoodList.size() > 0) {
                            ImportSelectFurnitureActivity.start(context, new ArrayList<>(addGoodList.values()));
                        }
                        EventBus.getDefault().post(new EventBusMsg.updateShareMsg());
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
        HttpClient.setCangWangDetail(context, map, listenner);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        contentLayout.removeView(mSwipeView);
        mSwipeView = null;
    }

    public static void start(Context context, String changWangName, String changWangCode) {
        Intent intent = new Intent(context, AddChangWangGoodActivity.class);
        if (intent != null) {
            intent.putExtra("goodType", changWangName);
            intent.putExtra("code", changWangCode);
        }
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new EventBusMsg.updateShareMsg());
        super.onBackPressed();
    }

}
