package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.util.PermissionUtil;

import java.io.File;
/**
 * Created by zhaojin on 15/11/16.
 */
public class ChangeHeaderImgDialog {
    public final int REQUST_CAMARE = 0x02;
    public final int REQUST_PHOTOSELECT = 0x03;
    public final int REQUST_CLIP = 0x04;
    File mOutputFile;
    Bitmap bm;
    Activity context;
    ImageView headerIv;
    Fragment fragment;

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

    // 剪切界面
    private void cropBitmap(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true"); // 开启剪裁
        intent.putExtra("aspectX", 1); // 宽高比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300); // 宽高
        intent.putExtra("outputY", 300);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mOutputFile));
        if (fragment == null) {
            context.startActivityForResult(intent, REQUST_CLIP);
        } else {
            fragment.startActivityForResult(intent, REQUST_CLIP);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUST_PHOTOSELECT) {
            try {
                Uri uri = data.getData();
                if (uri != null) {
                    cropBitmap(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUST_CLIP) {
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
                bm = null;
            }
            bm = BitmapFactory.decodeFile(mOutputFile.getAbsolutePath());
            if (bm != null && bm.getHeight() > 0) {
                // postFile(imageData.getUri());
                getResult(mOutputFile);
            }
        } else if (requestCode == REQUST_CAMARE) {
            Uri uri = Uri.fromFile(mOutputFile);
            if (mOutputFile.length() > 0 && uri != null) {
                cropBitmap(Uri.fromFile(mOutputFile));
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
}
