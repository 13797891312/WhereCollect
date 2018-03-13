package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
public class RecordShareEditDialog {
    public final int REQUST_CLIP = 0x04;
    Context context;
    Dialog dialog;

    public RecordShareEditDialog(Activity context) {
        this.context = context;
        dialog = new Dialog(context,
                R.style.Transparent2);
        dialog.setCanceledOnTouchOutside(true);
        View view = View.inflate(context,
                R.layout.layout_record_share_dialog, null);
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
                share();
                dialog.dismiss();
                break;
            case R.id.delete_tv:
                save();
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
     * 回调
     */
    protected void share(){

    }

    /**
     * 回调
     */
    protected void save(){

    }
}
