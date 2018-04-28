package com.gongwu.wherecollect.object;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
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
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.AddGoodsOtherContentActivity;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加物品
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.add_goods_text));
        type = getIntent().getIntExtra("type", 0);
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
            addMoreTv.setVisibility(View.GONE);
            goodsInfoView.setVisibility(View.VISIBLE);
            goodsInfoView.init(tempBean);
            showOtherEditLayout();
            if (!TextUtils.isEmpty(tempBean.getName())) {
                goodsNameEv.setText(tempBean.getName());
            }
            if (!TextUtils.isEmpty(tempBean.getObject_url())) {
                setCameraIvParams(100);
                cameraIv.setHead("", "", tempBean.getObject_url());
                imgOldFile = getFileByUri(tempBean.getObject_url());
            }
        } else {
            editGoodsType = 0;
        }
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
                if (count > 0) {
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

    private void setCameraIv(boolean isSet) {
        if (isSet && TextUtils.isEmpty(tempBean.getObject_url())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCameraIvParams(100);
                    cameraIv.setHead("", goodsNameEv.getText().toString().trim(), "");
                }
            }, 1000);
        } else if (!isSet && TextUtils.isEmpty(tempBean.getObject_url())) {
            setCameraIvParams(30);
            head.setImageDrawable(getResources().getDrawable(R.drawable.camera));
            name.setText("");
        }

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
                loading = Loading.show(loading, this, "");
                if (editGoodsType == 1) {
                    if (!tempBean.getObject_url().contains("http")) {
                        upLoadImg(tempBean.getObjectFiles());
                    } else {
                        addObject();
                    }
                    return;
                }
                if (TextUtils.isEmpty(tempBean.getObject_url())) {
                    new Thread(runnable).start();
                } else {
                    upLoadImg(tempBean.getObjectFiles());
                }
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
                    addObjects(list);
                }
            }
        };
        uploadUtil.start();
    }

    /**
     * 添加物品
     */
    private void addObjects(List<String> list) {
        if (StringUtils.isEmpty(list)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
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
        map.put("name", goodsNameEv.getText().toString());
        map.put("price_max", tempBean.getPrice() + "");
        map.put("price_min", tempBean.getPrice() + "");
        map.put("season", tempBean.getSeason());
        map.put("star", tempBean.getStar() + "");
        map.put("image_urls", sb.toString());
        map.put("count", tempBean.getObject_count() + "");
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
        map.put("image_url", tempBean.getObject_url());
        map.put("object_count", tempBean.getObject_count() + "");
        map.put("price_max", tempBean.getPrice_max() + "");
        map.put("price_min", tempBean.getPrice_min() + "");
        map.put("season", tempBean.getSeason());
        map.put("name", tempBean.getName());
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
        PostListenner listenner = new PostListenner(this, Loading.show(null, this,
                "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                newBean = JsonUtils.objectFromJson(r.getResult(), ObjectBean.class);
                deleteObject();
            }
        };
        HttpClient.getAddObject(this, map, listenner);
    }

    private void deleteObject() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("object_id", tempBean.get_id());
        PostListenner listenner = new PostListenner(this, Loading.show(null, this,
                "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                Intent intent = new Intent();
                intent.putExtra("bean", newBean);
                setResult(100, intent);
                finish();
            }
        };
        HttpClient.deleteGoods(this, map, listenner);
    }


    SelectImgDialog selectImgDialog;

    /**
     * 图片选择dialog
     */
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
                setCameraIvParams(100);
                cameraIv.setHead("", "", imgFile.getAbsolutePath());
                tempBean.setObject_url(imgFile.getAbsolutePath());
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
            setCameraIvParams(100);
            cameraIv.setHead("", "", imgFile.getAbsolutePath());
            tempBean.setObject_url(imgFile.getAbsolutePath());
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

    public void viewSaveToImage(View view) {
        /**
         * View组件显示的内容可以通过cache机制保存为bitmap
         * 我们要获取它的cache先要通过setDrawingCacheEnable方法把cache开启，
         * 然后再调用getDrawingCache方法就可 以获得view的cache图片了
         * 。buildDrawingCache方法可以不用调用，因为调用getDrawingCache方法时，
         * 若果 cache没有建立，系统会自动调用buildDrawingCache方法生成cache。
         * 若果要更新cache, 必须要调用destoryDrawingCache方法把旧的cache销毁，才能建立新的。
         */
        //        view.setDrawingCacheEnabled(true);
        //        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //设置绘制缓存背景颜色
        //        view.setDrawingCacheBackgroundColor(Color.WHITE);

        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);
        //保存在本地 产品还没决定要不要保存在本地
        FileOutputStream fos;
        File file;
        try {
            // 判断手机设备是否有SD卡
            boolean isHasSDCard = Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
            if (isHasSDCard) {
                // SD卡根目录
                String path = MyApplication.CACHEPATH;
                file = new File(path, System.currentTimeMillis() + ".jpg");
                //通过文件的对象file的createNewFile()方法来创建文件
                file.createNewFile();
                fos = new FileOutputStream(file);
            } else {
                throw new Exception("创建文件失败!");
            }
            //压缩图片 30 是压缩率，表示压缩70%; 如果不压缩是100，表示压缩率为0
            cachebmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
            Message message = handler.obtainMessage();
            message.obj = file;
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.destroyDrawingCache();
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.draw(c);

        return bmp;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewSaveToImage(cameraIv);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            File file = (File) msg.obj;
            List<File> fils = new ArrayList<>();
            fils.add(file);
            upLoadImg(fils);
        }
    };

    private File getPath(String path) {
        Uri uri = Uri.parse(path);
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor;
        if (Build.VERSION.SDK_INT < 11) {
            cursor = managedQuery(uri, projection, null, null, null);
        } else {
            CursorLoader cursorLoader = new CursorLoader(this, uri, projection, null, null, null);
            cursor = cursorLoader.loadInBackground();
        }
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return new File(cursor.getString(column_index));
    }

    public File getFileByUri(String s) {
        Uri uri = Uri.parse(s);
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append( MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {  MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA }, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex( MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex( MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        } else {
//            Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

}
