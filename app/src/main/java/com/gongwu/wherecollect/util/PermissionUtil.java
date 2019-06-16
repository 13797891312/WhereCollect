package com.gongwu.wherecollect.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.gongwu.wherecollect.activity.MainActivity;

/**
 * ClassName:PermissionException
 * Function:
 * Date: 2017/5/4
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class PermissionUtil {
    public PermissionUtil(final Activity context, String msg) {
        DialogUtil.show("提示", msg, "设置", "取消", ((Activity) context), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", "com.gongwu.wherecollect", null));
                    context.startActivity(intent);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 判断相机是否可用
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }
        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    public static boolean judgeXuanFuPermission(final Context context, String msg) {
        //兼容api23版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {

            } else {
                DialogUtil.show("提示", msg, "设置", "取消", ((Activity) context), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            //若没有权限，提示获取.
                            context.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                return false;
            }
        }
        return true;
    }
}
