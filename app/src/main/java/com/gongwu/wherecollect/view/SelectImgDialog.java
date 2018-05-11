package com.gongwu.wherecollect.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gongwu.wherecollect.ImageSelect.ImageGridActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.ImportHelpActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BookBean;
import com.gongwu.wherecollect.entity.ImageData;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.PermissionUtil;
import com.gongwu.wherecollect.util.ToastUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.zhaojin.myviews.Loading;
import com.zsitech.oncon.barcode.core.CaptureActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by zhaojin on 15/11/16.
 * 注意在activity的onActivityResult方法中要调用 if (dialog != null) {
 * dialog.onActivityResult(requestCode, resultCode, data);
 * }
 */
public class SelectImgDialog {
    private static final int VIDEO_CAPTURE = 156;
    public final int REQUST_CAMARE = 0x02;
    public final int REQUST_PHOTOSELECT = 0x03;
    File mOutputFile;
    Bitmap bm;
    Activity context;
    ImageView headerIv;
    @Bind(R.id.camare)
    TextView camare;
    @Bind(R.id.select)
    TextView select;
    @Bind(R.id.qr_book)
    TextView qrBook;
    @Bind(R.id.import_buy)
    TextView importBuy;
    @Bind(R.id.cancel)
    TextView cancel;
    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;
    @Bind(R.id.root)
    RelativeLayout root;
    @Bind(R.id.hint_layout)
    LinearLayout hintLayout;
    @Bind(R.id.edit_img_tv)
    TextView editImgTv;

    Dialog dialog;
    File imgFile;
    int imgMax;

