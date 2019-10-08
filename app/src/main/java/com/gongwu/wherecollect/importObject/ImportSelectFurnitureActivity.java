package com.gongwu.wherecollect.importObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationEdit.LocationEditActivity;
import com.gongwu.wherecollect.LocationLook.LocationIndicatorView;
import com.gongwu.wherecollect.LocationLook.LocationObectListView;
import com.gongwu.wherecollect.LocationLook.LocationPage;
import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.LocationLook.furnitureLook.FurnitureLookActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.view.ObjectView;
import com.zhaojin.myviews.TagViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImportSelectFurnitureActivity extends BaseViewActivity {

    private static final int START_ACT_CODE = 0x213;

    @Bind(R.id.indicatorView)
    LocationIndicatorView indicatorView;
    @Bind(R.id.tagViewPager)
    TagViewPager viewPager;
    @Bind(R.id.objectListView)
    LocationObectListView objectListView;
    @Bind(R.id.impor_btn)
    TextView hintText;

    private Map<Integer, LocationPage> pageMap = new HashMap<>();
    private List<ObjectBean> objectBeans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_selectfurniture);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initPage();
        initData();
    }

    private void initView() {
        titleLayout.setBack(true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusMsg.updateShareMsg());
                finish();
            }
        });
        titleLayout.setTitle("归入物品");
        titleLayout.textBtn.setText("编辑");
        titleLayout.textBtn.setVisibility(View.VISIBLE);
        titleLayout.textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImportSelectFurnitureActivity.this, LocationEditActivity.class);
                startActivity(intent);
            }
        });
        //桌布空间头点击事件
        indicatorView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(indicatorView.getSelection());
            }
        });
        indicatorView.init(MainLocationFragment.mlist);
        objectListView.setSelectShape(false);
        objectListView.adapter.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(int positions, View view) {
                startShakeByView(hintText, 10, 400);
            }
        });
    }

    private void initData() {
        objectBeans = (List<ObjectBean>) getIntent().getSerializableExtra("objectBeans");
        if (objectBeans != null && objectBeans.size() > 0) {
            objectListView.notifyData(objectBeans, -1);
        }
    }

    private void initPage() {
        viewPager.init(R.drawable.shape_photo_tag_select, R.drawable.shape_photo_tag_nomal, 0, 0, 0, 0);
        viewPager.setAutoNext(false, 0);
        viewPager.setId(14);
        viewPager.setOnGetView(new TagViewPager.OnGetView() {
            @Override
            public View getView(ViewGroup container, final int position) {
                LocationPage v = new LocationPage(context);
                container.addView(v);
                v.init(false);
                v.setOnItemClickListener(new LocationPage.OnItemClickListener() {
                    @Override
                    public void itemClick(ObjectView view) {
                        Intent intent = new Intent(context, FurnitureLookActivity.class);
                        intent.putExtra("furnitureObject", view.getObjectBean());
                        intent.putExtra("list", (Serializable) MainLocationFragment.objectMap.get(MainLocationFragment.mlist.get(viewPager.getCurrentItem()).getCode()));
                        intent.putExtra("position", viewPager.getCurrentItem());
                        intent.putExtra("title", indicatorView.getCurrentLocation().getName() + ">" + view.getObjectBean().getName());
                        if (objectBeans != null && objectBeans.size() > 0) {
                            intent.putExtra("objectBeans", (Serializable) objectBeans);
                        }
                        startActivityForResult(intent, START_ACT_CODE);
//                        finish();
                    }

                    @Override
                    public void bgClick() {
                    }

                    @Override
                    public void upSlide() {

                    }

                    @Override
                    public void downSlide() {
                    }
                });
                v.getLocationChild(position);
                pageMap.put(position, v);
                return v;
            }
        });
        viewPager.setAdapter(MainLocationFragment.mlist.size(), 0);
        viewPager.setOnSelectedListoner(new TagViewPager.OnSelectedListoner() {
            @Override
            public void onSelected(int position) {
                indicatorView.setSelection(position);
                indicatorView.notifyView();
            }
        });
        viewPager.setCurrentItem(indicatorView.getSelection());
    }

    private void startShakeByView(View view, float shakeDegrees, long duration) {
        Animation rotateAnim = new TranslateAnimation(-shakeDegrees, shakeDegrees, 0, 0);
        rotateAnim.setDuration(duration / 10);
        rotateAnim.setRepeatMode(Animation.REVERSE);
        rotateAnim.setRepeatCount(3);
        view.startAnimation(rotateAnim);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_ACT_CODE && resultCode == RESULT_OK) {
            List<ObjectBean> resultBeans = (List<ObjectBean>) data.getSerializableExtra("objectBeans");
            if (resultBeans != null && resultBeans.size() > 0) {
                objectBeans = resultBeans;
                objectListView.notifyData(objectBeans, -1);
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String str) {
        if (EventBusMsg.SPACE_EDIT.contains(str)) {
            indicatorView.init(MainLocationFragment.mlist);
            indicatorView.notifyView();
            initPage();
        } else if (EventBusMsg.SELECT_VIEW_PAGER.contains(str)) {
            viewPager.setCurrentItem(indicatorView.getSelection());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.EditLocationMsg msg) {
        LocationPage page = pageMap.get(msg.position);
        if (page != null) {
            page.getNetData("", MainLocationFragment.mlist.get(msg.position).getCode());
        }
    }

    public static void start(Context context, List<ObjectBean> objectBeans) {
        Intent intent = new Intent(context, ImportSelectFurnitureActivity.class);
        if (intent != null) {
            intent.putExtra("objectBeans", (Serializable) objectBeans);
        }
        context.startActivity(intent);
    }
}
