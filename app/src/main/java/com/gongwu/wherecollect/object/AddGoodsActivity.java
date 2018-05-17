package com.gongwu.wherecollect.object;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.volley.request.QiNiuUploadUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.AddGoodsOtherContentActivity;
import com.gongwu.wherecollect.activity.AddMoreGoodsActivity;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.activity.ImportHelpActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.BookBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.importObject.ImportSelectFurnitureActivity;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.GoodsImageView;
import com.gongwu.wherecollect.view.ObjectInfoLookView;
import com.gongwu.wherecollect.view.SelectImgDialog;
import com.zhaojin.myviews.Loading;
import com.zsitech.oncon.barcode.core.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * 添加物品界面
 */
public class AddGoodsActivity extends BaseViewActivity {
    @Bind(R.id.textBtn)
    TextView addMoreTv;
    @Bind(R.id.goods_name_et)
    EditText goodsNameEv;
    @Bind(R.id.commit_btn)
    Button commitBtn;
    @Bind(R.id.camera_iv)
    GoodsImageView cameraIv;
    @Bind(R.id.goodsInfo_view)
    ObjectInfoLookView goodsInfoView;
    @Bind(R.id.add_other_content_edit_layout)
    LinearLayout otherEditLayout;
    @Bind(R.id.add_other_content_layout)
    LinearLayout otherLayout;
    @Bind(R.id.head)
    ImageView head;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.goods_name_and_image_layout)
    LinearLayout goods_name_and_image_layout;
    @Bind(R.id.add_shopping_layout)
    LinearLayout add_shopping_layout;
    @Bind(R.id.add_code_layout)
    LinearLayout add_code_layout;

    private ObjectBean tempBean = new ObjectBean();
    private File imgFile;
    private File imgOldFile;
    private final int imgMax = 1;
    private final int BOOK_CODE = 1;
    private final int OTHER_CODE = 0x123;
    private final int MORE_CODE = 0x124;
    public static final String MORE_TYPE = "more_type";
    private Loading loading;
    private int type;//1为导入物品列表跳过来的，添加完了要返回去；
    private int editGoodsType = 0;//0 为添加物品  1为编辑物品
    private ObjectBean newBean;
    private final String IMG_COLOR_CODE = 0 + "";//默认图片颜色的值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.add_goods_text));
        type = getIntent().getIntExtra("type", 0);
        EventBus.getDefault().register(this);
        initView();
        initEvent();
        initData();
    }

    /**
     * 初始化数据，判断是否是直接添加物品，还是编辑物品
     */
    private void initData() {
        ObjectBean bean = (ObjectBean) getIntent().getSerializableExtra("bean");
        if (bean != null) {
            editGoodsType = 1;
            tempBean = bean;
            commitBtn.setText("确认更改");
            titleLayout.setTitle("编辑");
            //隐藏批量按钮
            addMoreTv.setVisibility(View.GONE);
            goodsInfoView.setVisibility(View.VISIBLE);
            goodsInfoView.setLocationlayoutVisibility(true);
            goodsInfoView.init(tempBean);
            showOtherEditLayout();
            //设置物品名字
            if (!TextUtils.isEmpty(tempBean.getName())) {
                goodsNameEv.setText(tempBean.getName());
            }
            //设置图片
            if (!TextUtils.isEmpty(tempBean.getObject_url())) {
                if (tempBean.getObject_url().contains("http")) {
                    setCameraIvParams(100);
                    cameraIv.setHead(IMG_COLOR_CODE, "", tempBean.getObject_url());
                    downloadImage();
                } else if (tempBean.getObject_url().contains("#")) {
                    setCameraIvParams(100);
                    cameraIv.name.setVisibility(View.VISIBLE);
                    cameraIv.name.setText(tempBean.getName());
                    cameraIv.head.setImageDrawable(null);
                    cameraIv.head.setBackgroundColor(Color.parseColor(tempBean.getObject_url()));
                }
            }
        } else {
            editGoodsType = 0;
            showHelp();
        }
    }

    private void downloadImage() {
        new Thread(new Runnable() {//下载图片
            @Override
            public void run() {
                try {
                    File file = Glide.with(context).load(tempBean.getObject_url()).downloadOnly(500, 500).get();
                    String newPath = MyApplication.CACHEPATH + System.currentTimeMillis() + ".jpg";
                    FileUtil.copyFile(file, newPath);
                    imgOldFile = new File(newPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        addMoreTv.setVisibility(View.VISIBLE);
        addMoreTv.setText(getResources().getString(R.string.add_more_text));
        head.setImageDrawable(getResources().getDrawable(R.drawable.camera));
    }

    /**
     * 控件监听
     */
    private void initEvent() {
        goodsNameEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (goodsNameEv.getText().toString().trim().length() > 0) {
                    setCommitBtnEnable(true);
                    setCameraIv(true);
                } else {
                    setCommitBtnEnable(false);
                    setCameraIv(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 监听物品edit，动态设置默认图片
     *
     * @param isSet
     */
    private void setCameraIv(boolean isSet) {
        if (isSet && TextUtils.isEmpty(tempBean.getObject_url())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCameraIvParams(100);
                    //未添加图片的时候，给个默认图片
                    //IMG_COLOR_CODE默认图片颜色的值
                    cameraIv.setHead(IMG_COLOR_CODE, goodsNameEv.getText().toString().trim(), "");
                }
            }, 1000);
        } else if (!isSet && TextUtils.isEmpty(tempBean.getObject_url())) {
            setCameraIvParams(30);
            head.setImageDrawable(getResources().getDrawable(R.drawable.camera));
            name.setText("");
        } else if (isSet && !TextUtils.isEmpty(tempBean.getObject_url())) {
            if (tempBean.getObject_url().contains("#")) {
                cameraIv.name.setText(goodsNameEv.getText().toString().trim());
            }
        }

    }

    @OnClick({R.id.add_shopping_layout, R.id.add_code_layout, R.id.camera_iv,
            R.id.add_other_content_tv, R.id.commit_btn, R.id.add_other_content_edit_iv, R.id.textBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_shopping_layout:
                //一键导入
                initTBData();
                break;
            case R.id.add_code_layout:
                //图书扫码
                qrBook();
                break;
            case R.id.camera_iv:
                //相机
                showSelectDialog();
                break;
            case R.id.add_other_content_tv:
            case R.id.add_other_content_edit_iv:
                startAddGoodsOtherContentActivity();
                break;
            case R.id.commit_btn:
                clickCommitBtn();
                break;
            case R.id.textBtn:
                startAddMoreGoodsActivity();
                break;
        }
    }

    /**
     * 导入网络商城数据
     */
    private void initTBData() {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData cd2 = cm.getPrimaryClip();
        String str = cd2.getItemAt(0).getText().toString();
        importBuy(str);
    }

    /**
     * 批量添加界面
     */
    private void startAddMoreGoodsActivity() {
        Intent addMoreIntent = new Intent(context, AddGoodsOtherContentActivity.class);
        if (tempBean != null) {
            addMoreIntent.putExtra("tempBean", tempBean);
            addMoreIntent.putExtra("type", MORE_TYPE);
        }
        startActivityForResult(addMoreIntent, MORE_CODE);
    }

    /**
     * 跳转添加其他属性界面
     */
    private void startAddGoodsOtherContentActivity() {
        //其他属性
        Intent intent = new Intent(context, AddGoodsOtherContentActivity.class);
        if (tempBean != null) {
            intent.putExtra("tempBean", tempBean);
        }
        startActivityForResult(intent, OTHER_CODE);
    }

    /**
     * 点击确认
     */
    private void clickCommitBtn() {
        //确定添加
        loading = Loading.show(loading, this, "加载中...");
        //编辑物品
        if (editGoodsType == 1) {
            //判断图片是否更改，没更改的情况下 图片地址应该为网络路径
            if (!tempBean.getObject_url().contains("http") && !tempBean.getObject_url().contains("#")) {
                //图片有更改，先上传
                upLoadImg(tempBean.getObjectFiles());
            } else {
                //无修改图片时，直接调用修改其他属性的接口
                addObject();
            }
            return;
        }
        //如果图片没有地址，则传一个颜色服务牌
        if (TextUtils.isEmpty(tempBean.getObject_url())) {
            //调用接口
            tempBean.setObject_url("#B5B5B5");
            addObjects();
        } else if (tempBean.getObject_url().contains("http")) {
            addObjects();
        } else {
            //图片有地址 直接上传
            upLoadImg(tempBean.getObjectFiles());
        }
    }

    /**
     * 上传图片
     */
    private void upLoadImg(List<File> list) {
        QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(this, list, "object/image/") {
            @Override
            protected void finish(List<String> list) {
                super.finish(list);
                if (editGoodsType == 1) {
                    tempBean.setObject_url(list.get(0));
                    addObject();
                } else {
                    tempBean.setObject_url(list.get(0));
                    addObjects();
                }
            }
        };
        uploadUtil.start();
    }

    /**
     * 添加物品
     */
    private void addObjects() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("ISBN", ISBN);
        map.put("category_codes", StringUtils.isEmpty(tempBean.getCategories()) ? "" : tempBean.getCategories().get
                (tempBean.getCategories().size() - 1).getCode());
        map.put("channel", TextUtils.isEmpty(tempBean.getChannel()) ? "" : JsonUtils.jsonFromObject(tempBean
                .getChannel().split(">")));
        map.put("color", TextUtils.isEmpty(tempBean.getColor()) ? "" : JsonUtils.jsonFromObject(tempBean
                .getColor().split("、")));
        map.put("detail", TextUtils.isEmpty(tempBean.getDetail()) ? "" : tempBean.getDetail());
        map.put("price_max", tempBean.getPrice() + "");
        map.put("price_min", tempBean.getPrice() + "");
        map.put("season", tempBean.getSeason());
        map.put("star", tempBean.getStar() + "");
        List<String> names = new ArrayList<>();
        names.add(goodsNameEv.getText().toString());
        map.put("name", JsonUtils.jsonFromObject(names));
        List<String> files = new ArrayList<>();
        files.add(tempBean.getObject_url());
        map.put("image_urls", JsonUtils.jsonFromObject(files));
        map.put("count", tempBean.getObject_count() + "");
        map.put("buy_date", tempBean.getBuy_date());
        map.put("expire_date", tempBean.getExpire_date());
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                if (type == 0) {
                    Intent intent = new Intent(context, ImportSelectFurnitureActivity.class);
                    startActivity(intent);
                } else {
                    setResult(RESULT_OK);
                }
                finish();
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                if (loading != null) {
                    loading.dismiss();
                }
            }
        };
        HttpClient.addObjects(this, map, listenner);
    }

    /**
     * 编辑其实是先添加一条，再把老的删除
     */
    private void addObject() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("detail", tempBean.getDetail());
        map.put("image_url", TextUtils.isEmpty(tempBean.getObject_url()) ? "#B5B5B5" : tempBean.getObject_url());//B5B5B5
        map.put("object_count", tempBean.getObject_count() + "");
        map.put("price_max", tempBean.getPrice() + "");
        map.put("price_min", tempBean.getPrice() + "");
        map.put("season", tempBean.getSeason());
        map.put("code", tempBean.get_id());
        map.put("name", goodsNameEv.getText().toString().trim());
        map.put("star", tempBean.getStar() + "");
        map.put("coordinates", JsonUtils.jsonFromObject(tempBean.getCoordinates()));
        StringBuilder ca = new StringBuilder();
        for (int i = 0; i < StringUtils.getListSize(tempBean.getCategories()); i++) {
            ca.append(tempBean.getCategories().get(i).getCode());
            if (i != tempBean.getCategories().size() - 1) {
                ca.append(",");
            }
        }
        map.put("category_codes", ca.toString());
        StringBuilder lc = new StringBuilder();
        for (int i = 0; i < StringUtils.getListSize(tempBean.getLocations()); i++) {
            lc.append(tempBean.getLocations().get(i).getCode());
            if (i != tempBean.getLocations().size() - 1) {
                lc.append(",");
            }
        }
        map.put("location_codes", lc.toString());
        map.put("channel", JsonUtils.jsonFromObject(tempBean.getChannel().split(">")));
        map.put("color", JsonUtils.jsonFromObject(tempBean.getColor().split("、")));
        map.put("buy_date", tempBean.getBuy_date());
        map.put("expire_date", tempBean.getExpire_date());
        PostListenner listenner = new PostListenner(this, Loading.show(null, this,
                "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                newBean = JsonUtils.objectFromJson(r.getResult(), ObjectBean.class);
                editCacheGoods(newBean, tempBean.get_id());
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                Intent intent = new Intent();
                intent.putExtra("bean", newBean);
                setResult(100, intent);
                finish();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                if (loading != null) {
                    loading.dismiss();
                }
            }
        };
        HttpClient.getAddObject(this, map, listenner);
    }

    private void editCacheGoods(ObjectBean newBean, String code) {
        for (List<ObjectBean> beanList : MainLocationFragment.objectMap.values()) {
            for (int i = 0; i < beanList.size(); i++) {
                ObjectBean objectBean = beanList.get(i);
                if (objectBean.get_id().equals(code)) {
                    objectBean.setExpire_date(newBean.getExpire_date());
                    objectBean.setBuy_date(newBean.getBuy_date());
                    objectBean.setObject_url(newBean.getObject_url());
                    objectBean.setStar(newBean.getStar());
                    objectBean.setObject_count(newBean.getObject_count());
                    objectBean.setName(newBean.getName());
                    objectBean.setCategories(newBean.getCategories());
                    objectBean.setPrice(newBean.getPrice());
                    objectBean.setColor(newBean.getColor());
                    objectBean.setSeason(newBean.getSeason());
                    objectBean.setChannel(newBean.getChannel());
                    objectBean.setDetail(newBean.getDetail());
                    objectBean.set_id(newBean.get_id());
                    EventBus.getDefault().post(EventBusMsg.REFRESH_GOODS);
                }
            }
        }

    }

    SelectImgDialog selectImgDialog;

    /**
     * 图片选择dialog
     */
    private void showSelectDialog() {
        if (imgOldFile != null && TextUtils.isEmpty(tempBean.getObject_url())) {
            imgOldFile = null;
        }
        selectImgDialog = new SelectImgDialog(this, null, imgMax, imgOldFile) {
            @Override
            public void getResult(List<File> list) {
                super.getResult(list);
                imgOldFile = list.get(0);
                selectImgDialog.cropBitmap(imgOldFile);
            }

            @Override
            protected void resultFile(File file) {
                super.resultFile(file);
                imgFile = file;
                setCameraIvParams(100);
                cameraIv.setHead(IMG_COLOR_CODE, "", imgFile.getAbsolutePath());
                tempBean.setObject_url(imgFile.getAbsolutePath());
                setCommitBtnEnable(true);
            }
        };
        selectImgDialog.hintLayout();
        //编辑选择是否隐藏的 根据imgOldFile来判断
        selectImgDialog.showEditIV(imgOldFile == null ? View.GONE : View.VISIBLE);
    }


    /**
     * 图书扫描
     */
    private void qrBook() {
        startActivityForResult(new Intent(context, CaptureActivity.class), BOOK_CODE);
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
                            AddGoodsActivity.this.runOnUiThread(new Runnable() {//回主线程
                                @Override
                                public void run() {
                                    DialogUtil.show("提醒", "此操作会将部分共同属性刷新，是否继续?", "继续", "取消", (Activity) context, new
                                            DialogInterface.OnClickListener
                                                    () {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    getBookResult(book);
                                                }
                                            }, null);
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

            @Override
            protected void error() {
                super.error();
                ToastUtil.showTopToast(context, "获取商品信息失败");
                Intent intent = new Intent(context, ImportHelpActivity.class);
                context.startActivity(intent);
            }
        };
        HttpClient.getTaobaoInfo(context, map, listenner);
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
                            AddGoodsActivity.this.runOnUiThread(new Runnable() {//回主线程
                                @Override
                                public void run() {
                                    DialogUtil.show("提醒", "此操作会将部分共同属性刷新，是否继续?", "继续", "取消", (Activity) context, new
                                            DialogInterface.OnClickListener
                                                    () {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    getBookResult(book);
                                                }
                                            }, null);
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
     * 图书扫描后修改公共属性
     */
    String ISBN = "";

    public void updateBeanWithBook(BookBean book) {
        if (book == null || book.getImageFile() == null) {
            return;
        }
        ISBN = book.getIsbnCode();
        if (editGoodsType != 1) {
            tempBean = new ObjectBean();
        }
        if (book.getImageFile() != null) {
            imgOldFile = book.getImageFile();
            setCameraIvParams(100);
            cameraIv.setHead(IMG_COLOR_CODE, "", imgOldFile.getAbsolutePath());
            tempBean.setObject_url(imgOldFile.getAbsolutePath());
            setCommitBtnEnable(true);
        }
        if (!TextUtils.isEmpty(book.getTitle())) {
            goodsNameEv.setText(book.getTitle());
            showOtherEditLayout();
        }
        if (!TextUtils.isEmpty(book.getSummary())) {
            tempBean.setDetail(book.getSummary());
            showOtherEditLayout();
        }
        if (!TextUtils.isEmpty(book.getChannel())) {
            tempBean.setChannel(book.getChannel());
            showOtherEditLayout();
        }
        if (!TextUtils.isEmpty(book.getPrice())) {
            tempBean.setPrice(book.getPrice());
            showOtherEditLayout();
        }
        if (book.getCategory() != null) {
            List<BaseBean> temp = new ArrayList<>();
            temp.add(book.getCategory());
            tempBean.setCategories(temp);
            showOtherEditLayout();
        }
        goodsInfoView.setVisibility(View.VISIBLE);
        goodsInfoView.init(tempBean);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        if (resultCode == 100) {
            tempBean = (ObjectBean) data.getSerializableExtra("bean");
            goodsInfoView.init(tempBean);
        }
        if (requestCode == OTHER_CODE && resultCode == RESULT_OK) {
            tempBean = (ObjectBean) data.getSerializableExtra("tempBean");
            goodsInfoView.init(tempBean);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (goodsInfoView.getVisibility() == View.VISIBLE) {
                        showOtherEditLayout();
                    }
                }
            }, 200);
        }
    }


    private void setCameraIvParams(int dp) {
        ViewGroup.LayoutParams params = cameraIv.getLayoutParams();
        params.width = StringUtils.pxConvertDp(dp, context);
        params.height = StringUtils.pxConvertDp(dp, context);
        cameraIv.setLayoutParams(params);
    }

    /**
     * 重写用来回调图书二维码的扫描
     *
     * @param book
     */
    protected void getBookResult(BookBean book) {
        updateBeanWithBook(book);
    }

    /**
     * 设置提交按钮接收事件状态
     *
     * @param btnEnable
     */
    private void setCommitBtnEnable(boolean btnEnable) {
        commitBtn.setEnabled(btnEnable);
    }

    /**
     * 跳转到NewObjectsAddActivity
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, AddGoodsActivity.class);
        context.startActivity(intent);
    }

    private void showOtherEditLayout() {
        otherLayout.setVisibility(View.GONE);
        otherEditLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String str) {
        if (EventBusMsg.ACTIVITY_FINISH.contains(str)) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ImportFromBugSucces msg) {
        if (!AddMoreGoodsActivity.START_MORE_ACTIVITY) {
            updateBeanWithBook(msg.bean);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 首次引导
     */
    private void showHelp() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setContentTextColor(getResources().getColor(R.color.white));
        config.setMaskColor(getResources().getColor(R.color.black_87));
        config.setDelay(200); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "addGoods");
        sequence.setConfig(config);
        MaterialShowcaseView sequenceItem1 = (new MaterialShowcaseView.Builder(this)).setTarget(goods_name_and_image_layout)
                .setContentText("名称和图片至少添加一项")
                .setTargetTouchable(false)
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTargetTouch(true)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem1);
        MaterialShowcaseView sequenceItem2 = (new MaterialShowcaseView.Builder(this))
                .setTarget(add_shopping_layout).
                        setContentText("复制链接导入\n复制淘口令、天猫和京东链\n接，自动抓取物品数据")
                .setTargetTouchable(false)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setDismissOnTargetTouch(false)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem2);
        MaterialShowcaseView sequenceItem3 = (new MaterialShowcaseView.Builder(this))
                .setTarget(add_code_layout).
                        setContentText("扫条码导入\n支持图书扫码，及电脑端淘宝\n、天猫、京东网页商品二维\n码扫描")
                .setTargetTouchable(false)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setDismissOnTargetTouch(false)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem3);
        MaterialShowcaseView sequenceItem4 = (new MaterialShowcaseView.Builder(this))
                .setTarget(otherLayout)
                .setContentText("其他属性\n为方便筛选查找，还可添加数\n量、时间、分类、购获渠道等\n更多物品属性")
                .setTargetTouchable(false)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTouch(true)
                .setDelay(200)
                .setShapePadding(0)
                .setDismissOnTargetTouch(false)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem4);
        MaterialShowcaseView sequenceItem5 = (new MaterialShowcaseView.Builder(this))
                .setTarget(addMoreTv).
                        setContentText("批量添加\n选择共同属性，批量添加多\n个物品")
                .setTargetTouchable(false)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setDismissOnTargetTouch(false)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem5);
        sequence.start();
    }
}
