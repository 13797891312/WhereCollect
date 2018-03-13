package com.gongwu.wherecollect.view;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;

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
public class BoxEditDialog extends Dialog {
    Context context;
    @Bind(R.id.dialog_edit_et)
    EditText dialogEditEt;
    @Bind(R.id.cancel)
    TextView cancel;
    @Bind(R.id.commit)
    TextView commit;
    String str;
    @Bind(R.id.delete_btn)
    TextView deleteBtn;
    @Bind(R.id.move_btn)
    TextView moveBtn;
    @Bind(R.id.title)
    TextView titleTv;
    @Bind(R.id.root)
    RelativeLayout root;
    int type = 0;

    public BoxEditDialog(Context context, String str) {
        super(context, R.style.dialog);
        this.context = context;
        this.str = str;
        init();
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleTv.setText(title);
    }

    /**
     * @param type 0为新建，1为编辑
     */
    public void setType(int type) {
        this.type = type;
        if (type == 0) {
            deleteBtn.setVisibility(View.GONE);
            moveBtn.setVisibility(View.GONE);
        } else {
            deleteBtn.setVisibility(View.VISIBLE);
            moveBtn.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        View v = View.inflate(context, R.layout.box_edit_dialog, null);
        ButterKnife.bind(this, v);
        setContentView(v);
        dialogEditEt.setText(str);
        dialogEditEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setBtStatu();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialogEditEt.setText(str);
    }

    @Override
    public void show() {
        super.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogEditEt.setSelection(dialogEditEt.getText().length());
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(dialogEditEt, 0);
            }
        }, 100);
    }

    @OnClick({R.id.cancel, R.id.commit, R.id.delete_btn, R.id.move_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.commit:
                if (dialogEditEt.getText().toString().equals(str)) {
                    if (type == 1) {//编辑
                        dismiss();
                    } else {
                        commit(dialogEditEt.getText().toString(), type);
                        dismiss();
                    }
                    return;
                } else {
                    commit(dialogEditEt.getText().toString(), type);
                    dismiss();
                }
                break;
            case R.id.delete_btn:
                delete();
                dismiss();
                break;
            case R.id.move_btn:
                move();
                dismiss();
                break;
        }
    }

    /**
     * 用来回调
     *
     * @param str
     */
    protected void commit(String str, int type) {
    }

    /**
     * 用来回调
     */
    protected void delete() {
    }

    /**
     * 用来回调
     */
    protected void move() {
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
