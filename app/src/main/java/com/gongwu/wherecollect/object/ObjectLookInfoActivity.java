package com.gongwu.wherecollect.object;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.importObject.ImportSelectFurnitureActivity;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.ObjectInfoLookView;
import com.gongwu.wherecollect.view.ObjectsLookMenuDialog;
import com.handmark.pulltorefresh.library.PullToScrollView;
import com.tencent.bugly.beta.download.BetaReceiver;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ObjectLookInfoActivity extends BaseViewActivity {
    ObjectBean bean;
    @Bind(R.id.goods_image)
    ImageView goodsImage;
    @Bind(R.id.time_tv)
    TextView timeTv;
    @Bind(R.id.name_tv)
    EditText nameTv;
    @Bind(R.id.goodsInfo_view)
    ObjectInfoLookView goodsInfoView;
    @Bind(R.id.activity_goods_add)
    PullToScrollView activityGoodsAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_lookinfo);
        ButterKnife.bind(this);
        titleLayout.setTitle("查看物品");
        titleLayout.setBack(true, null);
        titleLayout.imageBtn.setVisibility(View.VISIBLE);
        bean = (ObjectBean) getIntent().getSerializableExtra("bean");
        titleLayout.imageBtn.setOnClickListener(new View.OnClickListener() {
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
        initScroll();
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
                                            return;
                                        }
                                    }
                                }
                            }
                            bean.getLocations().clear();
                            goodsInfoView.init(bean);
                            Intent intent = new Intent(context, ImportSelectFurnitureActivity.class);
                            context.startActivity(intent);
                        }
                    };
                    HttpClient.removeObjectFromFurnitrue(context, map, listenner);
                }
            }, null);
        }
    }

    private void initScroll() {
        final int maxMargin = (int) (100 * BaseViewActivity.getScreenScale(ObjectLookInfoActivity.this));
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
    }

    @OnClick({R.id.edit_goods_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_goods_iv:
                Intent intent = new Intent(context, AddGoodsActivity.class);
                intent.putExtra("bean", bean);
                ((Activity) context).startActivityForResult(intent, 0);
                MobclickAgent.onEvent(context, "050103");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            bean = (ObjectBean) data.getSerializableExtra("bean");
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
