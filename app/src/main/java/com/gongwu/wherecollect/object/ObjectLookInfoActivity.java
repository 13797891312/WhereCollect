package com.gongwu.wherecollect.object;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.LocationLook.furnitureLook.FurnitureLookActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.activity.MainActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.ImageData;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.importObject.ImportSelectFurnitureActivity;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.FlowViewGroup;
import com.gongwu.wherecollect.view.GoodsImageView;
import com.gongwu.wherecollect.view.ObjectInfoLookView;
import com.gongwu.wherecollect.view.ObjectsLookMenuDialog;
import com.gongwu.wherecollect.view.PhotosDialog;
import com.handmark.pulltorefresh.library.PullToScrollView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 查看物品详情
 */
public class ObjectLookInfoActivity extends BaseViewActivity {
    ObjectBean bean;

    @Bind(R.id.name_tv)
    EditText nameTv;
    @Bind(R.id.goodsInfo_view)
    ObjectInfoLookView goodsInfoView;
    @Bind(R.id.ac_location_layout)
    LinearLayout locationLayout;
    @Bind(R.id.objrct_position_hint_tv)
    TextView positionHintTv;
    @Bind(R.id.ac_location_flow)
    FlowViewGroup locationFlow;
    @Bind(R.id.ac_location_btn)
    ImageView locationBtn;
    @Bind(R.id.objrct_position_set_iv)
    ImageView objectPositionConfiIv;
    @Bind(R.id.goods_image_iv)
    GoodsImageView goodsImageIv;

//    @Bind(R.id.goods_image_tv)
//    TextView goodsImageTv;
//    @Bind(R.id.goods_image)
//    ImageView goodsImage;
//    @Bind(R.id.activity_goods_add)
//    PullToScrollView activityGoodsAdd;

    private final String IMG_COLOR_CODE = 0 + "";//默认图片颜色的值
    private boolean isSetResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_lookinfo);
        ButterKnife.bind(this);
        titleLayout.setTitle("查看物品");
        titleLayout.setBack(true, null);
        bean = (ObjectBean) getIntent().getSerializableExtra("bean");
        titleLayout.textBtn.setVisibility(View.VISIBLE);
        titleLayout.textBtn.setText("编辑");
        titleLayout.textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectsLookMenuDialog dialog = new ObjectsLookMenuDialog(ObjectLookInfoActivity.this, bean) {
                    @Override
                    protected void editLocation() {
                        super.editLocation();
                        editLocatio();
                    }
                };
            }
        });
        initValues();
        EventBus.getDefault().register(this);
    }

    /**
     * 编辑位置
     */
    private void editLocatio() {
        if (StringUtils.isEmpty(bean.getLocations())) {
            Intent intent = new Intent(context, ImportSelectFurnitureActivity.class);
            context.startActivity(intent);
        } else {
            final List<BaseBean> temp = bean.getLocations();
            DialogUtil.show("提示", "将原有位置清空？", "确定", "取消", ((Activity) context), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Map<String, String> map = new TreeMap<>();
                    map.put("code", bean.getId());
                    map.put("uid", MyApplication.getUser(context).getId());
                    PostListenner listenner = new PostListenner(context) {
                        @Override
                        protected void code2000(final ResponseResult r) {
                            super.code2000(r);
                            EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                            for (int i = 0; i < temp.size(); i++) {
                                if (temp.get(i).getLevel() == 0) {
                                    BaseBean bb = temp.get(i);
                                    for (int j = 0; j < MainLocationFragment.mlist.size(); j++) {
                                        if (bb.getCode().equals(MainLocationFragment.mlist.get(j).getCode())) {
                                            EventBus.getDefault().post(new EventBusMsg.ImportObject(j));
                                        }
                                    }
                                }
                            }
                            bean.getLocations().clear();
                            goodsInfoView.init(bean);
                            Intent intent = new Intent(ObjectLookInfoActivity.this, ImportSelectFurnitureActivity.class);
                            startActivity(intent);
                        }
                    };
                    HttpClient.removeObjectFromFurnitrue(context, map, listenner);
                }
            }, null);
        }
    }

