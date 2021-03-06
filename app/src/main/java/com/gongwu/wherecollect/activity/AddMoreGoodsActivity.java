package com.gongwu.wherecollect.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.volley.request.QiNiuUploadUtil;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.AddMoreGoodsListAdapter;
import com.gongwu.wherecollect.adapter.MyOnItemClickListener;
import com.gongwu.wherecollect.adapter.StackAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.BookBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.importObject.ImportSelectFurnitureActivity;
import com.gongwu.wherecollect.object.AddGoodsActivity;
import com.gongwu.wherecollect.swipecardview.SwipeFlingAdapterView;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.AddGoodsDialog;
import com.gongwu.wherecollect.view.SelectImgDialog;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zhaojin.myviews.Loading;
import com.zsitech.oncon.barcode.core.CaptureActivity;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 批量添加
 */
public class AddMoreGoodsActivity extends BaseViewActivity implements SwipeFlingAdapterView.onFlingListener {

    private final int BOOK_CODE = 0x132;
    /**
     * 判断AddMoreGoodsActivity是否显示
     * 显示的时候,AddGoodsActivity不接收eventBus发送的淘宝商品信息
     */
    public static boolean START_MORE_ACTIVITY = false;

    @Bind(R.id.textBtn)
    TextView addGoodsTv;
    @Bind(R.id.more_goods_list_view)
    SwipeMenuRecyclerView mListView;
    @Bind(R.id.more_commit_btn)
    Button commitBtn;
    //    @Bind(R.id.swipe_view)
    SwipeFlingAdapterView mSwipeView;
    @Bind(R.id.content_layout)
    RelativeLayout contentLayout;

    private AddGoodsDialog mDialog;
    private List<ObjectBean> mDatas;

    private AddMoreGoodsListAdapter mAdapter;
    /**
     * 记录点击的item
     */
    private int currentItem = -1;

    private StackAdapter mStackAdapter;

