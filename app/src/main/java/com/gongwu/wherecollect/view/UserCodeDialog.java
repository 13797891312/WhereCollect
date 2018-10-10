package com.gongwu.wherecollect.view;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import com.gongwu.wherecollect.R;

/**
 * Created by Administrator on 2018/10/10.
 */

public class UserCodeDialog extends Dialog {
    private Window window = null;

    public UserCodeDialog(Context context) {
        super(context);
    }

    public void showDialog(int layoutResID) {
        setContentView(layoutResID);
        //设置触摸对话框意外的地方取消对话框
        setCanceledOnTouchOutside(true);
        show();
    }

    @Override
    public void show() {
        super.show();
        window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        window.setBackgroundDrawableResource(R.color.vifrification); //设置对话框背景为透明
    }
}
