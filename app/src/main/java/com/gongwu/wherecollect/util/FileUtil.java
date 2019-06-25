package com.gongwu.wherecollect.util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gongwu.wherecollect.application.MyApplication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
public class FileUtil {
    public static final String FOLDER_SHOT_MIX = "/ShotMix"; // 截屏答题（合成后）
    public static final String FOLDER_SHOT_ORG = "/ShotOrg"; // 截屏答题（合成前）
    /**
     * 下载的app存放的文件夹
     */
    public static final String DOWNLOAD_APP_PATH = "/apps";
    // 后缀名，MIME类型
    private static final String[][] MIME_MapTable = {{".apk", "application/vnd.android.package-archive"},
            {".txt", "text/plain"}, {".htm", "text/html"}, {".html", "text/html"}, {".xml", "text/plain"},
            {".java", "text/plain"},
            {".pdf", "application/pdf"}, {".doc", "application/msword"}, {".xls", "application/vnd.ms-excel"},
            {".ppt", "application/vnd.ms-powerpoint"}, {".wps", "application/vnd.ms-works"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".png", "image/png"}, {".jpeg", "image/jpeg"}, {".jpg", "image/jpeg"}, {".bmp", "image/bmp"},
            {".gif", "image/gif"},
            {".swf", "application/x-shockwave-flash"}, {".flv", "video/x-flv"}, {".wmv", "video/x-ms-wmv"},
            {".mp4", "video/mp4"}, {".mpe", "video/mpeg"}, {".mpeg", "video/mpeg"}, {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"}, {".rmvb", "video/x-pn-realaudio"}, {".avi", "video/x-msvideo"},
            {".wav", "audio/x-wav"}, {".mp3", "audio/x-mpeg"}, {".wma", "audio/x-ms-wma"}, {"", "*/*"}};
    public static File updateDir = null;
    public static File updateFile = null;
    /**
     * 获取文件大小
     *
     * @return
     */
    public static DecimalFormat df00 = new DecimalFormat("#.00");
    public static DecimalFormat df0 = new DecimalFormat("#.0");
    public static DecimalFormat df = new DecimalFormat("#");

    /***
     * 创建文件
     */
    public static void createFile(String name) {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory()
                    + "/" + MyApplication.CACHEPATH);
            updateFile = new File(updateDir + "/" + name + ".apk");
            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            if (!updateFile.exists()) {
                try {
                    updateFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(File oldfile, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            InputStream inStream = new FileInputStream(oldfile); // 读入原文件
            File newFile = new File(newPath);
            newFile.delete();
            newFile.createNewFile();
            FileOutputStream fs = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread; // 字节数 文件大小
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
                fs.flush();
            }
            inStream.close();
            fs.close();
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * view截图,使用于View在界面上展示出来过
     *
     * @param contentLayout
     * @param path
     * @return
     */
    public static String viewToFile(View contentLayout, String path) {
        contentLayout.setDrawingCacheEnabled(true);
        contentLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //        contentLayout.layout(0, 0, contentLayout.getMeasuredWidth(),
        //                contentLayout.getMeasuredHeight());
        contentLayout.buildDrawingCache();
        Bitmap bitmap = contentLayout.getDrawingCache();
        File small = new File(MyApplication.CACHEPATH + "small");
        if (!small.exists()) {
            small.mkdirs();
        }
        if (TextUtils.isEmpty(path)) {
            path = MyApplication.CACHEPATH + "/small/" + System.currentTimeMillis() + "save.jpg";
        } else {
            new File(path).delete();
            path = MyApplication.CACHEPATH + "/small/" + System.currentTimeMillis() + "save.jpg";
        }
        File file = new File(path);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        contentLayout.destroyDrawingCache();
        return path;
    }

    //刷新本地图片到多媒体数据库
    public static void updateGallery(final Context context, String filename)//filename是我们的文件全名，包括后缀哦
    {
        MediaScannerConnection.scanFile(context,
                new String[]{filename}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    /**
     * 判断是否存在SD卡
     */
    public static boolean isExistSdCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取项目根目录
     */
    public static String getRootPath() {
        if (isExistSdCard()) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/sundata";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            return path;
        }
        return "";
    }

    public static String FormetFileSize(String fileSize) {// 转换文件大小
        if ("0".equals(fileSize)) {
            return "0B";
        }
        long fileS = Long.parseLong(fileSize);
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1024 * 1024) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1024 * 1024 * 1024) {
            fileSizeString = df0.format((double) fileS / (1024 * 1024)) + "M";
        } else {
            fileSizeString = df00.format((double) fileS / (1024 * 1024 * 1024)) + "G";
        }
        return fileSizeString;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 下载的app本地路径
     *
     * @return
     */
    public static String getDownloadAppPath() {
        if (isExistSdCard()) {
            File file = new File(getRootPath() + DOWNLOAD_APP_PATH);
            if (!file.exists()) {
                file.mkdir();
            }
            return file.getPath();
        }
        return "";
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExists(String path) {
        if (TextUtils.isEmpty(path))
            return false;
        return new File(path).exists();
    }

    /**
     * 获取文件后缀名
     */
    public static String getNameExt(String filename) {
        if (filename == null || filename.trim().length() <= 0)
            return null;
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex < 0)
            return null;
        return filename.substring(dotIndex);
    }

    /**
     * 根据后缀名获取文件类型
     */
    public static String getMimeType(String fileext) {
        String type = "*/*";
        if (fileext != null) {
            for (int i = 0; i < MIME_MapTable.length; i++) {
                if (MIME_MapTable[i][0].equalsIgnoreCase(fileext)) {
                    type = MIME_MapTable[i][1];
                    break;
                }
            }
        }
        return type;
    }

    /**
     * 使用第三方插件打开文件
     */
    public static boolean openFile(Activity activity, String fileUrl) {
        if (TextUtils.isEmpty(fileUrl))
            return false;
        fileUrl = fileUrl.replaceAll("-bat", "");
        if (!isFileExists(fileUrl))
            return false;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW); // "android.intent.action.VIEW"
        intent.addCategory("android.intent.category.DEFAULT");
        String name = FileUtil.getNameExt(fileUrl);
        String mime = FileUtil.getMimeType(name);
        if ("*/*".equals(mime)) {
            // 未找到匹配类型
            Log.e("TAG", "未找到匹配的类型： " + mime);
            //            Toast.makeText(activity, "暂不支持打开此文件！", Toast.LENGTH_SHORT).show();
        }
        intent.setDataAndType(Uri.fromFile(new File(fileUrl)), mime);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        } else {
            // 未安装第三方应用
            Toast.makeText(activity, "没有找到第三方插件", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean openFile(Activity activity, File file) {
        return openFile(activity, file.getPath());
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //return size/1048576;
        return size;
    }

    public static long getFileSize(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.length();
        }
        return 0;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 图片压缩
     *
     * @param file
     * @return
     */
    public static File getSmallFile(File file) {
        return getSmallFile(file, 1440, 1920);
    }

    /**
     * @param file
     * @param w
     * @param h
     * @return
     */
    public static File getSmallFile(File file, int w, int h) {
        try {
            if (!file.getAbsolutePath().endsWith(".png")) {
                if ((file == null || file.length() < 700 * 1024)) {
                    return file;
                }
            }
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            options.inSampleSize = calculateInSampleSize(options, w, h);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            int degree = readPictureDegree(file.getAbsolutePath());
            Bitmap bitmap = rotaingImageView(degree, BitmapFactory.decodeFile(file.getAbsolutePath(), options));
            File newFile = getFile(bitmap, file.getName());
            if (!newFile.exists() || newFile.length() == 0) {
                return file;
            } else {
                return newFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return file;
        }
    }

    /**
     * bitmap转File
     *
     * @param bitmap
     * @param fileName
     * @return
     */
    public static File getFile(Bitmap bitmap, String fileName) {
        if (fileName.endsWith(".png")) {
            fileName = fileName.replace(".png", ".jpg");
        }
        File small = new File(MyApplication.CACHEPATH + "small");
        if (!small.exists()) {
            small.mkdirs();
        }
        File file = new File(MyApplication.CACHEPATH + "small/" + fileName);//将要保存图片的路径
        if (file.exists() && file.length() > 10240)
            return file;
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, bos);
            bos.flush();
            bos.close();
            LogUtil.e("bitmap:" + bitmap.getWidth() + "--" + bitmap.getHeight());
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.e("newFile:" + file.length());
        return file;
    }

    /**
     * 获取旋转角度
     *
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface
                    .ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        try {
            //旋转图片 动作
            Matrix matrix = new Matrix();
            ;
            matrix.postRotate(angle);
            // 创建新的图片
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return resizedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    /**
     * 根据 文件获取uri
     * @param context 上下文对象
     * @param file 文件
     * @return uri
     */
    public static Uri getUriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static File compress(File originFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置此参数是仅仅读取图片的宽高到options中，不会将整张图片读到内存中，防止oom
        options.inJustDecodeBounds = true;
        Bitmap emptyBitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath(), options);

        options.inJustDecodeBounds = false;
        options.inSampleSize = 4;
        Bitmap resultBitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath(), options);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        File newFile = new File(MyApplication.CACHEPATH, System.currentTimeMillis() + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }
}
