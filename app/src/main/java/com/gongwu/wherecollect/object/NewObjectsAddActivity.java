package com.gongwu.wherecollect.object;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.AddGoodsOtherContentActivity;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.activity.ImportHelpActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.BookBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.ObjectInfoLookView;
import com.gongwu.wherecollect.view.SelectImgDialog;
import com.zhaojin.myviews.Loading;
import com.zsitech.oncon.barcode.core.CaptureActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewObjectsAddActivity extends BaseViewActivity {
    @Bind(R.id.textBtn)
    TextView addMoreTv;
    @Bind(R.id.goods_name_et)
    EditText goodsNameEv;
    @Bind(R.id.commit_btn)
    Button commitBtn;
    @Bind(R.id.camera_iv)
    ImageView cameraIv;
    @Bind(R.id.goodsInfo_view)
    ObjectInfoLookView goodsInfoView;
    @Bind(R.id.add_other_content_edit_layout)
    LinearLayout otherEditLayout;
    @Bind(R.id.add_other_content_layout)
    LinearLayout otherLayout;

    private ObjectBean tempBean = new ObjectBean();
    private File imgFile;
    private File imgOldFile;
    private final int imgMax = 1;
    private final int BOOK_CODE = 1;
    private final int OTHER_CODE = 0x123;
    private final int MORE_CODE = 0x124;
    public static final String MORE_TYPE = "more_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_objects_add);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.add_goods_text));
        initView();
        initEvent();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        addMoreTv.setVisibility(View.VISIBLE);
        addMoreTv.setText(getResources().getString(R.string.add_more_text));
    }

    /**
     * 控件监听
     */
    private void initEvent() {
        goodsNameEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    setCommitBtnEnable(true);
                } else {
                    setCommitBtnEnable(false);
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

    @OnClick({R.id.add_shopping_layout, R.id.add_book_layout, R.id.camera_iv,
            R.id.add_other_content_tv, R.id.commit_btn, R.id.add_other_content_edit_iv, R.id.textBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_shopping_layout:
                //一键导入
                importBuy();
                break;
            case R.id.add_book_layout:
                //图书扫码
                qrBook();
                break;
            case R.id.camera_iv:
                //相机
                showSelectDialog();
                break;
            case R.id.add_other_content_tv:
            case R.id.add_other_content_edit_iv:
                //其他属性
                Intent intent = new Intent(context, AddGoodsOtherContentActivity.class);
                if (tempBean != null) {
                    intent.putExtra("tempBean", tempBean);
                }
                startActivityForResult(intent, OTHER_CODE);
                break;
            case R.id.commit_btn:
                //确定添加
                break;
            case R.id.textBtn:
                Intent addMoreIntent = new Intent(context, AddGoodsOtherContentActivity.class);
                if (tempBean != null) {
                    addMoreIntent.putExtra("tempBean", tempBean);
                    addMoreIntent.putExtra("type", MORE_TYPE);
                }
                startActivityForResult(addMoreIntent, MORE_CODE);
                break;
        }
    }

    SelectImgDialog selectImgDialog;

    private void showSelectDialog() {
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
                ViewGroup.LayoutParams params = cameraIv.getLayoutParams();
                params.width = StringUtils.pxConvertDp(100, context);
                params.height = StringUtils.pxConvertDp(100, context);
                cameraIv.setLayoutParams(params);
                ImageLoader.loadFromFile(context, imgFile, cameraIv);
                setCommitBtnEnable(true);
            }
        };
        selectImgDialog.hintLayout();
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
    private void importBuy() {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData cd2 = cm.getPrimaryClip();
        String str = cd2.getItemAt(0).getText().toString();
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
                            NewObjectsAddActivity.this.runOnUiThread(new Runnable() {//回主线程
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
                            NewObjectsAddActivity.this.runOnUiThread(new Runnable() {//回主线程
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
        };
        HttpClient.getBookInfo(context, map, listenner);
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
        tempBean = new ObjectBean();
        if (book.getImageFile() != null) {
            imgOldFile = book.getImageFile();
            ViewGroup.LayoutParams params = cameraIv.getLayoutParams();
            params.width = StringUtils.pxConvertDp(100, context);
            params.height = StringUtils.pxConvertDp(100, context);
            cameraIv.setLayoutParams(params);
            ImageLoader.loadFromFile(context, imgOldFile, cameraIv);
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
            getBookInfo(data.getStringExtra("result"));
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
        Intent intent = new Intent(context, NewObjectsAddActivity.class);
        context.startActivity(intent);
    }

    private void showOtherEditLayout() {
        otherLayout.setVisibility(View.GONE);
        otherEditLayout.setVisibility(View.VISIBLE);
    }
}
