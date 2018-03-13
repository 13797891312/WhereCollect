package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.util.PermissionUtil;
import com.gongwu.wherecollect.util.ToastUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

import static android.app.Activity.RESULT_OK;
/**
 * Created by zhaojin on 15/11/16.
 */
/**
 * Function: 家具背景图，和空间展示图，获取，截取两次图，得到两张图片
 * Date: 2016-08-20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class EditFurnitureImgDialog {
    public final int REQUST_CAMARE = 0x02;
    public final int REQUST_PHOTOSELECT = 0x03;
    File mOutputFile;
    File file1, file2;
    Bitmap bm;
    Activity context;
    ImageView headerIv;
    Fragment fragment;
    int count = 0;
    private int aspectX = 1;
    private int aspectY = 1;
    private Uri uri;

    public EditFurnitureImgDialog(Activity context, final Fragment fragment, ImageView headerIv) {
        this.headerIv = headerIv;
        this.context = context;
        this.fragment = fragment;
        String sdPath = MyApplication.CACHEPATH;
        File file = new File(sdPath);
        if (!file.exists()) {
            file.mkdir();
        }
        mOutputFile = new File(sdPath, System.currentTimeMillis() + ".jpg");
        final Dialog dialog = new Dialog(context,
                R.style.Transparent2);
        dialog.setCanceledOnTouchOutside(true);
        View view = View.inflate(context,
                R.layout.layout_selectheader, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.camare).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ##########拍照##########
                        try {
                            if (!PermissionUtil.cameraIsCanUse()) {
                                new PermissionUtil((Activity) v.getContext(), v.getContext().getResources
                                        ().getString(R.string.permission_capture));
                                return;
                            }
                            Intent newIntent = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            newIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(mOutputFile));
                            if (fragment == null) {
                                EditFurnitureImgDialog.this.context
                                        .startActivityForResult(newIntent,
                                                REQUST_CAMARE);
                            } else {
                                fragment.startActivityForResult(newIntent,
                                        REQUST_CAMARE);
                            }
                            // ##############################
                        } catch (Exception e) {
                            e.printStackTrace();
                            new PermissionUtil((Activity) v.getContext(), v.getContext().getResources
                                    ().getString(R.string.permission_capture));
                        }
                        dialog.dismiss();
                    }
                });
        view.findViewById(R.id.select).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ######### 调到图片选择界面##########
                        try {
                            Intent i = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            if (fragment == null) {
                                EditFurnitureImgDialog.this.context
                                        .startActivityForResult(i, REQUST_PHOTOSELECT);
                            } else {
                                fragment.startActivityForResult(i, REQUST_PHOTOSELECT);
                            }
                            // ###############################
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(EditFurnitureImgDialog.this.context, "未找到系统相册,请选择拍照", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        dialog.dismiss();
                    }
                });
        dialog.setContentView(view);
        Animation ani = AnimationUtils.loadAnimation(context, R.anim.push_bottom_in);
        view.findViewById(R.id.linearLayout).startAnimation(ani);
        dialog.show();
        //        WindowManager.LayoutParams params =
        //                this.getWindow().getAttributes();
        //        params.width = (int) (MainActivity.getScreenWidth(getContext()));
        //        params.height = (int) (MainActivity.getScreenHeigth(getContext()));
        //        this.getWindow().setAttributes(params);
    }

    /**
     * 设置裁剪框宽高比，默认正方形
     *
     * @param aspectX
     * @param aspectY
     */
    public void setAspectXY(int aspectX, int aspectY) {
        this.aspectX = aspectX;
        this.aspectY = aspectY;
    }

    // 剪切界面
    private void cropBitmap(Uri uri) {
        File file;
        if (count == 0) {
            file = file1 = new File(MyApplication.CACHEPATH, System.currentTimeMillis() + ".jpg");
            ToastUtil.showTopToast(context, "截取空间展示图——（请模拟实际长宽比例截图）");
        } else {
            file = file2 = new File(MyApplication.CACHEPATH, System.currentTimeMillis() + ".jpg");
            ToastUtil.showTopToast(context, "截取家具内部背景图——（正方形截图）");
        }
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(file));
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
        if (count == 0) {
            options.setFreeStyleCropEnabled(true);
            options.setToolbarTitle("家具空间展示-实际比例");
        } else {
            options.setFreeStyleCropEnabled(false);
            options.setToolbarTitle("家具背景展示-正方形");
        }
        uCrop.withOptions(options)
                .withMaxResultSize(720, 720);
        //设置裁剪图片的宽高比，比如16：9（设置后就不能选择其他比例了、选择面板就不会出现了）
        uCrop.withAspectRatio(aspectX, aspectY);
        if (fragment != null) {
            uCrop.start(context, fragment);
        } else {
            uCrop.start(context);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (count == 0) {
                count = 1;
                cropBitmap(uri != null ? uri : Uri.fromFile(mOutputFile));
            } else {
                getResult(file1, file2);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
        if (requestCode == REQUST_PHOTOSELECT) {
            try {
                uri = data.getData();
                if (uri != null) {
                    if (count == 0) {
                        cropBitmap(uri);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUST_CAMARE) {
            Uri uri = Uri.fromFile(mOutputFile);
            if (mOutputFile.length() > 0 && uri != null) {
                cropBitmap(Uri.fromFile(mOutputFile));
            }
        }
    }

    public void getResult(File file1, File file2) {
    }

    public void onSave(Bundle outState) {
        outState.putSerializable("file", mOutputFile);
    }

    private String getRealPathFromURI(Uri contentUri) { //传入图片uri地址
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
