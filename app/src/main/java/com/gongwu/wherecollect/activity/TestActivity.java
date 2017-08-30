package com.gongwu.wherecollect.activity;
import android.os.Bundle;

import com.gongwu.wherecollect.R;
public class TestActivity extends BaseViewActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("测试");
    }
}
