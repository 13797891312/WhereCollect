package com.gongwu.wherecollect.LocationLook.furnitureLook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.activity.MainActivity;
import com.gongwu.wherecollect.adapter.OnFloatClickListener;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.furnitureEdit.CustomTableRowLayout;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.FloatWindowView;
import com.gongwu.wherecollect.view.drawerLayout.DrawerLayout;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
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
    @Bind(R.id.float_view)
    FloatWindowView floatView;

    private String title;
    public ObjectBean selectObject;//选择的需要变高亮物品
    private UserBean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_look);
        ButterKnife.bind(this);
        titleLayout.setBack(true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActResult();
            }
        });
        title = getIntent().getStringExtra("title");
        spacePosition = getIntent().getIntExtra("position", 0);
        titleLayout.setTitle(title);
        furnitureObject = (ObjectBean) getIntent().getSerializableExtra("furnitureObject");
        ObjectBean objectBean = (ObjectBean) getIntent().getSerializableExtra("object");
        selectObject = objectBean;
        //TODO 后台数据错误，把盒子也当隔层返回来了
        for (int i = StringUtils.getListSize(furnitureObject.getLayers()) - 1; i >= 0; i--) {
            if (furnitureObject.getLayers().get(i).getScale() == null) {
                furnitureObject.getLayers().remove(i);
            }
        }
        initView(objectBean);
        initLocation();
        EventBus.getDefault().register(objectListView);
    }

    private void initLocation() {
        List<ObjectBean> objectBeans = (List<ObjectBean>) getIntent().getSerializableExtra("objectBeans");
        if (objectBeans != null && objectBeans.size() > 0) {
            objectListView.initCWListView(objectBeans);
            objectListView.setOnFinishActivityLisener(new FurnitureObectListView.OnFinishActivityLisener() {
                @Override
                public void finishActivity() {
                    setActResult();
                }
            });

        }
    }

    private void initView(final ObjectBean objectBean) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (!TextUtils.isEmpty(furnitureObject.getImage_url())) {
            ImageLoader.load(this, imageView, furnitureObject.getBackground_url(), R.drawable.ic_img_error);
        }
        structView.init(drawerLayout, furnitureObject.getLayers(), furnitureObject.getRatio());
        structView.setBean(furnitureObject);
//        objectListView.init(furnitureObject, (List<ObjectBean>) getIntent().getSerializableExtra("list"));
        objectListView.init(furnitureObject, MainLocationFragment.objectMap.get(MainLocationFragment.mlist.get(((FurnitureLookActivity) context).spacePosition).getCode()));
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
                if (structView.getChildView() != null) {
                    floatView.setEnabled(true);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                floatView.setEnabled(false);
                objectListView.hide();
            }

            @Override
            public void onDrawerStateChanged(@DrawerLayout.State int newState) {
            }
        });
        user = MyApplication.getUser(context);
        if (user == null) return;
        if (!SaveDate.getInstence(context).getBreathLook(user.getId())) {
            mHandler.postDelayed(runnable, animTime);
            //传入id，当用户切换隔层的时候 好判断是否开启呼吸查看
            objectListView.setUserId(user.getId());
        }
        if (MainActivity.floatBean != null) {
            floatView.setEnabled(false);
            floatView.setVisibility(View.VISIBLE);
            floatView.setNameTv(MainActivity.floatBean.getName());
        } else {
            floatView.setVisibility(View.GONE);
        }
        structView.setSelectItemViewListener(new FurnitureDrawerView.SelectItemViewListener() {
            @Override
            public void selectItemView(boolean select) {
                floatView.setEnabled(select);
            }
        });
        objectListView.setFloatShow(new FurnitureObectListView.FloatShowListener() {
            @Override
            public void floatShow(ObjectBean select, boolean show) {
                if (show) {
                    MainActivity.floatBean = select;
                    floatView.setEnabled(true);
                    floatView.setVisibility(View.VISIBLE);
                    floatView.setNameTv(MainActivity.floatBean.getName());
                } else {
                    floatView.setVisibility(View.GONE);
                }
            }
        });
        floatView.setOnFloatClickListener(new OnFloatClickListener() {
            @Override
            public void onClick(View view) {
                if (floatView.getEnable()) {
                    objectListView.floatbox = true;
                    objectListView.getMoveLayout().setTag(MainActivity.floatBean);
                    if (MainActivity.floatBean.isLayer()) {
                        objectListView.moveLayer();
                    } else {
                        objectListView.moveBox();
                    }
                }
            }

            @Override
            public void onLongClick(View view) {
                //删除
                DialogUtil.show("提示", "是否取消迁移", "确定", "取消", (Activity) context, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.floatBean = null;
                        floatView.setVisibility(View.GONE);
                    }
                }, null);
            }
        });
    }

    private long animTime = 5000;
    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startViewAlphaAnim();
        }
    };

    /**
     * 用户切换隔层的时候 当该隔层有数据的时候，重新开启呼吸查看
     */
    public void reStartViewAlphaAnim() {
        if (mHandler != null && runnable != null) {
            mHandler.removeCallbacks(runnable);
            mHandler.postDelayed(runnable, animTime);
        }
    }

    /**
     * 显示呼吸查看动画
     */
    public void startViewAlphaAnim() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);//初始化操作，参数传入0和1，即由透明度0变化到透明度为1
        alphaAnimation.setFillAfter(false);//动画结束后保持状态
        alphaAnimation.setDuration(1000);//动画持续时间，单位为毫秒
        //view显示的时候才 开启动画
        if (objectListView.recyclerView.getVisibility() == View.VISIBLE) {
            objectListView.recyclerView.startAnimation(alphaAnimation);//开始动画
        }
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束，刷新数据并开启动画显示出来
                if (objectListView != null && objectListView.adapter != null) {
                    objectListView.adapter.refreshData();
                    //view显示的时候才 开启动画
                    if (objectListView.recyclerView.getVisibility() == View.VISIBLE) {
                        setViewAnim(objectListView.recyclerView);
                    }
                }
                if (mHandler != null) {
                    if (!SaveDate.getInstence(context).getBreathLook(user.getId())) {
                        mHandler.postDelayed(runnable, animTime);
                    } else {
                        objectListView.adapter.defaultData();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //View 显示动画
    private void setViewAnim(View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);//初始化操作，参数传入0和1，即由透明度0变化到透明度为1
        alphaAnimation.setFillAfter(false);//动画结束后保持状态
        alphaAnimation.setDuration(1000);//动画持续时间，单位为毫秒
        view.startAnimation(alphaAnimation);//开始动画
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
        if (mHandler != null && runnable != null) {
            mHandler.removeCallbacks(runnable);
            mHandler = null;
            runnable = null;
        }
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
                } catch (Exception e) {
                }
                MaterialShowcaseView.showcaseView = null;
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

    @Override
    public void onBackPressed() {
        setActResult();
        super.onBackPressed();
    }

    private void setActResult() {
        EventBus.getDefault().post(new EventBusMsg.updateShareMsg());
        Intent intent = new Intent();
        if (objectListView != null && objectListView.getCWList() != null && objectListView.getCWList().size() > 0) {
            List<ObjectBean> beans = objectListView.getCWList();
            intent.putExtra("objectBeans", (Serializable) beans);
        }
        if (objectListView != null && floatView.getVisibility() == View.VISIBLE) {
            ObjectBean moveBox = (ObjectBean) objectListView.getMoveLayout().getTag();
            intent.putExtra("moveBean", (Serializable) moveBox);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}