    public SelectImgDialog(final Activity context, ImageView headerIv, final int max) {
        this.imgMax = max;
        this.headerIv = headerIv;
        this.context = context;
        String sdPath = MyApplication.CACHEPATH;
        File file = new File(sdPath);
        if (!file.exists()) {
            file.mkdir();
        }
        mOutputFile = new File(sdPath, System.currentTimeMillis() + ".jpg");
        dialog = new Dialog(context,
                R.style.Transparent2);
        dialog.setCanceledOnTouchOutside(true);
        View view = View.inflate(context,
                R.layout.layout_selectimg, null);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    public SelectImgDialog(final Activity context, ImageView headerIv, final int imgMax, File imgFile) {
        this.imgMax = imgMax;
        this.headerIv = headerIv;
        this.context = context;
        this.imgFile = imgFile;
        String sdPath = MyApplication.CACHEPATH;
        File file = new File(sdPath);
        if (!file.exists()) {
            file.mkdir();
        }
        mOutputFile = new File(sdPath, System.currentTimeMillis() + ".jpg");
        dialog = new Dialog(context,
                R.style.Transparent2);
        dialog.setCanceledOnTouchOutside(true);
        View view = View.inflate(context,
                R.layout.layout_selectimg, null);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUST_PHOTOSELECT && resultCode == ImageGridActivity.RESULT) {
            List<ImageData> temp = (ArrayList<ImageData>) data.getSerializableExtra("list");
            List<File> mlist = new ArrayList<>();
            for (ImageData id : temp) {
                mlist.add(new File(id.getBigUri()));
            }
            getResult(mlist);
        } else if (requestCode == REQUST_CAMARE) {
            Uri uri = Uri.fromFile(mOutputFile);
            if (mOutputFile.length() > 0 && uri != null) {
                List<File> list = new ArrayList<>();
                list.add(mOutputFile);
                getResult(list);
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == VIDEO_CAPTURE) {
            Uri uri = data.getData();
            Cursor cursor = context.getContentResolver().query(uri, null, null,
                    null, null);
            if (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                        id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
                //ThumbnailUtils类2.2以上可用
                // Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MICRO_KIND);
                cursor.close();
                getResult(new File(filePath), bitmap);
            }
        } else if (resultCode == CaptureActivity.result) {//扫描的到结果
            getBookInfo(data.getStringExtra("result"));
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //            final Uri resultUri = UCrop.getOutput(data);
            //            mOutputFile = new File(getRealPathFromURI(resultUri));
            resultFile(mOutputFile);
        }
    }

    /**
     * 相册选取的照片
     *
     * @param list
     */
    public void getResult(List<File> list) {
    }

    /**
     * 视频录制
     *
     * @param file
     * @param bitmap
     */
    public void getResult(File file, Bitmap bitmap) {
        try {
            FileUtil.updateGallery(context, file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写用来回调图书二维码的扫描
     *
     * @param book
     */
    protected void getBookResult(BookBean book) {
    }

    public void onSave(Bundle outState) {
        outState.putSerializable("file", mOutputFile);
    }

    @OnClick({R.id.camare, R.id.select, R.id.qr_book, R.id.import_buy, R.id.cancel, R.id.linearLayout, R.id.root, R.id.edit_img_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camare:
                camare();
                break;
            case R.id.select:
                select();
                break;
            case R.id.qr_book:
                qrBook();
                break;
            case R.id.import_buy:
                importBuy();
                dialog.dismiss();
                break;
            case R.id.cancel:
            case R.id.linearLayout:
            case R.id.root:
                dialog.dismiss();
                break;
            case R.id.edit_img_tv:
                if (imgFile != null) {
                    cropBitmap(imgFile);
                }
                dialog.dismiss();
                break;
        }
    }

    // 剪切界面
    public void cropBitmap(File imgFile) {
        mOutputFile = new File(MyApplication.CACHEPATH, System.currentTimeMillis() + ".jpg");
        try {
            mOutputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UCrop uCrop = UCrop.of(Uri.fromFile(imgFile), Uri.fromFile(mOutputFile));
        //初始化UCrop配置
        UCrop.Options options = new UCrop.Options();
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, 0);
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(context, R.color.black));
        options.setToolbarWidgetColor(ActivityCompat.getColor(context, R.color.white));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(context, R.color.black));
        //是否能调整裁剪框
        //        options.setAspectRatioOptions(5,new AspectRatio("1:1",1f,1f),new AspectRatio("1:1",1f,1f));
        //是否隐藏底部容器，默认显示
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(false);
        uCrop.withOptions(options)
                .withMaxResultSize(720, 720);
        //设置裁剪图片的宽高比，比如16：9（设置后就不能选择其他比例了、选择面板就不会出现了）
        uCrop.withAspectRatio(1, 1);
        uCrop.start(((Activity) context));

    }

    /**
     * 图书扫描
     */
    private void qrBook() {
        context.startActivityForResult(new Intent(context, CaptureActivity.class), 1);
        dialog.dismiss();
    }

    /**
     * 拍照
     */
    private void camare() {
        try {
            if (!PermissionUtil.cameraIsCanUse()) {
                new PermissionUtil(SelectImgDialog.this.context, SelectImgDialog.this.context
                        .getResources
                                ().getString(R.string.permission_capture));
                return;
            }
            Intent newIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            newIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mOutputFile));
            SelectImgDialog.this.context
                    .startActivityForResult(newIntent,
                            REQUST_CAMARE);
            // ##############################
        } catch (Exception e) {
            e.printStackTrace();
            new PermissionUtil(SelectImgDialog.this.context, SelectImgDialog.this.context.getResources
                    ().getString(R.string.permission_capture));
        }
        dialog.dismiss();
    }

    private void select() {
        // ######### 调到图片选择界面##########
        Intent i = new Intent(context, ImageGridActivity.class);
        i.putExtra("max", imgMax);
        SelectImgDialog.this.context
                .startActivityForResult(i, REQUST_PHOTOSELECT);
        // ###############################
        dialog.dismiss();
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
                            context.runOnUiThread(new Runnable() {//回主线程
                                @Override
                                public void run() {
                                    DialogUtil.show("提醒", "此操作会将部分共同属性刷新，是否继续?", "继续", "取消", context, new
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
                            context.runOnUiThread(new Runnable() {//回主线程
                                @Override
                                public void run() {
                                    DialogUtil.show("提醒", "此操作会将部分共同属性刷新，是否继续?", "继续", "取消", context, new
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
     * 隐藏导入跟扫码
     */
    public void hintLayout() {
        hintLayout.setVisibility(View.GONE);
    }

    /**
     * 是否显示编辑
     *
     * @param visible
     */
    public void showEditIV(int visible) {
        editImgTv.setVisibility(visible);
    }


    /**
     * 重写回调获取编辑后的照片
     *
     * @param file
     */
    protected void resultFile(File file) {
    }
}
