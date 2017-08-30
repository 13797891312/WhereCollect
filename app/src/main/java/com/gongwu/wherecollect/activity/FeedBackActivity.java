package com.gongwu.wherecollect.activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
public class FeedBackActivity extends BaseViewActivity implements View.OnClickListener {
    @Bind(R.id.title)
    EditText title;
    @Bind(R.id.info)
    EditText info;
    @Bind(R.id.phone)
    EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        titleLayout.textBtn.setVisibility(View.VISIBLE);
        titleLayout.setOnClickListener(this);
        titleLayout.setTitle("意见反馈");
        titleLayout.setBack(true,null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textBtn:
                ToastUtil.show(this, "提交", Toast.LENGTH_SHORT);
                break;
        }
    }
}
