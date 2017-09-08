package com.gongwu.wherecollect.util;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
public class SaveDate {
    static SharedPreferences sharedPreferences;
    private static SaveDate SAVEDATE;
    //上下文
    private Context con;

    private SaveDate(Context con) {
        this.con = con;
    }

    /***
     * 得到一个单例对象
     ***/
    public static SaveDate getInstence(Context con) {
        if (SAVEDATE == null) {
            SAVEDATE = new SaveDate(con);
        }
        if (sharedPreferences == null) {
            sharedPreferences = con.getSharedPreferences("saveDate", Context.MODE_PRIVATE);
        }
        return SAVEDATE;
    }
    //#########################get/set方法区 开始################################################

    /**
     * @return isOnce
     */
    public boolean isOnce() {
        return sharedPreferences.getBoolean("isOnce", true);
    }

    public void setIsOnce(boolean isOnce) {
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
}