//    private void initScroll() {
//        final int maxMargin = (int) (100 * BaseViewActivity.getScreenScale(ObjectLookInfoActivity.this));
//        activityGoodsAdd.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver
//                .OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                if (activityGoodsAdd.getScrollY() / 2 < -maxMargin) {
//                    return;
//                }
//                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) goodsImage.getLayoutParams();
//                lp.topMargin = (-maxMargin - activityGoodsAdd.getScrollY() / 2);
//                goodsImage.setLayoutParams(lp);
//                activityGoodsAdd.getScrollY();
//            }
//        });
//    }

    private void initValues() {
        if (bean == null)
            return;
        if (bean.getObject_url().contains("http")) {
            goodsImageIv.setHead(IMG_COLOR_CODE, "", bean.getObject_url());
        } else if (bean.getObject_url().contains("#")) {
            goodsImageIv.name.setVisibility(View.VISIBLE);
            goodsImageIv.name.setText(bean.getName());
            goodsImageIv.head.setImageDrawable(null);
            goodsImageIv.head.setBackgroundColor(Color.parseColor(bean.getObject_url()));
        }
        nameTv.setText(bean.getName());
        goodsInfoView.setLocationlayoutVisibility(true);
        goodsInfoView.init(bean);
        goodsInfoView.showGoodsLayout();
        setLocation();
    }

    @OnClick({R.id.edit_goods_iv, R.id.ac_location_btn, R.id.objrct_position_set_iv
            , R.id.goods_image_iv})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.edit_goods_iv:
                intent = new Intent(context, AddGoodsActivity.class);
                intent.putExtra("bean", bean);
                ((Activity) context).startActivityForResult(intent, 0);
                MobclickAgent.onEvent(context, "050103");
                break;
            case R.id.ac_location_btn:
                intent = new Intent(context, MainActivity.class);
                intent.putExtra("object", bean);
                startActivity(intent);
                break;
            case R.id.objrct_position_set_iv:
                editLocatio();
                break;
            case R.id.goods_image_iv:
                if (bean.getObject_url().contains("#")) return;
                List<ImageData> imageDatas = new ArrayList<>();
                ImageData imageData = new ImageData();
                imageData.setUrl(bean.getObject_url());
                imageDatas.add(imageData);
                PhotosDialog photosDialog = new PhotosDialog(this, false, false, imageDatas);
                photosDialog.showPhotos(0);
                break;
        }
    }

    /**
     * 设置位置
     */
    private void setLocation() {
        if (bean.getLocations() == null || bean.getLocations().size() == 0) {
            locationLayout.setVisibility(View.GONE);
            positionHintTv.setVisibility(View.VISIBLE);
            return;
        } else {
            locationLayout.setVisibility(View.VISIBLE);
            positionHintTv.setVisibility(View.GONE);
        }
        locationFlow.removeAllViews();
        Collections.sort(bean.getLocations(), new Comparator<BaseBean>() {
            @Override
            public int compare(BaseBean lhs, BaseBean rhs) {
                return lhs.getLevel() - rhs.getLevel();
            }
        });
        for (int i = 0; i < StringUtils.getListSize(bean.getLocations()); i++) {
            TextView text = (TextView) View.inflate(context, R.layout.flow_textview, null);
            locationFlow.addView(text);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) text.getLayoutParams();
            lp.bottomMargin = 5;
            lp.topMargin = 5;
            lp.rightMargin = 10;
            lp.leftMargin = 10;
            text.setLayoutParams(lp);
            text.setText(bean.getLocations().get(i).getName());
            text.setBackgroundResource(R.drawable.shape_maingoods2_bg);
        }
        if (StringUtils.isEmpty(bean.getLocations())) {
            locationBtn.setVisibility(View.GONE);
        } else {
            locationBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            bean = (ObjectBean) data.getSerializableExtra("bean");
            isSetResult = true;
            initValues();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ImportObject msg) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(this, "030101");
    }
}
