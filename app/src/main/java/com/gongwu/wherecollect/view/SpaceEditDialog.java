package com.gongwu.wherecollect.view;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.util.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Function:
 * Date: 2017/11/22
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class SpaceEditDialog extends Dialog {
    Context context;
    @Bind(R.id.dialog_edit_et)
    EditText dialogEditEt;
    @Bind(R.id.number)
    TextView number;
    @Bind(R.id.cancel)
    TextView cancel;
    @Bind(R.id.commit)
    TextView commit;
    String str;

    public SpaceEditDialog(Context context, String str) {
        super(context, R.style.dialog);
        this.context = context;
        this.str = str;
        init();
    }

    private void init() {
        View v = View.inflate(context, R.layout.space_edit_dialog, null);
        ButterKnife.bind(this, v);
        setContentView(v);
        LogUtil.e(dialogEditEt + "---" + str);
        dialogEditEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                number.setText(String.format("%d/8", dialogEditEt.getText().length()));
                setBtStatu();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialogEditEt.setText(str);
        dialogEditEt.setSelection(str.length());
    }

    @Override
    public void show() {
        super.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(dialogEditEt, 0);
            }
        }, 100);
    }

    @OnClick({R.id.cancel, R.id.commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.commit:
                if (dialogEditEt.getText().toString().equals(str)) {
                    dismiss();
                    return;
                } else {
                    commit(dialogEditEt.getText().toString());
                    dismiss();
                }
                break;
        }
    }

    /**
     * 用来回调
     *
     * @param str
     */
    protected void commit(String str) {
    }

    private void setBtStatu() {
        if (dialogEditEt.getText().length() == 0) {
            commit.setEnabled(false);
            commit.setTextColor(context.getResources().getColor(R.color.black_26));
        } else {
            commit.setEnabled(true);
            commit.setTextColor(context.getResources().getColor(R.color.maincolor));
        }
    }
}
