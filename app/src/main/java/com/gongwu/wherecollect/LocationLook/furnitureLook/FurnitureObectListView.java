package com.gongwu.wherecollect.LocationLook.furnitureLook;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.LocationLook.LocationObectListView;
import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.MainActivity;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.adapter.MyOnItemLongClickListener;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.importObject.ImportObectsActivity;
import com.gongwu.wherecollect.object.ObjectLookInfoActivity;
import com.gongwu.wherecollect.util.ACacheClient;
import com.gongwu.wherecollect.util.AnimationUtil;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.ShowCaseUtil;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.BoxEditDialog;
import com.gongwu.wherecollect.view.ErrorView;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Function:
 * Date: 2017/11/14
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class FurnitureObectListView extends RelativeLayout {

    public ObjectListAdapter adapter;
    List<ObjectBean> mList = new ArrayList<>();//物品
    List<ObjectBean> filterList = new ArrayList<>();//筛选出来的物品列表
    List<ObjectBean> mBoxList = new ArrayList<>();//盒子
    List<ObjectBean> mFilterBoxList = new ArrayList<>();//盒子筛选后列表
    List<ObjectBean> mCWList = new ArrayList<>();//常忘物品列表
    Context context;
    @Bind(R.id.goods_list_view)
    public RecyclerView recyclerView;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.indicatorListView)
    RecyclerView indicatorListView;
    @Bind(R.id.emtpy_view)
    ErrorView emtpyView;
    @Bind(R.id.add_btn)
    TextView addBtn;
    @Bind(R.id.impor_btn)
    TextView imporBtn;
    @Bind(R.id.btn_layout)
    LinearLayout btnLayout;
    @Bind(R.id.noSelect_tv)
    TextView noSelectTv;
    @Bind(R.id.indicatorLayout)
    LinearLayout indicatorLayout;
    @Bind(R.id.move_layout)
    LinearLayout moveLayout;
    @Bind(R.id.btnlayout)
    LinearLayout btnlayout;
    @Bind(R.id.move_commit)
    Button moveCommit;
    @Bind(R.id.chang_wang_list)
    LocationObectListView mCWListView;

    private ObjectBean objectBean;//隔层对象
    private ObjectBean furnitureBean;//家具对象
    private GridLayoutManager mLayoutManager;
    private OnitemClickLisener listener;
    private OnFinishActivityLisener finishListener;
    private BoxListAdapter boxAdapter;
    private boolean cwFunction;//判断是否为物品界面添加定位
    private boolean cwSelect;//判断物品是否勾选

    public FurnitureObectListView(Context context) {
        this(context, null);
    }

    public FurnitureObectListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View.inflate(context, R.layout.layout_objectlist_view, this);
        ButterKnife.bind(this);
    }

    public void setOnItemClickListener(OnitemClickLisener listener) {
        this.listener = listener;
    }

    public void setOnFinishActivityLisener(OnFinishActivityLisener listener) {
        this.finishListener = listener;
    }

    public void init(ObjectBean furnitureBean, List<ObjectBean> list) {
        mList.clear();
        if (list != null && list.size() > 0) {
            mList.addAll(list);
        }
        this.furnitureBean = furnitureBean;
        initInduction();
        recyclerView.setHasFixedSize(true);
        adapter = new ObjectListAdapter(context, filterList);
        mLayoutManager = new GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {//点击别处取消物品选择
                if (event.getAction() == MotionEvent.ACTION_UP && ((FurnitureLookActivity) context).selectObject != null) {
                    ((FurnitureLookActivity) context).selectObject = null;
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        //获取缓存
        getDetailData();
        adapter.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(int positions, View view) {
                //添加物品跳转过来,点击空间内的物品直接删除并移到未导入list
                if (mCWListView.getVisibility() == View.VISIBLE) {
                    deleteGoods(positions);
                    return;
                }
                //从家具界面点击过来,先给选中状态
                if (getSelectObjectId().equals(filterList.get(positions).get_id())) {
                    Intent intent = new Intent(context, ObjectLookInfoActivity.class);
                    intent.putExtra("bean", filterList.get(positions));
                    context.startActivity(intent);
                    return;
                }
                //然后再点击的话进去物品详情界面
                ((FurnitureLookActivity) context).selectObject = filterList.get(positions);
                adapter.notifyDataSetChanged();
                ((FurnitureLookActivity) context).findObject(filterList.get(positions));
                if (listener != null) {
                    listener.itemClick(positions, filterList.get(positions), view);
                }
            }
        });
        adapter.setmOnItemLongClickListener(new MyOnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position, View view) {
                DialogUtil.show("提示", "将物品移出该位置 ？物品不会被删除", "确定", "取消", ((Activity) context), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGoods(position);
                    }
                }, null);
            }
        });
        title.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogUtil.show("提示", "整体迁移该隔层内收纳盒和物品?", "确定", "取消", ((Activity) context), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        objectBean.setLayer(true);
                        moveLayout.setTag(objectBean);
                        if (floatShowListener != null) {
                            floatShowListener.floatShow(objectBean, true);
                        }
                    }
                }, null);
                return false;
            }
        });
        notifyObject(null, null);
    }

    /**
     * 删除物品
     *
     * @param position
     */
    private void deleteGoods(final int position) {
        Map<String, String> map = new TreeMap<>();
        map.put("code", filterList.get(position).getId());
        map.put("uid", MyApplication.getUser(context).getId());
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                EventBus.getDefault().post(new EventBusMsg.ImportObject(((FurnitureLookActivity) context).spacePosition));
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                if (mCWListView.getVisibility() == View.VISIBLE) {
                    mCWList.add(filterList.get(position));
                    mCWListView.notifyData(mCWList, -1);
                    if (filterList.size() == 0) emtpyView.setVisibility(VISIBLE);
                }
                mList.remove(filterList.remove(position));
                adapter.notifyDataSetChanged();
            }
        };
        HttpClient.removeObjectFromFurnitrue(context, map, listenner);
    }

    public List<ObjectBean> getCWList() {
        return mCWList;
    }

    public View getMoveLayout() {
        return moveLayout;
    }

    /**
     * 常忘物品添加
     *
     * @param datas
     */

    public void initCWListView(List<ObjectBean> datas) {
        cwFunction = true;
        mList.clear();
        mCWList.clear();
        filterList.clear();
        adapter.notifyDataSetChanged();
        imporBtn.setText("放置此处");
        emtpyView.setErrorMsg("请选择上方隔层,可添加物品和收纳盒");
        noSelectTv.setVisibility(View.GONE);
        recyclerView.setVisibility(GONE);
        emtpyView.setVisibility(VISIBLE);
        indicatorLayout.setVisibility(INVISIBLE);
        if (datas != null && datas.size() > 0) {
            mCWList.addAll(datas);
            mCWListView.setVisibility(View.VISIBLE);
            mCWListView.notifyData(mCWList, -1);
            mCWListView.adapter.setOnItemClickListener(new MyOnItemClickListener() {
                @Override
                public void onItemClick(int positions, View view) {
                    mCWList.get(positions).setSelect(!mCWList.get(positions).isSelect());
                    mCWListView.adapter.notifyDataSetChanged();
                    //隔层要选中
                    for (ObjectBean objectBean : mCWList) {
                        if (objectBean.isSelect()) {
                            if (addBtn.isSelected()) {
                                emtpyView.setErrorMsg("点击“放置此处”完成物品定位");
                                imporBtn.setSelected(true);
                            }
                            cwSelect = true;
                            break;
                        } else {
                            imporBtn.setSelected(false);
                            cwSelect = false;
                        }
                    }
                    if (!imporBtn.isSelected() && addBtn.isSelected())
                        emtpyView.setErrorMsg("请选择下方要放置的物品");
                }
            });
        }
    }

    /**
     * 初始化盒子列表
     */
    private void initInduction() {
        indicatorListView.setHasFixedSize(true);
        boxAdapter = new BoxListAdapter(context, mFilterBoxList) {
            @Override
            protected void onLongClick(final int position) {
                super.onLongClick(position);
                BoxEditDialog dialog = new BoxEditDialog(context, mFilterBoxList.get(position).getName()) {
                    @Override
                    protected void commit(String str, int type) {
                        super.commit(str, type);
                        editBox(position, str);
                    }

                    @Override
                    protected void delete() {
                        super.delete();
                        deleteBox(position);
                    }

                    @Override
                    protected void move() {
                        super.move();
//                        moveLayout.setVisibility(VISIBLE);
//                        btnlayout.setVisibility(GONE);
                        moveLayout.setTag(mFilterBoxList.get(position));
                        if (floatShowListener != null) {
                            floatShowListener.floatShow(mFilterBoxList.get(position), true);
                        }
                    }
                };
                dialog.setTitle("编辑收纳盒");
                dialog.setType(1);
                dialog.show();
            }
        };
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        indicatorListView.setLayoutManager(mLayoutManager);
        indicatorListView.setAdapter(boxAdapter);
        boxAdapter.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(int positions, View view) {
                boxAdapter.selectPostion = positions;
                boxAdapter.notifyDataSetChanged();
                notifyObject(objectBean, mFilterBoxList.get(positions));
                title.setBackgroundColor(Color.WHITE);
            }
        });
    }

    /**
     * 刷新物品列表
     *
     * @param bean 隔层对象，null代表筛选家具
     * @param bean 盒子对象，null代表只选了隔层
     */
    public void notifyObject(ObjectBean bean, ObjectBean boxBean) {
        this.objectBean = bean;
        if (objectBean == null) {
            if (!cwFunction) {
                noSelectTv.setVisibility(VISIBLE);
                indicatorLayout.setVisibility(GONE);
            } else {
                indicatorLayout.setVisibility(INVISIBLE);
                emtpyView.setVisibility(VISIBLE);
            }
            addBtn.setSelected(false);
            imporBtn.setSelected(false);
            moveCommit.setEnabled(false);
            if (mCWListView.getVisibility() == View.VISIBLE)
                emtpyView.setErrorMsg("请选择上方隔层,可添加物品和收纳盒");
        } else {
            if (!cwFunction) {
                noSelectTv.setVisibility(GONE);
            } else {
                emtpyView.setVisibility(GONE);
            }
            indicatorLayout.setVisibility(VISIBLE);
            addBtn.setSelected(true);
            if (mCWListView.getVisibility() != View.VISIBLE) {
                imporBtn.setSelected(true);
            }
            if (cwSelect) imporBtn.setSelected(true);
            title.setText(objectBean.getName());
            moveCommit.setEnabled(true);
            if (addBtn.isSelected() && mCWListView.getVisibility() == View.VISIBLE)
                emtpyView.setErrorMsg(cwSelect ? "点击“放置此处”完成物品定位" : "请选择下方要放置的物品");
        }
        if (boxBean == null) {//选择了隔层
            notifyInducation(objectBean);
            title.setBackgroundResource(R.drawable.icon_text_select);
        }
        filterObjects(boxBean == null ? bean : boxBean);
        if (StringUtils.isEmpty(filterList)) {
            recyclerView.setVisibility(GONE);
            emtpyView.setVisibility(VISIBLE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            emtpyView.setVisibility(GONE);
            //切换隔层 先判断userid
            if (!TextUtils.isEmpty(userId)) {
                //再判断呼吸查看 开关是否开启
                if (!SaveDate.getInstence(context).getBreathLook(userId)) {
                    //重新开启呼吸动画
                    ((FurnitureLookActivity) context).reStartViewAlphaAnim();
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 刷新盒子列表
     */
    private void notifyInducation(ObjectBean layerBean) {
        boxAdapter.selectPostion = -1;
        if (layerBean == null) {
            mFilterBoxList.clear();
            boxAdapter.notifyDataSetChanged();
            return;
        }
        filterBoxList(layerBean);
        boxAdapter.selectPostion = -1;
        boxAdapter.notifyDataSetChanged();
    }

    /**
     * 递归获取所有的物品
     *
     * @param bean
     */
    private void filterObjects(ObjectBean bean) {
        filterList.clear();
        if (bean == null) {
            bean = furnitureBean;
        }
        for (int i = 0; i < mList.size(); i++) {
            List<BaseBean> locations = mList.get(i).getLocations();
            for (int j = 0; j < StringUtils.getListSize(locations); j++) {
                if (locations.get(j) != null && locations.get(j).getCode().equals(bean.getCode())) {
                    filterList.add(mList.get(i));
                    break;
                }
            }
        }
    }

    /**
     * 初始化盒子数据
     */
    private void filterBoxList(@NonNull ObjectBean layerObject) {
        mFilterBoxList.clear();
        for (int i = 0; i < mBoxList.size(); i++) {
            List<BaseBean> locations = mBoxList.get(i).getParents();
            for (int j = 0; j < StringUtils.getListSize(locations); j++) {
                if (locations.get(j) != null && locations.get(j).getCode().equals(layerObject.getCode())) {
                    mFilterBoxList.add(mBoxList.get(i));
                    break;
                }
            }
        }
    }

    @OnClick({R.id.title, R.id.add_btn, R.id.impor_btn, R.id.move_cancel, R.id.move_commit, R.id.btn_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                if (addBtn.isSelected()) {
                    BoxEditDialog dialog = new BoxEditDialog(context, String.format("盒子%d", mFilterBoxList.size() + 1)) {
                        @Override
                        protected void commit(String str, int type) {
                            super.commit(str, type);
                            addLocation(str);
                        }
                    };
                    dialog.setTitle("添加收纳盒");
                    dialog.setType(0);
                    dialog.show();
                } else {
                    AnimationUtil.StartTranslate(cwFunction ? emtpyView : noSelectTv);
                }
                break;
            case R.id.impor_btn:
                if (addBtn.isSelected() && imporBtn.isSelected()) {
                    if (mCWListView.getVisibility() == View.VISIBLE) {
                        addCWLocation();
                    } else {
                        Intent intent = new Intent(context, ImportObectsActivity.class);
                        ((Activity) context).startActivityForResult(intent, 100);
                    }
                } else {
                    AnimationUtil.StartTranslate(cwFunction ? emtpyView : noSelectTv);
                }
                break;
            case R.id.title:
                if (objectBean == null) {
                    return;
                }
                boxAdapter.selectPostion = -1;
                boxAdapter.notifyDataSetChanged();
                title.setBackgroundResource(R.drawable.icon_text_select);
                notifyObject(objectBean, null);
                break;
            case R.id.move_cancel:
                moveLayout.setVisibility(GONE);
                btnlayout.setVisibility(VISIBLE);
                break;
            case R.id.move_commit:
                moveLayout.setVisibility(GONE);
                btnlayout.setVisibility(VISIBLE);
                if (((ObjectBean) moveLayout.getTag()).isLayer()) {
                    moveLayer();
                } else {
                    moveBox();
                }
                break;
            case R.id.btn_layout:
                break;
        }
    }

    private void addCWLocation() {
        Map<String, ObjectBean> map = new HashMap<>();
        for (int i = 0; i < mCWList.size(); i++) {
            ObjectBean objectBean = mCWList.get(i);
            if (objectBean.isSelect()) {
                map.put(objectBean.getId(), objectBean);
                mCWList.remove(i);
                mCWListView.removeData(i);
                i--;
            }
        }
        postImport(map);
    }

    /**
     * 盒子迁移
     */
    public void moveBox() {
        final ObjectBean moveBox = (ObjectBean) moveLayout.getTag();
        for (int i = 0; i < StringUtils.getListSize(moveBox.getParents()); i++) {
            if (moveBox.getParents() != null && moveBox.getParents().get(i).getCode().equals(objectBean.getCode())) {
                ToastUtil.show(context, "无法迁移到同一个地方", Toast.LENGTH_SHORT);
                return;
            }
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("code", moveBox.getCode());
        map.put("location_code", objectBean.getCode());
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                for (int i = 0; i < StringUtils.getListSize(moveBox.getParents()); i++) {
                    if (moveBox.getParents() != null && moveBox.getParents().get(i).getLevel() == 2) {
                        moveBox.getParents().get(i).setCode(objectBean.getCode());
                        break;
                    }
                }
                filterObjects(moveBox);
                for (int i = 0; i < filterList.size(); i++) {//物品
                    for (int j = 0; j < StringUtils.getListSize(filterList.get(i).getLocations()); j++) {
                        if (filterList.get(i).getLocations().get(j).getLevel() == 2) {
                            filterList.get(i).getLocations().get(j).setCode(objectBean.getCode());
                            break;
                        }
                    }
                }
                notifyObject(objectBean, null);
                EventBusMsg.EditLocationMsg msg1 = new EventBusMsg.EditLocationMsg(((FurnitureLookActivity) context).spacePosition);
                msg1.hasObjectChanged = true;
                msg1.hasFurnitureChanged = false;
                EventBus.getDefault().post(msg1);
                if (floatbox) {
                    getNetDate("");
                    MainActivity.floatBean = null;
                    floatShowListener.floatShow(null, false);
                }
            }
        };
        HttpClient.moveFurniture(context, map, listenner);
    }

    /**
     * 隔层迁移
     */
    public void moveLayer() {
        final ObjectBean moveBox = (ObjectBean) moveLayout.getTag();
        if (moveBox.getCode().equals(objectBean.getCode())) {
            ToastUtil.show(context, "无法迁移到同一个地方", Toast.LENGTH_SHORT);
            return;
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("code", moveBox.getCode());
        map.put("location_code", objectBean.getCode());
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                for (int i = 0; i < StringUtils.getListSize(mBoxList); i++) {
                    List<BaseBean> tempList = mBoxList.get(i).getParents();
                    for (int j = 0; j < StringUtils.getListSize(tempList); j++) {
                        if (tempList.get(j).getCode().equals(moveBox.getCode())) {
                            tempList.get(j).setCode(objectBean.getCode());
                        }
                    }
                }
                for (int i = 0; i < StringUtils.getListSize(mList); i++) {
                    List<BaseBean> tempList = mList.get(i).getLocations();
                    for (int j = 0; j < StringUtils.getListSize(tempList); j++) {
                        if (tempList.get(j).getCode().equals(moveBox.getCode())) {
                            tempList.get(j).setCode(objectBean.getCode());
                        }
                    }
                }
                notifyObject(objectBean, null);
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                EventBusMsg.EditLocationMsg msg = new EventBusMsg.EditLocationMsg(((FurnitureLookActivity) context).spacePosition);
                msg.hasObjectChanged = true;
                EventBus.getDefault().post(msg);
                if (floatbox) {
                    getNetDate("");
                    MainActivity.floatBean = null;
                    floatShowListener.floatShow(null, false);
                }
            }
        };
        HttpClient.moveLayer(context, map, listenner);
    }

    /**
     * 添加收纳盒
     *
     * @param name
     */
    private void addLocation(String name) {
        if (objectBean == null) return;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("location_name", name);
        map.put("location_code", objectBean.getCode());
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                ObjectBean temp = JsonUtils.objectFromJson(r.getResult(), ObjectBean.class);
                mBoxList.add(temp);
                mFilterBoxList.add(temp);
                boxAdapter.notifyDataSetChanged();
            }
        };
        HttpClient.addLocation(context, map, listenner);
    }

    /**
     * 删除收纳盒
     */
    private void deleteBox(final int position) {
        DialogUtil.show("提示", String.format("是否删除 %s ？删除后该空间内所有内容的位置将会归为未定义", mFilterBoxList.get(position).getName()), "确定", "取消", ((Activity) context), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, String> map = new TreeMap<>();
                map.put("code", mFilterBoxList.get(position).getCode());
                map.put("uid", MyApplication.getUser(context).getId());
                mBoxList.remove(mFilterBoxList.remove(position));
                notifyObject(objectBean, null);
                PostListenner listenner = new PostListenner(context) {
                    @Override
                    protected void code2000(final ResponseResult r) {
                        super.code2000(r);
                        //后台数据可能没改过来，需要缓一下 物品位置才清空
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //刷新物品界面 物品数据
                                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                                //删除位置界面 物品总览的缓存
                                EventBus.getDefault().post(new EventBusMsg.ImportObject(((FurnitureLookActivity) context).spacePosition));
                            }
                        }, 1500);

                    }
                };
                HttpClient.deleteLocation(context, map, listenner);
            }
        }, null);
    }

    /**
     * 编辑盒子
     *
     * @param position
     */
    private void editBox(final int position, final String name) {
        mFilterBoxList.get(position).setName(name);
        //修改盒子源，盒子源在哪里？
        boxAdapter.notifyDataSetChanged();
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("name", name);
        PostListenner listenner = new PostListenner(context) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
            }
        };
        HttpClient.editSpace(context, map, listenner, mFilterBoxList.get(position).getCode());
    }

    /**
     * 导入物品
     *
     * @param chooseMap
     */
    public void importObjects(Map<String, ObjectBean> chooseMap) {
        postImport(chooseMap);
    }

    public void show() {
        setVisibility(VISIBLE);
        AnimationUtil.upSlide(this, 300);
    }

    public void hide() {
        AnimationUtil.downSlide(this, 300);
    }

    /**
     * 导入物品
     */
    private void postImport(final Map<String, ObjectBean> chooseMap) {
        StringBuilder sb = new StringBuilder();
        for (ObjectBean bean : chooseMap.values()) {
            sb.append(bean.get_id()).append(",");
        }
        if (sb.length() != 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        String code;
        String location_code = "";
        List<BaseBean> locations;
        if (objectBean == null) {
            ToastUtil.show(context, "结构异常,请退出重新设置", Toast.LENGTH_SHORT);
            return;
        }
        if (boxAdapter.selectPostion == -1) {
            code = objectBean.getCode();
            if (objectBean.getParents().size() > 0) {
                location_code = objectBean.getParents().get(0).getCode();
            }
            locations = getLocations(objectBean, null);
        } else {
            if (boxAdapter.getSelectObject().getParents().size() > 0) {
                location_code = boxAdapter.getSelectObject().getParents().get(0).getCode();
            }
            code = boxAdapter.getSelectObject().getCode();
            locations = getLocations(objectBean, boxAdapter.getSelectObject());
        }
        for (ObjectBean object : chooseMap.values()) {
            object.setLocations(locations);
            object.setSelect(false);
            mList.add(object);
            filterList.add(object);
        }
        if (StringUtils.isEmpty(filterList)) {
            recyclerView.setVisibility(GONE);
            emtpyView.setVisibility(VISIBLE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            emtpyView.setVisibility(GONE);
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("user_id", MyApplication.getUser(context).getId());
        map.put("object_codes", sb.toString());
        map.put("code", code);
        map.put("location_code", location_code);
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                MyApplication.addGood = true;
                adapter.notifyDataSetChanged();
                List<ObjectBean> temp = MainLocationFragment.objectMap.get(MainLocationFragment.mlist.get(((FurnitureLookActivity) context).spacePosition).getCode());
                if (temp != null) {
                    temp.addAll(chooseMap.values());
                    EventBus.getDefault().post(new EventBusMsg.ImportObject(((FurnitureLookActivity) context).spacePosition));
                    EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                    int oldNum = SaveDate.getInstence(context).getGoodsNum(userId);
                    SaveDate.getInstence(context).setGoodsNum(userId, oldNum + chooseMap.size());
                }
                if (mCWListView.getVisibility() == View.VISIBLE) {
                    //全部勾选则关闭界面
                    if (mCWList.size() > 0) {
                        mCWListView.notifyData(null, -1);
                    } else {
                        if (finishListener != null) {
                            finishListener.finishActivity();
                        }
                    }
                }
            }
        };
        HttpClient.importObjects(context, map, listenner);
    }

    private List<BaseBean> getLocations(ObjectBean geceng, ObjectBean box) {
        List<BaseBean> locations = new ArrayList<>();
        //空间
        BaseBean space = new BaseBean();
        space.setName(MainLocationFragment.mlist.get(((FurnitureLookActivity) context).spacePosition).getName());
        space.set_id(MainLocationFragment.mlist.get(((FurnitureLookActivity) context).spacePosition).getId());
        space.setCode(MainLocationFragment.mlist.get(((FurnitureLookActivity) context).spacePosition).getCode());
        space.setLevel(0);
        locations.add(space);
        //家具
        BaseBean funiture = new BaseBean();
        funiture.setName(((FurnitureLookActivity) context).furnitureObject.getName());
        funiture.set_id(((FurnitureLookActivity) context).furnitureObject.get_id());
        funiture.setCode(((FurnitureLookActivity) context).furnitureObject.getCode());
        funiture.setLevel(1);
        locations.add(funiture);
        //隔层
        BaseBean gc = new BaseBean();
        gc.setName(geceng.getName());
        gc.set_id(geceng.get_id());
        gc.setCode(geceng.getCode());
        gc.setLevel(2);
        locations.add(gc);
        if (box != null) {
            //盒子
            BaseBean bx = new BaseBean();
            bx.setName(box.getName());
            bx.set_id(box.get_id());
            bx.setCode(box.getCode());
            bx.setLevel(3);
            locations.add(bx);
        }
        return locations;
    }

    /**
     * 获取家具详情
     */
    private void getDetailData() {
        final String cache = ACacheClient.getFurnitrueDetail(context, MyApplication.getUser(context).getId(), furnitureBean.getCode());
        if (!TextUtils.isEmpty(cache)) {
            mBoxList.clear();
            mFilterBoxList.clear();
            List<ObjectBean> tempBoxs = JsonUtils.listFromJsonWithSubKey(cache, ObjectBean.class, "locations");
            mBoxList.addAll(tempBoxs);
            notifyObject(null, null);
        }
        getNetDate(cache);
    }

    public boolean floatbox;

    /**
     * 获取网络数据
     */
    public void getNetDate(final String cache) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("code", furnitureBean.getCode());
        PostListenner listenner = new PostListenner(context, TextUtils.isEmpty(cache) ? Loading.show(null, context, "") : null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                ACacheClient.saveFurnitrueDetail(context, MyApplication.getUser(context).getId(), furnitureBean.getCode(), r.getResult());
                if (r.getResult().equals(cache) && !floatbox) {
                    return;
                }
                floatbox = false;
                mBoxList.clear();
                mFilterBoxList.clear();
                List<ObjectBean> tempBoxObjects = JsonUtils.listFromJsonWithSubKey(r.getResult(), ObjectBean.class, "locations");
                mBoxList.addAll(tempBoxObjects);
                notifyInducation(objectBean);
            }
        };
        HttpClient.getFurnitureDetail(context, map, listenner);
    }

    /**
     * 动画提示物品位置
     *
     * @param bean
     */
    public void findObject(ObjectBean bean) {
        ((FurnitureLookActivity) context).selectObject = bean;
        for (int i = 0; i < mFilterBoxList.size(); i++) {
            for (int j = 0; j < StringUtils.getListSize(bean.getLocations()); j++) {
                if (mBoxList.get(i).getCode().equals(bean.getLocations().get(j).getCode())) {
                    indicatorListView.scrollToPosition(i);
                    if (boxAdapter.selectPostion == i) {
                        return;
                    } else {
                        try {
                            final int finalI = i;
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    for (int k = 0; k < indicatorListView.getChildCount(); k++) {
                                        if (indicatorListView.getChildAdapterPosition(indicatorListView.getChildAt(k)) == finalI) {
                                            ShowCaseUtil.showCaseForObject(((Activity) context), indicatorLayout, indicatorListView.getChildAt(k));
                                            AnimationUtil.StartTada(indicatorListView.getChildAt(k), 1.0f);
                                        }
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
            }
        }
        if (indicatorLayout.getVisibility() == VISIBLE && (!mFilterBoxList.isEmpty())) {
            ShowCaseUtil.showCaseForObject(((Activity) context), indicatorLayout, title);
            AnimationUtil.StartTranslate(title);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.getLocationObjectsMsg msg) {
        if (msg.position == ((FurnitureLookActivity) context).spacePosition && mCWListView.getVisibility() != View.VISIBLE) {
            mList.clear();
            mList.addAll(msg.objectList);
            notifyObject(objectBean, boxAdapter.getSelectObject());
        }
    }

    /**
     * 当前选择的物品code
     *
     * @return
     */
    public String getSelectObjectId() {
        return ((FurnitureLookActivity) context).selectObject == null ? "" : ((FurnitureLookActivity) context).selectObject.get_id();
    }

    public interface OnitemClickLisener {
        void itemClick(int position, ObjectBean bean, View view);
    }

    public interface OnFinishActivityLisener {
        void finishActivity();
    }


    private FloatShowListener floatShowListener;

    public void setFloatShow(FloatShowListener floatShowListener) {
        this.floatShowListener = floatShowListener;
    }

    public interface FloatShowListener {
        void floatShow(ObjectBean select, boolean show);
    }
}
