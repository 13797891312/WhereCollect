package com.gongwu.wherecollect.record;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class RecordShareActivity extends BaseViewActivity {
    @Bind(R.id.imageview)
    ImageView imageview;
    String url = "";
    @Bind(R.id.bitmap_layout)
    RelativeLayout bitmapLayout;
    @Bind(R.id.layout)
    RelativeLayout layout;
    RoomRecordBean bean;
    @Bind(R.id.type_tv)
    TextView typeTv;
    @Bind(R.id.xiangce_layout)
    LinearLayout xiangceLayout;
    private int type;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_share);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("分享");
        bean = (RoomRecordBean) getIntent().getSerializableExtra("bean");
        type = getIntent().getIntExtra("type", 0);
        if (type == 1) {
            typeTv.setVisibility(View.VISIBLE);
            xiangceLayout.setVisibility(View.GONE);
        } else {
            typeTv.setVisibility(View.GONE);
            xiangceLayout.setVisibility(View.VISIBLE);
        }
        initView();
    }

    private void initView() {
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
                        RecordShareActivity.this.bitmap = bitmap;
                        bitmapLayout.getViewTreeObserver().addOnPreDrawListener(
                                new ViewTreeObserver.OnPreDrawListener() {
                                    @Override
                                    public boolean onPreDraw() {
                                        bitmapLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                                        int height = bitmapLayout.getHeight(); // 获取高度
                                        int width = bitmap.getWidth() * height / bitmap
                                                .getHeight();
                                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bitmapLayout
                                                .getLayoutParams();
                                        lp.width = width;
                                        lp.height = bitmapLayout.getHeight();
                                        bitmapLayout.setLayoutParams(lp);
                                        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) layout
                                                .getLayoutParams();
                                        lp1.width = (int) (width + 20 * BaseViewActivity.getScreenScale
                                                (RecordShareActivity.this));
                                        layout.setLayoutParams(lp1);
                                        return true;
                                    }
                                });
                        return false;
                    }
                })
                .into(imageview);
    }

    @OnClick({R.id.wx_layout, R.id.pyq_layout, R.id.wb_layout, R.id.qq_layout, R.id.qqzone_layout, R.id.xiangce_layout})
    public void onClick(final View view) {
        share(view);
    }

    /**
     * 分享
     *
     * @param view
     */
    private void share(View view) {
        MobclickAgent.onEvent(this, "070101");
        switch (view.getId()) {
            case R.id.wx_layout:
                ShareUtil.share(this, SHARE_MEDIA.WEIXIN, null, "收哪儿分享", "", bean.getImage_url());
                break;
            case R.id.pyq_layout:
                ShareUtil.share(this, SHARE_MEDIA.WEIXIN_CIRCLE, null, "收哪儿分享", "", bean.getImage_url());
                break;
            case R.id.wb_layout:
                ShareUtil.share(this, SHARE_MEDIA.SINA, null, "收哪儿分享", "", bean.getImage_url());
                break;
            case R.id.qq_layout:
                ShareUtil.share(this, SHARE_MEDIA.QQ, null, "收哪儿分享", "", bean.getImage_url());
                break;
            case R.id.qqzone_layout:
                ShareUtil.share(this, SHARE_MEDIA.QZONE, null, "收哪儿分享", "", bean.getImage_url());
                break;
            case R.id.xiangce_layout:
                if (bitmap != null) {
                    BitmapUtil.saveImageToGallery(this, bitmap);
                    ToastUtil.show(this,"已保存到sdcard/shiji目录", Toast.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