    private List<ObjectBean> selectImgs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_goods);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.add_more_goods_text));
        mDatas = new ArrayList<>();
        EventBus.getDefault().register(this);
        START_MORE_ACTIVITY = true;
        initView();
        initEvent();
        List<String> list = getIntent().getStringArrayListExtra("imgList");
        if (list != null && list.size() > 0) {
            // 为AdapterViewFlipper设置Adapter
            initSwipeViewAdapter();
            initSwipeView();
            upLoadSelectImgs(list);
            addGoodsTv.setEnabled(false);
            commitBtn.setEnabled(false);
        } else {
            startDialog(null);
        }
    }

    private void initSwipeViewAdapter() {
        mStackAdapter = new StackAdapter(context, selectImgs) {
            @Override
            public void selectItem(boolean select, String name, String url) {
                //点击确定设置缓解文件为null
                imgOldFile= null;
                if (select) {
                    ObjectBean objectBean = new ObjectBean();
                    objectBean.setObject_url(url);
                    objectBean.setName(name);
                    upLoadImg(objectBean);
                }
                mSwipeView.swipeRight();
            }

            @Override
            public void onClickCamera() {
                qrBook();
            }

            @Override
            public void selectImage(int position) {
                //编辑图片判断文件是否为null
                if (imgOldFile==null){
                    imgOldFile = new File(selectImgs.get(position).getObject_url());
                }
                showSelectDialog();
            }
        };
    }

    /**
     * 图片选择
     */
    private SelectImgDialog selectImgDialog;
    private File imgOldFile;
    private void showSelectDialog() {
        selectImgDialog = new SelectImgDialog((Activity) context, null, 1, imgOldFile) {
            @Override
            public void getResult(List<File> list) {
                super.getResult(list);
                //相册获取的图片
                imgOldFile = list.get(0);
                selectImgDialog.cropBitmap(imgOldFile);
            }

            @Override
            protected void resultFile(File file) {
                super.resultFile(file);
                //剪切后的图片
                selectImgs.get(0).setObject_url(file.getAbsolutePath());
                initSwipeView();
            }
        };
        selectImgDialog.hintLayout();
        //编辑选择是否隐藏的 根据imgOldFile来判断
        selectImgDialog.showEditIV(imgOldFile == null ? View.GONE : View.VISIBLE);
    }

    private void initSwipeView() {
        if (mSwipeView != null) {
            contentLayout.removeView(mSwipeView);
        }
        mSwipeView = new SwipeFlingAdapterView(context);
        mSwipeView.setMaxVisible(4);
        mSwipeView.setMinStackInAdapter(4);
        contentLayout.addView(mSwipeView);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSwipeView.getLayoutParams();
        int topDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
        lp.setMargins(0, topDip, 0, 0);
        lp.height = 1600;
        mSwipeView.setLayoutParams(lp);
        mSwipeView.setAdapter(mStackAdapter);
        mSwipeView.setIsNeedSwipe(true);
        mSwipeView.setFlingListener(this);
    }

    private void initView() {
        addGoodsTv.setText("添加");
        addGoodsTv.setVisibility(View.VISIBLE);
    }

    private void initEvent() {
        mAdapter = new AddMoreGoodsListAdapter(context, mDatas);
        mAdapter.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(int positions, View view) {
                currentItem = positions;
                startDialog(mDatas.get(positions));
            }
        });
        // 创建菜单：
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(AddMoreGoodsActivity.this);
                // 各种文字和图标属性设置。
                deleteItem.setBackground(new ColorDrawable(Color.RED));
                deleteItem.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                deleteItem.setWidth((int) (60 * BaseViewActivity.getScreenScale(AddMoreGoodsActivity.this)));
                deleteItem.setText("删除");
                deleteItem.setTextColor(Color.WHITE);
                deleteItem.setTextSize(15);
                rightMenu.addMenuItem(deleteItem); // 在Item左侧添加一个菜单。
            }
        };
        mListView.setSwipeMenuCreator(mSwipeMenuCreator);
        // 菜单点击监听。
        SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu();
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                mDatas.remove(adapterPosition);
                mAdapter.notifyDataSetChanged();
            }
        };
        mListView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        mListView.setItemViewSwipeEnabled(false);// 开启滑动删除。
        //设置默认的布局方式
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置adapter
        mListView.setAdapter(mAdapter);
    }

    @OnClick({R.id.textBtn, R.id.more_commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textBtn:
                startDialog(null);
                break;
            case R.id.more_commit_btn:
                if (mDatas.size() > 0) {
                    initImgUrls();
                } else {
                    Toast.makeText(context, "请先添加物品", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //整理参数跳转到共同属性界面
    private void initImgUrls() {
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> files = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            ObjectBean bean = mDatas.get(i);
            name.add(bean.getName());
            files.add(bean.getObject_url());
        }
        AddGoodsOtherContentActivity.start(context, name, files, AddGoodsActivity.MORE_TYPE);
        FileUtil.deleteFolderFiles(MyApplication.CACHEPATH,false);
        finish();
    }

    /**
     * 添加物品的dialog
     */
    private void startDialog(ObjectBean objectBean) {
        if (mDialog == null) {
            //添加
            mDialog = new AddGoodsDialog(context, mDatas.size()) {
                @Override
                public void result(ObjectBean bean) {
                    //上传
                    if (!TextUtils.isEmpty(bean.getObject_url()) && !bean.getObject_url().contains("7xroa4") && !bean.getObject_url().contains("#") && !bean.getObject_url().contains("cdn.shouner.com/object/image")) {
                        upLoadImg(bean);
                        return;
                    }
                    //currentItem不为默认值时，修改记录的item的值
                    if (currentItem != -1) {
                        mDatas.set(currentItem, bean);
                        currentItem = -1;
                    } else {
                        //为默认值，就是新添加的
                        mDatas.add(bean);
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void results(List<ObjectBean> beans) {
                    upLoadImgs(beans);
                }

                @Override
                public void cancel() {
                    currentItem = -1;
                }

                @Override
                public void scanCode() {
                    qrBook();
                }
            };
        }
        mDialog.show();
        mDialog.setObjectBean(objectBean);
    }

    /**
     * 上传图片
     */
    private void upLoadImg(final ObjectBean objectBean) {
        List<File> list = new ArrayList<>();
        list.add(new File(objectBean.getObject_url()));
        loading = Loading.show(loading, this, "加载中...");
        QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(this, list, "object/image/") {
            @Override
            protected void finish(List<String> list) {
                super.finish(list);
                //currentItem不为默认值时，修改记录的item的值
                objectBean.setObject_url(list.get(0));
//                if (selectImgs != null && selectImgs.size() > 0) {
//                    selectImgs.remove(0);
//                    selectImgs.add(0, objectBean);
//                    initSwipeView();
//                } else {
                if (currentItem != -1) {
                    mDatas.set(currentItem, objectBean);
                    currentItem = -1;
                } else {
                    //为默认值，就是新添加的
                    mDatas.add(objectBean);
                }
                mAdapter.notifyDataSetChanged();
//                }
                if (loading != null) {
                    loading.dismiss();
                }
            }
        };
        uploadUtil.start();
    }

    /**
     * 上传图片
     */
    private Loading loading;

    private void upLoadImgs(final List<ObjectBean> objectBeans) {
        List<File> list = new ArrayList<>();
        for (ObjectBean bean : objectBeans) {
            list.add(new File(bean.getObject_url()));
        }
        loading = Loading.show(loading, this, "上传中...");
        QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(this, list, "object/image/") {
            @Override
            protected void finish(List<String> list) {
                super.finish(list);
                for (int i = 0; i < list.size(); i++) {
                    objectBeans.get(i).setObject_url(list.get(i));
                }
                mDatas.addAll(objectBeans);
                mAdapter.notifyDataSetChanged();
                if (loading != null) {
                    loading.dismiss();
                }
            }
        };
        uploadUtil.start();
    }

    private void upLoadSelectImgs(List<String> strings) {
        for (String s : strings) {
            ObjectBean objectBean = new ObjectBean();
            objectBean.setObject_url(s);
            selectImgs.add(objectBean);
        }
        mStackAdapter.notifyDataSetChanged();
    }

    /**
     * 图书扫描
     */
    private void qrBook() {
        startActivityForResult(new Intent(context, CaptureActivity.class), BOOK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mDialog != null) {
            mDialog.onActivityResult(requestCode, resultCode, data);
        }
        if (selectImgDialog != null) {
            selectImgDialog.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == BOOK_CODE && resultCode == CaptureActivity.result) {//扫描的到结果
            String result = data.getStringExtra("result");
            if (result.contains("http")) {
                //网络商城
                importBuy(result);
            } else {
                //书本
                getBookInfo(result);
            }
        }
    }

    /**
     * 获取书本信息
     *
     * @param qrStr
     */
    private void getBookInfo(String qrStr) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("ISBN", qrStr);
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                final BookBean book = JsonUtils.objectFromJson(r.getResult(), BookBean.class);
                new Thread(new Runnable() {//下载图片
                    @Override
                    public void run() {
                        try {
                            File file = Glide.with(context).load(book.getPic()).downloadOnly(500, 500).get();
                            String newPath = MyApplication.CACHEPATH + System.currentTimeMillis() + ".jpg";
                            FileUtil.copyFile(file, newPath);
                            file = new File(newPath);
                            book.setImageFile(file);
                            AddMoreGoodsActivity.this.runOnUiThread(new Runnable() {//回主线程
                                @Override
                                public void run() {
                                    updateBeanWithBook(book);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            protected void codeOther(ResponseResult r) {
                super.codeOther(r);
                ToastUtil.showTopToast(context, "获取商品信息失败");
                Intent intent = new Intent(context, ImportHelpActivity.class);
                context.startActivity(intent);
            }
        };
        HttpClient.getBookInfo(context, map, listenner);
    }

    /**
     * 导入网购商品
     */
    private void importBuy(String str) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("key", str);
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                final BookBean book = JsonUtils.objectFromJson(r.getResult(), BookBean.class);
                if (book == null || TextUtils.isEmpty(book.getPic())) {
                    ToastUtil.showTopToast(context, "获取商品信息失败");
                    Intent intent = new Intent(context, ImportHelpActivity.class);
                    context.startActivity(intent);
                    return;
                }
                new Thread(new Runnable() {//下载图片
                    @Override
                    public void run() {
                        try {
                            File file = Glide.with(context).load(book.getPic()).downloadOnly(500, 500).get();
                            String newPath = MyApplication.CACHEPATH + System.currentTimeMillis() + ".jpg";
                            FileUtil.copyFile(file, newPath);
                            file = new File(newPath);
                            book.setImageFile(file);
                            AddMoreGoodsActivity.this.runOnUiThread(new Runnable() {//回主线程
                                @Override
                                public void run() {
                                    updateBeanWithBook(book);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            protected void codeOther(ResponseResult r) {
                super.codeOther(r);
                ToastUtil.showTopToast(context, "获取商品信息失败");
                Intent intent = new Intent(context, ImportHelpActivity.class);
                context.startActivity(intent);
            }
        };
        HttpClient.getTaobaoInfo(context, map, listenner);
    }

    /**
     * 图书扫描后修改公共属性
     */
    String ISBN = "";

    private void updateBeanWithBook(BookBean book) {
        if (book == null || book.getImageFile() == null) {
            return;
        }
        ISBN = book.getIsbnCode();
        ObjectBean tempBean = new ObjectBean();
        if (book.getImageFile() != null) {
            tempBean.setObject_url(book.getImageFile().getAbsolutePath());
        }
        if (!TextUtils.isEmpty(book.getTitle())) {
            tempBean.setName(book.getTitle());
        }
        if (!TextUtils.isEmpty(book.getSummary())) {
            tempBean.setDetail(book.getSummary());
        }
        if (!TextUtils.isEmpty(book.getChannel())) {
            tempBean.setChannel(book.getChannel());
        }
        if (!TextUtils.isEmpty(book.getPrice())) {
            tempBean.setPrice(book.getPrice());
        }
        if (book.getCategory() != null) {
            List<BaseBean> temp = new ArrayList<>();
            temp.add(book.getCategory());
            tempBean.setCategories(temp);
        }
        if (selectImgs != null && selectImgs.size() > 0) {
            upLoadImg(tempBean);
        } else if (mDialog != null) {
            mDialog.setObjectBean(tempBean);
            mDialog.show();
        }
    }

    public static void start(Context context, ObjectBean objectBean) {
        Intent intent = new Intent(context, AddMoreGoodsActivity.class);
        if (objectBean != null) {
            intent.putExtra("bean", objectBean);
        }
        context.startActivity(intent);
    }

    public static void start(Context context, ArrayList<String> lists) {
        Intent intent = new Intent(context, AddMoreGoodsActivity.class);
        if (lists != null) {
            intent.putStringArrayListExtra("imgList", lists);
        }
        context.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ImportFromBugSucces msg) {
        updateBeanWithBook(msg.bean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        START_MORE_ACTIVITY = false;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void removeFirstObjectInAdapter() {

    }

    @Override
    public void onLeftCardExit(Object dataObject) {

    }

    @Override
    public void onRightCardExit(Object dataObject) {
        mStackAdapter.remove(0);
        if (selectImgs.size() == 0) {
            mSwipeView.setVisibility(View.GONE);
            selectImgDialog = null;
            addGoodsTv.setEnabled(true);
            commitBtn.setEnabled(true);
        }
    }

    @Override
    public void onAdapterAboutToEmpty(int itemsInAdapter) {

    }

    @Override
    public void onScroll(float progress, float scrollXProgress) {

    }
}
