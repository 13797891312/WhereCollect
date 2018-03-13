package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

import static android.app.Activity.RESULT_OK;
/**
 * Created by zhaojin on 15/11/16.
 */
public class ChangeHeaderImgDialog {
    public final int REQUST_CAMARE = 0x02;
    public final int REQUST_PHOTOSELECT = 0x03;
    File mOutputFile;
    Bitmap bm;
    Activity context;
    ImageView headerIv;
    Fragment fragment;
    private boolean isClip = true;
    private int aspectX = 1;
    private int aspectY = 1;
    private boolean isCanChangeAspect=false;

    public ChangeHeaderImgDialog(Activity context, final Fragment fragment, ImageView headerIv) {
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
                                ChangeHeaderImgDialog.this.context
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
                                ChangeHeaderImgDialog.this.context
                                        .startActivityForResult(i, REQUST_PHOTOSELECT);
                            } else {
                                fragment.startActivityForResult(i, REQUST_PHOTOSELECT);
                            }
                            // ###############################
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(ChangeHeaderImgDialog.this.context, "未找到系统相册,请选择拍照", Toast.LENGTH_SHORT)
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

    public boolean isClip() {
        return isClip;
    }

    public void setClip(boolean clip) {
        isClip = clip;
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
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(mOutputFile));
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
        options.setFreeStyleCropEnabled(isCanChangeAspect);
        uCrop.withOptions(options)
        .withMaxResultSize(720, 720);
        //设置裁剪图片的宽高比，比如16：9（设置后就不能选择其他比例了、选择面板就不会出现了）
        uCrop.withAspectRatio(aspectX, aspectY);
        if (fragment != null) {
            uCrop.start(context, fragment);
        }else{
            uCrop.start(context);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//            final Uri resultUri = UCrop.getOutput(data);
//            mOutputFile = new File(getRealPathFromURI(resultUri));
            getResult(mOutputFile);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
        if (requestCode == REQUST_PHOTOSELECT) {
            try {
                Uri uri = data.getData();
                if (uri != null) {
                    if (isClip) {
                        cropBitmap(uri);
                    } else {
                        mOutputFile = new File(getRealPathFromURI(uri));
                        getResult(mOutputFile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (requestCode == REQUST_CAMARE) {
            Uri uri = Uri.fromFile(mOutputFile);
            if (mOutputFile.length() > 0 && uri != null) {
                if (isClip) {
                    cropBitmap(Uri.fromFile(mOutputFile));
                } else {
                    getResult(mOutputFile);
                }
            }
        }
    }

    public void getResult(File file) {
        if (headerIv != null) {
        }
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
