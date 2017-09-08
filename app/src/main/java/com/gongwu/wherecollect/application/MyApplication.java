package com.gongwu.wherecollect.application;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;

import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
/**
 * Function:
 * Date: 2017/8/29
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class MyApplication extends Application {
    public static final String CACHEPATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/shouner/";
    private static UserBean user;

    public static UserBean getUser(Context context) {
        if (user == null) {
            user = JsonUtils.objectFromJson(SaveDate.getInstence(context).getUser(), UserBean.class);
        }
        return user;
    }

    public static void setUser(UserBean user) {
        MyApplication.user = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initUM();
        initCache();
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCache() {
        File file = new File(CACHEPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void initUM() {
        PlatformConfig.setWeixin("wx40bce19f267f0463", "90774b219b12d48041f5d608cf8c4b7b");
        PlatformConfig.setQQZone("1105780975", "YtsbvRT5V9PUaG8X");
        PlatformConfig.setSinaWeibo("1912998019", "270bc99f157db5f9c257b481ba548030", "http://sns.whalecloud.com");
        UMShareAPI.get(this);
    }
}
