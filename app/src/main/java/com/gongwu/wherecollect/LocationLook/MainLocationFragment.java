package com.gongwu.wherecollect.LocationLook;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.LocationLook.furnitureLook.FurnitureLookActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.AddSharePersonActivity;
import com.gongwu.wherecollect.activity.MainActivity;
import com.gongwu.wherecollect.adapter.OnFloatClickListener;
import com.gongwu.wherecollect.afragment.BaseFragment;
import com.gongwu.wherecollect.afragment.MainFragment1;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.LocationBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.quickadd.QuickSpaceSelectListActivity;
import com.gongwu.wherecollect.record.MakeRecordActivity;
import com.gongwu.wherecollect.util.AnimationUtil;
import com.gongwu.wherecollect.util.AppConstant;
import com.gongwu.wherecollect.util.BitmapUtil;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.ErrorView;
import com.gongwu.wherecollect.view.FloatWindowView;
import com.gongwu.wherecollect.view.ObjectView;
import com.umeng.analytics.MobclickAgent;
import com.zhaojin.myviews.TagViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainLocationFragment extends BaseFragment {
    public static List<LocationBean> mlist = new ArrayList<>();//静态空间数据
    public static Map<String, List<ObjectBean>> objectMap = new HashMap<>();
    //物品总览
    public static Map<String, List<ObjectBean>> locationMap = new HashMap<>();//家具
    public static Map<Integer, LocationPage> pageMap = new HashMap<>();
    public static Bitmap bitmap;
    View view;
    @Bind(R.id.fragment_indicatorView)
    LocationIndicatorView indicatorView;
    @Bind(R.id.objectListView)
    LocationObectListView objectListView;
    @Bind(R.id.tagViewPager)
    TagViewPager viewPager;
    @Bind(R.id.empty)
    ErrorView empty;
    @Bind(R.id.quick_layout)
    View quickLayout;
    @Bind(R.id.help_layout)
    View helpLayout;
    @Bind(R.id.objectListView_layout)
    FrameLayout listviewLayout;
    @Bind(R.id.share_iv)
    ImageView shareIv;
    @Bind(R.id.float_view)
    FloatWindowView floatView;

    private UserBean user;
    boolean isLoaded = false;
    Handler hd = new Handler();

    public static MainLocationFragment newInstance() {
        MainLocationFragment fragment = new MainLocationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location_look, container, false);
        ButterKnife.bind(this, view);
        initView();
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onShow() {
        super.onShow();
        if (isLoaded) {
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "mainlocation");
            if (!sequence.hasFired()) {
                initPage();
            }
        }
        if (!isLoaded) {
            initHandler.postDelayed(initRunnable, initTime);
            isLoaded = true;
        }
    }

    private long initTime = 100;
    Handler initHandler = new Handler();
    Runnable initRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager == null) {
                initHandler.postDelayed(this, initTime);
            } else {
                getLocationList();
                initHandler.removeCallbacks(this);
            }
        }
    };


    private void initView() {
        floatView.setEnabled(false);
        //桌布空间头点击事件
        indicatorView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(indicatorView.getSelection());
            }
        });
        objectListView.init();
        //物品列表点击事件
        objectListView.setOnItemClickListener(new LocationObectListView.OnitemClickLisener() {
            @Override
            public void itemClick(int position, ObjectBean bean, View view) {
                if (pageMap.get(indicatorView.getSelection()) != null) {
                    pageMap.get(indicatorView.getSelection()).findView(bean);
                    MobclickAgent.onEvent(getActivity(), "030301");
                }
            }
        });
        floatView.setOnFloatClickListener(new OnFloatClickListener() {
            @Override
            public void onClick(View view) {

            }

            @Override
            public void onLongClick(View view) {
                //删除
                DialogUtil.show("提示", "是否取消迁移", "确定", "取消", getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.floatBean = null;
                        floatView.setVisibility(View.GONE);
                    }
                }, null);
            }
        });
        user = MyApplication.getUser(getActivity());
    }

    //动画间隔
    private long animTime = 5000;
    Handler mHandler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            startViewAlphaAnim();
        }
    };

    private void startViewAlphaAnim() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);//初始化操作，参数传入0和1，即由透明度0变化到透明度为1
        alphaAnimation.setFillAfter(false);//动画结束后保持状态
        alphaAnimation.setDuration(1000);//动画持续时间，单位为毫秒
        if (objectListView != null) {
            objectListView.startAnimation(alphaAnimation);//开始动画
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
                    setViewAnim(objectListView);
                }
                startRunnableOrEnd(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startRunnableOrEnd(boolean startOrEnd) {
        if (user != null && !SaveDate.getInstence(getContext()).getBreathLook(user.getId())) {
            //先取消 再开启
            mHandler.removeCallbacks(r);
            if (startOrEnd) {
                mHandler.postDelayed(r, animTime);
            } else {
                objectListView.adapter.defaultData();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (indicatorView != null) {
            indicatorView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.e("isVisibleToUser:" + isVisibleToUser);
        if (!isVisibleToUser) {
            startRunnableOrEnd(false);
            hideObjectList();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        startRunnableOrEnd(false);
        hideObjectList();
    }

    //View 显示动画
    private void setViewAnim(View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);//初始化操作，参数传入0和1，即由透明度0变化到透明度为1
        alphaAnimation.setFillAfter(false);//动画结束后保持状态
        alphaAnimation.setDuration(1000);//动画持续时间，单位为毫秒
        view.startAnimation(alphaAnimation);//开始动画
    }

    //呼吸查看
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.GoodsIsCloseBreathLook msg) {
        startRunnableOrEnd(!msg.isCloseBreathLook);
    }

    @OnClick({R.id.share_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_iv:
                Intent intent = new Intent(getActivity(), AddSharePersonActivity.class);
                intent.putExtra("startType", 101);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        startRunnableOrEnd(false);
        hideObjectList();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取最外层的位置
     */
    private void getLocationList() {
        final String cache = SaveDate.getInstence(getActivity()).getSpace();
        if (!TextUtils.isEmpty(cache)) {
            List<LocationBean> temp = JsonUtils.listFromJson(cache, LocationBean.class);
            mlist.clear();
            mlist.addAll(temp);
            if (indicatorView != null && mlist.size() > 0) {
                indicatorView.init(mlist);
            }
            initPage();
            if (mlist.size() > 0) {
                EventBus.getDefault().post(new EventBusMsg.showShareImgList(mlist.get(0)));
            }
        }
        getSpaceData(cache);
    }

    private void initPage() {
        viewPager.init(R.drawable.shape_photo_tag_select, R.drawable.shape_photo_tag_nomal, 0,
                0, 0, 0);
        viewPager.setAutoNext(false, 0);
        viewPager.setId(12);
        viewPager.setOnGetView(new TagViewPager.OnGetView() {
            @Override
            public View getView(ViewGroup container, final int position) {
                LocationPage v = new LocationPage(getActivity()) {
                    @Override
                    protected void getNetDataListener(List<ObjectBean> list) {
                        super.getNetDataListener(list);
                        if (position == viewPager.getCurrentItem()) {
                            objectListView.notifyData(list, indicatorView.getSelection());
                            EventBus.getDefault().post(new EventBusMsg.getLocationObjectsMsg(position, list));
                        }
                    }

                    @Override
                    protected void getChildListener(int position) {
                        super.getChildListener(position);
                        if (position == viewPager.getCurrentItem()) {
                            showHelp(this);

                        }
                    }

                    @Override
                    public void cancelFind() {
                        super.cancelFind();
                        hd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (objectListView == null || objectListView.adapter == null) {
                                    return;
                                }
                                objectListView.adapter.selectPostion = -1;
                                objectListView.adapter.notifyDataSetChanged();
                            }
                        }, 200);
                    }
                };
                container.addView(v);
                v.init(false);
                v.setOnItemClickListener(new LocationPage.OnItemClickListener() {
                    @Override
                    public void itemClick(ObjectView view) {
                        Intent intent = new Intent(getActivity(), FurnitureLookActivity.class);
                        intent.putExtra("furnitureObject", view.getObjectBean());
                        intent.putExtra("position", viewPager.getCurrentItem());
                        if (objectListView.adapter.selectPostion != -1) {
                            intent.putExtra("object", objectListView.adapter.getItem(objectListView.adapter
                                    .selectPostion));
                        }
                        intent.putExtra("title", indicatorView.getCurrentLocation().getName() + ">" + view
                                .getObjectBean()
                                .getName());
                        getActivity().startActivityForResult(intent, AppConstant.START_FURNITURE_LOOK_ACT_CODE);
                    }

                    @Override
                    public void bgClick() {
                    }

                    @Override
                    public void upSlide() {
                        listviewLayout.setVisibility(View.VISIBLE);
                        AnimationUtil.upSlide(listviewLayout, 150);
                        shareIv.setVisibility(View.VISIBLE);
                        AnimationUtil.upLeft(shareIv, 150);
                        startRunnableOrEnd(true);
                    }

                    @Override
                    public void downSlide() {
                        AnimationUtil.downSlide(listviewLayout, 150);
                        AnimationUtil.upRight(shareIv, 150);
                        startRunnableOrEnd(false);
                    }
                });
                v.getLocationChild(position);
                v.getObjectList();
                pageMap.put(position, v);
                return v;
            }
        });
        viewPager.setAdapter(mlist.size(), 0);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (!StringUtils.isEmpty(mlist)) {
                    checkedShijiCache(0);
                }
            }
        });
        viewPager.viewPager.setOffscreenPageLimit(1);
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (viewPager != null) {
                    viewPager.viewPager.setOffscreenPageLimit(mlist.size());
                }
            }
        }, 2000);
        viewPager.setOnSelectedListoner(new TagViewPager.OnSelectedListoner() {
            @Override
            public void onSelected(int position) {
                indicatorView.setSelection(position);
                indicatorView.notifyView();
                pageMap.get(position).getObjectList();
                checkedShijiCache(position);
                indicatorView.scrollToPosition(position);
                EventBus.getDefault().post(new EventBusMsg.showShareImgList(mlist.get(position)));
                if (!SaveDate.getInstence(getContext()).getBreathLook(user.getId())) {
                    //先取消 再开启
                    mHandler.removeCallbacks(r);
                    objectListView.adapter.defaultData();
                    mHandler.postDelayed(r, animTime);
                }
                hideObjectList();
            }
        });
        viewPager.setCurrentItem(indicatorView.getSelection());
        setEmpty();
    }

    /**
     * 检测室迹暂存
     */
    private void checkedShijiCache(int position) {
//        String cache = ACacheClient.getRecord(getActivity(), mlist.get(position).getCode());
//        ((MainFragment1) ((MainActivity) getActivity()).fragments.get(0)).setRedStatus(!TextUtils.isEmpty
//                (cache));
    }

    private void getSpaceData(final String cache) {
        final int selectPostion = indicatorView.getSelection();
        Map<String, String> map = new HashMap<>();
        map.put("uid", MyApplication.getUser(getActivity()).getId());
        PostListenner listenner = new PostListenner(getActivity()) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                if (r.getResult().equals(cache)) {//如果缓存和网络获取的一样则不做处理
                    return;
                }
                List<LocationBean> temp = JsonUtils.listFromJson(r.getResult(), LocationBean.class);
                SaveDate.getInstence(getActivity()).setSpace(r.getResult());
                mlist.clear();
                mlist.addAll(temp);
                if (selectPostion != -1 && selectPostion <= mlist.size() - 1) {
                    mlist.get(selectPostion).setSelect(true);
                }
                indicatorView.init(mlist);
                initPage();
                EventBus.getDefault().post(new EventBusMsg.showShareImgList(mlist.get(0)));
                EventBus.getDefault().post(new EventBusMsg.RequestSpaceEdit());
                EventBus.getDefault().post(EventBusMsg.SPACE_EDIT);
                startSystemAddLocationHint();
            }
        };
        HttpClient.getLocationList(getActivity(), map, listenner);
    }

    private void startSystemAddLocationHint() {
        //判断是否提示过快速构建,并且不是测试账号
        if (!SaveDate.getInstence(getActivity()).getQuickAdd
                (MyApplication.getUser(getActivity()).getId())
                && !MyApplication.getUser(getActivity()).isTest()) {
            //没有提示 判断是否有家具 有家具则不跳转到提示界面并设置默认状态
            if (mlist.size() == 0) {
                //如果不是测试账号，提示过快速添加
                SaveDate.getInstence(getActivity()).setQuickAdd(MyApplication.getUser(getActivity()).getId(), true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), QuickSpaceSelectListActivity.class);
                        startActivity(intent);
                    }
                }, 200);
            } else {
                SaveDate.getInstence(getActivity()).setQuickAdd(MyApplication.getUser(getActivity()).getId(), true);
            }
        }
    }

    //空间顺序或名称变动过
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String str) {
        if (EventBusMsg.SPACE_EDIT.contains(str)) {
            indicatorView.notifyView();
            initPage();
        }
    }

    //空间需要切换到哪一页
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.EditLocationPositionChangeMsg msg) {
        if (viewPager != null) {
            viewPager.setCurrentItem(msg.position);
        }
    }

    //空间需要重新网络获取
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.RequestSpace msg) {
        getSpaceData("");
    }

    //某一页的空间需要刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.EditLocationMsg msg) {
        LocationPage page = pageMap.get(msg.position);
        if (page != null) {
            if (msg.onlyNotifyUi) {
                page.getLocationChild(msg.position);
            } else {
                if (msg.hasFurnitureChanged) {//需要重新从服务区获取数据
                    page.getNetData("", MainLocationFragment.mlist.get(msg.position).getCode());
                } else if (msg.changeBean != null) {//家具数据有传过来直接替换
                    List<ObjectBean> temp = MainLocationFragment.locationMap.get(MainLocationFragment.mlist.get(msg
                            .position).getCode());
                    for (int i = 0; i < StringUtils.getListSize(temp); i++) {
                        if (temp.get(i).get_id().equals(msg.changeBean.get_id())) {
                            temp.remove(i);
                            temp.add(msg.changeBean);
                            page.getLocationChild(msg.position);
                            break;
                        }
                    }
                }
                if (msg.hasObjectChanged) {//如果需要物品总览刷新
                    MainLocationFragment.objectMap.remove(mlist.get(msg.position).getCode());
                    page.getObjectList();
                }
            }
        }
    }

    //更换了账号
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ChangeUser msg) {
        locationMap.clear();
        getSpaceData("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.updateShareMsg msg) {
        getLocationList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ImportObject msg) {
        MainLocationFragment.objectMap.remove(mlist.get(msg.position).getCode());
        pageMap.get(msg.position).getObjectList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.DeleteGoodsMsg deleteGoodsMsg) {
        for (List<ObjectBean> beanList : MainLocationFragment.objectMap.values()) {
            for (int i = 0; i < beanList.size(); i++) {
                ObjectBean objectBean = beanList.get(i);
                if (objectBean.get_id().equals(deleteGoodsMsg.goodsId)) {
                    beanList.remove(i);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.FloatBoxMsg floatBoxMsg) {
        if (floatBoxMsg.show) {
            floatView.setVisibility(View.VISIBLE);
            floatView.setNameTv(floatBoxMsg.title);
        } else {
            floatView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mlist.clear();
        locationMap.clear();
        pageMap.clear();
        objectMap.clear();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 事迹点击事件
     */
    public void shijiClick() {
        if (MainLocationFragment.mlist.isEmpty()) {
            ToastUtil.show(getActivity(), "暂无空间，您可以尝试快速构建你的家", Toast.LENGTH_LONG);
            return;
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = BitmapUtil.viewToBitmap(viewPager.getPrimaryItem());
        Intent intent = new Intent(getActivity(), MakeRecordActivity.class);
        intent.putExtra("name", indicatorView.getCurrentLocation().getName());
        intent.putExtra("code", indicatorView.getCurrentLocation().getCode());
        getActivity().startActivity(intent);
    }


    /**
     * 定位物品
     */
    public void findObject(ObjectBean objectBean) {
        for (int i = 0; i < MainLocationFragment.mlist.size(); i++) {
            List<BaseBean> locations = objectBean.getLocations();
            for (int j = 0; j < StringUtils.getListSize(locations); j++) {
                if (locations.get(j).getCode().equals(mlist.get(i).getCode())) {
                    viewPager.setCurrentItem(i);
                    objectListView.findObject(objectBean);

                    return;
                }
            }
        }
    }

    /**
     * 隐藏物品总览
     */
    public void hideObjectList() {
        if (listviewLayout != null) {
            AnimationUtil.downSlide(listviewLayout, 150);
        }
        if (shareIv != null) {
            AnimationUtil.upRight(shareIv, 150);
        }
    }

    /**
     * 首次安装提示信息
     */
    private void showHelp(LocationPage pageView) {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setContentTextColor(getResources().getColor(R.color.white));
        config.setMaskColor(getResources().getColor(R.color.black_87));
        config.setDelay(200); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "mainlocation");
        sequence.setConfig(config);
        MaterialShowcaseView sequenceItem1 = (new MaterialShowcaseView.Builder(getActivity())).setTarget(helpLayout)
                .setContentText("空间展示区\n可模拟家中家具布局展示")
                .setTargetTouchable(false)
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTargetTouch(true)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem1);
        View targetView;
        final int objectListViewPosition;
        if (pageView.getChildCount() >= 3) {
            targetView = pageView.getChildAt(1);
            MaterialShowcaseView sequenceItem2 = (new MaterialShowcaseView.Builder(getActivity()))
                    .setTarget(targetView)
                    .setContentText("家具图\n点击可展开查看其内部结构以及其中放置的物品")
                    .setTargetTouchable(false)
                    .setMaskColour(getResources().getColor(R.color.black_70))
                    .setDismissOnTouch(true)
                    .setDelay(200)
                    .setShapePadding(0)
                    .setDismissOnTargetTouch(false)
                    .withRectangleShape(false).build();
            sequence.addSequenceItem(sequenceItem2);
            objectListViewPosition = 2;
        } else {
            objectListViewPosition = 1;
        }
        MaterialShowcaseView sequenceItem3 = (new MaterialShowcaseView.Builder(getActivity()))
                .setTarget(objectListView).setContentText("空间物品总览\n上划展开查看，单次点击物品图可定位其所在位置")
                .setTargetTouchable(false)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setDismissOnTargetTouch(false)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem3);
        MaterialShowcaseView sequenceItem4 = (new MaterialShowcaseView.Builder(getActivity()))
                .setTarget(objectListView).setContentText("呼吸查看\n位置页物品图滚动展示图片和名\n称内容。\n可在“我的”里选择关闭")
                .setTargetTouchable(false)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setDismissOnTargetTouch(false)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem4);
        MainFragment1 fragment1 = (MainFragment1) ((MainActivity) getActivity()).fragments.get(0);
        if (fragment1.myFragmentLayout != null) {
            TextView view = (TextView) fragment1.myFragmentLayout.findViewById(R.id.text_edit);
            MaterialShowcaseView sequenceItem5 = (new MaterialShowcaseView.Builder(getActivity()))
                    .setTarget(view).setContentText("编辑\n进入后，可创建和排序空间，添\n加更多家具，调整家具图大小和\n位置，以及进行家具细节编辑")
                    .setTargetTouchable(false)
                    .setMaskColour(getResources().getColor(R.color.black_70))
                    .setDismissOnTouch(true)
                    .setShapePadding(0)
                    .setDelay(200)
                    .setDismissOnTargetTouch(false)
                    .withRectangleShape(false).build();
            sequence.addSequenceItem(sequenceItem5);
        }
        MaterialShowcaseView sequenceItem6 = (new MaterialShowcaseView.Builder(getActivity()))
                .setTarget(shareIv).setContentText("共享\n创建共享后，可共同查看和\n编辑该空间和空间内所有物\n品")
                .setTargetTouchable(false)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setDismissOnTargetTouch(false)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem6);
        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
                if (position == objectListViewPosition) {
                    listviewLayout.setVisibility(View.VISIBLE);
                    AnimationUtil.upSlide(listviewLayout, 150);
                    shareIv.setVisibility(View.VISIBLE);
                    AnimationUtil.upLeft(shareIv, 150);
                    startRunnableOrEnd(true);
                }
            }
        });
        sequence.start();
    }

    /**
     * 设置空数据
     */
    private void setEmpty() {
        if (mlist.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            quickLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), QuickSpaceSelectListActivity.class);
                    startActivity(intent);
                }
            });
            quickLayout.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }
    }
}
