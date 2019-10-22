package com.gongwu.wherecollect.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.gongwu.wherecollect.R;

/**
 * Created by Administrator on 2018/10/10.
 */

public class RemindDialog extends Dialog {
    private Window window = null;
    private Context context;

    public RemindDialog(Context context) {
        super(context, R.style.userCodeDialogStyle);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showDialog(String remindText) {
        setContentView(R.layout.dialog_remind_layout);
        TextView remindTv = (TextView) findViewById(R.id.remind_text);
        remindTv.setText(remindText);
        TextView submitTv = (TextView) findViewById(R.id.submit_remind_tv);
        submitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        //设置触摸对话框意外的地方取消对话框
        setCanceledOnTouchOutside(true);
        show();
    }

    @Override
    public void show() {
        super.show();
        window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
    }
}
