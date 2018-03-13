package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gongwu.wherecollect.ImageSelect.ImageGridActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ImageData;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.util.PermissionUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
/**
 * 编辑物品图片是调用
 * Created by zhaojin on 15/11/16.
 * 注意在activity的onActivityResult方法中要调用 if (dialog != null) {
 * dialog.onActivityResult(requestCode, resultCode, data);
 * }
 */
public class SelectImgEditDialog {
    private static final int VIDEO_CAPTURE = 156;
    public final int REQUST_CLIP = 0x04;
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
    Dialog dialog;
    int max;
    String url;

    public SelectImgEditDialog(final Activity context, ImageView headerIv, final int max, String url) {
        this.url = url;
        this.max = max;
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
        qrBook.setVisibility(View.GONE);
        importBuy.setText("编辑照片");
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
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //            final Uri resultUri = UCrop.getOutput(data);
            //            mOutputFile = new File(getRealPathFromURI(resultUri));
            List<File> list = new ArrayList<>();
            list.add(mOutputFile);
            getResult(list);
        }
    }

    /**
     * 相册选取的照片
     *
     * @param list
     */
    public void getResult(List<File> list) {
    }

    public void onSave(Bundle outState) {
        outState.putSerializable("file", mOutputFile);
    }

    @OnClick({R.id.camare, R.id.select, R.id.qr_book, R.id.import_buy, R.id.cancel, R.id.linearLayout, R.id.root})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camare:
                camare();
                dialog.dismiss();
                break;
            case R.id.select:
                select();
                dialog.dismiss();
                break;
            case R.id.import_buy://编辑照片
                editImg();
                dialog.dismiss();
                break;
            case R.id.cancel:
            case R.id.linearLayout:
            case R.id.root:
                dialog.dismiss();
                break;
        }
    }

    /**
     * 编辑照片
     */
    private void editImg() {
        new Thread(new Runnable() {//下载图片
            @Override
            public void run() {
                try {
                    File file = Glide.with(context).load(url).downloadOnly(500, 500).get();
                    String newPath = MyApplication.CACHEPATH + System.currentTimeMillis() + ".jpg";
                    FileUtil.copyFile(file, newPath);
                    file = new File(newPath);
                    final File finalFile = file;
                    context.runOnUiThread(new Runnable() {//回主线程
                        @Override
                        public void run() {
                            cropBitmap(finalFile);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 拍照
     */
    private void camare() {
        try {
            if (!PermissionUtil.cameraIsCanUse()) {
                new PermissionUtil(SelectImgEditDialog.this.context, SelectImgEditDialog.this.context
                        .getResources
                                ().getString(R.string.permission_capture));
                return;
            }
            Intent newIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            newIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mOutputFile));
            SelectImgEditDialog.this.context
                    .startActivityForResult(newIntent,
                            REQUST_CAMARE);
            // ##############################
        } catch (Exception e) {
            e.printStackTrace();
            new PermissionUtil(SelectImgEditDialog.this.context, SelectImgEditDialog.this.context.getResources
                    ().getString(R.string.permission_capture));
        }
        dialog.dismiss();
    }

    private void select() {
        // ######### 调到图片选择界面##########
        Intent i = new Intent(context, ImageGridActivity.class);
        i.putExtra("max", max);
        SelectImgEditDialog.this.context
                .startActivityForResult(i, REQUST_PHOTOSELECT);
        // ###############################
        dialog.dismiss();
    }

    // 剪切界面
    private void cropBitmap(File file) {
        mOutputFile = new File(MyApplication.CACHEPATH, System.currentTimeMillis() + ".jpg");
        try {
            mOutputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UCrop uCrop = UCrop.of(Uri.fromFile(file), Uri.fromFile(mOutputFile));
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
}
