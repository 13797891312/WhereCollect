package com.gongwu.wherecollect.util;
import android.content.Context;
import android.text.TextUtils;
/**
 * Function:
 * Date: 2018/1/20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ACacheClient {
    /**
     * 删除一个缓存
     *
     * @param context
     * @param key
     */
    public static void removeCache(Context context, String key) {
        ACache aCache = ACache.get(context);
        aCache.remove(key);
    }
    /**
     * 缓存家具详情
     *
     * @param context
     * @param uid
     * @param code
     * @param json
     */
    public static void saveFurnitrueDetail(Context context, String uid, String code, String json) {
        ACache aCache = ACache.get(context);
        aCache.put(String.format("furnitrueDetail%s%s", uid, code), json, 30 * ACache.TIME_DAY);
    }

    public static String getFurnitrueDetail(Context context, String uid, String code) {
        ACache aCache = ACache.get(context);
        return aCache.getAsString(String.format("furnitrueDetail%s%s", uid, code));
    }

    /**
     * 暂存室迹
     *
     * @param context
     * @param code
     * @param json
     */
    public static void saveRecord(Context context,String code, String json) {
        ACache aCache = ACache.get(context);
        aCache.put(String.format("record%s",code), json, 30 * ACache.TIME_DAY);
    }
    public static String getRecord(Context context,String code) {
        ACache aCache = ACache.get(context);
        return aCache.getAsString(String.format("record%s", code));
    }

    /**
     * 设置室迹列表缓存
     * @param context
     * @param uid
     * @param json
     */
    public static void setRecordList(Context context,String uid, String json) {
        ACache aCache = ACache.get(context);
        aCache.put(String.format("recordlist%s",uid), json, 30 * ACache.TIME_DAY);
    }
    public static String getRecordList(Context context,String uid) {
        ACache aCache = ACache.get(context);
        return aCache.getAsString(String.format("recordlist%s", uid));
    }
}
