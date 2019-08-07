package com.gongwu.wherecollect.application;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import com.gongwu.wherecollect.BuildConfig;
import com.gongwu.wherecollect.entity.UserBean;
import com.gongwu.wherecollect.util.AppConstant;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.SaveDate;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.media.UMImage;

import org.litepal.LitePalApplication;

import java.io.File;

/**
 * Function:
 * Date: 2017/8/29
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class MyApplication extends Application {
    public static String CACHEPATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/shouner/";
    private static UserBean user;
    private static Context appContext;
    public static boolean addGood = false;

    public static UserBean getUser(Context context) {
        if (user == null) {
            user = JsonUtils.objectFromJson(SaveDate.getInstence(context).getUser(), UserBean.class);
        }
        return user;
    }

    public static void setUser(UserBean user) {
        MyApplication.user = user;
    }

    public static Context getContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        LitePalApplication.initialize(this);
        initUM();
        initCache();
        try {//必须加上/否则剪切照片可能会出错
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                builder.detectFileUriExposure();
            }
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
        Config.DEBUG = BuildConfig.LOGSHOW;
        PlatformConfig.setWeixin("wx9b2c37b4717d4c13", "e1777498993b4eecbc20e9ef8c520c5d");
//        PlatformConfig.setQQZone("1105780975", "YtsbvRT5V9PUaG8X");
        PlatformConfig.setQQZone("1106091663", "DknG4bIDrqPOQPSa");
        PlatformConfig.setSinaWeibo("2932944667", "ce56f1cd16996a7895964192463a3027", "https://sns.whalecloud" +
                ".com/sina2/callback");
        MobclickAgent.setCatchUncaughtExceptions(true);
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(this).setShareConfig(config);
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "de26ade0140020dd98cc6999598a4ff6");
        //获取消息推送代理示例
        //注册推送服务，每次调用register方法都会回调该接口
        PushAgent.getInstance(this).register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                AppConstant.DEVICE_TOKEN = deviceToken;
                LogUtil.e("PushAgent register Success");
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });
    }
}
