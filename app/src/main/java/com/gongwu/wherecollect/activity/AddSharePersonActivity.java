package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.zsitech.oncon.barcode.core.CaptureActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加共享人
 */
public class AddSharePersonActivity extends BaseViewActivity {

    private final int START_CODE = 101;

    @Bind(R.id.add_share_title_tv)
    TextView titleView;
    @Bind(R.id.add_share_edit)
    EditText addShareEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_share_person);
        titleLayout.setVisibility(View.GONE);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initView() {
        titleView.setText("添加共享人");
    }

    private void initEvent() {

    }

    @OnClick({R.id.add_share_scan_tv, R.id.add_share_back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_share_back_btn:
                onBackPressed();
                break;
            case R.id.add_share_scan_tv:
                startActivityForResult(new Intent(context, CaptureActivity.class), START_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_CODE && resultCode == CaptureActivity.result) {//扫描的到结果
            String result = data.getStringExtra("result");
            addShareEditView.setText(result);
            addShareEditView.setSelection(result.length());
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AddSharePersonActivity.class);
        context.startActivity(intent);
    }
}
