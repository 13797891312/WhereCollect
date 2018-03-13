package com.gongwu.wherecollect.activity;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.view.TitleLayout;
import com.umeng.analytics.MobclickAgent;
import com.zhaojin.myviews.HackyViewPager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
/**
 * acticity的父类
 */
public class BaseViewActivity extends FragmentActivity {
    private static int screenWidth;
    private static int screenHeigth;
    private static float screenScale;// px dip比例
    public TitleLayout titleLayout;
    public boolean mIsDestoryed = false;
    protected Context context;
    private boolean isPaused = false;
    private LinearLayout contentView;

    public static int getScreenWidth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        screenWidth = dm.widthPixels;
        return screenWidth;
    }

    public static int getScreenHeigth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        screenHeigth = dm.heightPixels;
        return screenHeigth;
    }

    public static float getScreenScale(Activity context) {
        if (screenScale <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay()
                    .getMetrics(dm);
            screenScale = dm.scaledDensity;
        }
        return screenScale;
    }

    // 获取状态栏高度。不能在onCreate回调方法中获取
    public static int getStateHeight(Context context) {
        Rect frame = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        String vendor = Build.MANUFACTURER;
        if (vendor.toLowerCase(Locale.ENGLISH).contains("xiaomi")) {
            MIUISetStatusBarLightMode(this.getWindow(), true);
        } else if (vendor.toLowerCase(Locale.ENGLISH).contains("meizu")) {
            FlymeSetStatusBarLightMode(this.getWindow(),true);
        }
        FlymeSetStatusBarLightMode(this.getWindow(), true);
        contentView = (LinearLayout) View.inflate(this, R.layout.activity_base_layout, null);
        titleLayout = (TitleLayout) contentView.findViewById(R.id.title_layout);
        super.setContentView(contentView);
        context = this;
        getScreenScale(this);
        getScreenWidth(this);
        getScreenHeigth(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        View.inflate(this, layoutResID, contentView);
    }
    //   @Subscribe(threadMode = ThreadMode.MAIN)
    //    public void onMessageEvent(Object object) {
    //
    //   };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestoryed = true;
        System.gc();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            HackyViewPager.isItercept = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo
                    .IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        isPaused = false;
    }

    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return super.isDestroyed();
        } else {
            return mIsDestoryed;// 在onDestroy中设置true
        }
    }

    /**
     * 小米手机状态栏字体黑色
     * @param window
     * @param dark
     * @return
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 魅族手机状态栏字体黑色
     * @param window
     * @param dark
     * @return
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }
}
