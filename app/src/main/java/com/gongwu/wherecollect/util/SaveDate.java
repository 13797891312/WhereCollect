package com.gongwu.wherecollect.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gongwu.wherecollect.application.MyApplication;

public class SaveDate {
    static SharedPreferences sharedPreferences;
    private static SaveDate SAVEDATE;

    /***
     * 得到一个单例对象
     ***/
    public static SaveDate getInstence(Context con) {
        if (SAVEDATE == null) {
            SAVEDATE = new SaveDate();
        }
        if (sharedPreferences == null) {
            sharedPreferences = MyApplication.getContext().getSharedPreferences("saveDate", Context.MODE_PRIVATE);
        }
        return SAVEDATE;
    }
    //#########################get/set方法区 开始################################################

    public boolean getRecordSaved(String uid) {
        return sharedPreferences.getBoolean(String.format("recordsaved%s", uid), false);
    }

    public boolean getRecordRed(String uid) {
        return sharedPreferences.getBoolean(String.format("recordvisible%s", uid), false);
    }

    /**
     * @return 储存版本，版本升级后用来比较版本
     */
    public int getVersion() {
        return sharedPreferences.getInt("version", 0);
    }

    public void setVersion(int version) {
        Editor ed = sharedPreferences.edit();
        ed.putInt("version", version);
        ed.commit();
    }

    /**
     * @return isOnce
     */
    public boolean isOnceShiji() {
        return sharedPreferences.getBoolean("isOnce", true);
    }

    public void setIsOnceShiji(boolean isOnce) {
        Editor ed = sharedPreferences.edit();
        ed.putBoolean("isOnce", isOnce);
        ed.commit();
    }

    public String getPhone() {
        return sharedPreferences.getString("phone", "");
    }

    public void setPhone(String phone) {
        Editor ed = sharedPreferences.edit();
        ed.putString("phone", phone);
        ed.commit();
    }
    //############################################

    public String getUser() {
        return sharedPreferences.getString("user", "");
    }

    public void setUser(String user) {
        Editor ed = sharedPreferences.edit();
        ed.putString("user", user);
        ed.commit();
    }

    public String getPWD() {
        return sharedPreferences.getString("pwd", "");
    }

    public void setPWD(String str) {
        Editor ed = sharedPreferences.edit();
        ed.putString("pwd", str);
        ed.commit();
    }

    /**
     * 缓存物品列表
     *
     * @return
     */
    public String getObjectList() {
        return sharedPreferences.getString("objectList", "");
    }

    /**
     * 缓存物品列表
     */
    public void setObjectList(String str) {
        Editor ed = sharedPreferences.edit();
        ed.putString("objectList", str);
        ed.commit();
    }

    /**
     * 缓存物品筛选条件
     *
     * @return
     */
    public String getObjectFilter(String uid) {
        return sharedPreferences.getString(String.format("objectFilter%s", uid), "");
    }

    /**
     * 缓存物品筛选条件
     */
    public void setObjectFilter(String id, String str) {
        Editor ed = sharedPreferences.edit();
        ed.putString(String.format("objectFilter%s", id), str);
        ed.commit();
    }

    /**
     * 缓存空间桌布
     *
     * @return
     */
    public String getSpace() {
        return sharedPreferences.getString("space", "");
    }

    /**
     * 缓存空间桌布
     *
     * @return
     */
    public void setSpace(String space) {
        Editor ed = sharedPreferences.edit();
        ed.putString("space", space);
        ed.commit();
    }
    //############################################

    /**
     * 获取缓存位置
     *
     * @return
     */
    public String getLocation(String code) {
        return sharedPreferences.getString(String.format("location%s", code), "");
    }

    /**
     * 缓存位置
     */
    public void setLocation(String code, String location) {
        Editor ed = sharedPreferences.edit();
        ed.putString(String.format("location%s", code), location);
        ed.commit();
    }

    /**
     * 缓存位置
     */
    public void setObjectWithCode(String code, String objectListJson) {
        Editor ed = sharedPreferences.edit();
        ed.putString(String.format("object%s", code), objectListJson);
        ed.commit();
    }

    /**
     * 获取空间里物品总览
     *
     * @return
     */
    public String getObjectWithCode(String code) {
        return sharedPreferences.getString(String.format("object%s", code), "");
    }

    /**
     * 是否在本机保存过室迹
     *
     * @param uid
     */
    public void setRecordSaved(String uid, boolean issaved) {
        Editor ed = sharedPreferences.edit();
        ed.putBoolean(String.format("recordsaved%s", uid), issaved);
        ed.commit();
    }

    /**
     * 是否已经显示过了室迹通览的红点
     *
     * @param uid
     */
    public void setRecordRed(String uid, boolean visible) {
        Editor ed = sharedPreferences.edit();
        ed.putBoolean(String.format("recordvisible%s", uid), visible);
        ed.commit();
    }

    /**
     * 是否已经显示过了室迹通览的红点
     *
     * @param uid
     */
    public void setShareClicked(String uid, boolean clicked) {
        Editor ed = sharedPreferences.edit();
        ed.putBoolean(String.format("shareclicked%s", uid), clicked);
        ed.commit();
    }

    public boolean getShareClicked(String uid) {
        return sharedPreferences.getBoolean(String.format("shareclicked%s", uid), false);
    }

    /**
     * 是否已经提示过快速导入空间家具
     *
     * @param uid
     */
    public void setQuickAdd(String uid, boolean isQuickAdd) {
        Editor ed = sharedPreferences.edit();
        ed.putBoolean(String.format("quickadd%s", uid), isQuickAdd);
        ed.commit();
    }

    public boolean getQuickAdd(String uid) {
        return sharedPreferences.getBoolean(String.format("quickadd%s", uid), false);
    }

    /**
     * 是否呼吸查看
     *
     * @param uid
     */
    public void setBreathLook(String uid, boolean isCloseBreathLook) {
        Editor ed = sharedPreferences.edit();
        ed.putBoolean(String.format("breathlook%s", uid), isCloseBreathLook);
        ed.commit();
    }

    public boolean getBreathLook(String uid) {
        return sharedPreferences.getBoolean(String.format("breathlook%s", uid), false);
    }

    /**
     * 分享app
     *
     * @param uid
     */
    public void setShareApp(String uid, boolean isShareApp) {
        Editor ed = sharedPreferences.edit();
        ed.putBoolean(String.format("shareapp%s", uid), isShareApp);
        ed.commit();
    }

    public boolean getShareApp(String uid) {
        return sharedPreferences.getBoolean(String.format("shareapp%s", uid), false);
    }

    /**
     * 统计位置物品数量
     *
     * @param uid
     */
    public void setGoodsNum(String uid, int num) {
        Editor ed = sharedPreferences.edit();
        ed.putInt(String.format("goodsnum%s", uid), num);
        ed.commit();
    }

    public int getGoodsNum(String uid) {
        return sharedPreferences.getInt(String.format("goodsnum%s", uid), 0);
    }
}
