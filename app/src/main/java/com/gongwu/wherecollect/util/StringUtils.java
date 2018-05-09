package com.gongwu.wherecollect.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.gongwu.wherecollect.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static SimpleDateFormat dateFormat = null;

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
    }
    // /*** 获取URL ***/
    // public static String getHttpsUrl(String function,String key[], String
    // value[]) {
    // String url = "";
    // StringBuffer sb = new StringBuffer();
    // for (int i = 0; i < value.length; i++) {
    // sb.append(key[i] + "=");
    // LogUtils.e("参数", key[i] + "=" + value[i]);
    // try {
    // sb.append(value[i]);
    // LogUtils.e("参数",value[i]);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // sb.append("&");
    // }
    // sb.delete(sb.length() - 1, sb.length());
    // url = My +
    // return url;
    // }

    /**
     * @创建时间：Feb 17, 2013 9:23:14 PM
     * @方法描述：（对象为空字符或null或集合长度为0）
     * @返回值：true/false
     * @version
     */
    public static boolean isNullOrBlanK(Object param) {
        if (param == null) {
            return true;
        } else {
            if (String.class.isInstance(param)) {
                if (!"".equals(((String) param).trim())) {
                    return false;
                }
            } else if (List.class.isInstance(param)) {
                if (((ArrayList) param).size() != 0) {
                    return false;
                }
            } else if (java.util.Map.class.isInstance(param)) {
                if (((HashMap) param).size() != 0) {
                    return false;
                }
            } else if (String[].class.isInstance(param)) {
                if (((Object[]) param).length != 0) {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        }
    }

    /****
     * 是否是有效电话
     ****/
    public static boolean isPhone(String name) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(17[0-9])|(14[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /***
     * 是否是有效Email
     ***/
    public static boolean isEmail(String email) {
        boolean flag = true;
        if (email.indexOf("@") == -1 || email.indexOf(".") == -1) {
            flag = false;
        }
        if (flag) {
            if (email.indexOf("@") > email.indexOf("."))
                flag = false;
        }
        return flag;
    }

    /**
     * 是否是有效密码
     ***/
    public static boolean isGoodPWD(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 是否是有效日期
     ***/
    public static boolean isGoodDate(String s) {
        try {
            dateFormat.parse(s);
            return true;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return false;
        }
    }

    public static int getCurrentVersion(Context context) {
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    public static String getCurrentVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 返回首字母
     *
     * @param strChinese
     * @return
     */
    public static String getPYIndexStr(String strChinese) {
        if (strChinese.equals("阚")) {
            return "K";
        }
        try {
            StringBuffer buffer = new StringBuffer();
            byte b[] = strChinese.getBytes("GBK");// 把中文转化成byte数组
            for (int i = 0; i < b.length; i++) {
                if ((b[i] & 255) > 128) {
                    int char1 = b[i++] & 255;
                    char1 <<= 8;// 左移运算符用“<<”表示，是将运算符左边的对象，向左移动运算符右边指定的位数，并且在低位补零。其实，向左移n
                    // 位，就相当于乘上2的n次方
                    int chart = char1 + (b[i] & 255);
                    buffer.append(getPYIndexChar((char) chart));
                    continue;
                }
                char c = (char) b[i];
                if (!Character.isJavaIdentifierPart(c))// 确定指定字符是否可以是 Java
                    // 标识符中首字符以外的部分。
                    c = 'A';
                buffer.append(Character.toUpperCase(c));
            }
            return buffer.toString();
        } catch (Exception e) {
            System.out.println((new StringBuilder())
                    .append("\u53D6\u4E2D\u6587\u62FC\u97F3\u6709\u9519")
                    .append(e.getMessage()).toString());
        }
        return null;
    }

    /**
     * 得到首字母
     *
     * @param strChinese 文字
     */
    private static char getPYIndexChar(char strChinese) {
        int charGBK = strChinese;
        char result;
        if (charGBK >= 45217 && charGBK <= 45252)
            result = 'A';
        else if (charGBK >= 45253 && charGBK <= 45760)
            result = 'B';
        else if (charGBK >= 45761 && charGBK <= 46317)
            result = 'C';
        else if (charGBK >= 46318 && charGBK <= 46825)
            result = 'D';
        else if (charGBK >= 46826 && charGBK <= 47009)
            result = 'E';
        else if (charGBK >= 47010 && charGBK <= 47296)
            result = 'F';
        else if (charGBK >= 47297 && charGBK <= 47613)
            result = 'G';
        else if (charGBK >= 47614 && charGBK <= 48118)
            result = 'H';
        else if (charGBK >= 48119 && charGBK <= 49061)
            result = 'J';
        else if (charGBK >= 49062 && charGBK <= 49323)
            result = 'K';
        else if (charGBK >= 49324 && charGBK <= 49895)
            result = 'L';
        else if (charGBK >= 49896 && charGBK <= 50370)
            result = 'M';
        else if (charGBK >= 50371 && charGBK <= 50613)
            result = 'N';
        else if (charGBK >= 50614 && charGBK <= 50621)
            result = 'O';
        else if (charGBK >= 50622 && charGBK <= 50905)
            result = 'P';
        else if (charGBK >= 50906 && charGBK <= 51386)
            result = 'Q';
        else if (charGBK >= 51387 && charGBK <= 51445)
            result = 'R';
        else if (charGBK >= 51446 && charGBK <= 52217)
            result = 'S';
        else if (charGBK >= 52218 && charGBK <= 52697)
            result = 'T';
        else if (charGBK >= 52698 && charGBK <= 52979)
            result = 'W';
        else if (charGBK >= 52980 && charGBK <= 53688)
            result = 'X';
        else if (charGBK >= 53689 && charGBK <= 54480)
            result = 'Y';
        else if (charGBK >= 54481 && charGBK <= 55289)
            result = 'Z';
        else
            result = (char) (65 + (new Random()).nextInt(25));
        result = Character.toUpperCase(result);
        return result;
    }

    @TargetApi(19)
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     * @param context
     * @return 图片的绝对地址
     */
    public static String getPath(Activity context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, proj, null, null, null);
        cursor.moveToFirst();
        int actual_image_column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(actual_image_column_index);
    }

    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */
    public static boolean isGoodName(char c) {
        if (isChinese(c)) {
            return true;
        } else if (Character.isLowerCase(c) || Character.isUpperCase(c)) {
            return true;
        }
        return false;
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
            return true;
        }
        return false;
    }

    public static String getImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context
                .TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        return imei;
    }

    /**
     * @param i
     * @return 输入1返回01
     */
    public static String formatIntTime(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return i + "";
        }
    }

    /**
     * 习题详情，替换url字符串
     *
     * @param str
     * @param list
     * @return
     */
    public static String replaceImgUrl(String str, List<String> list) {
        if (list == null || list.size() == 0) {
            return str;
        }
        for (int i = 0; i < list.size(); i++) {
            if (str.contains("/$img$/")) {
                String s;
                int indexOf = str.indexOf("/$img$/");
                String ss = str.substring(0, indexOf);
                s = ss + "<img src=" + "\"" + list.get(i).replace("\\", "/") + "\">";
                str = s + str.substring(indexOf + 7, str.length());
            }
        }
        return str;
    }

    /**
     * @param list
     * @return
     */
    public static int getListSize(List list) {
        return list == null ? 0 : list.size();
    }

    /**
     * @param list
     * @return
     */
    public static boolean isEmpty(List list) {
        return list == null ? true : list.isEmpty();
    }

    /**
     * 是否能在线打开的文件
     *
     * @param url
     * @return
     */
    public static boolean isOpen(String url) {
        url = url.toLowerCase();
        if (url.endsWith(".gif") || url.endsWith(".jpg") || url.endsWith(".bmp") || url.endsWith(".jpeg") || url
                .endsWith(".png") || url.endsWith(".html") || url.endsWith(".flv")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是PAD还是手机
     *
     * @param context
     * @return
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 判断键盘是否打开
     *
     * @param context
     * @return
     */
    public static boolean isSoftShowing(Activity context) {
        //获取当前屏幕内容的高度
        int screenHeight = context.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom > 200;
    }

    /**
     * px转换dp
     *
     * @param px
     * @param context
     * @return
     */
    public static int pxConvertDp(int px, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());
    }

    public static int getResId(int position) {
        int i;
        if (position > 9) {
            i = position % 10;
        } else {
            i = position;
        }
        switch (i) {
            case 0:
                return R.color.goods_color_0;
            case 1:
                return R.color.goods_color_1;
            case 2:
                return R.color.goods_color_2;
            case 3:
                return R.color.goods_color_3;
            case 4:
                return R.color.goods_color_4;
            case 5:
                return R.color.goods_color_5;
            case 6:
                return R.color.goods_color_6;
            case 7:
                return R.color.goods_color_7;
            case 8:
                return R.color.goods_color_8;
            case 9:
                return R.color.goods_color_9;
            default:
                return R.color.goods_color_0;
        }
    }

    public static String getResCode(int position) {
        int i;
        if (position > 9) {
            i = position % 10;
        } else {
            i = position;
        }
        switch (i) {
            case 0:
                return "#B5B5B5";
            case 1:
                return "#9076F2";
            case 2:
                return "#F19EC2";
            case 3:
                return "#13B5B1";
            case 4:
                return "#E66868";
            case 5:
                return "#F29B76";
            case 6:
                return "#AFC4D5";
            case 7:
                return "#32B16C";
            case 8:
                return "#13B5B1";
            case 9:
                return "#7ECEF4";
            default:
                return "#35BFBB";
        }
    }
}
