package com.gongwu.wherecollect.record;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.entity.RoomRecordBean;
import com.gongwu.wherecollect.util.BitmapUtil;
import com.gongwu.wherecollect.util.ShareUtil;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.RecordShareEditDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import butterknife.Bind;
import butterknife.ButterKnife;
public class RecordLookActivity extends BaseViewActivity {
    @Bind(R.id.image)
    ImageView image;
    RoomRecordBean bean;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_look);
        ButterKnife.bind(this);
        bean = (RoomRecordBean) getIntent().getSerializableExtra("bean");
        titleLayout.setTitle("分享");
        titleLayout.setBack(true, null);
        titleLayout.imageBtn.setVisibility(View.VISIBLE);
        titleLayout.imageBtn.setImageResource(R.drawable.icon_menu);
        titleLayout.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordShareEditDialog dialog = new RecordShareEditDialog(RecordLookActivity.this) {
                    @Override
                    protected void save() {
                        super.save();
                        if (bitmap != null) {
                            BitmapUtil.saveImageToGallery(context, bitmap);
                            ToastUtil.show(context, "已保存到sdcard/shiji目录", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    protected void share() {
                        super.share();
                        if (bitmap != null) {
                            ShareUtil.openShareDialog(RecordLookActivity.this, null, "收哪儿分享", "", bean.getImage_url());
                            MobclickAgent.onEvent(context, "070101");
                        } else {
                            ToastUtil.show(RecordLookActivity.this, "获取图片失败", Toast.LENGTH_SHORT);
                        }
                    }
                };
            }
        });
        Glide.with(context)
                .load(bean.getImage_url())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<Bitmap> target, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final Bitmap bitmap, String s, Target<Bitmap> target, boolean b,
                                                   boolean b1) {
                        RecordLookActivity.this.bitmap = bitmap;
                        return false;
                    }
                })
                .into(image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
