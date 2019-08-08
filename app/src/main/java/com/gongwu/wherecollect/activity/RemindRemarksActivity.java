package com.gongwu.wherecollect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 3.6说明备注
 */
public class RemindRemarksActivity extends BaseViewActivity {

    @Bind(R.id.remind_remarks_et)
    EditText mEditText;
    @Bind(R.id.remind_remarks_text_num_tv)
    TextView numTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_remarks);
        ButterKnife.bind(this);
        initView();
        initEvent();
        String text = getIntent().getStringExtra("remind_remarks");
        mEditText.setText(text);
    }

    private void initView() {
        titleLayout.setTitle(getString(R.string.remind_remarks_title_text));
        titleLayout.imageBtn.setVisibility(View.VISIBLE);
        titleLayout.imageBtn.setImageResource(R.drawable.icon_confirm);
    }

    private void initEvent() {
        titleLayout.setBack(true, null);
        titleLayout.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (!TextUtils.isEmpty(mEditText.getText().toString().trim())) {
                    intent.putExtra("remind_remarks", mEditText.getText().toString().trim());
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                numTv.setText(String.format(getString(R.string.remind_remarks_num_text), charSequence.length() + ""));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
