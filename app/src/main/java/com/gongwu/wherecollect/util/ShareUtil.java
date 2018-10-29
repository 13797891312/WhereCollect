package com.gongwu.wherecollect.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * Function:
 * Date: 2017/12/19
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ShareUtil {
    /**
     * @param context
     * @param sm
     * @param listener
     * @param title
     * @param content
     * @param url      图片地址
     */
    public static void share(Activity context, SHARE_MEDIA sm, UMShareListener listener, String title, String content,
                             String url) {
        UMImage image = new UMImage(context, url);
        image.setTitle(title);
        image.setDescription(content);
        ShareAction action = new ShareAction(context);
        action.setPlatform(sm);//传入平台
        action.withMedia(image);
        if (listener != null) {
            action.setCallback(listener);//回调监听器
        }
        action.share();
    }

    /**
     * @param context
     * @param listener
     * @param title
     * @param content
     */
    public static void openShareDialog(final Activity context, UMShareListener listener, String title, String
            content, String url) {
        //                shareFromSys(context,bitmap);
        UMImage image = new UMImage(context, url);
        image.setTitle(title);
        image.setDescription(content);
        new ShareAction(context)
                .withMedia(image)
                .setDisplayList(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ,
                        SHARE_MEDIA.QZONE)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        if (throwable.getMessage().contains("新浪上传图片失败")) {
                            ToastUtil.show(context, "请先安装新浪微博客户端", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                    }
                })
                .open();
    }

    /**
     * 打开分享面板
     *
     * @param context
     */
    public static void openShareDialog(Activity context) {
        UMImage thumb = new UMImage(context, R.drawable.icon_lun);
        UMWeb web = new UMWeb("http://www.shouner.com/");
        web.setTitle("收哪儿-你的物品收纳记录管家");//标题
        web.setThumb(thumb);  //缩略图
        web.setDescription("找东西,不操心");//描述
        new ShareAction(context)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ,
                        SHARE_MEDIA.QZONE)
                .open();
    }

    public static void shareFromSys(Context context, Bitmap bitmap) {
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
}