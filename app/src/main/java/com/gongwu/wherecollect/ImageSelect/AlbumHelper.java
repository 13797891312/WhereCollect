package com.gongwu.wherecollect.ImageSelect;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ImageData;
import com.gongwu.wherecollect.util.PermissionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * 获取图片列表帮助类
 *
 * @author Administrator
 */
public class AlbumHelper {
    private static AlbumHelper instance;
    Context context;
    ContentResolver cr;
    /**
     * 是否创建了图片集
     */
    boolean hasBuildImagesBucketList = false;

    private AlbumHelper() {
    }

    public static AlbumHelper getHelper() {
        if (instance == null) {
            instance = new AlbumHelper();
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        if (this.context == null) {
            this.context = context;
            cr = context.getContentResolver();
        }
        //                context.sendBroadcast(new Intent(
        //                        Intent.ACTION_MEDIA_MOUNTED,
        //                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

    public List<ImageData> getImages() {
        // 指定要查询的uri资源
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // 获取ContentResolver
        ContentResolver contentResolver = context.getContentResolver();
        // 查询的字段
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
        // 条件
        String selection = MediaStore.Images.Media.MIME_TYPE + "=?"
                + " or " + MediaStore.Images.Media.MIME_TYPE + "=?";
        // 条件值(這裡的参数不是图片的格式，而是标准，所有不要改动)
        String[] selectionArgs = {"image/jpeg", "image/png"};
        // 排序
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        // 查询sd卡上的图片
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, projection, selection,
                    selectionArgs, sortOrder);
        } catch (Exception e) {
            e.printStackTrace();
            new PermissionUtil(((Activity) context), context.getResources().getString(R.string.permission_sdcard));
        }
        List<ImageData> imageList = new ArrayList<ImageData>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                ImageData data = new ImageData();
                //                // 获得图片的id
                //                imageMap.put("imageID", cursor.getString(cursor
                //                        .getColumnIndex(MediaStore.Images.Media._ID)));
                // 获得图片显示的名称
                data.setName(cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                // 获得图片所在的路径(可以使用路径构建URI)
                data.setBigUri(cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA)));
                File file = new File(data.getBigUri());
                if (!TextUtils.isEmpty(data.getBigUri()) && file.exists() && file.length() > 1024)
                    imageList.add(data);
            } while (cursor.moveToNext());
            // 关闭cursor
            cursor.close();
        }
        return imageList;
    }
}
