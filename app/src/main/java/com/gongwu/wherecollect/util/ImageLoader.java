package com.gongwu.wherecollect.util;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
/**
 * ClassName:ImageLoader
 * Function:图片加载工具类
 * Date: 2016/8/25
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ImageLoader {

    /**
     * 普通加载图片,设置错误图片
     *
     * @param context
     * @param iv
     * @param url
     * @param errorRes 加载错误后的图片
     */
    public static void load(Context context, ImageView iv, String url, int errorRes) {
        Glide.with(context)
                .load(url)
                .dontAnimate()
                .error(errorRes)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);
    }
    /**
     * 普通加载图片,设置错误图片
     *
     * @param context
     * @param iv
     * @param url
     * @param errorRes 加载错误后的图片
     */
    public static void load(Context context, ImageView iv, String url, int errorRes,int r) {
        Glide.with(context)
                .load(url)
                .dontAnimate()
                .error(errorRes)
                .transform(new GlideRoundTransform(context,r))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);
    }

    /**
     * 加载圆形图标,设置错误图片
     *
     * @param context
     * @param iv
     * @param url
     * @param errorRes     加载错误后的图片
     */
    public static void loadCircle(final Context context, final ImageView iv, String url, int
            errorRes) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .diskCacheStrategy
                        (DiskCacheStrategy.ALL)
                .placeholder(errorRes)
                .dontAnimate()
                .error(errorRes)
                .into(new BitmapImageViewTarget(iv) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(),
                                        resource);
                        circularBitmapDrawable.setCircular(true);
                        iv.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    /**
     * 加载原型图片，通过ID
     *
     * @param context
     * @param resId
     * @param iv
     */
    public static void loadCircle(final Context context, int resId, final ImageView iv) {
        Glide.with(context)
                .load(resId)
                .asBitmap()
                .into(new BitmapImageViewTarget(iv) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(),
                                        resource);
                        circularBitmapDrawable.setCircular(true);
                        iv.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    /**
     * 加载原型图片
     *
     * @param context
     * @param iv
     */
    public static void loadCircleFromFile(final Context context, File file, final ImageView
            iv) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .into(new BitmapImageViewTarget(iv) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(),
                                        resource);
                        circularBitmapDrawable.setCircular(true);
                        iv.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    /**
     * 加载file图片
     *
     * @param context
     * @param iv
     */
    public static void loadFromFile(final Context context, File file, final ImageView
            iv) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .diskCacheStrategy
                        (DiskCacheStrategy.NONE)
                .into(iv);
    }


    public static void loadUrlAsBitmap(String url, final ImageView iv, Context context) {
        Glide.with(context)
                .load(url.replace("\\", "/"))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
//                .error(R.drawable.icon_image_loading)
                .override(720, ImageLoader.getMaxBitmap())
                .into(iv);
    }

    public static void loadUrlAsBitmap(File file, final ImageView iv, Context context) {
        Glide.with(context)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(720, ImageLoader.getMaxBitmap())
                .into(iv);
    }

    /**
     * @return 获取android设备支持的图片最大值
     */
    public static int getMaxBitmap() {
        int max;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            max = getGLESTextureLimitEqualAboveLollipop();
        } else {
            max = getGLESTextureLimitBelowLollipop();
        }
        if (max > 4000) {
            return 4000;
        } else if (max == 0) {
            return 3000;
        }
        return max;
    }

    private static int getGLESTextureLimitBelowLollipop() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    private static int getGLESTextureLimitEqualAboveLollipop() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] vers = new int[2];
        egl.eglInitialize(dpy, vers);
        int[] configAttr = {
                EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                EGL10.EGL_LEVEL, 0,
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig);
        if (numConfig[0] == 0) {// TROUBLE! No config found.
        }
        EGLConfig config = configs[0];
        int[] surfAttr = {
                EGL10.EGL_WIDTH, 64,
                EGL10.EGL_HEIGHT, 64,
                EGL10.EGL_NONE
        };
        EGLSurface surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr);
        final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;  // missing in EGL10
        int[] ctxAttrib = {
                EGL_CONTEXT_CLIENT_VERSION, 1,
                EGL10.EGL_NONE
        };
        EGLContext ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib);
        egl.eglMakeCurrent(dpy, surf, surf, ctx);
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        egl.eglMakeCurrent(dpy, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(dpy, surf);
        egl.eglDestroyContext(dpy, ctx);
        egl.eglTerminate(dpy);
        return maxSize[0];
    }
}
