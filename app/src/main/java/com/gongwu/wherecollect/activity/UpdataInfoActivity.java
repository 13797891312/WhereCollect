package com.gongwu.wherecollect.activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gongwu.wherecollect.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 版本升级后的提示页面
 */
public class UpdataInfoActivity extends BaseViewActivity {
    @Bind(R.id.tv_info)
    TextView tvInfo;
    @Bind(R.id.commit)
    Button commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata_info);
        ButterKnife.bind(this);
        titleLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.commit)
    public void onClick() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        finish();
    }
}
