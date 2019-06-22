package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Button;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.importObject.ImportSelectFurnitureActivity;
import com.gongwu.wherecollect.object.AddGoodsActivity;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.ObjectInfoEditView;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置其他属性
 */
public class AddGoodsOtherContentActivity extends BaseViewActivity {

    @Bind(R.id.goodsInfo_other_view)
    ObjectInfoEditView goodsInfoView;
    @Bind(R.id.commit_btn)
    Button commitBtn;
    private ObjectBean tempBean;
    private String startType;

    private ArrayList<String> nameList;
    private ArrayList<String> fileList;

    public static void start(Context context, ArrayList<String> name, ArrayList<String> files, String startType) {
        Intent intent = new Intent(context, AddGoodsOtherContentActivity.class);
        if (name != null) {
            intent.putStringArrayListExtra("nameList", name);
        }
        if (files != null) {
            intent.putStringArrayListExtra("fileList", files);
        }
        intent.putExtra("type", startType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods_other_content);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.add_goods_other_content_text));
        EventBus.getDefault().register(this);
        initData();
        initEvent();
        nameList = getIntent().getStringArrayListExtra("nameList");
        fileList = getIntent().getStringArrayListExtra("fileList");
        if (nameList != null && fileList != null) {
            tempBean = new ObjectBean();
            goodsInfoView.init(tempBean);
        }
    }

    private void initData() {
        tempBean = (ObjectBean) getIntent().getSerializableExtra("tempBean");
        startType = getIntent().getStringExtra("type");
        if (tempBean != null) {
            goodsInfoView.init(tempBean);
        }
        if (AddGoodsActivity.MORE_TYPE.equals(startType)) {
            commitBtn.setText("跳过");
            titleLayout.setTitle(getResources().getString(R.string.add_goods_other_content_text_2));
            goodsInfoView.hintMoreGoodsLayout();
        }
    }


    private void initEvent() {
        goodsInfoView.setChangeListener(new ObjectInfoEditView.ChangeListener() {
            @Override
            public void change() {
                setViewText();
            }
        });

    }

    @OnClick({R.id.commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit_btn:
                if (AddGoodsActivity.MORE_TYPE.equals(startType)) {
//                    AddMoreGoodsActivity.start(context, tempBean);
                    addObjects(nameList, fileList);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("tempBean", tempBean);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    /**
     * 添加物品
     */
    private void addObjects(List<String> name, List<String> files) {
        Map<String, String> map = new TreeMap<>();
        map.put("name", JsonUtils.jsonFromObject(name));
        map.put("image_urls", JsonUtils.jsonFromObject(files));
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("user_id", MyApplication.getUser(this).getId());
        if (tempBean != null) {
            map.put("category_codes", StringUtils.isEmpty(tempBean.getCategories()) ? "" : tempBean.getCategories().get(tempBean.getCategories().size() - 1).getCode());
            map.put("channel", TextUtils.isEmpty(tempBean.getChannel()) ? "" : JsonUtils.jsonFromObject(tempBean.getChannel().split(">")));
            map.put("color", TextUtils.isEmpty(tempBean.getColor()) ? "" : JsonUtils.jsonFromObject(tempBean.getColor().split("、")));
            map.put("detail", TextUtils.isEmpty(tempBean.getDetail()) ? "" : tempBean.getDetail());
            map.put("price_max", tempBean.getPrice() + "");
            map.put("price_min", tempBean.getPrice() + "");
            map.put("season", tempBean.getSeason());
            map.put("star", tempBean.getStar() + "");
            map.put("buy_date", tempBean.getBuy_date());
            map.put("expire_date", tempBean.getExpire_date());
        }
        PostListenner listenner = new PostListenner(this, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                EventBus.getDefault().post(EventBusMsg.ACTIVITY_FINISH);
                JSONArray features = null;// 创建一个包含原始json串的json对象
                List<ObjectBean> objectBeans = new ArrayList<>();
                try {
                    features = new JSONArray(r.getResult());
                    for (int i = 0; i < features.length(); i++) {
                        JSONObject info = features.getJSONObject(i);// 获取features数组的第i个json对象
                        String color = info.getString("color");
                        String channel = info.getString("channel");
                        info.remove("color");
                        info.remove("channel");
                        List<String> colors = JsonUtils.listFromJson(color, String.class);
                        List<String> channels = JsonUtils.listFromJson(channel, String.class);
                        ObjectBean bean = JsonUtils.objectFromJson(info.toString(), ObjectBean.class);
                        bean.setColors(colors);
                        bean.setChannels(channels);
                        objectBeans.add(bean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ImportSelectFurnitureActivity.start(context, objectBeans);
                finish();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.addMoreGoods(this, map, listenner);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            tempBean = (ObjectBean) data.getSerializableExtra("bean");
            goodsInfoView.init(tempBean);
            if (!tempBean.isEmpty()) {
                setViewText();
            }
        }
    }

    private void setViewText() {
        if (AddGoodsActivity.MORE_TYPE.equals(startType)) {
            commitBtn.setText("下一步");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String str) {
        if (EventBusMsg.ACTIVITY_FINISH.contains(str)) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
