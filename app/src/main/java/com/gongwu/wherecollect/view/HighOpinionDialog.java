package com.gongwu.wherecollect.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.util.QRCode;

/**
 * Created by Administrator on 2018/10/10.
 */

public class HighOpinionDialog extends Dialog {
    private Window window = null;
    private Context context;

    public HighOpinionDialog(Context context) {
        super(context, R.style.userCodeDialogStyle);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showDialog() {
        setContentView(R.layout.dialog_high_opinion_layout);
        TextView startApp = (TextView) findViewById(R.id.start_app_tv);
        TextView startAct = (TextView) findViewById(R.id.start_act_tv);
        TextView cancel = (TextView) findViewById(R.id.cancel_tv);
        startApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startApp();
                dismiss();
            }
        });
        startAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAct();
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        //设置触摸对话框意外的地方取消对话框
        setCanceledOnTouchOutside(true);
        show();
    }

    public void startApp() {

    }

    public void startAct() {

    }

    @Override
    public void show() {
        super.show();
        window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
    }
}
