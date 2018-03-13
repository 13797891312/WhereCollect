package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
/**
 * Function://物品查看时右上角menu
 * Date: 2017/9/7
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ImageEditDialog {
    public final int REQUST_CLIP = 0x04;
    Context context;
    Dialog dialog;
    File file;
    File mOutputFile;
    Bitmap bm;

    public ImageEditDialog(Activity context, File file) {
        this.file = file;
        this.context = context;
        dialog = new Dialog(context,
                R.style.Transparent2);
        dialog.setCanceledOnTouchOutside(true);
        View view = View.inflate(context,
                R.layout.layout_image_edit_dialog, null);
        ButterKnife.bind(this, view);
        dialog.setContentView(view);
        Animation ani = AnimationUtils.loadAnimation(context, R.anim.push_bottom_in);
        view.findViewById(R.id.linearLayout).startAnimation(ani);
        dialog.show();
    }

    @OnClick({R.id.edit_tv, R.id.delete_tv, R.id.cancel, R.id.linearLayout, R
            .id.root})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_tv:
                cropBitmap();
                dialog.dismiss();
                break;
            case R.id.delete_tv:
                delete();
                dialog.dismiss();
                break;
            case R.id.cancel:
            case R.id.linearLayout:
            case R.id.root:
                dialog.dismiss();
                break;
        }
    }

    // 剪切界面
    private void cropBitmap() {
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //            final Uri resultUri = UCrop.getOutput(data);
            //            mOutputFile = new File(getRealPathFromURI(resultUri));
            resultFile(mOutputFile);
        }
    }

    /**
     * 重写回调获取编辑后的照片
     *
     * @param file
     */
    protected void resultFile(File file) {
    }

    /**
     * 重写删除回调
     */
    protected void delete() {
    }
}
