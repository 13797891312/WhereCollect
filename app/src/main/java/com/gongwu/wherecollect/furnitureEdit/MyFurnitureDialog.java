package com.gongwu.wherecollect.furnitureEdit;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Function:
 * Date: 2017/12/4
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class MyFurnitureDialog extends Dialog {
    Context context;
    View view;
    @Bind(R.id.edit_1)
    EditText edit1;
    @Bind(R.id.edit_2)
    EditText edit2;
    @Bind(R.id.text_1)
    TextView text1;
    @Bind(R.id.text_2)
    TextView text2;
    @Bind(R.id.btn_cancal)
    Button btnCancal;
    @Bind(R.id.btn_commit)
    Button btnCommit;

    public MyFurnitureDialog(Context context) {
        super(context, R.style.Transparent2);
        this.context = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        view = View.inflate(context, R.layout.layout_myfurniture, null);
        ButterKnife.bind(this,view);
        setContentView(view);
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams params =
                getWindow().getAttributes();
        params.width = (int) (BaseViewActivity.getScreenWidth(((Activity) context)) * 6.0f / 7.0f);
        getWindow().setAttributes(params);
    }

    @OnClick({R.id.text_1, R.id.text_2, R.id.btn_cancal, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_1:
                break;
            case R.id.text_2:
                break;
            case R.id.btn_cancal:
                dismiss();
                break;
            case R.id.btn_commit:
                dismiss();
                break;
        }
    }
}
