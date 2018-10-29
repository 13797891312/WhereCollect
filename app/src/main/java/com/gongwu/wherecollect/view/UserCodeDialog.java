package com.gongwu.wherecollect.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.util.QRCode;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2018/10/10.
 */

public class UserCodeDialog extends Dialog {
    private Window window = null;
    private Context context;

    public UserCodeDialog(Context context) {
        super(context, R.style.userCodeDialogStyle);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showDialog(String id, Bitmap usericon) {
        setContentView(R.layout.dialog_user_code);
        ImageView image_code = (ImageView) findViewById(R.id.image_code);
        String contnet = "https://www.shouner.com/user/profile?id=" + id;
        Bitmap bitmap = QRCode.createQRCodeWithLogo(contnet, 500,usericon
                );
//        BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)
        image_code.setImageBitmap(bitmap);
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
