package com.gongwu.wherecollect.object;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.volley.request.QiNiuUploadUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.afragment.MainGoodsFragment;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.ObjectInfoEditView;
import com.gongwu.wherecollect.view.SelectImgEditDialog;
import com.handmark.pulltorefresh.library.PullToScrollView;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class ObjectEditActivity extends BaseViewActivity {
    ObjectBean bean, newBean;
    @Bind(R.id.goods_image)
    ImageView goodsImage;
    @Bind(R.id.time_tv)
    TextView timeTv;
    @Bind(R.id.name_tv)
    EditText nameTv;
    @Bind(R.id.goodsInfo_view)
    ObjectInfoEditView goodsInfoView;
    @Bind(R.id.commit_btn)
    Button commitBtn;
    @Bind(R.id.activity_goods_add)
    PullToScrollView activityGoodsAdd;
    private SelectImgEditDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_edit);
        ButterKnife.bind(this);
        titleLayout.setTitle("编辑物品");
        titleLayout.setBack(true, null);
        bean = (ObjectBean) getIntent().getSerializableExtra("bean");
        initValues();
        initScroll();
        commitBtn.setEnabled(false);
        goodsInfoView.setChangeListener(new ObjectInfoEditView.ChangeListener() {
            @Override
            public void change() {
                commitBtn.setEnabled(true);
            }
        });
    }

    private void initScroll() {
        final int maxMargin = (int) (100 * BaseViewActivity.getScreenScale(this));
        activityGoodsAdd.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver
                .OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (activityGoodsAdd.getScrollY() / 2 < -maxMargin) {
                    return;
                }
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) goodsImage.getLayoutParams();
                lp.topMargin = (-maxMargin - activityGoodsAdd.getScrollY() / 2);
                goodsImage.setLayoutParams(lp);
                activityGoodsAdd.getScrollY();
            }
        });
    }

    private void initValues() {
        if (bean == null)
            return;
        ImageLoader.load(context, goodsImage, bean.getObject_url(), R.drawable.ic_img_error);
        nameTv.setText(bean.getName());
        timeTv.setText(String.format("创建于：%s", bean.getCreated_at()));
        goodsInfoView.init(bean);
        nameTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                bean.setName(nameTv.getText().toString());
                commitBtn.setEnabled(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (dialog != null) {
            dialog.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == 100) {
            bean = (ObjectBean) data.getSerializableExtra("bean");
            goodsInfoView.init(bean);
            commitBtn.setEnabled(true);
        }
    }

    @OnClick({R.id.commit_btn, R.id.goods_image, R.id.image_click})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_btn:
                addObject();
                break;
            case R.id.image_click:
            case R.id.goods_image:
                dialog = new SelectImgEditDialog(this, null, 1, bean.getObject_url()) {
                    @Override
                    public void getResult(List<File> list) {
                        super.getResult(list);
                        dialog = null;
                        commitBtn.setEnabled(true);
                        QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(ObjectEditActivity.this, list,
                                "object/image/") {
                            @Override
                            protected void finish(List<String> list) {
                                super.finish(list);
                                if (StringUtils.isEmpty(list))
                                    return;
                                bean.setObject_url(list.get(0));
                                ImageLoader.load(context, goodsImage, bean.getObject_url(), R.drawable.ic_img_error);
                            }
                        };
                        uploadUtil.start();
                    }
                };
                break;
        }
    }

    /**
     * 编辑其实是先添加一条，再把老的删除
     */
    private void addObject() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("detail", bean.getDetail());
        map.put("image_url", bean.getObject_url());
        map.put("object_count", bean.getObject_count() + "");
        map.put("price_max", bean.getPrice_max() + "");
        map.put("price_min", bean.getPrice_min() + "");
        map.put("season", bean.getSeason());
        map.put("name", bean.getName());
        map.put("star", bean.getStar() + "");
        map.put("coordinates", JsonUtils.jsonFromObject(bean.getCoordinates()));
        StringBuilder ca = new StringBuilder();
        for (int i = 0; i < StringUtils.getListSize(bean.getCategories()); i++) {
            ca.append(bean.getCategories().get(i).getCode());
            if (i != bean.getCategories().size() - 1) {
                ca.append(",");
            }
        }
        map.put("category_codes", ca.toString());
        StringBuilder lc = new StringBuilder();
        for (int i = 0; i < StringUtils.getListSize(bean.getLocations()); i++) {
            lc.append(bean.getLocations().get(i).getCode());
            if (i != bean.getLocations().size() - 1) {
                lc.append(",");
            }
        }
        map.put("location_codes", lc.toString());
        map.put("channel", JsonUtils.jsonFromObject(bean.getChannel().split(">")));
        map.put("color", JsonUtils.jsonFromObject(bean.getColor().split("、")));
        PostListenner listenner = new PostListenner(this, Loading.show(null, this,
                "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                newBean = JsonUtils.objectFromJson(r.getResult(), ObjectBean.class);
                deleteObject();
            }
        };
        HttpClient.getAddObject(this, map, listenner);
    }

    private void deleteObject() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("object_id", bean.get_id());
        PostListenner listenner = new PostListenner(this, Loading.show(null, this,
                "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                Intent intent = new Intent();
                intent.putExtra("bean", newBean);
                ObjectEditActivity.this.setResult(100, intent);
                ObjectEditActivity.this.finish();
            }
        };
        HttpClient.deleteGoods(this, map, listenner);
    }
}
