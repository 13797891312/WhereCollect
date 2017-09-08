package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
/**
 * Created by Mucll on 2016/8/19.
 */
public abstract class EditTextDialog {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_PWD = 2;
    public static final int TYPE_EMAIL = 3;
    public EditText editText;
    private Context context;
    private String title;
    private String content;
    private int type;

    /**
     * @param context
     * @param title   标题
     * @param content 提示内容：请输入XXXX
     * @param type    输入类型
     */
    public EditTextDialog(Context context, String title, String content, int type) {
        this.context = context;
        this.title = title;
        this.content = content;
        this.type = type;
    }

    public void show() {
        View view = View.inflate(context, R.layout.dialog_edit_text, null);
        editText = (EditText) view.findViewById(R.id.dialog_edit_et);
        switch (type) {
            case TYPE_PWD:
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case TYPE_EMAIL:
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
        }
        editText.setHint(content);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder
                (context, AlertDialog
                        .THEME_HOLO_LIGHT);
        builder.setView(view);
        builder.setTitle(title);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String result = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(result)) {
                    result(result);
                } else {
                    Toast.makeText(context, title + "不能为空，请重新输入", Toast.LENGTH_SHORT).show();
                    show();
                }
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
        WindowManager.LayoutParams params =
                dialog.getWindow().getAttributes();
        params.width = (int) (BaseViewActivity.getScreenWidth(((Activity) context)) * 6.0f / 7.0f);
        dialog.getWindow().setAttributes(params);
    }

    public abstract void result(String result);
}
