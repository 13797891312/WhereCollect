package com.gongwu.wherecollect.LocationLook.furnitureLook;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.furnitureEdit.CustomTableRowLayout;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.drawerLayout.DrawerLayout;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
public class FurnitureLookActivity extends BaseViewActivity {
    public ObjectBean furnitureObject;
    public int spacePosition;//哪个空间桌布
    @Bind(R.id.structView)
    public FurnitureDrawerView structView;
    @Bind(R.id.objectListView)
    public FurnitureObectListView objectListView;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.expanded_tv)
    TextView expandedTv;
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    private String title;
    public ObjectBean selectObject;//选择的需要变高亮物品

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_look);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        title = getIntent().getStringExtra("title");
        spacePosition = getIntent().getIntExtra("position", 0);
        titleLayout.setTitle(title);
        furnitureObject = (ObjectBean) getIntent().getSerializableExtra("furnitureObject");
        ObjectBean objectBean = (ObjectBean) getIntent().getSerializableExtra("object");
        selectObject=objectBean;
        //TODO 后台数据错误，把盒子也当隔层返回来了
        for (int i = StringUtils.getListSize(furnitureObject.getLayers()) - 1; i >= 0; i--) {
            if (furnitureObject.getLayers().get(i).getScale() == null) {
                furnitureObject.getLayers().remove(i);
            }
        }
        initView(objectBean);
        EventBus.getDefault().register(objectListView);
    }

    private void initView(final ObjectBean objectBean) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (!TextUtils.isEmpty(furnitureObject.getImage_url())) {
            ImageLoader.load(this, imageView, furnitureObject.getBackground_url(), R.drawable.ic_img_error);
        }
        structView.init(drawerLayout, furnitureObject.getLayers(), furnitureObject.getRatio());
        structView.setBean(furnitureObject);
        objectListView.init(furnitureObject, (List<ObjectBean>) getIntent().getSerializableExtra("list"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.openDrawer(Gravity.RIGHT);
                objectListView.show();
                if (objectBean != null) {
                   findObject(objectBean);
                }
            }
        }, 500);
        expandedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
                objectListView.show();
            }
        });
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                objectListView.show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                objectListView.hide();
            }

            @Override
            public void onDrawerStateChanged(@DrawerLayout.State int newState) {
            }
        });
    }

    /**
     * 刷新物品列表
     *
     * @param objectBean 隔层对象
     */
    public void refrushListView(ObjectBean objectBean) {
        objectListView.notifyObject(objectBean, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Map<String, ObjectBean> chooseMap = (Map<String, ObjectBean>) data.getSerializableExtra("result");
            objectListView.importObjects(chooseMap);
        } else if (requestCode == 34 && resultCode == RESULT_OK) {
            furnitureObject.getLayers().clear();
            furnitureObject.getLayers().addAll((ArrayList) data.getSerializableExtra("result"));
            furnitureObject.setRatio(data.getFloatExtra("shape", CustomTableRowLayout.shape_width));
            structView.notifyData(furnitureObject.getLayers(), furnitureObject.getRatio());
            refrushListView(null);
            objectListView.getNetDate("");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            structView.tablelayout.cancelFind();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(objectListView);
    }

    /**
     * 定位物品
     *
     * @param objectBean
     */
    public void findObject(ObjectBean objectBean) {
        ((FurnitureLookActivity) context).structView.tablelayout.findView(objectBean, false);
        objectListView.findObject(objectBean);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (MaterialShowcaseView.showcaseView != null) {
                try {
                    MaterialShowcaseView.showcaseView.hide();
                }catch (Exception e){
                }
                MaterialShowcaseView.showcaseView=null;
            } else if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
                objectListView.hide();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(context, "030201");
    }
}
