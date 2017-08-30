package com.gongwu.wherecollect.application;
import android.app.Application;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
/**
 * Function:
 * Date: 2017/8/29
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initUM();
    }

    private void initUM() {
        PlatformConfig.setWeixin("wx1ab6fdc63d403355", "6f395b39a0c8a4914a4c5c6d991de51e");
        PlatformConfig.setQQZone("1105780975", "YtsbvRT5V9PUaG8X");
        PlatformConfig.setSinaWeibo("1912998019", "270bc99f157db5f9c257b481ba548030", "http://sns.whalecloud.com");
        UMShareAPI.get(this);
    }
}
