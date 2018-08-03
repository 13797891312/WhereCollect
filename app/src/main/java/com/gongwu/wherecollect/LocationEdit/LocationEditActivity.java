package com.gongwu.wherecollect.LocationEdit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.LocationLook.LocationIndicatorView;
import com.gongwu.wherecollect.LocationLook.LocationPage;
import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.furnitureEdit.EditFurenitureActivity;
import com.gongwu.wherecollect.furnitureEdit.FurnitureSysListActivity;
import com.gongwu.wherecollect.quickadd.QuickSpaceSelectListActivity;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.ShowCaseUtil;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.ErrorView;
import com.gongwu.wherecollect.view.ObjectView;
import com.zhaojin.myviews.Loading;
import com.zhaojin.myviews.TagViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class LocationEditActivity extends BaseViewActivity {
    public static LocationPage addPage;
    @Bind(R.id.tagViewPager)
    public TagViewPager viewPager;
    View view;
    @Bind(R.id.indicatorView)
    LocationIndicatorView indicatorView;
    @Bind(R.id.edit_space)
    ImageButton editSpace;
    @Bind(R.id.delete_btn)
    TextView deleteBtn;
    @Bind(R.id.edit_btn)
    TextView editBtn;
    @Bind(R.id.move_btn)
    TextView moveBtn;
    @Bind(R.id.edit_layout)
    LinearLayout editLayout;
    @Bind(R.id.add_furniture)
    Button addFurniture;
    ObjectView selectView;
    int selectPosition;//点击迁移家具时记录当前页
    @Bind(R.id.move_cancel)
    Button moveCancel;
    @Bind(R.id.move_commit)
    Button moveCommit;
    @Bind(R.id.move_layout)
    LinearLayout moveLayout;
    @Bind(R.id.empty)
    ErrorView empty;
    @Bind(R.id.quick_layout)
    View quickLayout;
    private boolean isMoveing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("位置编辑");
        setContentView(R.layout.activity_location_edit);
        ButterKnife.bind(this);
        initView();
        indicatorView.init(MainLocationFragment.mlist);
        //判断跳转是由快速构建点击进来还是其他的点击
        int type = getIntent().getIntExtra("type", 0);
        if (type == 1) {
            init = false;
        }
        EventBus.getDefault().register(this);
        initPage();
    }

    private void initView() {
        //桌布空间头点击事件
        indicatorView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(indicatorView.getSelection());
            }
        });
    }

    //初次跳转的时候,initPage会调用2次,为了防止引导界面2次调用,添加init进行限制
    //初始化activity调用一次  接收eventbus调用一次
    private boolean init = true;

    private void initPage() {
        viewPager.init(R.drawable.shape_photo_tag_select, R.drawable.shape_photo_tag_nomal, 0,
                0, 0, 0);
        viewPager.setAutoNext(false, 0);
        viewPager.setId(13);
        viewPager.setOnGetView(new TagViewPager.OnGetView() {
            @Override
            public View getView(ViewGroup container, final int position) {
                LocationPage v = new LocationPage(context) {
                    @Override
                    protected void getChildListener(int position) {
                        super.getChildListener(position);
                        if (position == viewPager.getCurrentItem() && init) {
                            showHelp(this);
                        } else {
                            init = true;
                        }
                    }
                };
                container.addView(v);
                v.init(true);
                v.setOnItemClickListener(new LocationPage.OnItemClickListener() {
                    @Override
                    public void itemClick(ObjectView view) {
                        if (!isMoveing) {
                            setCurrentView(view);
                        }
                    }

                    @Override
                    public void bgClick() {
                        if (!isMoveing) {
                            if (selectView != null) {
                                selectView.setEditable(false);
                                selectView = null;
                                addFurniture.setVisibility(View.VISIBLE);
                                editLayout.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void upSlide() {
                    }

                    @Override
                    public void downSlide() {
                    }
                });
                v.getLocationChild(position);
                return v;
            }
        });
        viewPager.setAdapter(MainLocationFragment.mlist.size(), 0);
        viewPager.setOnSelectedListoner(new TagViewPager.OnSelectedListoner() {
            @Override
            public void onSelected(int position) {
                //翻页时如果不是迁移状态
                if (moveLayout.getVisibility() != View.VISIBLE) {
                    resetBtnStatus();
                }
                indicatorView.setSelection(position);
                indicatorView.notifyView();
                indicatorView.scrollToPosition(position);
                EventBus.getDefault().post(new EventBusMsg.EditLocationPositionChangeMsg(position));
            }
        });
        viewPager.setCurrentItem(indicatorView.getSelection());
        setEmpty();
    }

    private void setCurrentView(ObjectView view) {
        view.setEditable(!view.isEdit());
        if (selectView != null && view != selectView) {
            selectView.setEditable(!selectView.isEdit());
        }
        if (view.isEdit()) {
            view.bringToFront();
            addFurniture.setVisibility(View.GONE);
            editLayout.setVisibility(View.VISIBLE);
            selectView = view;
            showEditHelp();
        } else {
            addFurniture.setVisibility(View.VISIBLE);
            editLayout.setVisibility(View.GONE);
            selectView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        addPage = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick({R.id.edit_space, R.id.add_furniture, R.id.delete_btn, R.id.edit_btn, R.id.move_btn, R.id.move_commit, R
            .id.move_cancel})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.edit_space:
                resetBtnStatus();
                intent = new Intent(this, SpaceEditActivity.class);
                startActivity(intent);
                break;
            case R.id.add_furniture:
                if (MainLocationFragment.mlist.isEmpty()) {
                    intent = new Intent(this, SpaceEditActivity.class);
                    startActivity(intent);
                    ToastUtil.show(this, "暂无空间，请先添加空间", Toast.LENGTH_LONG);
                    return;
                }
                intent = new Intent(this, FurnitureSysListActivity.class);
                intent.putExtra("code", indicatorView.getCurrentLocation().getCode());
                intent.putExtra("position", viewPager.getCurrentItem());
                startActivity(intent);
                addPage = (LocationPage) viewPager.getPrimaryItem();
                break;
            case R.id.delete_btn:
                deleLocation();
                break;
            case R.id.edit_btn:
                intent = new Intent(this, EditFurenitureActivity.class);
                intent.putExtra("object", selectView.getObjectBean());
                intent.putExtra("position", viewPager.getCurrentItem());
                startActivity(intent);
                break;
            case R.id.move_btn:
                selectPosition = viewPager.getCurrentItem();
                moveLayout.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.GONE);
                addFurniture.setVisibility(View.GONE);
                isMoveing = true;
                break;
            case R.id.move_commit:
                movePost();
                break;
            case R.id.move_cancel:
                resetBtnStatus();
                break;
        }
    }

    /**
     * 恢复下面的按钮初始状态
     */
    private void resetBtnStatus() {
        isMoveing = false;
        moveLayout.setVisibility(View.GONE);
        editLayout.setVisibility(View.GONE);
        addFurniture.setVisibility(View.VISIBLE);
        if (selectView != null) {
            selectView.setEditable(false);
        }
        selectView = null;
    }

    /**
     * 移动移动家具到憋到空间
     */
    private void movePost() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("code", selectView.getObjectBean().getCode());
        map.put("location_code", MainLocationFragment.mlist.get(viewPager.getCurrentItem()).getCode());
        map.put("position", JsonUtils.jsonFromObject(selectView.getObjectBean().getPosition()));
        map.put("scale", JsonUtils.jsonFromObject(selectView.getObjectBean().getScale()));
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                /********此段代码是因为位置查看和位置编辑页的家具对象不是同一个了，所以需要手动赋值********/
                String code = MainLocationFragment.mlist.get(selectPosition).getCode();
                List<ObjectBean> list = MainLocationFragment.locationMap.get(code);
                if (!list.contains(selectView.getObjectBean())) {
                    for (int i = 0; i < StringUtils.getListSize(list); i++) {
                        if (list.get(i).getCode().equals(selectView.getObjectBean().getCode())) {
                            list.remove(i);
                            break;
                        }
                    }
                } else {
                    MainLocationFragment.locationMap.get(MainLocationFragment.mlist.get(selectPosition).getCode())
                            .remove
                                    (selectView.getObjectBean());//删除数据
                }
                /***********************/
                MainLocationFragment.locationMap.get(MainLocationFragment.mlist.get(viewPager.getCurrentItem())
                        .getCode()).add(selectView.getObjectBean());//添加数据
                EventBusMsg.EditLocationMsg msg1 = new EventBusMsg.EditLocationMsg(viewPager.getCurrentItem());
                msg1.hasObjectChanged = true;
                EventBusMsg.EditLocationMsg msg2 = new EventBusMsg.EditLocationMsg(selectPosition);
                msg2.hasObjectChanged = true;
                EventBus.getDefault().post(msg1);
                EventBus.getDefault().post(msg2);
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                initPage();
                resetBtnStatus();
            }
        };
        HttpClient.moveFurniture(this, map, listenner);
    }

    /**
     * 删除家具
     */
    private void deleLocation() {
        DialogUtil.show("提示", String.format("是否删除 %s ？删除后该空间内所有内容的位置将会归为未定义", selectView.getObjectBean().getName()),
                "确定", "取消", ((Activity) context), new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ObjectBean temp = selectView.getObjectBean();
                                ((LocationPage) viewPager.getPrimaryItem()).removeObject(selectView);
                                selectView = null;
                                addFurniture.setVisibility(View.VISIBLE);
                                editLayout.setVisibility(View.GONE);
                                Map<String, String> map = new TreeMap<>();
                                map.put("code", temp.getCode());
                                map.put("uid", MyApplication.getUser(context).getId());
                                PostListenner listenner = new PostListenner(context) {
                                    @Override
                                    protected void code2000(final ResponseResult r) {
                                        super.code2000(r);
                                        EventBusMsg.EditLocationMsg msg = new EventBusMsg.EditLocationMsg(viewPager
                                                .getCurrentItem());
                                        msg.hasObjectChanged = true;
                                        EventBus.getDefault().post(msg);
                                        //刷新物品界面 物品数据
                                        EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                                    }
                                };
                                HttpClient.deleteLocation(context, map, listenner);
                            }
                        }, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String str) {
        if (EventBusMsg.SPACE_EDIT.equals(str)) {
            indicatorView.notifyView();
            initPage();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.AddFurnitureMsg msg) {
        ObjectView ov = ((LocationPage) viewPager.getPrimaryItem()).addFuniture(msg.objectBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.RequestSpaceEdit msg) {
        indicatorView.notifyView();
        initPage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.editFurnitureMsg msg) {
        selectView.getObjectBean().setName(msg.name);
        selectView.getObjectBean().setRatio(msg.shape);
        selectView.getObjectBean().setImage_url(msg.imageUrl);
        selectView.getObjectBean().setBackground_url(msg.background_url);
        if (msg.layers != null) {
            selectView.getObjectBean().setLayers(msg.layers);
        }
        selectView.setObject(selectView.getObjectBean());
    }

    /**
     * 设置空数据
     */
    private void setEmpty() {
        if (MainLocationFragment.mlist.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            quickLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LocationEditActivity.this, QuickSpaceSelectListActivity.class);
                    startActivity(intent);
                }
            });
            quickLayout.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }
    }

    /**
     * 首次引导
     *
     * @param pageView
     */
    private void showHelp(LocationPage pageView) {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setContentTextColor(getResources().getColor(R.color.white));
        config.setMaskColor(getResources().getColor(R.color.black_87));
        config.setDelay(200); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "editSpace");
        sequence.setConfig(config);
        MaterialShowcaseView sequenceItem1 = (new MaterialShowcaseView.Builder(this)).setTarget(editSpace)
                .setContentText("空间编辑\n进入后，可对空间进行添加、\n删除、修改名称及排序")
                .setTargetTouchable(false)
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTargetTouch(true)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem1);
        View targetView;
        if (pageView.getChildCount() >= 3) {
            targetView = pageView.getChildAt(1);
            MaterialShowcaseView sequenceItem2 = (new MaterialShowcaseView.Builder(this)).setTarget(targetView)
                    .setContentText("长按可移动位置\n点击可选中编辑")
                    .setTargetTouchable(false)
                    .setDismissOnTouch(true)
                    .setShapePadding(0)
                    .setDelay(200)
                    .setMaskColour(getResources().getColor(R.color.black_70))
                    .setDismissOnTargetTouch(true)
                    .withRectangleShape(false).build();
            sequence.addSequenceItem(sequenceItem2);
        }
        sequence.start();

    }

    private void showEditHelp() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setContentTextColor(getResources().getColor(R.color.white));
        config.setMaskColor(getResources().getColor(R.color.black_87));
        config.setDelay(200); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "selectView");
        sequence.setConfig(config);
        MaterialShowcaseView sequenceItem1 = (new MaterialShowcaseView.Builder(this)).setTarget(selectView)
                .setContentText("家具调整\n点选家具图，长按拖动位置，托\n拽右下角调整图片大小")
                .setTargetTouchable(false)
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTargetTouch(true)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem1);
        MaterialShowcaseView sequenceItem2 = (new MaterialShowcaseView.Builder(this)).setTarget(moveBtn)
                .setContentText("家具迁移\n家具及其内部已归入物品可整体\n跨空间迁移")
                .setTargetTouchable(false)
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTargetTouch(true)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem2);
        MaterialShowcaseView sequenceItem3 = (new MaterialShowcaseView.Builder(this)).setTarget(editBtn)
                .setContentText("家具编辑页\n进入后，可编辑家具名称，家具\n展示图及图片截图区域，家具内\n部结构")
                .setTargetTouchable(false)
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTargetTouch(true)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem3);
        sequence.start();
    }
}
